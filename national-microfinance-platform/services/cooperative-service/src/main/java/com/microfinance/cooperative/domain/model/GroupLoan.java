package com.microfinance.cooperative.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name = "group_loans")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class GroupLoan {
    @Id private String id;
    @Column(nullable = false) private String cooperativeId;
    @Column(nullable = false) private String applicantUserId;
    @Column(nullable = false) private String applicantMemberId;
    @Column(nullable = false, unique = true) private String loanNumber;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal amountRequested;
    @Column(precision = 19, scale = 2) private BigDecimal amountApproved;
    @Column(nullable = false, precision = 6, scale = 4) private BigDecimal interestRate;
    @Column(nullable = false) private int tenureMonths;
    @Column(precision = 19, scale = 2) private BigDecimal monthlyRepayment;
    @Column(precision = 19, scale = 2) private BigDecimal totalRepayable;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal outstandingBalance;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private GroupLoanStatus status;
    private String purpose;
    @Column(columnDefinition = "TEXT") private String guarantorUserIds; // JSON array
    @Column(nullable = false) private Instant appliedAt;
    private Instant approvedAt;
    private String approvedBy;
    private Instant disbursedAt;
    private Instant closedAt;
    @CreatedDate @Column(nullable = false, updatable = false) private Instant createdAt;
    @LastModifiedDate @Column(nullable = false) private Instant updatedAt;
}
