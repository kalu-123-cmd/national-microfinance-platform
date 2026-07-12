package com.microfinance.savings.dto;

import com.microfinance.savings.domain.model.SavingsTransaction;
import com.microfinance.savings.domain.model.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class SavingsTransactionResponse {
    private String id;
    private String accountId;
    private String reference;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private BigDecimal fee;
    private String channel;
    private String description;
    private Instant createdAt;

    public static SavingsTransactionResponse from(SavingsTransaction t) {
        return SavingsTransactionResponse.builder()
                .id(t.getId())
                .accountId(t.getAccountId())
                .reference(t.getReference())
                .transactionType(t.getTransactionType())
                .amount(t.getAmount())
                .balanceBefore(t.getBalanceBefore())
                .balanceAfter(t.getBalanceAfter())
                .fee(t.getFee())
                .channel(t.getChannel())
                .description(t.getDescription())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
