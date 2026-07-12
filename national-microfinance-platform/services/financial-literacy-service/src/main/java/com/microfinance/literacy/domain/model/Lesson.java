package com.microfinance.literacy.domain.model;

import lombok.Data;

@Data
public class Lesson {
    private String lessonId;
    private String title;
    private String content;
    private String contentType; // TEXT, VIDEO, QUIZ
    private int orderIndex;
    private int pointsOnCompletion;
    private QuizQuestion quiz; // optional, only for QUIZ type
}
