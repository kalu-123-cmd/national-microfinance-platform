package com.microfinance.kyc.controller;

import com.microfinance.common.dto.ApiResponse;
import com.microfinance.common.dto.PageResponse;
import com.microfinance.kyc.dto.*;
import com.microfinance.kyc.service.KycService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/kyc")
@RequiredArgsConstructor
@Tag(name = "KYC", description = "Identity & KYC verification endpoints")
public class KycController {

    private final KycService kycService;

    @PostMapping("/initiate")
    @Operation(summary = "Initiate KYC application")
    public ResponseEntity<ApiResponse<KycApplicationResponse>> initiate(@Valid @RequestBody InitiateKycRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(kycService.initiateKyc(request), "KYC application created"));
    }

    @PostMapping("/{applicationId}/submit")
    @Operation(summary = "Submit KYC application for verification")
    public ResponseEntity<ApiResponse<KycApplicationResponse>> submit(@PathVariable String applicationId) {
        return ResponseEntity.ok(ApiResponse.success(kycService.submitApplication(applicationId), "Application submitted"));
    }

    @PostMapping("/{applicationId}/review")
    @Operation(summary = "Manual review decision")
    public ResponseEntity<ApiResponse<KycApplicationResponse>> manualReview(
            @PathVariable String applicationId,
            @Valid @RequestBody ManualReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.success(kycService.manualReview(applicationId, request), "Review recorded"));
    }

    @GetMapping("/{applicationId}")
    @Operation(summary = "Get KYC application")
    public ResponseEntity<ApiResponse<KycApplicationResponse>> getApplication(@PathVariable String applicationId) {
        return ResponseEntity.ok(ApiResponse.success(kycService.getApplication(applicationId), "Retrieved"));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user KYC applications")
    public ResponseEntity<ApiResponse<List<KycApplicationResponse>>> getUserApplications(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.success(kycService.getUserApplications(userId), "Retrieved"));
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending applications (admin/reviewer)")
    public ResponseEntity<ApiResponse<PageResponse<KycApplicationResponse>>> getPending(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(kycService.getPendingApplications(pageable)), "Retrieved"));
    }

    @GetMapping("/{applicationId}/checks")
    @Operation(summary = "Get verification checks for application")
    public ResponseEntity<ApiResponse<List<VerificationCheckResponse>>> getChecks(@PathVariable String applicationId) {
        return ResponseEntity.ok(ApiResponse.success(kycService.getApplicationChecks(applicationId), "Retrieved"));
    }
}