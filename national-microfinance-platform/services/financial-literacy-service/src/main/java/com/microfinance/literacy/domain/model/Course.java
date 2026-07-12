package com.microfinance.literacy.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "courses")
public class Course {
    @Id
    private String id;
    private String title;
    private String description;
    private String category; // SAVINGS, LOANS, BUDGETING, INVESTMENT, INSURANCE
    private String level;    // BEGINNER, INTERMEDIATE, ADVANCED
    private String language; // en, am (Amharic), or, so
    private List<Lesson> lessons;
    private int totalPoints;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
