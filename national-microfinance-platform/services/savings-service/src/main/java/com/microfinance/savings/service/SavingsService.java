package com.microfinance.savings.service;

import com.microfinance.savings.domain.model.*;
import com.microfinance.savings.domain.repository.*;
import com.microfinance.savings.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SavingsService {

    // Default interest rates by account type (annual, decimal)
    private static final Map<AccountType, BigDecimal> DEFAULT_RATES = Map.of(
            AccountType.REGULAR,       new BigDecimal("0.0500"), // 5%
            AccountType.SAVINGS_GOAL,  new BigDecimal("0.0600"), // 6%
            AccountType.CHILDREN,      new BigDecimal("0.0700"), // 7%
            AccountType.PENSION,       new BigDecimal("0.0800"), // 8%
            AccountType.FIXED_DEPOSIT, new BigDecimal("0.1000")  // 10% (base; FD rates vary by tenure)
    );

    // FD rate by tenure band (months → rate)
    private static final BigDecimal FD_RATE_1_3  = new BigDecimal("0.0900");  // 1–3 months: 9%
    private static final BigDecimal FD_RATE_3_6  = new BigDecimal("0.1000");  // 3–6 months: 10%
    private static final BigDecimal FD_RATE_6_12 = new BigDecimal("0.1100");  // 6–12 months: 11%
    private static final BigDecimal FD_RATE_12P  = new BigDecimal("0.1200");  // 12+ months: 12%

    private final SavingsAccountRepository accountRepository;
    private final FixedDepositRepository fixedDepositRepository;
    private final SavingsGoalRepository goalRepository;
    private final SavingsTransactionRepository transactionRepository;
    private final InterestCalculationService interestCalculationService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // =========================================================================
    // Savings Account operations
    // =========================================================================

    @Transactional
    public SavingsAccountResponse createAccount(CreateSavingsAccountRequest req) {
        BigDecimal rate = DEFAULT_RATES.getOrDefault(req.getAccountType(), new BigDecimal("0.0500"));
        CompoundingFrequency freq = req.getCompoundingFreq() != null
                ? req.getCompoundingFreq() : CompoundingFrequency.MONTHLY;

        SavingsAccount account = SavingsAccount.builder()
                .id(UUID.randomUUID().toString())
                .userId(req.getUserId())
                .accountNumber(generateAccountNumber())
                .accountName(req.getAccountName() != null ? req.getAccountName()
                        : req.getAccountType().name().replace("_", " ") + " Account")
                .accountType(req.getAccountType())
                .balance(req.getInitialDeposit() != null ? req.getInitialDeposit() : BigDecimal.ZERO)
                .minimumBalance(BigDecimal.valueOf(10))
                .interestRate(rate)
                .compoundingFreq(freq)
                .currency(req.getCurrency() != null ? req.getCurrency() : "ETB")
                .status(AccountStatus.ACTIVE)
                .build();

        account = accountRepository.save(account);

        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            recordTransaction(account, TransactionType.DEPOSIT, account.getBalance(),
                    BigDecimal.ZERO, BigDecimal.ZERO, "MOBILE_APP", "Initial deposit", null, null, "USER");
        }

        kafkaTemplate.send("savings.account.created", account.getId(),
                Map.of("accountId", account.getId(), "userId", account.getUserId(),
                        "accountNumber", account.getAccountNumber(), "type", account.getAccountType()));

        log.info("Created savings account {} for user {}", account.getAccountNumber(), account.getUserId());
        return SavingsAccountResponse.from(account);
    }

    @Transactional
    public SavingsTransactionResponse deposit(DepositRequest req, String initiatedBy) {
        SavingsAccount account = getActiveAccount(req.getAccountId());
        BigDecimal balanceBefore = account.getBalance();
        account.setBalance(balanceBefore.add(req.getAmount()));
        account.setLastTransactionAt(Instant.now());
        accountRepository.save(account);

        SavingsTransaction tx = recordTransaction(account, TransactionType.DEPOSIT, req.getAmount(),
                balanceBefore, BigDecimal.ZERO, req.getChannel(), req.getDescription(),
                null, null, initiatedBy);

        kafkaTemplate.send("savings.deposit", account.getId(),
                Map.of("accountId", account.getId(), "userId", account.getUserId(),
                        "amount", req.getAmount(), "balance", account.getBalance()));

        return SavingsTransactionResponse.from(tx);
    }

    @Transactional
    public SavingsTransactionResponse withdraw(WithdrawRequest req, String initiatedBy) {
        SavingsAccount account = getActiveAccount(req.getAccountId());

        BigDecimal available = account.getBalance().subtract(account.getMinimumBalance());
        if (req.getAmount().compareTo(available) > 0) {
            throw new IllegalStateException(
                    "Insufficient balance. Available for withdrawal: " + available + " ETB");
        }

        BigDecimal balanceBefore = account.getBalance();
        account.setBalance(balanceBefore.subtract(req.getAmount()));
        account.setLastTransactionAt(Instant.now());
        accountRepository.save(account);

        SavingsTransaction tx = recordTransaction(account, TransactionType.WITHDRAWAL, req.getAmount(),
                balanceBefore, BigDecimal.ZERO, req.getChannel(), req.getDescription(),
                null, null, initiatedBy);

        return SavingsTransactionResponse.from(tx);
    }

    public List<SavingsAccountResponse> getAccountsByUser(String userId) {
        return accountRepository.findByUserId(userId)
                .stream()
                .map(SavingsAccountResponse::from)
                .toList();
    }

    public SavingsAccountResponse getAccount(String accountId) {
        return SavingsAccountResponse.from(
                accountRepository.findById(accountId)
                        .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId)));
    }

    public Page<SavingsTransactionResponse> getTransactions(String accountId, Pageable pageable) {
        return transactionRepository.findByAccountId(accountId, pageable)
                .map(SavingsTransactionResponse::from);
    }

    // =========================================================================
    // Fixed Deposit operations
    // =========================================================================

    @Transactional
    public FixedDepositResponse createFixedDeposit(CreateFixedDepositRequest req) {
        BigDecimal rate = fdRateByTenure(req.getTenureMonths());
        CompoundingFrequency freq = req.getCompoundingFreq() != null
                ? req.getCompoundingFreq() : CompoundingFrequency.MONTHLY;

        BigDecimal maturityAmount = interestCalculationService.calculateMaturityAmount(
                req.getPrincipalAmount(), rate, req.getTenureMonths(), freq);
        LocalDate maturityDate = LocalDate.now().plusMonths(req.getTenureMonths());

        // Create a backing savings account for the FD
        SavingsAccount fdAccount = SavingsAccount.builder()
                .id(UUID.randomUUID().toString())
                .userId(req.getUserId())
                .accountNumber(generateAccountNumber())
                .accountName("Fixed Deposit " + req.getTenureMonths() + "M")
                .accountType(AccountType.FIXED_DEPOSIT)
                .balance(req.getPrincipalAmount())
                .minimumBalance(BigDecimal.ZERO)
                .interestRate(rate)
                .compoundingFreq(freq)
                .currency("ETB")
                .status(AccountStatus.ACTIVE)
                .build();
        fdAccount = accountRepository.save(fdAccount);

        FixedDeposit fd = FixedDeposit.builder()
                .id(UUID.randomUUID().toString())
                .userId(req.getUserId())
                .accountId(fdAccount.getId())
                .depositNumber(generateDepositNumber())
                .principalAmount(req.getPrincipalAmount())
                .interestRate(rate)
                .tenureMonths(req.getTenureMonths())
                .compoundingFreq(freq)
                .maturityAmount(maturityAmount)
                .maturityDate(maturityDate)
                .status(FixedDepositStatus.ACTIVE)
                .autoRenew(req.isAutoRenew())
                .penaltyRate(new BigDecimal("0.0200"))
                .interestEarned(BigDecimal.ZERO)
                .sourceAccountId(req.getSourceAccountId())
                .build();

        fd = fixedDepositRepository.save(fd);

        log.info("Fixed deposit {} created for user {} - principal={} ETB, tenure={}M, rate={}%, maturity={}",
                fd.getDepositNumber(), fd.getUserId(), fd.getPrincipalAmount(),
                fd.getTenureMonths(), rate.multiply(BigDecimal.valueOf(100)), maturityDate);

        kafkaTemplate.send("savings.fd.created", fd.getId(),
                Map.of("depositId", fd.getId(), "userId", fd.getUserId(),
                        "amount", fd.getPrincipalAmount(), "maturityDate", maturityDate.toString()));

        return FixedDepositResponse.from(fd);
    }

    @Transactional
    public FixedDepositResponse closeFixedDepositPremature(String fdId) {
        FixedDeposit fd = fixedDepositRepository.findById(fdId)
                .orElseThrow(() -> new IllegalArgumentException("Fixed deposit not found: " + fdId));

        if (fd.getStatus() != FixedDepositStatus.ACTIVE) {
            throw new IllegalStateException("Fixed deposit is not active");
        }

        BigDecimal payoutAmount = interestCalculationService.calculatePrematureClosureAmount(fd);
        fd.setStatus(FixedDepositStatus.PREMATURELY_CLOSED);
        fd.setMaturedAt(Instant.now());
        fixedDepositRepository.save(fd);

        // Credit savings account with net payout
        SavingsAccount fdAccount = getActiveAccount(fd.getAccountId());
        fdAccount.setBalance(payoutAmount);
        fdAccount.setStatus(AccountStatus.CLOSED);
        fdAccount.setClosedAt(Instant.now());
        fdAccount.setCloseReason("Premature closure requested");
        accountRepository.save(fdAccount);

        log.info("Fixed deposit {} closed prematurely. Payout: {} ETB", fd.getDepositNumber(), payoutAmount);
        return FixedDepositResponse.from(fd);
    }

    /** Scheduled job: mature FDs that have reached their maturity date */
    @Scheduled(cron = "0 30 0 * * *") // 00:30 daily
    @Transactional
    public void processMaturingFixedDeposits() {
        List<FixedDeposit> matured = fixedDepositRepository.findMaturedDeposits(LocalDate.now());
        log.info("Processing {} matured fixed deposits", matured.size());

        for (FixedDeposit fd : matured) {
            try {
                fd.setStatus(FixedDepositStatus.MATURED);
                fd.setInterestEarned(fd.getMaturityAmount().subtract(fd.getPrincipalAmount()));
                fd.setMaturedAt(Instant.now());
                fixedDepositRepository.save(fd);

                SavingsAccount fdAccount = accountRepository.findById(fd.getAccountId()).orElse(null);
                if (fdAccount != null) {
                    BigDecimal balBefore = fdAccount.getBalance();
                    fdAccount.setBalance(fd.getMaturityAmount());
                    accountRepository.save(fdAccount);

                    recordTransaction(fdAccount, TransactionType.FD_MATURITY_CREDIT,
                            fd.getMaturityAmount().subtract(fd.getPrincipalAmount()),
                            balBefore, fd.getMaturityAmount(), "SYSTEM",
                            "Fixed deposit maturity credit - " + fd.getDepositNumber(),
                            fd.getId(), "FIXED_DEPOSIT", "SYSTEM");
                }

                if (fd.isAutoRenew()) {
                    CreateFixedDepositRequest renewReq = new CreateFixedDepositRequest();
                    renewReq.setUserId(fd.getUserId());
                    renewReq.setPrincipalAmount(fd.getMaturityAmount());
                    renewReq.setTenureMonths(fd.getTenureMonths());
                    renewReq.setCompoundingFreq(fd.getCompoundingFreq());
                    renewReq.setAutoRenew(true);
                    createFixedDeposit(renewReq);
                    log.info("Auto-renewed FD {} for user {}", fd.getDepositNumber(), fd.getUserId());
                }

                kafkaTemplate.send("savings.fd.matured", fd.getId(),
                        Map.of("depositId", fd.getId(), "userId", fd.getUserId(),
                                "maturityAmount", fd.getMaturityAmount()));
            } catch (Exception e) {
                log.error("Error processing FD maturity for {}: {}", fd.getDepositNumber(), e.getMessage());
            }
        }
    }

    public List<FixedDepositResponse> getFixedDepositsByUser(String userId) {
        return fixedDepositRepository.findByUserId(userId)
                .stream().map(FixedDepositResponse::from).toList();
    }

    // =========================================================================
    // Savings Goal operations
    // =========================================================================

    @Transactional
    public SavingsGoalResponse createGoal(CreateSavingsGoalRequest req) {
        // Create a dedicated savings account for this goal
        CreateSavingsAccountRequest accountReq = new CreateSavingsAccountRequest();
        accountReq.setUserId(req.getUserId());
        accountReq.setAccountType(AccountType.SAVINGS_GOAL);
        accountReq.setAccountName(req.getGoalName() + " (Goal)");
        SavingsAccountResponse accountResp = createAccount(accountReq);

        SavingsGoal goal = SavingsGoal.builder()
                .id(UUID.randomUUID().toString())
                .userId(req.getUserId())
                .accountId(accountResp.getId())
                .goalName(req.getGoalName())
                .goalDescription(req.getGoalDescription())
                .targetAmount(req.getTargetAmount())
                .currentAmount(BigDecimal.ZERO)
                .targetDate(req.getTargetDate())
                .category(req.getCategory())
                .status(GoalStatus.ACTIVE)
                .autoSaveAmount(req.getAutoSaveAmount())
                .autoSaveFrequency(req.getAutoSaveFrequency())
                .build();

        goal = goalRepository.save(goal);
        log.info("Savings goal '{}' created for user {}", goal.getGoalName(), goal.getUserId());
        return SavingsGoalResponse.from(goal);
    }

    @Transactional
    public SavingsGoalResponse contributeToGoal(String goalId, BigDecimal amount, String initiatedBy) {
        SavingsGoal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found: " + goalId));

        if (goal.getStatus() != GoalStatus.ACTIVE) {
            throw new IllegalStateException("Goal is not active");
        }

        SavingsAccount account = getActiveAccount(goal.getAccountId());
        BigDecimal balanceBefore = account.getBalance();
        account.setBalance(balanceBefore.add(amount));
        accountRepository.save(account);

        goal.setCurrentAmount(goal.getCurrentAmount().add(amount));
        goal.setLastAutoSaveAt(Instant.now());

        // Check if goal is achieved
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.ACHIEVED);
            goal.setAchievedAt(Instant.now());
            log.info("Savings goal '{}' ACHIEVED by user {}! Target: {} ETB",
                    goal.getGoalName(), goal.getUserId(), goal.getTargetAmount());
            kafkaTemplate.send("savings.goal.achieved", goalId,
                    Map.of("goalId", goalId, "userId", goal.getUserId(), "goalName", goal.getGoalName()));
        }

        goalRepository.save(goal);
        recordTransaction(account, TransactionType.GOAL_CONTRIBUTION, amount,
                balanceBefore, account.getBalance(), "MOBILE_APP",
                "Contribution to goal: " + goal.getGoalName(), goalId, "GOAL", initiatedBy);

        return SavingsGoalResponse.from(goal);
    }

    /** Auto-save scheduler: daily at 08:00 */
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void processAutoSave() {
        Instant cutoff = Instant.now().minusSeconds(86400); // goals not saved in last 24h
        List<SavingsGoal> goals = goalRepository.findGoalsDueForAutoSave(cutoff);
        log.info("Processing auto-save for {} goals", goals.size());
        for (SavingsGoal goal : goals) {
            try {
                contributeToGoal(goal.getId(), goal.getAutoSaveAmount(), "SYSTEM");
            } catch (Exception e) {
                log.error("Auto-save failed for goal {}: {}", goal.getId(), e.getMessage());
            }
        }
    }

    public List<SavingsGoalResponse> getGoalsByUser(String userId) {
        return goalRepository.findByUserId(userId)
                .stream().map(SavingsGoalResponse::from).toList();
    }

    public SavingsGoalResponse getGoal(String goalId) {
        return SavingsGoalResponse.from(
                goalRepository.findById(goalId)
                        .orElseThrow(() -> new IllegalArgumentException("Goal not found: " + goalId)));
    }

    // =========================================================================
    // Internal helpers
    // =========================================================================

    private SavingsAccount getActiveAccount(String accountId) {
        SavingsAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active. Status: " + account.getStatus());
        }
        return account;
    }

    private SavingsTransaction recordTransaction(SavingsAccount account, TransactionType type,
                                                  BigDecimal amount, BigDecimal balanceBefore,
                                                  BigDecimal balanceAfter, String channel,
                                                  String description, String relatedId,
                                                  String relatedType, String initiatedBy) {
        SavingsTransaction tx = SavingsTransaction.builder()
                .id(UUID.randomUUID().toString())
                .accountId(account.getId())
                .userId(account.getUserId())
                .transactionType(type)
                .reference("SAV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter.compareTo(BigDecimal.ZERO) == 0 ? account.getBalance() : balanceAfter)
                .fee(BigDecimal.ZERO)
                .channel(channel)
                .description(description)
                .relatedEntityId(relatedId)
                .relatedEntityType(relatedType)
                .initiatedBy(initiatedBy)
                .build();
        return transactionRepository.save(tx);
    }

    private String generateAccountNumber() {
        return "SAV" + System.currentTimeMillis() % 1_000_000_000L;
    }

    private String generateDepositNumber() {
        return "FD" + System.currentTimeMillis() % 1_000_000_000L;
    }

    private BigDecimal fdRateByTenure(int months) {
        if (months < 3) return FD_RATE_1_3;
        if (months < 6) return FD_RATE_3_6;
        if (months < 12) return FD_RATE_6_12;
        return FD_RATE_12P;
    }
}
