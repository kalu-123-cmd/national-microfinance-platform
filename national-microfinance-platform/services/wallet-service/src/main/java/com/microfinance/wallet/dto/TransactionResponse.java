package com.microfinance.wallet.dto;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TransactionResponse {
    private String id;
    private String reference;
    private String walletId;
    private String userId;
    private String type;
    private String direction;
    private String status;
    private BigDecimal amount;
    private BigDecimal fee;
    private String currency;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String description;
    private String channel;
    private Instant createdAt;
}