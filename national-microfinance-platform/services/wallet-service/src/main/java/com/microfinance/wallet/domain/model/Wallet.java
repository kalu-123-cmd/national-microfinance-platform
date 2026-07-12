package com.microfinance.wallet.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "wallets")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Wallet {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false, unique = true, length = 50)
    private String userId;

    @Column(name = "wallet_number", nullable = false, unique = true, length = 20)
    private String walletNumber;

    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(name = "reserved_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal reservedBalance; // Funds pending/on hold

    @Column(name = "currency", nullable = false, length = 5)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private WalletStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "wallet_type", nullable = false, length = 20)
    private WalletType walletType;

    @Column(name = "daily_limit", precision = 19, scale = 2)
    private BigDecimal dailyLimit;

    @Column(name = "monthly_limit", precision = 19, scale = 2)
    private BigDecimal monthlyLimit;

    @Column(name = "single_tx_limit", precision = 19, scale = 2)
    private BigDecimal singleTxLimit;

    @Column(name = "daily_spent", precision = 19, scale = 2)
    private BigDecimal dailySpent;

    @Column(name = "monthly_spent", precision = 19, scale = 2)
    private BigDecimal monthlySpent;

    @Column(name = "daily_reset_at")
    private Instant dailyResetAt;

    @Column(name = "monthly_reset_at")
    private Instant monthlyResetAt;

    @Column(name = "pin_hash", length = 255)
    private String pinHash;

    @Column(name = "last_transaction_at")
    private Instant lastTransactionAt;

    @Column(name = "total_credited", precision = 19, scale = 2)
    private BigDecimal totalCredited;

    @Column(name = "total_debited", precision = 19, scale = 2)
    private BigDecimal totalDebited;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public BigDecimal getAvailableBalance() {
        return balance.subtract(reservedBalance);
    }

    public boolean hasEnoughBalance(BigDecimal amount) {
        return getAvailableBalance().compareTo(amount) >= 0;
    }

    public boolean isActive() {
        return status == WalletStatus.ACTIVE;
    }
}