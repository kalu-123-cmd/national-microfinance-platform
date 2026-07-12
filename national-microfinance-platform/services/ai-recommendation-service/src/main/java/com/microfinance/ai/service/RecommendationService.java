package com.microfinance.ai.service;

import com.microfinance.ai.dto.*;

import java.util.List;

public interface RecommendationService {
    List<RecommendationResponse> generateRecommendations(RecommendationRequest request);
    List<RecommendationResponse> getUserRecommendations(String userId);
    void recordAction(String recommendationId, RecommendationActionRequest request);
    RecommendationMetricsResponse getPerformanceMetrics();
}