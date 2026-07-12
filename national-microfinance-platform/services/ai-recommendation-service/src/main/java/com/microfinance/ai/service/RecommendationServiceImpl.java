package com.microfinance.ai.service;

import com.microfinance.ai.dto.*;
import com.microfinance.ai.entity.UserRecommendation;
import com.microfinance.ai.repository.UserRecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private final UserRecommendationRepository recommendationRepository;
    private final RuleEngine ruleEngine;

    @Override
    public List<RecommendationResponse> generateRecommendations(RecommendationRequest request) {
        List<RecommendationResponse> recommendations = recommendProducts(request).stream()
                .map(this::toEntity)
                .map(recommendationRepository::save)
                .map(this::toResponse)
                .collect(Collectors.toList());
        return recommendations;
    }

    @Override
    public List<RecommendationResponse> getUserRecommendations(String userId) {
        return recommendationRepository.findByUserIdAndStatus(userId, "ACTIVE").stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void recordAction(String recommendationId, RecommendationActionRequest request) {
        UserRecommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new IllegalArgumentException("Recommendation not found"));
        recommendation.setStatus(request.getAction());
        recommendationRepository.save(recommendation);
        log.info("Recorded action {} for recommendation {}", request.getAction(), recommendationId);
    }

    @Override
    public RecommendationMetricsResponse getPerformanceMetrics() {
        List<UserRecommendation> all = recommendationRepository.findAll();
        long total = all.size();
        long accepted = all.stream().filter(r -> "ACCEPTED".equals(r.getStatus())).count();
        long clicked = all.stream().filter(r -> "CLICKED".equals(r.getStatus())).count();
        
        return RecommendationMetricsResponse.builder()
                .totalRecommendations(total)
                .acceptedCount(accepted)
                .acceptanceRate(total > 0 ? java.math.BigDecimal.valueOf(accepted).divide(java.math.BigDecimal.valueOf(total)) : java.math.BigDecimal.ZERO)
                .clickedCount(clicked)
                .clickThroughRate(total > 0 ? java.math.BigDecimal.valueOf(clicked).divide(java.math.BigDecimal.valueOf(total)) : java.math.BigDecimal.ZERO)
                .build();
    }

    private List<RecommendationResponse> recommendProducts(RecommendationRequest request) {
        return ruleEngine.evaluate(request);
    }

    private UserRecommendation toEntity(RecommendationResponse response) {
        return UserRecommendation.builder()
                .id(UUID.randomUUID().toString())
                .productType(response.getProductType())
                .productName(response.getProductName())
                .description(response.getDescription())
                .confidenceScore(response.getConfidenceScore())
                .actionUrl(response.getActionUrl())
                .status("ACTIVE")
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(86400))
                .build();
    }

    private RecommendationResponse toResponse(UserRecommendation entity) {
        return RecommendationResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .productType(entity.getProductType())
                .productName(entity.getProductName())
                .description(entity.getDescription())
                .confidenceScore(entity.getConfidenceScore())
                .actionUrl(entity.getActionUrl())
                .expiresAt(entity.getExpiresAt())
                .build();
    }
}