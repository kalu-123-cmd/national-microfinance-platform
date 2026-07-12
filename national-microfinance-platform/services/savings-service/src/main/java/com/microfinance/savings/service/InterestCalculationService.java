package com.microfinance.savings.service;

import com.microfinance.savings.domain.model.*;
import com.microfinance.savings.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * InterestCalculationService
 *
 * Handles daily interest accrual, end-of-period compounding,
 * and fixed deposit maturity amount calculation.
 *
 * Formula (compound interest):
 *   A = P * (1 + r/n)^(n*t)
 *   Where:
 *     P = principal
 *     r = annual interest rate (decimal)
 *     n = compounding frequency per year
 *     t = time in years
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InterestCalculationService {

    private static final BigDecimal DAYS_IN_YEAR = BigDecimal.valueOf(365);

    private final SavingsAccountRepository accountRepository;
    private final InterestAccrualLogRepository accrualLogRepository;
    private final SavingsTransactionRepository transactionRepository;
    private final FixedDepositRepository fixedDepositRepository;

    // -------------------------------------------------------------------------
    // Fixed Deposit Maturity Amount Calculation
    // -------------------------------------------------------------------------

    /**
     * Calculate the maturity amount for a fixed deposit using compound interest.
     *
     * @param principal      deposited principal
     * @param annualRate     annual interest rate (decimal, e.g. 0.12 for 12%)
     * @param tenureMonths   term in months
     * @param compoundingFreq how often interest is compounded
     * @return total maturity amount (principal + interest)
     */
    public BigDecimal calculateMaturityAmount(BigDecimal principal, BigDecimal annualRate,
                                              int tenureMonths, CompoundingFrequency compoundingFreq) {
        int n = periodsPerYear(compoundingFreq);
        double t = tenureMonths / 12.0;
        double r = annualRate.doubleValue();
        double maturity = principal.doubleValue() * Math.pow(1 + r / n, n * t);
        return BigDecimal.valueOf(maturity).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate early-withdrawal amount after applying penalty on earned interest.
     */
    public BigDecimal calculatePrematureClosureAmount(FixedDeposit fd) {
        BigDecimal earned = fd.getInterestEarned();
        BigDecimal penalty = earned.multiply(fd.getPenaltyRate()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal net = fd.getPrincipalAmount().add(earned).subtract(penalty);
        log.info("FD {} premature closure: principal={}, earned={}, penalty={}, net={}",
                fd.getDepositNumber(), fd.getPrincipalAmount(), earned, penalty, net);
        return net.max(fd.getPrincipalAmount()); // always return at least principal
    }

    // -------------------------------------------------------------------------
    // Daily Accrual Job (runs at midnight)
    // -------------------------------------------------------------------------

    /**
     * Scheduled daily at midnight: accrues interest for all active REGULAR/PENSION accounts.
     * Actual posting happens at end-of-month.
     */
    @Scheduled(cron = "0 0 0 * * *") // midnight
    @Transactional
    public void runDailyAccrual() {
        LocalDate today = LocalDate.now();
        log.info("Running daily interest accrual for date: {}", today);

        List<SavingsAccount> activeAccounts = accountRepository.findByStatus(AccountStatus.ACTIVE);
        int accrued = 0;
        for (SavingsAccount account : activeAccounts) {
            if (account.getBalance().compareTo(account.getMinimumBalance()) <= 0) {
                continue; // no interest on accounts at or below minimum balance
            }
            try {
                accrueInterestForAccount(account, today);
                accrued++;
            } catch (Exception e) {
                log.error("Failed to accrue interest for account {}: {}", account.getAccountNumber(), e.getMessage());
            }
        }
        log.info("Daily accrual complete. Processed {} accounts.", accrued);
    }

    /**
     * Monthly posting job: posts all accrued (unposted) interest to account balances.
     * Runs on the 1st of every month at 01:00.
     */
    @Scheduled(cron = "0 0 1 1 * *") // 1st of month at 01:00
    @Transactional
    public void postMonthlyInterest() {
        log.info("Posting accumulated interest credits to savings accounts...");
        List<InterestAccrualLog> unposted = accrualLogRepository.findUnpostedAccruals();

        for (InterestAccrualLog log_ : unposted) {
            try {
                postInterestToAccount(log_);
            } catch (Exception e) {
                log.error("Failed to post interest accrual {}: {}", log_.getId(), e.getMessage());
            }
        }
        log.info("Monthly interest posting complete. {} entries posted.", unposted.size());
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private void accrueInterestForAccount(SavingsAccount account, LocalDate date) {
        // Skip if already accrued today
        if (accrualLogRepository.findByAccountIdAndAccrualDate(account.getId(), date).isPresent()) {
            return;
        }

        BigDecimal dailyRate = account.getInterestRate().divide(DAYS_IN_YEAR, 10, RoundingMode.HALF_UP);
        BigDecimal interestAmount = account.getBalance().multiply(dailyRate).setScale(4, RoundingMode.HALF_UP);

        InterestAccrualLog entry = InterestAccrualLog.builder()
                .id(UUID.randomUUID().toString())
                .accountId(account.getId())
                .accrualDate(date)
                .openingBalance(account.getBalance())
                .dailyRate(dailyRate)
                .interestAmount(interestAmount)
                .posted(false)
                .build();
        accrualLogRepository.save(entry);
    }

    @Transactional
    public void postInterestToAccount(InterestAccrualLog accrual) {
        SavingsAccount account = accountRepository.findById(accrual.getAccountId())
                .orElseThrow(() -> new IllegalStateException("Account not found: " + accrual.getAccountId()));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            return; // skip closed/frozen
        }

        BigDecimal interest = accrual.getInterestAmount().setScale(2, RoundingMode.HALF_UP);
        if (interest.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        BigDecimal balanceBefore = account.getBalance();
        account.setBalance(balanceBefore.add(interest));
        account.setLastInterestDate(accrual.getAccrualDate());
        accountRepository.save(account);

        // Record transaction
        SavingsTransaction tx = SavingsTransaction.builder()
                .id(UUID.randomUUID().toString())
                .accountId(account.getId())
                .userId(account.getUserId())
                .transactionType(TransactionType.INTEREST_CREDIT)
                .reference("INT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .amount(interest)
                .balanceBefore(balanceBefore)
                .balanceAfter(account.getBalance())
                .fee(BigDecimal.ZERO)
                .description("Interest credit for " + accrual.getAccrualDate())
                .initiatedBy("SYSTEM")
                .build();
        transactionRepository.save(tx);

        accrual.setPosted(true);
        accrual.setPostedAt(Instant.now());
        accrualLogRepository.save(accrual);
    }

    private int periodsPerYear(CompoundingFrequency freq) {
        return switch (freq) {
            case DAILY -> 365;
            case MONTHLY -> 12;
            case QUARTERLY -> 4;
            case ANNUALLY -> 1;
        };
    }
}
