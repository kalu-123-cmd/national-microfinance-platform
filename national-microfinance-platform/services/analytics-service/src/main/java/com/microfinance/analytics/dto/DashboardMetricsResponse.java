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
public class DashboardMetricsResponse {
    private Long totalUsers;
    private Long activeUsers;
    private Long totalTransactions;
    private BigDecimal totalTransactionVolume;
    private Long totalLoans;
    private BigDecimal totalLoanOutstanding;
    private Long totalAgents;
    private BigDecimal totalSavings;
}