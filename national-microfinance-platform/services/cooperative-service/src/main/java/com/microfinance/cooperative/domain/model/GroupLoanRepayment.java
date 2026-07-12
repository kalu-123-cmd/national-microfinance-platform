package com.microfinance.cooperative.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name = "group_loan_repayments")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class GroupLoanRepayment {
    @Id private String id;
    @Column(nullable = false) private String loanId;
    @Column(nullable = false) private String cooperativeId;
    @Column(nullable = false) private int installmentNumber;
    @Column(nullable = false) private LocalDate dueDate;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal principalAmount;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal interestAmount;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal totalDue;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal amountPaid;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private RepaymentStatus status;
    private Instant paidAt;
    private String paymentReference;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal penaltyAmount;
    @CreatedDate @Column(nullable = false, updatable = false) private Instant createdAt;
}
