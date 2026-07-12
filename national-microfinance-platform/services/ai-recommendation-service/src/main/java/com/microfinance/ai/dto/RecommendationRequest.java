package com.microfinance.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationRequest {
    private String userId;
    private String context; // ACCOUNT_OPENING, LOAN_APPLICATION, SAVINGS_GOAL, etc.
    private BigDecimal walletBalance;
    private Integer creditScore;
    private String region;
}