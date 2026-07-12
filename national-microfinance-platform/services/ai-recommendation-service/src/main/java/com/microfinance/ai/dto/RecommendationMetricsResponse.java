package com.microfinance.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationMetricsResponse {
    private Long totalRecommendations;
    private Long acceptedCount;
    private BigDecimal acceptanceRate;
    private Long clickedCount;
    private BigDecimal clickThroughRate;
}