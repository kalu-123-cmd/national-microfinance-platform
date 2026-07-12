package com.microfinance.ai.service;

import com.microfinance.ai.dto.RecommendationRequest;
import com.microfinance.ai.dto.RecommendationResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class RuleEngine {

    public List<RecommendationResponse> evaluate(RecommendationRequest request) {
        List<RecommendationResponse> recommendations = new ArrayList<>();
        
        BigDecimal balance = request.getWalletBalance() != null ? request.getWalletBalance() : BigDecimal.ZERO;
        int score = request.getCreditScore() != null ? request.getCreditScore() : 500;
        String context = request.getContext() != null ? request.getContext() : "GENERAL";

        // 1. High Balance -> Fixed Deposit or High Yield Savings
        if (balance.compareTo(new BigDecimal("10000")) > 0) {
            recommendations.add(RecommendationResponse.builder()
                    .productType("SAVINGS")
                    .productName("Fixed Deposit Account")
                    .description("Lock in your funds for a higher interest rate and maximize your returns.")
                    .confidenceScore(0.88)
                    .actionUrl("/api/v1/savings/fixed-deposit")
                    .build());
        } else if (balance.compareTo(new BigDecimal("1000")) > 0) {
            recommendations.add(RecommendationResponse.builder()
                    .productType("SAVINGS")
                    .productName("High Yield Savings")
                    .description("Grow your idle cash with our High Yield Savings product.")
                    .confidenceScore(0.82)
                    .actionUrl("/api/v1/savings/high-yield")
                    .build());
        } else {
            // Low balance -> Daily Savings
            recommendations.add(RecommendationResponse.builder()
                    .productType("SAVINGS")
                    .productName("Daily Savings Plan")
                    .description("Start small. Save a little every day to build a strong financial foundation.")
                    .confidenceScore(0.75)
                    .actionUrl("/api/v1/savings/daily")
                    .build());
        }

        // 2. High Credit Score -> Group Loan or Micro-Loan
        if (score >= 700) {
            recommendations.add(RecommendationResponse.builder()
                    .productType("LOAN")
                    .productName("Premium Micro-Loan")
                    .description("You pre-qualify for our low-interest Premium Micro-Loan!")
                    .confidenceScore(0.95)
                    .actionUrl("/api/v1/loans/apply")
                    .build());
        } else if (score >= 550) {
            recommendations.add(RecommendationResponse.builder()
                    .productType("COOPERATIVE")
                    .productName("Group Lending")
                    .description("Join a local cooperative to access group lending features.")
                    .confidenceScore(0.80)
                    .actionUrl("/api/v1/cooperatives/join")
                    .build());
        } else {
            // Low credit score -> Financial Literacy
            recommendations.add(RecommendationResponse.builder()
                    .productType("LITERACY")
                    .productName("Credit Builder Course")
                    .description("Learn how to improve your credit score with our free mini-course.")
                    .confidenceScore(0.90)
                    .actionUrl("/api/v1/literacy/credit-builder")
                    .build());
        }

        // 3. Context Specific
        if ("ACCOUNT_OPENING".equals(context)) {
            recommendations.add(RecommendationResponse.builder()
                    .productType("ONBOARDING")
                    .productName("Complete Profile")
                    .description("Finish your profile setup to unlock higher transaction limits.")
                    .confidenceScore(0.99)
                    .actionUrl("/api/v1/users/profile")
                    .build());
        }
        
        return recommendations;
    }
}
