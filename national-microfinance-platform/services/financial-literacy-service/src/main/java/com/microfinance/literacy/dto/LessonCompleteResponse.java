package com.microfinance.literacy.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LessonCompleteResponse {
    private boolean success;
    private boolean quizCorrect;
    private int pointsEarned;
    private int totalPoints;
    private boolean courseCompleted;
    private String newBadgeEarned; // null if no new badge
    private String message;
}
