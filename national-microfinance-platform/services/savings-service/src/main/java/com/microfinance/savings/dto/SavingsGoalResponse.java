package com.microfinance.savings.dto;

import com.microfinance.savings.domain.model.SavingsGoal;
import com.microfinance.savings.domain.model.GoalStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class SavingsGoalResponse {
    private String id;
    private String userId;
    private String accountId;
    private String goalName;
    private String goalDescription;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private BigDecimal progressPercent;
    private BigDecimal remainingAmount;
    private LocalDate targetDate;
    private String category;
    private GoalStatus status;
    private BigDecimal autoSaveAmount;
    private String autoSaveFrequency;
    private Instant achievedAt;
    private Instant createdAt;

    public static SavingsGoalResponse from(SavingsGoal g) {
        BigDecimal progress = g.getTargetAmount().compareTo(BigDecimal.ZERO) > 0
                ? g.getCurrentAmount().multiply(BigDecimal.valueOf(100)).divide(g.getTargetAmount(), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal remaining = g.getTargetAmount().subtract(g.getCurrentAmount()).max(BigDecimal.ZERO);

        return SavingsGoalResponse.builder()
                .id(g.getId())
                .userId(g.getUserId())
                .accountId(g.getAccountId())
                .goalName(g.getGoalName())
                .goalDescription(g.getGoalDescription())
                .targetAmount(g.getTargetAmount())
                .currentAmount(g.getCurrentAmount())
                .progressPercent(progress)
                .remainingAmount(remaining)
                .targetDate(g.getTargetDate())
                .category(g.getCategory())
                .status(g.getStatus())
                .autoSaveAmount(g.getAutoSaveAmount())
                .autoSaveFrequency(g.getAutoSaveFrequency())
                .achievedAt(g.getAchievedAt())
                .createdAt(g.getCreatedAt())
                .build();
    }
}
