package com.microfinance.literacy.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "user_progress")
@CompoundIndex(def = "{'userId': 1, 'courseId': 1}", unique = true)
public class UserProgress {
    @Id
    private String id;
    private String userId;
    private String courseId;
    private String courseTitle;
    private int totalPointsEarned;
    private int completedLessons;
    private int totalLessons;
    private boolean courseCompleted;
    private List<String> completedLessonIds;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastActivityAt;
}
