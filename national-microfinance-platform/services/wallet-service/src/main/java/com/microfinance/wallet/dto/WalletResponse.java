package com.microfinance.wallet.dto;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class WalletResponse {
    private String id;
    private String userId;
    private String walletNumber;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private BigDecimal reservedBalance;
    private String currency;
    private String status;
    private String walletType;
    private BigDecimal dailyLimit;
    private BigDecimal monthlyLimit;
    private BigDecimal singleTxLimit;
    private BigDecimal dailySpent;
    private BigDecimal monthlySpent;
    private BigDecimal totalCredited;
    private BigDecimal totalDebited;
    private Instant lastTransactionAt;
    private Instant createdAt;
}