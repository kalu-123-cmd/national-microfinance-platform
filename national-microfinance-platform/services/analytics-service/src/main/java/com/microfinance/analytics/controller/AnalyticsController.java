package com.microfinance.analytics.controller;

import com.microfinance.analytics.dto.*;
import com.microfinance.analytics.service.AnalyticsService;
import com.microfinance.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardMetricsResponse>> getDashboardMetrics(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate) {
        DashboardMetricsResponse response = analyticsService.getDashboardMetrics(region, fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/transactions/volume")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTransactionVolume(
            @RequestParam LocalDate fromDate, @RequestParam LocalDate toDate,
            @RequestParam(required = false) String groupBy) {
        Map<String, Object> response = analyticsService.getTransactionVolume(fromDate, toDate, groupBy);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/users/growth")
    public ResponseEntity<ApiResponse<List<UserGrowthResponse>>> getUserGrowth(
            @RequestParam LocalDate fromDate, @RequestParam LocalDate toDate) {
        List<UserGrowthResponse> response = analyticsService.getUserGrowth(fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/loans/repayment")
    public ResponseEntity<ApiResponse<LoanRepaymentAnalyticsResponse>> getLoanRepaymentAnalytics(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate) {
        LoanRepaymentAnalyticsResponse response = analyticsService.getLoanRepaymentAnalytics(region, fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/wallets/balance-distribution")
    public ResponseEntity<ApiResponse<BalanceDistributionResponse>> getBalanceDistribution() {
        BalanceDistributionResponse response = analyticsService.getBalanceDistribution();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}