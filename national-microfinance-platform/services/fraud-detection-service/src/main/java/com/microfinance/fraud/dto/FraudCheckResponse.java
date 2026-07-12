package com.microfinance.fraud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudCheckResponse {
    private String transactionId;
    private boolean isFraudulent;
    private String riskLevel;
    private List<String> triggeredRules;
    private String recommendation; // PROCEED, BLOCK, REVIEW
}
