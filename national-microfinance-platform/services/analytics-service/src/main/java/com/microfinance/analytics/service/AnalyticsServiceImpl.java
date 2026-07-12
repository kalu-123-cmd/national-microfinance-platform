package com.microfinance.analytics.service;

import com.microfinance.analytics.dto.*;
import com.microfinance.analytics.domain.model.AnalyticsEvent;
import com.microfinance.analytics.domain.repository.AnalyticsEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    private final AnalyticsEventRepository eventRepository;

    @Override
    public DashboardMetricsResponse getDashboardMetrics(String region, LocalDate fromDate, LocalDate toDate) {
        Instant from = fromDate != null ? fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant() : Instant.now().minusSeconds(86400 * 30);
        Instant to = toDate != null ? toDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant() : Instant.now();

        List<AnalyticsEvent> events = eventRepository.findByEventTimeBetween(from, to);
        if (region != null && !region.isEmpty()) {
            events = events.stream().filter(e -> region.equals(e.getRegion())).collect(Collectors.toList());
        }

        return DashboardMetricsResponse.builder()
                .totalUsers(countByType(events, "USER_CREATED"))
                .activeUsers(countByType(events, "USER_ACTIVE"))
                .totalTransactions(countByType(events, "TRANSACTION_COMPLETED"))
                .totalTransactionVolume(sumAmountByType(events, "TRANSACTION_COMPLETED"))
                .totalLoans(countByType(events, "LOAN_CREATED"))
                .totalLoanOutstanding(sumAmountByType(events, "LOAN_DISBURSED"))
                .totalAgents(countByType(events, "AGENT_REGISTERED"))
                .totalSavings(sumAmountByType(events, "SAVINGS_DEPOSIT"))
                .build();
    }

    @Override
    public Map<String, Object> getTransactionVolume(LocalDate fromDate, LocalDate toDate, String groupBy) {
        Instant from = fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant to = toDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        List<AnalyticsEvent> events = eventRepository.findByEventTimeBetween(from, to)
                .stream().filter(e -> "TRANSACTION_COMPLETED".equals(e.getEventType())).collect(Collectors.toList());

        // Stub implementation
        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", events.size());
        result.put("totalAmount", sumAmount(events));
        result.put("groupBy", groupBy != null ? groupBy : "daily");
        return result;
    }

    @Override
    public List<UserGrowthResponse> getUserGrowth(LocalDate fromDate, LocalDate toDate) {
        // Stub implementation
        List<UserGrowthResponse> result = new ArrayList<>();
        LocalDate current = fromDate;
        while (!current.isAfter(toDate)) {
            result.add(UserGrowthResponse.builder()
                    .date(current)
                    .newUsers((long) (Math.random() * 100))
                    .activeUsers((long) (Math.random() * 1000))
                    .verifiedUsers((long) (Math.random() * 500))
                    .build());
            current = current.plusDays(1);
        }
        return result;
    }

    @Override
    public LoanRepaymentAnalyticsResponse getLoanRepaymentAnalytics(String region, LocalDate fromDate, LocalDate toDate) {
        // Stub implementation
        return LoanRepaymentAnalyticsResponse.builder()
                .totalLoans(1000L)
                .onTrackRepayments(950L)
                .overdueLoans(50L)
                .recoveryRate(new BigDecimal("95.00"))
                .averageDaysToMaturity(new BigDecimal("180"))
                .build();
    }

    @Override
    public BalanceDistributionResponse getBalanceDistribution() {
        // Stub implementation
        return BalanceDistributionResponse.builder()
                .avgBalance(new BigDecimal("1250.00"))
                .medianBalance(new BigDecimal("500.00"))
                .zeroBalanceCount(150L)
                .lowBalanceCount(450L)
                .mediumBalanceCount(300L)
                .highBalanceCount(100L)
                .build();
    }

    private Long countByType(List<AnalyticsEvent> events, String type) {
        return events.stream().filter(e -> type.equals(e.getEventType())).count();
    }

    private BigDecimal sumAmountByType(List<AnalyticsEvent> events, String type) {
        return events.stream()
                .filter(e -> type.equals(e.getEventType()) && e.getAmount() != null)
                .map(AnalyticsEvent::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumAmount(List<AnalyticsEvent> events) {
        return events.stream()
                .filter(e -> e.getAmount() != null)
                .map(AnalyticsEvent::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}