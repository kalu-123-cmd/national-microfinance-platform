package com.microfinance.fraud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudCheckRequest {
    private String transactionId;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String transactionType;
    private String location;
    private String deviceId;
}
