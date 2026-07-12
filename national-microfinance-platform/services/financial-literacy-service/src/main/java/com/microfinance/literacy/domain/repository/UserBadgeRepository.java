package com.microfinance.literacy.domain.repository;

import com.microfinance.literacy.domain.model.UserBadge;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBadgeRepository extends MongoRepository<UserBadge, String> {
    List<UserBadge> findByUserId(String userId);
    boolean existsByUserIdAndBadgeName(String userId, String badgeName);
    long countByUserId(String userId);
}
