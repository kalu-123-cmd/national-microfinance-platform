package com.microfinance.cooperative.service;

import com.microfinance.cooperative.domain.model.*;
import com.microfinance.cooperative.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CooperativeService {

    private final CooperativeRepository cooperativeRepo;
    private final CooperativeMemberRepository memberRepo;
    private final ContributionRepository contributionRepo;
    private final RoscaCycleRepository roscaRepo;
    private final GroupLoanRepository loanRepo;
    private final GroupLoanRepaymentRepository repaymentRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // =========================================================================
    // Cooperative CRUD
    // =========================================================================

    @Transactional
    public Cooperative createCooperative(String name, String regNumber, String adminUserId,
                                          CooperativeType type, BigDecimal monthlyContrib,
                                          int maxMembers, String location, String phone) {
        if (cooperativeRepo.existsByRegistrationNumber(regNumber)) {
            throw new IllegalArgumentException("Registration number already exists: " + regNumber);
        }

        Cooperative coop = Cooperative.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .registrationNumber(regNumber)
                .type(type)
                .adminUserId(adminUserId)
                .maxMembers(maxMembers)
                .membershipFee(BigDecimal.valueOf(50))
                .monthlyContribution(monthlyContrib)
                .totalPoolBalance(BigDecimal.ZERO)
                .loanInterestRate(new BigDecimal("0.1200"))
                .status(CooperativeStatus.ACTIVE)
                .location(location)
                .phone(phone)
                .build();

        coop = cooperativeRepo.save(coop);

        // Admin is auto-enrolled as first member
        addMember(coop.getId(), adminUserId, MemberRole.ADMIN);

        kafkaTemplate.send("cooperative.created", coop.getId(),
                Map.of("cooperativeId", coop.getId(), "name", coop.getName(),
                        "adminUserId", adminUserId, "type", type.name()));

        log.info("Cooperative '{}' created with ID {}", name, coop.getId());
        return coop;
    }

    @Transactional
    public CooperativeMember addMember(String cooperativeId, String userId, MemberRole role) {
        Cooperative coop = getActiveCooperative(cooperativeId);

        if (memberRepo.existsByCooperativeIdAndUserId(cooperativeId, userId)) {
            throw new IllegalStateException("User " + userId + " is already a member of this cooperative");
        }

        long currentCount = memberRepo.countByCooperativeIdAndStatus(cooperativeId, MemberStatus.ACTIVE);
        if (currentCount >= coop.getMaxMembers()) {
            throw new IllegalStateException("Cooperative has reached maximum member capacity: " + coop.getMaxMembers());
        }

        CooperativeMember member = CooperativeMember.builder()
                .id(UUID.randomUUID().toString())
                .cooperativeId(cooperativeId)
                .userId(userId)
                .memberNumber("MBR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                .role(role != null ? role : MemberRole.MEMBER)
                .status(MemberStatus.ACTIVE)
                .totalContributed(BigDecimal.ZERO)
                .totalWithdrawn(BigDecimal.ZERO)
                .joinDate(LocalDate.now())
                .build();

        member = memberRepo.save(member);
        kafkaTemplate.send("cooperative.member.joined", cooperativeId,
                Map.of("cooperativeId", cooperativeId, "userId", userId, "memberId", member.getId()));
        log.info("User {} joined cooperative {} as {}", userId, cooperativeId, member.getRole());
        return member;
    }

    // =========================================================================
    // Contributions
    // =========================================================================

    @Transactional
    public Contribution recordContribution(String cooperativeId, String userId, BigDecimal amount, String month) {
        CooperativeMember member = memberRepo.findByCooperativeIdAndUserId(cooperativeId, userId)
                .orElseThrow(() -> new IllegalArgumentException("User is not a member of this cooperative"));

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new IllegalStateException("Member is not active");
        }

        // Idempotency check
        if (contributionRepo.findByCooperativeIdAndMemberIdAndContributionMonth(cooperativeId, member.getId(), month).isPresent()) {
            throw new IllegalStateException("Contribution for " + month + " already recorded");
        }

        Contribution contribution = Contribution.builder()
                .id(UUID.randomUUID().toString())
                .cooperativeId(cooperativeId)
                .memberId(member.getId())
                .userId(userId)
                .contributionMonth(month)
                .amount(amount)
                .status(ContributionStatus.PAID)
                .paidAt(Instant.now())
                .paymentReference("CONT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .build();

        contribution = contributionRepo.save(contribution);

        // Update member total and cooperative pool
        member.setTotalContributed(member.getTotalContributed().add(amount));
        memberRepo.save(member);

        Cooperative coop = cooperativeRepo.findById(cooperativeId).orElseThrow();
        coop.setTotalPoolBalance(coop.getTotalPoolBalance().add(amount));
        cooperativeRepo.save(coop);

        kafkaTemplate.send("cooperative.contribution.paid", cooperativeId,
                Map.of("cooperativeId", cooperativeId, "userId", userId, "amount", amount, "month", month));

        return contribution;
    }

    public List<Contribution> getMemberContributions(String cooperativeId, String userId) {
        CooperativeMember member = memberRepo.findByCooperativeIdAndUserId(cooperativeId, userId)
                .orElseThrow(() -> new IllegalArgumentException("User not a member"));
        return contributionRepo.findByMemberIdOrderByContributionMonthDesc(member.getId());
    }

    // =========================================================================
    // ROSCA Cycle Management
    // =========================================================================

    /**
     * Initialise all ROSCA cycles for a cooperative.
     * Each active member gets one turn to receive the pot.
     * Order is randomised / by join date.
     */
    @Transactional
    public List<RoscaCycle> initRoscaCycles(String cooperativeId) {
        Cooperative coop = getActiveCooperative(cooperativeId);
        if (coop.getMonthlyContribution() == null) {
            throw new IllegalStateException("Cooperative must have a monthly contribution to run ROSCA");
        }

        List<CooperativeMember> members = memberRepo.findByCooperativeIdAndStatus(cooperativeId, MemberStatus.ACTIVE);
        if (members.size() < 2) {
            throw new IllegalStateException("Need at least 2 active members to initialise ROSCA");
        }

        int existingCycles = roscaRepo.countByCooperativeId(cooperativeId);
        if (existingCycles > 0) {
            throw new IllegalStateException("ROSCA cycles already initialised for this cooperative");
        }

        BigDecimal potAmount = coop.getMonthlyContribution().multiply(BigDecimal.valueOf(members.size()));
        List<RoscaCycle> cycles = new ArrayList<>();
        LocalDate startDate = LocalDate.now().withDayOfMonth(1).plusMonths(1); // start next month

        for (int i = 0; i < members.size(); i++) {
            CooperativeMember beneficiary = members.get(i);
            RoscaCycle cycle = RoscaCycle.builder()
                    .id(UUID.randomUUID().toString())
                    .cooperativeId(cooperativeId)
                    .cycleNumber(i + 1)
                    .beneficiaryUserId(beneficiary.getUserId())
                    .beneficiaryMemberId(beneficiary.getId())
                    .potAmount(potAmount)
                    .status(RoscaStatus.SCHEDULED)
                    .scheduledDate(startDate.plusMonths(i))
                    .build();
            cycles.add(roscaRepo.save(cycle));
        }

        log.info("Initialised {} ROSCA cycles for cooperative {} (pot per round: {} ETB)",
                cycles.size(), cooperativeId, potAmount);
        return cycles;
    }

    /** Scheduled: disburse ROSCA pot to today's beneficiary */
    @Scheduled(cron = "0 0 9 1 * *") // 1st of each month at 09:00
    @Transactional
    public void disburseDueRoscaCycles() {
        List<RoscaCycle> due = roscaRepo.findByStatusAndScheduledDateLessThanEqual(RoscaStatus.SCHEDULED, LocalDate.now());
        for (RoscaCycle cycle : due) {
            try {
                cycle.setStatus(RoscaStatus.DISBURSED);
                cycle.setDisbursedAt(Instant.now());
                cycle.setDisbursedAmount(cycle.getPotAmount());
                roscaRepo.save(cycle);

                // Deduct from cooperative pool
                Cooperative coop = cooperativeRepo.findById(cycle.getCooperativeId()).orElseThrow();
                coop.setTotalPoolBalance(coop.getTotalPoolBalance().subtract(cycle.getPotAmount()).max(BigDecimal.ZERO));
                cooperativeRepo.save(coop);

                kafkaTemplate.send("cooperative.rosca.disbursed", cycle.getId(),
                        Map.of("cycleId", cycle.getId(), "cooperativeId", cycle.getCooperativeId(),
                                "beneficiaryUserId", cycle.getBeneficiaryUserId(),
                                "amount", cycle.getPotAmount()));

                log.info("ROSCA cycle {} disbursed {} ETB to user {}",
                        cycle.getCycleNumber(), cycle.getPotAmount(), cycle.getBeneficiaryUserId());
            } catch (Exception e) {
                log.error("Failed to disburse ROSCA cycle {}: {}", cycle.getId(), e.getMessage());
            }
        }
    }

    public List<RoscaCycle> getRoscaCycles(String cooperativeId) {
        return roscaRepo.findByCooperativeIdOrderByCycleNumberAsc(cooperativeId);
    }

    // =========================================================================
    // Group Loans
    // =========================================================================

    @Transactional
    public GroupLoan applyForGroupLoan(String cooperativeId, String userId,
                                        BigDecimal amount, int tenureMonths, String purpose) {
        Cooperative coop = getActiveCooperative(cooperativeId);
        CooperativeMember member = memberRepo.findByCooperativeIdAndUserId(cooperativeId, userId)
                .orElseThrow(() -> new IllegalArgumentException("User is not a member of this cooperative"));

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new IllegalStateException("Only active members can apply for loans");
        }

        // Simple eligibility: loan must not exceed 2x member's total contributions
        BigDecimal maxLoanable = member.getTotalContributed().multiply(BigDecimal.valueOf(2));
        if (amount.compareTo(maxLoanable) > 0) {
            throw new IllegalStateException(
                    "Loan amount exceeds eligibility. Max: " + maxLoanable + " ETB (2x your contributions)");
        }

        GroupLoan loan = GroupLoan.builder()
                .id(UUID.randomUUID().toString())
                .cooperativeId(cooperativeId)
                .applicantUserId(userId)
                .applicantMemberId(member.getId())
                .loanNumber("GL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .amountRequested(amount)
                .interestRate(coop.getLoanInterestRate())
                .tenureMonths(tenureMonths)
                .outstandingBalance(BigDecimal.ZERO)
                .status(GroupLoanStatus.APPLIED)
                .purpose(purpose)
                .appliedAt(Instant.now())
                .build();

        loan = loanRepo.save(loan);
        kafkaTemplate.send("cooperative.loan.applied", loan.getId(),
                Map.of("loanId", loan.getId(), "cooperativeId", cooperativeId,
                        "userId", userId, "amount", amount));
        log.info("Group loan {} applied by user {} in cooperative {}", loan.getLoanNumber(), userId, cooperativeId);
        return loan;
    }

    @Transactional
    public GroupLoan approveGroupLoan(String loanId, BigDecimal approvedAmount, String approvedBy) {
        GroupLoan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found: " + loanId));

        if (loan.getStatus() != GroupLoanStatus.APPLIED) {
            throw new IllegalStateException("Loan is not in APPLIED status");
        }

        // Calculate repayment schedule
        BigDecimal monthlyRate = loan.getInterestRate().divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        BigDecimal factor = BigDecimal.ONE.add(monthlyRate).pow(loan.getTenureMonths());
        BigDecimal numerator = approvedAmount.multiply(monthlyRate).multiply(factor);
        BigDecimal denominator = factor.subtract(BigDecimal.ONE);
        BigDecimal monthlyRepayment = numerator.divide(denominator, 2, RoundingMode.HALF_UP);
        BigDecimal totalRepayable = monthlyRepayment.multiply(BigDecimal.valueOf(loan.getTenureMonths()));

        loan.setAmountApproved(approvedAmount);
        loan.setMonthlyRepayment(monthlyRepayment);
        loan.setTotalRepayable(totalRepayable);
        loan.setOutstandingBalance(approvedAmount);
        loan.setStatus(GroupLoanStatus.APPROVED);
        loan.setApprovedAt(Instant.now());
        loan.setApprovedBy(approvedBy);
        loan = loanRepo.save(loan);

        // Generate repayment schedule
        generateRepaymentSchedule(loan, monthlyRepayment);

        kafkaTemplate.send("cooperative.loan.approved", loan.getId(),
                Map.of("loanId", loan.getId(), "approvedAmount", approvedAmount,
                        "monthlyRepayment", monthlyRepayment));
        log.info("Group loan {} approved: {} ETB, monthly payment {} ETB",
                loan.getLoanNumber(), approvedAmount, monthlyRepayment);
        return loan;
    }

    @Transactional
    public GroupLoan disburseGroupLoan(String loanId) {
        GroupLoan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found: " + loanId));

        if (loan.getStatus() != GroupLoanStatus.APPROVED) {
            throw new IllegalStateException("Loan must be APPROVED before disbursement");
        }

        // Verify cooperative has sufficient pool
        Cooperative coop = cooperativeRepo.findById(loan.getCooperativeId()).orElseThrow();
        if (coop.getTotalPoolBalance().compareTo(loan.getAmountApproved()) < 0) {
            throw new IllegalStateException("Insufficient cooperative pool balance for disbursement");
        }

        coop.setTotalPoolBalance(coop.getTotalPoolBalance().subtract(loan.getAmountApproved()));
        cooperativeRepo.save(coop);

        loan.setStatus(GroupLoanStatus.DISBURSED);
        loan.setDisbursedAt(Instant.now());
        loan = loanRepo.save(loan);

        kafkaTemplate.send("cooperative.loan.disbursed", loan.getId(),
                Map.of("loanId", loan.getId(), "userId", loan.getApplicantUserId(),
                        "amount", loan.getAmountApproved(), "cooperativeId", loan.getCooperativeId()));
        return loan;
    }

    @Transactional
    public GroupLoanRepayment makeRepayment(String loanId, BigDecimal amount, String paymentRef) {
        GroupLoan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found: " + loanId));

        if (loan.getStatus() != GroupLoanStatus.DISBURSED && loan.getStatus() != GroupLoanStatus.ACTIVE) {
            throw new IllegalStateException("Loan is not active for repayments");
        }

        // Find the next pending installment
        List<GroupLoanRepayment> pending = repaymentRepo.findByLoanIdAndStatus(loanId, RepaymentStatus.PENDING);
        if (pending.isEmpty()) {
            throw new IllegalStateException("No pending repayments found");
        }

        GroupLoanRepayment installment = pending.get(0);
        installment.setAmountPaid(amount);
        installment.setStatus(amount.compareTo(installment.getTotalDue()) >= 0
                ? RepaymentStatus.PAID : RepaymentStatus.PARTIAL);
        installment.setPaidAt(Instant.now());
        installment.setPaymentReference(paymentRef);
        repaymentRepo.save(installment);

        // Update outstanding balance
        loan.setOutstandingBalance(loan.getOutstandingBalance().subtract(installment.getPrincipalAmount()).max(BigDecimal.ZERO));
        loan.setStatus(GroupLoanStatus.ACTIVE);

        // Return pool funds
        Cooperative coop = cooperativeRepo.findById(loan.getCooperativeId()).orElseThrow();
        coop.setTotalPoolBalance(coop.getTotalPoolBalance().add(amount));
        cooperativeRepo.save(coop);

        // Check if fully repaid
        long remainingInstallments = repaymentRepo.findByLoanIdAndStatus(loanId, RepaymentStatus.PENDING).size();
        if (remainingInstallments == 0 || loan.getOutstandingBalance().compareTo(BigDecimal.ZERO) <= 0) {
            loan.setStatus(GroupLoanStatus.CLOSED);
            loan.setClosedAt(Instant.now());
            kafkaTemplate.send("cooperative.loan.closed", loanId,
                    Map.of("loanId", loanId, "userId", loan.getApplicantUserId()));
        }

        loanRepo.save(loan);
        return installment;
    }

    /** Scheduled: mark overdue installments */
    @Scheduled(cron = "0 0 6 * * *") // 06:00 daily
    @Transactional
    public void markOverdueInstallments() {
        List<GroupLoanRepayment> overdue = repaymentRepo.findByStatusAndDueDateLessThan(
                RepaymentStatus.PENDING, LocalDate.now());
        for (GroupLoanRepayment r : overdue) {
            r.setStatus(RepaymentStatus.OVERDUE);
            r.setPenaltyAmount(r.getTotalDue().multiply(new BigDecimal("0.02"))); // 2% penalty
            repaymentRepo.save(r);
        }
        if (!overdue.isEmpty()) {
            log.warn("Marked {} loan installments as OVERDUE", overdue.size());
        }
    }

    public List<GroupLoan> getLoansByCooperative(String cooperativeId) {
        return loanRepo.findByCooperativeId(cooperativeId);
    }

    public List<GroupLoanRepayment> getRepaymentSchedule(String loanId) {
        return repaymentRepo.findByLoanIdOrderByInstallmentNumberAsc(loanId);
    }

    public List<Cooperative> getAllCooperatives() { return cooperativeRepo.findAll(); }
    public Cooperative getCooperativeById(String id) { return getActiveCooperative(id); }
    public List<CooperativeMember> getMembers(String cooperativeId) {
        return memberRepo.findByCooperativeId(cooperativeId);
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private Cooperative getActiveCooperative(String id) {
        Cooperative coop = cooperativeRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cooperative not found: " + id));
        if (coop.getStatus() != CooperativeStatus.ACTIVE) {
            throw new IllegalStateException("Cooperative is not active");
        }
        return coop;
    }

    private void generateRepaymentSchedule(GroupLoan loan, BigDecimal monthlyRepayment) {
        BigDecimal outstanding = loan.getAmountApproved();
        BigDecimal monthlyRate = loan.getInterestRate().divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        LocalDate dueDate = LocalDate.now().plusMonths(1).withDayOfMonth(1);

        for (int i = 1; i <= loan.getTenureMonths(); i++) {
            BigDecimal interest = outstanding.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principal = monthlyRepayment.subtract(interest).setScale(2, RoundingMode.HALF_UP);
            if (i == loan.getTenureMonths()) {
                principal = outstanding; // final installment clears balance
                monthlyRepayment = principal.add(interest);
            }

            GroupLoanRepayment repayment = GroupLoanRepayment.builder()
                    .id(UUID.randomUUID().toString())
                    .loanId(loan.getId())
                    .cooperativeId(loan.getCooperativeId())
                    .installmentNumber(i)
                    .dueDate(dueDate)
                    .principalAmount(principal)
                    .interestAmount(interest)
                    .totalDue(principal.add(interest))
                    .amountPaid(BigDecimal.ZERO)
                    .status(RepaymentStatus.PENDING)
                    .penaltyAmount(BigDecimal.ZERO)
                    .build();
            repaymentRepo.save(repayment);

            outstanding = outstanding.subtract(principal);
            dueDate = dueDate.plusMonths(1);
        }
    }
}
