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
public class LoanRepaymentAnalyticsResponse {
    private Long totalLoans;
    private Long onTrackRepayments;
    private Long overdueLoans;
    private BigDecimal recoveryRate;
    private BigDecimal averageDaysToMaturity;
}