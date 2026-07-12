package com.microfinance.ai.repository;

import com.microfinance.ai.entity.UserRecommendation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRecommendationRepository extends MongoRepository<UserRecommendation, String> {
    List<UserRecommendation> findByUserId(String userId);
    List<UserRecommendation> findByUserIdAndStatus(String userId, String status);
    void deleteByUserIdAndStatus(String userId, String status);
}