package com.microfinance.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashInRequest {
    private String userId;
    private String walletId;
    private BigDecimal amount;
    private String paymentMethod;
    private String reference;
    private String channel;
}