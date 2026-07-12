package com.microfinance.loan.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "loan_applications")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LoanApplication {

    @Id private String id;

    @Column(nullable = false, unique = true, length = 30)
    private String loanNumber;

    @Column(nullable = false, length = 50)
    private String userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal requestedAmount;

    @Column(precision = 19, scale = 2)
    private BigDecimal approvedAmount;

    @Column(precision = 19, scale = 2)
    private BigDecimal disbursedAmount;

    @Column(precision = 5, scale = 2)
    private BigDecimal interestRate; // Annual rate

    private Integer tenureMonths;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoanType loanType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LoanStatus status;

    private String purpose;
    private Integer creditScore;
    private String riskLevel;

    private String approvedBy;
    private Instant approvedAt;
    private String rejectionReason;

    private String walletId;         // Disbursement destination
    private Instant disbursedAt;

    @Column(precision = 19, scale = 2)
    private BigDecimal outstandingBalance;

    @Column(precision = 19, scale = 2)
    private BigDecimal totalInterest;

    @Column(precision = 19, scale = 2)
    private BigDecimal totalRepayable;

    private LocalDate firstRepaymentDate;
    private LocalDate maturityDate;

    private Integer missedPayments;
    private LocalDate lastPaymentDate;

    @Column(precision = 19, scale = 2)
    private BigDecimal lastPaymentAmount;

    private String collateralType;
    private String collateralDescription;
    private String guarantorUserId;

    @CreatedDate @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate @Column(nullable = false)
    private Instant updatedAt;

    public boolean isActive() {
        return status == LoanStatus.ACTIVE;
    }

    public boolean isOverdue() {
        return maturityDate != null && LocalDate.now().isAfter(maturityDate) &&
               outstandingBalance != null && outstandingBalance.compareTo(BigDecimal.ZERO) > 0;
    }
}