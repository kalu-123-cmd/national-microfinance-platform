package com.microfinance.ai.controller;

import com.microfinance.ai.dto.*;
import com.microfinance.ai.service.RecommendationService;
import com.microfinance.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<List<RecommendationResponse>>> generateRecommendations(
            @RequestBody RecommendationRequest request) {
        List<RecommendationResponse> response = recommendationService.generateRecommendations(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<RecommendationResponse>>> getUserRecommendations(
            @PathVariable String userId) {
        List<RecommendationResponse> response = recommendationService.getUserRecommendations(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{recommendationId}/action")
    public ResponseEntity<ApiResponse<Void>> recordAction(
            @PathVariable String recommendationId,
            @RequestBody RecommendationActionRequest request) {
        recommendationService.recordAction(recommendationId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/performance")
    public ResponseEntity<ApiResponse<RecommendationMetricsResponse>> getPerformanceMetrics() {
        RecommendationMetricsResponse response = recommendationService.getPerformanceMetrics();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}