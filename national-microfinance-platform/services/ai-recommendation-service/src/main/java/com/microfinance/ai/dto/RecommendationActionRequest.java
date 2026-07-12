package com.microfinance.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationActionRequest {
    private String action; // VIEWED, CLICKED, ACCEPTED, REJECTED
    private String userId;
}