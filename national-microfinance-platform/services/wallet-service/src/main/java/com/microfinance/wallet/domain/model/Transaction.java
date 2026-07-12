package com.microfinance.wallet.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Transaction {

    @Id
    private String id;

    @Column(name = "reference", nullable = false, unique = true, length = 50)
    private String reference;

    @Column(name = "wallet_id", nullable = false, length = 50)
    private String walletId;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "counterparty_wallet_id", length = 50)
    private String counterpartyWalletId;

    @Column(name = "counterparty_user_id", length = 50)
    private String counterpartyUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false, length = 10)
    private TransactionDirection direction; // CREDIT or DEBIT

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TransactionStatus status;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "fee", precision = 19, scale = 2)
    private BigDecimal fee;

    @Column(name = "currency", nullable = false, length = 5)
    private String currency;

    @Column(name = "balance_before", precision = 19, scale = 2)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "narration", length = 500)
    private String narration;

    @Column(name = "external_reference", length = 100)
    private String externalReference;

    @Column(name = "channel", length = 30)
    private String channel; // MOBILE, USSD, AGENT, API

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "device_id", length = 100)
    private String deviceId;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "reversed_at")
    private Instant reversedAt;

    @Column(name = "reversed_by", length = 50)
    private String reversedBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}