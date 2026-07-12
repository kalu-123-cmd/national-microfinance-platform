package com.microfinance.analytics.service;

import com.microfinance.analytics.dto.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AnalyticsService {
    DashboardMetricsResponse getDashboardMetrics(String region, LocalDate fromDate, LocalDate toDate);
    Map<String, Object> getTransactionVolume(LocalDate fromDate, LocalDate toDate, String groupBy);
    List<UserGrowthResponse> getUserGrowth(LocalDate fromDate, LocalDate toDate);
    LoanRepaymentAnalyticsResponse getLoanRepaymentAnalytics(String region, LocalDate fromDate, LocalDate toDate);
    BalanceDistributionResponse getBalanceDistribution();
}