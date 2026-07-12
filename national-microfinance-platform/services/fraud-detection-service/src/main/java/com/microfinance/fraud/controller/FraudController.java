package com.microfinance.fraud.controller;

import com.microfinance.common.dto.ApiResponse;
import com.microfinance.fraud.dto.FraudCheckRequest;
import com.microfinance.fraud.dto.FraudCheckResponse;
import com.microfinance.fraud.entity.FraudAlert;
import com.microfinance.fraud.service.FraudDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fraud")
@RequiredArgsConstructor
public class FraudController {

    private final FraudDetectionService fraudDetectionService;

    @PostMapping("/check")
    public ResponseEntity<ApiResponse<FraudCheckResponse>> checkFraud(@RequestBody FraudCheckRequest request) {
        FraudCheckResponse response = fraudDetectionService.evaluateTransaction(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/alerts/user/{userId}")
    public ResponseEntity<ApiResponse<List<FraudAlert>>> getUserAlerts(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.success(fraudDetectionService.getAlertsByUser(userId)));
    }

    @GetMapping("/alerts/transaction/{transactionId}")
    public ResponseEntity<ApiResponse<List<FraudAlert>>> getTransactionAlerts(@PathVariable String transactionId) {
        return ResponseEntity.ok(ApiResponse.success(fraudDetectionService.getAlertsByTransaction(transactionId)));
    }

    @PutMapping("/alerts/{alertId}/status")
    public ResponseEntity<ApiResponse<FraudAlert>> updateAlertStatus(
            @PathVariable Long alertId, 
            @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success(fraudDetectionService.updateAlertStatus(alertId, status)));
    }
}
