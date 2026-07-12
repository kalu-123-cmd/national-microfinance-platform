package com.microfinance.savings.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "fixed_deposits")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FixedDeposit {

    @Id
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String accountId;

    @Column(nullable = false, unique = true)
    private String depositNumber;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal principalAmount;

    /** Annual interest rate at time of creation */
    @Column(nullable = false, precision = 6, scale = 4)
    private BigDecimal interestRate;

    @Column(nullable = false)
    private Integer tenureMonths;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompoundingFrequency compoundingFreq;

    @Column(precision = 19, scale = 2)
    private BigDecimal maturityAmount;

    @Column(nullable = false)
    private LocalDate maturityDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FixedDepositStatus status;

    private boolean autoRenew;

    /** Penalty rate for premature closure, e.g. 0.02 = 2% */
    @Column(nullable = false, precision = 6, scale = 4)
    private BigDecimal penaltyRate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal interestEarned;

    private String sourceAccountId;
    private Instant maturedAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;
}
