package com.microfinance.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashTransactionResponse {
    private String id;
    private String agentId;
    private String userId;
    private String walletId;
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal commission;
    private String status;
    private String reference;
    private String channel;
    private String failureReason;
    private Instant processedAt;
}