package com.microfinance.loan.service;

import com.microfinance.common.exception.*;
import com.microfinance.loan.domain.model.*;
import com.microfinance.loan.domain.repository.*;
import com.microfinance.loan.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {

    private final LoanApplicationRepository loanRepo;
    private final RepaymentScheduleRepository scheduleRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public LoanResponse applyForLoan(LoanApplicationRequest request) {
        log.info("Loan application for userId: {} amount: {}", request.getUserId(), request.getRequestedAmount());

        String loanNumber = "LOAN-" + System.currentTimeMillis();
        LoanApplication loan = LoanApplication.builder()
            .id(UUID.randomUUID().toString())
            .loanNumber(loanNumber)
            .userId(request.getUserId())
            .requestedAmount(request.getRequestedAmount())
            .tenureMonths(request.getTenureMonths())
            .loanType(request.getLoanType())
            .purpose(request.getPurpose())
            .walletId(request.getWalletId())
            .status(LoanStatus.SUBMITTED)
            .collateralType(request.getCollateralType())
            .collateralDescription(request.getCollateralDescription())
            .guarantorUserId(request.getGuarantorUserId())
            .missedPayments(0)
            .build();

        LoanApplication saved = loanRepo.save(loan);
        log.info("Loan application created: {}", loanNumber);
        return toResponse(saved);
    }

    @Transactional
    public LoanResponse approveLoan(String loanId, ApproveLoanRequest request) {
        LoanApplication loan = findLoan(loanId);
        if (loan.getStatus() != LoanStatus.SUBMITTED && loan.getStatus() != LoanStatus.UNDER_REVIEW) {
            throw new BusinessException("Loan cannot be approved in current status: " + loan.getStatus());
        }

        BigDecimal rate = request.getInterestRate() != null ? request.getInterestRate() : BigDecimal.valueOf(18.0);
        BigDecimal approved = request.getApprovedAmount() != null ? request.getApprovedAmount() : loan.getRequestedAmount();

        // Calculate total repayable using reducing balance method
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        BigDecimal factor = monthlyRate.add(BigDecimal.ONE).pow(loan.getTenureMonths());
        BigDecimal emi = approved.multiply(monthlyRate).multiply(factor)
            .divide(factor.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
        BigDecimal totalRepayable = emi.multiply(BigDecimal.valueOf(loan.getTenureMonths()));
        BigDecimal totalInterest = totalRepayable.subtract(approved);

        loan.setStatus(LoanStatus.APPROVED);
        loan.setApprovedAmount(approved);
        loan.setInterestRate(rate);
        loan.setApprovedBy(request.getApprovedBy());
        loan.setApprovedAt(Instant.now());
        loan.setTotalRepayable(totalRepayable);
        loan.setTotalInterest(totalInterest);
        loan.setFirstRepaymentDate(LocalDate.now().plusMonths(1));
        loan.setMaturityDate(LocalDate.now().plusMonths(loan.getTenureMonths()));

        LoanApplication savedLoan = loanRepo.save(loan);

        // Generate repayment schedule
        generateRepaymentSchedule(savedLoan, emi);
        log.info("Loan {} approved for {} ETB @ {}%", loan.getLoanNumber(), approved, rate);
        return toResponse(savedLoan);
    }

    @Transactional
    public LoanResponse rejectLoan(String loanId, String reason) {
        LoanApplication loan = findLoan(loanId);
        loan.setStatus(LoanStatus.REJECTED);
        loan.setRejectionReason(reason);
        return toResponse(loanRepo.save(loan));
    }

    @Transactional
    public LoanResponse disburseLoan(String loanId) {
        LoanApplication loan = findLoan(loanId);
        if (loan.getStatus() != LoanStatus.APPROVED) {
            throw new BusinessException("Only approved loans can be disbursed");
        }

        loan.setStatus(LoanStatus.ACTIVE);
        loan.setDisbursedAmount(loan.getApprovedAmount());
        loan.setDisbursedAt(Instant.now());
        loan.setOutstandingBalance(loan.getTotalRepayable());

        LoanApplication saved = loanRepo.save(loan);

        // Publish disbursement event — wallet-service will credit the wallet
        kafkaTemplate.send("loan-events", new LoanDisbursedMsg(
            loan.getUserId(), loan.getWalletId(), loan.getApprovedAmount().toString(), loan.getLoanNumber()));

        log.info("Loan disbursed: {} to wallet: {}", loan.getLoanNumber(), loan.getWalletId());
        return toResponse(saved);
    }

    @Transactional
    public RepaymentResponse makeRepayment(RepaymentRequest request) {
        LoanApplication loan = findLoan(request.getLoanId());
        if (!loan.isActive()) throw new BusinessException("Loan is not active");

        RepaymentSchedule nextInstallment = scheduleRepo
            .findFirstByLoanIdAndStatusOrderByDueDate(loan.getId(), RepaymentStatus.PENDING)
            .orElseThrow(() -> new BusinessException("No pending installments found"));

        BigDecimal paid = request.getAmount();
        BigDecimal due = nextInstallment.getOutstandingAmount() != null ?
            nextInstallment.getOutstandingAmount() : nextInstallment.getTotalAmount();

        RepaymentStatus newStatus = paid.compareTo(due) >= 0 ? RepaymentStatus.PAID : RepaymentStatus.PARTIAL;

        nextInstallment.setPaidAmount(paid);
        nextInstallment.setOutstandingAmount(due.subtract(paid).max(BigDecimal.ZERO));
        nextInstallment.setStatus(newStatus);
        nextInstallment.setPaidAt(Instant.now());
        nextInstallment.setPaymentReference(request.getPaymentReference());
        nextInstallment.setPaymentChannel(request.getChannel());
        scheduleRepo.save(nextInstallment);

        // Update loan outstanding
        loan.setOutstandingBalance(loan.getOutstandingBalance().subtract(paid).max(BigDecimal.ZERO));
        loan.setLastPaymentDate(LocalDate.now());
        loan.setLastPaymentAmount(paid);

        // Close loan if fully paid
        if (loan.getOutstandingBalance().compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus(LoanStatus.CLOSED);
        }
        loanRepo.save(loan);

        log.info("Repayment of {} made for loan {}", paid, loan.getLoanNumber());
        return RepaymentResponse.builder()
            .loanId(loan.getId())
            .loanNumber(loan.getLoanNumber())
            .installmentId(nextInstallment.getId())
            .paidAmount(paid)
            .outstandingBalance(loan.getOutstandingBalance())
            .status(newStatus.toString())
            .paidAt(nextInstallment.getPaidAt())
            .build();
    }

    @Transactional(readOnly = true)
    public LoanResponse getLoan(String loanId) {
        return toResponse(findLoan(loanId));
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> getUserLoans(String userId) {
        return loanRepo.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<LoanResponse> getLoansByStatus(LoanStatus status, Pageable pageable) {
        return loanRepo.findByStatus(status, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponse> getRepaymentSchedule(String loanId) {
        return scheduleRepo.findByLoanIdOrderByInstallmentNumber(loanId).stream()
            .map(s -> ScheduleResponse.builder()
                .id(s.getId())
                .installmentNumber(s.getInstallmentNumber())
                .dueDate(s.getDueDate())
                .totalAmount(s.getTotalAmount())
                .principalAmount(s.getPrincipalAmount())
                .interestAmount(s.getInterestAmount())
                .paidAmount(s.getPaidAmount())
                .outstandingAmount(s.getOutstandingAmount())
                .status(s.getStatus().toString())
                .paidAt(s.getPaidAt())
                .build())
            .toList();
    }

    private void generateRepaymentSchedule(LoanApplication loan, BigDecimal emi) {
        List<RepaymentSchedule> schedule = new ArrayList<>();
        BigDecimal balance = loan.getApprovedAmount();
        BigDecimal monthlyRate = loan.getInterestRate()
            .divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);

        for (int i = 1; i <= loan.getTenureMonths(); i++) {
            BigDecimal interest = balance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principal = emi.subtract(interest).setScale(2, RoundingMode.HALF_UP);
            balance = balance.subtract(principal).max(BigDecimal.ZERO);

            schedule.add(RepaymentSchedule.builder()
                .id(UUID.randomUUID().toString())
                .loanId(loan.getId())
                .installmentNumber(i)
                .dueDate(loan.getFirstRepaymentDate().plusMonths(i - 1))
                .totalAmount(emi)
                .principalAmount(principal)
                .interestAmount(interest)
                .penaltyAmount(BigDecimal.ZERO)
                .paidAmount(BigDecimal.ZERO)
                .outstandingAmount(emi)
                .status(RepaymentStatus.PENDING)
                .build());
        }
        scheduleRepo.saveAll(schedule);
        log.info("Generated {} installments for loan {}", schedule.size(), loan.getLoanNumber());
    }

    private LoanApplication findLoan(String id) {
        return loanRepo.findById(id)
            .orElseThrow(() -> new NotFoundException("Loan not found: " + id));
    }

    private LoanResponse toResponse(LoanApplication l) {
        return LoanResponse.builder()
            .id(l.getId()).loanNumber(l.getLoanNumber()).userId(l.getUserId())
            .requestedAmount(l.getRequestedAmount()).approvedAmount(l.getApprovedAmount())
            .disbursedAmount(l.getDisbursedAmount()).interestRate(l.getInterestRate())
            .tenureMonths(l.getTenureMonths()).loanType(l.getLoanType() != null ? l.getLoanType().toString() : null)
            .status(l.getStatus().toString()).purpose(l.getPurpose())
            .outstandingBalance(l.getOutstandingBalance()).totalRepayable(l.getTotalRepayable())
            .totalInterest(l.getTotalInterest()).firstRepaymentDate(l.getFirstRepaymentDate())
            .maturityDate(l.getMaturityDate()).disbursedAt(l.getDisbursedAt())
            .approvedAt(l.getApprovedAt()).createdAt(l.getCreatedAt()).build();
    }

    record LoanDisbursedMsg(String userId, String walletId, String amount, String loanNumber) {}
}