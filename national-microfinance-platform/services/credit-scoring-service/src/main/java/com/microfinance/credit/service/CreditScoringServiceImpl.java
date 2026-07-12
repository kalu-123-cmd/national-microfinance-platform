package com.microfinance.credit.service;

import com.microfinance.credit.dto.CreditScoreRequest;
import com.microfinance.credit.dto.CreditScoreResponse;
import com.microfinance.credit.entity.CreditScore;
import com.microfinance.credit.repository.CreditScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditScoringServiceImpl implements CreditScoringService {

    private final CreditScoreRepository creditScoreRepository;

    @Override
    public CreditScoreResponse calculateScore(CreditScoreRequest request) {
        log.info("Calculating credit score for user: {}", request.getUserId());
        
        // This is a placeholder for actual ML or rule-based scoring logic
        int calculatedScore = generateRandomScore(); 
        String rating = determineRating(calculatedScore);
        double defaultProbability = calculateProbability(calculatedScore);

        Optional<CreditScore> existingScore = creditScoreRepository.findByUserId(request.getUserId());
        CreditScore score;
        
        if (existingScore.isPresent()) {
            score = existingScore.get();
            score.setScore(calculatedScore);
            score.setRating(rating);
            score.setDefaultProbability(defaultProbability);
        } else {
            score = CreditScore.builder()
                    .userId(request.getUserId())
                    .score(calculatedScore)
                    .rating(rating)
                    .defaultProbability(defaultProbability)
                    .build();
        }

        creditScoreRepository.save(score);

        return mapToResponse(score);
    }

    @Override
    public CreditScoreResponse getScore(String userId) {
        CreditScore score = creditScoreRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Credit score not found for user: " + userId));
        return mapToResponse(score);
    }

    private CreditScoreResponse mapToResponse(CreditScore score) {
        return CreditScoreResponse.builder()
                .userId(score.getUserId())
                .score(score.getScore())
                .rating(score.getRating())
                .defaultProbability(score.getDefaultProbability())
                .timestamp(score.getUpdatedAt() != null ? score.getUpdatedAt().toString() : score.getCreatedAt().toString())
                .build();
    }

    private int generateRandomScore() {
        return 300 + (int)(Math.random() * ((850 - 300) + 1));
    }

    private String determineRating(int score) {
        if (score >= 750) return "EXCELLENT";
        if (score >= 650) return "GOOD";
        if (score >= 550) return "FAIR";
        return "POOR";
    }

    private double calculateProbability(int score) {
        if (score >= 750) return 1.5;
        if (score >= 650) return 5.0;
        if (score >= 550) return 15.0;
        return 40.0;
    }
}
