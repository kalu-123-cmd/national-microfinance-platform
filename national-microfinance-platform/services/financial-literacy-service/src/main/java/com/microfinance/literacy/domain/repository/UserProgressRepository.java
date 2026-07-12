package com.microfinance.literacy.domain.repository;

import com.microfinance.literacy.domain.model.UserProgress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends MongoRepository<UserProgress, String> {
    List<UserProgress> findByUserId(String userId);
    Optional<UserProgress> findByUserIdAndCourseId(String userId, String courseId);
    long countByUserIdAndCourseCompletedTrue(String userId);
}
