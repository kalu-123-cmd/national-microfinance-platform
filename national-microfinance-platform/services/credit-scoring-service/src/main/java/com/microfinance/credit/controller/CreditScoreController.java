package com.microfinance.credit.controller;

import com.microfinance.common.dto.ApiResponse;
import com.microfinance.credit.dto.CreditScoreRequest;
import com.microfinance.credit.dto.CreditScoreResponse;
import com.microfinance.credit.service.CreditScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/credit")
@RequiredArgsConstructor
public class CreditScoreController {

    private final CreditScoringService creditScoringService;

    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<CreditScoreResponse>> calculateScore(@RequestBody CreditScoreRequest request) {
        CreditScoreResponse response = creditScoringService.calculateScore(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/score/{userId}")
    public ResponseEntity<ApiResponse<CreditScoreResponse>> getScore(@PathVariable String userId) {
        CreditScoreResponse response = creditScoringService.getScore(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
