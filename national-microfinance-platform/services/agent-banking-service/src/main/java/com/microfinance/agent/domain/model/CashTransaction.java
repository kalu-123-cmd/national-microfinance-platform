package com.microfinance.agent.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "cash_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashTransaction {
    @Id
    private String id;

    @Column(nullable = false)
    private String agentId;

    @Column(nullable = false)
    private String userId;

    @Column
    private String walletId;

    @Column(nullable = false)
    private String transactionType;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private BigDecimal fee = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal commission = BigDecimal.ZERO;

    @Column(nullable = false)
    private String status;

    @Column
    private String reference;

    @Column
    private String channel;

    @Column
    private String failureReason;

    @CreationTimestamp
    private Instant processedAt;
}