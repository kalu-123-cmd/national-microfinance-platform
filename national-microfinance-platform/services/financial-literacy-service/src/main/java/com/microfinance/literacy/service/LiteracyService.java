package com.microfinance.literacy.service;

import com.microfinance.literacy.domain.model.Course;
import com.microfinance.literacy.domain.model.UserBadge;
import com.microfinance.literacy.domain.model.UserProgress;
import com.microfinance.literacy.dto.LessonCompleteRequest;
import com.microfinance.literacy.dto.LessonCompleteResponse;
import com.microfinance.literacy.dto.UserStatsResponse;

import java.util.List;

public interface LiteracyService {
    List<Course> getAllCourses(String language);
    List<Course> getCoursesByCategory(String category);
    Course getCourseById(String courseId);
    UserProgress getUserCourseProgress(String userId, String courseId);
    List<UserProgress> getUserAllProgress(String userId);
    LessonCompleteResponse completeLesson(String userId, LessonCompleteRequest request);
    UserStatsResponse getUserStats(String userId);
    List<UserBadge> getUserBadges(String userId);
    List<UserProgress> getLeaderboard(int limit);
}
