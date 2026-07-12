package com.microfinance.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceDistributionResponse {
    private BigDecimal avgBalance;
    private BigDecimal medianBalance;
    private Long zeroBalanceCount;
    private Long lowBalanceCount; // < 100 ETB
    private Long mediumBalanceCount; // 100 - 1000 ETB
    private Long highBalanceCount; // > 1000 ETB
}