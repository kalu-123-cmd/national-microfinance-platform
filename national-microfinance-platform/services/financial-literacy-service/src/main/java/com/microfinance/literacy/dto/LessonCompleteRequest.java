package com.microfinance.literacy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LessonCompleteRequest {
    @NotBlank
    private String courseId;
    @NotBlank
    private String lessonId;
    private Integer quizAnswerIndex; // null if not a quiz lesson
}
