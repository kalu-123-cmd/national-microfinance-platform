package com.microfinance.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationResponse {
    private String id;
    private String userId;
    private String productType; // LOAN, SAVINGS, FIXED_DEPOSIT, INSURANCE
    private String productName;
    private String description;
    private Double confidenceScore;
    private String actionUrl;
    private Instant expiresAt;
}