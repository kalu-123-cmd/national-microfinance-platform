package com.microfinance.fraud.rules;

import com.microfinance.fraud.dto.FraudCheckRequest;
import com.microfinance.fraud.dto.FraudCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FraudRuleEvaluator {

    private final VelocityRule velocityRule;

    public FraudCheckResponse evaluate(FraudCheckRequest request) {
        List<String> triggeredRules = new ArrayList<>();
        int riskScore = 0;

        // Evaluate velocity rule
        if (velocityRule.isViolated(request)) {
            triggeredRules.add("VELOCITY_RULE");
            riskScore += 50;
        }

        // Example: High amount rule
        if (request.getAmount() != null && request.getAmount().doubleValue() > 10000) {
            triggeredRules.add("HIGH_AMOUNT_RULE");
            riskScore += 30;
        }

        // Determine outcome
        boolean isFraudulent = riskScore >= 50;
        String riskLevel = "LOW";
        String recommendation = "PROCEED";

        if (riskScore >= 80) {
            riskLevel = "CRITICAL";
            recommendation = "BLOCK";
        } else if (riskScore >= 50) {
            riskLevel = "HIGH";
            recommendation = "REVIEW";
        } else if (riskScore >= 20) {
            riskLevel = "MEDIUM";
        }

        return FraudCheckResponse.builder()
                .transactionId(request.getTransactionId())
                .isFraudulent(isFraudulent)
                .riskLevel(riskLevel)
                .triggeredRules(triggeredRules)
                .recommendation(recommendation)
                .build();
    }
}
