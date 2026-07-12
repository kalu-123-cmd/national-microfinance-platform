package com.microfinance.loan.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "repayment_schedules")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class RepaymentSchedule {

    @Id private String id;

    @Column(nullable = false)
    private String loanId;

    private Integer installmentNumber;
    private LocalDate dueDate;

    @Column(precision = 19, scale = 2) private BigDecimal totalAmount;
    @Column(precision = 19, scale = 2) private BigDecimal principalAmount;
    @Column(precision = 19, scale = 2) private BigDecimal interestAmount;
    @Column(precision = 19, scale = 2) private BigDecimal penaltyAmount;
    @Column(precision = 19, scale = 2) private BigDecimal paidAmount;
    @Column(precision = 19, scale = 2) private BigDecimal outstandingAmount;

    @Enumerated(EnumType.STRING)
    private RepaymentStatus status;

    private Instant paidAt;
    private String paymentReference;
    private String paymentChannel;

    @CreatedDate @Column(nullable = false, updatable = false)
    private Instant createdAt;
}