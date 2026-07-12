package com.microfinance.literacy.controller;

import com.microfinance.literacy.domain.model.Course;
import com.microfinance.literacy.domain.model.UserBadge;
import com.microfinance.literacy.domain.model.UserProgress;
import com.microfinance.literacy.dto.LessonCompleteRequest;
import com.microfinance.literacy.dto.LessonCompleteResponse;
import com.microfinance.literacy.dto.UserStatsResponse;
import com.microfinance.literacy.service.LiteracyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/literacy")
@RequiredArgsConstructor
public class LiteracyController {

    private final LiteracyService literacyService;

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getCourses(@RequestParam(required = false) String language,
                                                    @RequestParam(required = false) String category) {
        if (category != null) return ResponseEntity.ok(literacyService.getCoursesByCategory(category));
        return ResponseEntity.ok(literacyService.getAllCourses(language));
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<Course> getCourse(@PathVariable String courseId) {
        return ResponseEntity.ok(literacyService.getCourseById(courseId));
    }

    @PostMapping("/lessons/complete")
    public ResponseEntity<LessonCompleteResponse> completeLesson(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody LessonCompleteRequest request) {
        return ResponseEntity.ok(literacyService.completeLesson(userId, request));
    }

    @GetMapping("/progress")
    public ResponseEntity<List<UserProgress>> getUserProgress(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(literacyService.getUserAllProgress(userId));
    }

    @GetMapping("/progress/{courseId}")
    public ResponseEntity<UserProgress> getCourseProgress(@RequestHeader("X-User-Id") String userId,
                                                           @PathVariable String courseId) {
        return ResponseEntity.ok(literacyService.getUserCourseProgress(userId, courseId));
    }

    @GetMapping("/stats")
    public ResponseEntity<UserStatsResponse> getUserStats(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(literacyService.getUserStats(userId));
    }

    @GetMapping("/badges")
    public ResponseEntity<List<UserBadge>> getBadges(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(literacyService.getUserBadges(userId));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<UserProgress>> getLeaderboard(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(literacyService.getLeaderboard(limit));
    }
}
