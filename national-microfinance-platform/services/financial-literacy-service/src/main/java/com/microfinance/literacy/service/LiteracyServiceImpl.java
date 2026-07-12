package com.microfinance.literacy.service;

import com.microfinance.literacy.domain.model.*;
import com.microfinance.literacy.domain.repository.*;
import com.microfinance.literacy.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiteracyServiceImpl implements LiteracyService {

    private final CourseRepository courseRepository;
    private final UserProgressRepository progressRepository;
    private final UserBadgeRepository badgeRepository;

    @Override
    public List<Course> getAllCourses(String language) {
        if (language != null && !language.isBlank()) {
            return courseRepository.findByLanguageAndActiveTrue(language);
        }
        return courseRepository.findByActiveTrue();
    }

    @Override
    public List<Course> getCoursesByCategory(String category) {
        return courseRepository.findByCategoryAndActiveTrue(category);
    }

    @Override
    public Course getCourseById(String courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found: " + courseId));
    }

    @Override
    public UserProgress getUserCourseProgress(String userId, String courseId) {
        return progressRepository.findByUserIdAndCourseId(userId, courseId).orElse(null);
    }

    @Override
    public List<UserProgress> getUserAllProgress(String userId) {
        return progressRepository.findByUserId(userId);
    }

    @Override
    public LessonCompleteResponse completeLesson(String userId, LessonCompleteRequest request) {
        Course course = getCourseById(request.getCourseId());

        Lesson lesson = course.getLessons().stream()
                .filter(l -> l.getLessonId().equals(request.getLessonId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Lesson not found: " + request.getLessonId()));

        // Find or create progress
        UserProgress progress = progressRepository
                .findByUserIdAndCourseId(userId, request.getCourseId())
                .orElseGet(() -> {
                    UserProgress p = new UserProgress();
                    p.setUserId(userId);
                    p.setCourseId(course.getId());
                    p.setCourseTitle(course.getTitle());
                    p.setTotalLessons(course.getLessons().size());
                    p.setCompletedLessonIds(new ArrayList<>());
                    p.setStartedAt(LocalDateTime.now());
                    return p;
                });

        // Check if already completed
        if (progress.getCompletedLessonIds().contains(request.getLessonId())) {
            return LessonCompleteResponse.builder()
                    .success(false)
                    .message("Lesson already completed")
                    .totalPoints(progress.getTotalPointsEarned())
                    .build();
        }

        // Handle quiz validation
        boolean quizCorrect = true;
        if ("QUIZ".equals(lesson.getContentType()) && lesson.getQuiz() != null) {
            quizCorrect = request.getQuizAnswerIndex() != null &&
                    request.getQuizAnswerIndex() == lesson.getQuiz().getCorrectOptionIndex();
            if (!quizCorrect) {
                return LessonCompleteResponse.builder()
                        .success(false)
                        .quizCorrect(false)
                        .message("Incorrect answer. Try again!")
                        .totalPoints(progress.getTotalPointsEarned())
                        .build();
            }
        }

        // Award points and mark lesson complete
        int pointsEarned = lesson.getPointsOnCompletion();
        progress.getCompletedLessonIds().add(request.getLessonId());
        progress.setCompletedLessons(progress.getCompletedLessons() + 1);
        progress.setTotalPointsEarned(progress.getTotalPointsEarned() + pointsEarned);
        progress.setLastActivityAt(LocalDateTime.now());

        boolean courseCompleted = progress.getCompletedLessons() >= progress.getTotalLessons();
        if (courseCompleted && !progress.isCourseCompleted()) {
            progress.setCourseCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
            progress.setTotalPointsEarned(progress.getTotalPointsEarned() + course.getTotalPoints());
            pointsEarned += course.getTotalPoints();
        }

        progressRepository.save(progress);

        // Check for badge unlocks
        String newBadge = checkAndAwardBadge(userId, progress);

        return LessonCompleteResponse.builder()
                .success(true)
                .quizCorrect(quizCorrect)
                .pointsEarned(pointsEarned)
                .totalPoints(progress.getTotalPointsEarned())
                .courseCompleted(courseCompleted)
                .newBadgeEarned(newBadge)
                .message(courseCompleted ? "Congratulations! Course completed!" : "Lesson completed!")
                .build();
    }

    private String checkAndAwardBadge(String userId, UserProgress progress) {
        int total = getTotalPoints(userId);
        Map<String, Integer> milestones = Map.of(
                "First Step", 50,
                "Learner", 200,
                "Knowledge Seeker", 500,
                "Finance Pro", 1000,
                "Master", 2500
        );

        for (Map.Entry<String, Integer> milestone : milestones.entrySet()) {
            if (total >= milestone.getValue() && !badgeRepository.existsByUserIdAndBadgeName(userId, milestone.getKey())) {
                UserBadge badge = new UserBadge();
                badge.setUserId(userId);
                badge.setBadgeName(milestone.getKey());
                badge.setBadgeDescription("Earned by reaching " + milestone.getValue() + " points");
                badge.setPointsRequired(milestone.getValue());
                badge.setEarnedAt(LocalDateTime.now());
                badgeRepository.save(badge);
                return milestone.getKey();
            }
        }
        return null;
    }

    private int getTotalPoints(String userId) {
        return progressRepository.findByUserId(userId).stream()
                .mapToInt(UserProgress::getTotalPointsEarned).sum();
    }

    @Override
    public UserStatsResponse getUserStats(String userId) {
        int totalPoints = getTotalPoints(userId);
        long completedCourses = progressRepository.countByUserIdAndCourseCompletedTrue(userId);
        List<UserBadge> badges = badgeRepository.findByUserId(userId);

        return UserStatsResponse.builder()
                .userId(userId)
                .totalPoints(totalPoints)
                .completedCourses(completedCourses)
                .badgesEarned(badges.size())
                .badgeNames(badges.stream().map(UserBadge::getBadgeName).collect(Collectors.toList()))
                .build();
    }

    @Override
    public List<UserBadge> getUserBadges(String userId) {
        return badgeRepository.findByUserId(userId);
    }

    @Override
    public List<UserProgress> getLeaderboard(int limit) {
        // Aggregate top users by total points - simplified implementation
        return progressRepository.findAll().stream()
                .sorted(Comparator.comparingInt(UserProgress::getTotalPointsEarned).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
