package com.microfinance.kyc.service;

import com.microfinance.common.exception.*;
import com.microfinance.kyc.domain.model.*;
import com.microfinance.kyc.domain.repository.*;
import com.microfinance.kyc.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KycService {

    private final KycApplicationRepository applicationRepository;
    private final VerificationCheckRepository checkRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public KycApplicationResponse initiateKyc(InitiateKycRequest request) {
        log.info("Initiating KYC for userId: {}, tier: {}", request.getUserId(), request.getKycTier());

        // Check if active application exists
        applicationRepository.findTopByUserIdAndStatusOrderByCreatedAtDesc(
            request.getUserId(), KycApplicationStatus.APPROVED)
            .ifPresent(app -> {
                if (!app.isExpired()) {
                    throw new BusinessException("Active KYC approval already exists for user");
                }
            });

        String appNumber = "KYC-" + System.currentTimeMillis();
        KycApplication application = KycApplication.builder()
            .id(UUID.randomUUID().toString())
            .userId(request.getUserId())
            .applicationNumber(appNumber)
            .kycTier(request.getKycTier())
            .status(KycApplicationStatus.DRAFT)
            .submissionNotes(request.getNotes())
            .build();

        KycApplication saved = applicationRepository.save(application);
        log.info("KYC application created: {}", appNumber);
        return toResponse(saved);
    }

    @Transactional
    public KycApplicationResponse submitApplication(String applicationId) {
        log.info("Submitting KYC application: {}", applicationId);

        KycApplication app = findApplication(applicationId);

        if (app.getStatus() != KycApplicationStatus.DRAFT &&
            app.getStatus() != KycApplicationStatus.DOCUMENTS_PENDING) {
            throw new BusinessException("Application cannot be submitted in current status: " + app.getStatus());
        }

        app.setStatus(KycApplicationStatus.SUBMITTED);
        app.setSubmittedAt(Instant.now());
        KycApplication saved = applicationRepository.save(app);

        // Trigger async verification
        runAutomatedChecks(applicationId, app.getUserId(), app.getKycTier());

        return toResponse(saved);
    }

    @Async
    public void runAutomatedChecks(String applicationId, String userId, KycTier tier) {
        log.info("Running automated checks for application: {}", applicationId);

        applicationRepository.updateStatus(applicationId, KycApplicationStatus.UNDER_REVIEW, Instant.now());

        // Run checks based on tier
        runCheck(applicationId, userId, CheckType.DUPLICATE_DETECTION, "INTERNAL");
        runCheck(applicationId, userId, CheckType.PHONE_VERIFICATION, "INTERNAL");
        runCheck(applicationId, userId, CheckType.ID_DOCUMENT_VERIFICATION, "JUMIO");
        runCheck(applicationId, userId, CheckType.LIVENESS_DETECTION, "ONFIDO");
        runCheck(applicationId, userId, CheckType.FACE_MATCH, "ONFIDO");
        runCheck(applicationId, userId, CheckType.PEP_SCREENING, "REFINITIV");
        runCheck(applicationId, userId, CheckType.SANCTIONS_SCREENING, "REFINITIV");

        if (tier == KycTier.TIER_3) {
            runCheck(applicationId, userId, CheckType.ADDRESS_VERIFICATION, "EXPERIAN");
            runCheck(applicationId, userId, CheckType.ADVERSE_MEDIA, "REFINITIV");
        }

        evaluateAndDecide(applicationId);
    }

    private void runCheck(String applicationId, String userId, CheckType checkType, String provider) {
        long start = System.currentTimeMillis();
        CheckResult result;
        String details;

        try {
            // Simulated checks — replace with real provider integration
            result = simulateCheck(checkType);
            details = "Automated check completed";
        } catch (Exception e) {
            result = CheckResult.ERROR;
            details = "Error: " + e.getMessage();
            log.error("Check {} failed for application {}", checkType, applicationId, e);
        }

        VerificationCheck check = VerificationCheck.builder()
            .id(UUID.randomUUID().toString())
            .applicationId(applicationId)
            .userId(userId)
            .checkType(checkType)
            .result(result)
            .provider(provider)
            .confidenceScore(result == CheckResult.PASSED ? 0.95 : 0.0)
            .details(details)
            .executedAt(Instant.now())
            .durationMs(System.currentTimeMillis() - start)
            .build();

        checkRepository.save(check);
        log.info("Check {} completed with result {} for application {}", checkType, result, applicationId);
    }

    private CheckResult simulateCheck(CheckType checkType) {
        // In production, this calls the actual external provider API
        return CheckResult.PASSED;
    }

    @Transactional
    public void evaluateAndDecide(String applicationId) {
        List<VerificationCheck> checks = checkRepository.findByApplicationId(applicationId);

        long failed = checks.stream().filter(c -> c.getResult() == CheckResult.FAILED).count();
        long manualReview = checks.stream().filter(c -> c.getResult() == CheckResult.MANUAL_REVIEW).count();
        long errors = checks.stream().filter(c -> c.getResult() == CheckResult.ERROR).count();

        KycApplicationStatus newStatus;
        if (failed > 0) {
            newStatus = KycApplicationStatus.REJECTED;
        } else if (manualReview > 0 || errors > 0) {
            newStatus = KycApplicationStatus.PENDING_REVIEW;
        } else {
            newStatus = KycApplicationStatus.APPROVED;
        }

        KycApplication app = findApplication(applicationId);
        app.setStatus(newStatus);

        if (newStatus == KycApplicationStatus.APPROVED) {
            app.setApprovedAt(Instant.now());
            app.setExpiresAt(Instant.now().plus(365 * 2, ChronoUnit.DAYS)); // 2-year validity
            app.setComplianceScore(90);
            app.setRiskLevel("LOW");
            app.setBiometricVerified(true);
            app.setIdDocumentVerified(true);
        }

        applicationRepository.save(app);

        // Publish event
        kafkaTemplate.send("kyc-events", new KycStatusChangedEvent(app.getUserId(), app.getId(), newStatus.toString()));
        log.info("KYC application {} decided: {}", applicationId, newStatus);
    }

    @Transactional
    public KycApplicationResponse manualReview(String applicationId, ManualReviewRequest request) {
        KycApplication app = findApplication(applicationId);

        if (app.getStatus() != KycApplicationStatus.PENDING_REVIEW) {
            throw new BusinessException("Application is not pending manual review");
        }

        app.setReviewerId(request.getReviewerId());
        app.setReviewNotes(request.getNotes());
        app.setReviewedAt(Instant.now());

        if (request.isApproved()) {
            app.setStatus(KycApplicationStatus.APPROVED);
            app.setApprovedAt(Instant.now());
            app.setExpiresAt(Instant.now().plus(365 * 2, ChronoUnit.DAYS));
            app.setComplianceScore(85);
            app.setRiskLevel("MEDIUM");
        } else {
            app.setStatus(KycApplicationStatus.REJECTED);
            app.setRejectionReason(request.getRejectionReason());
        }

        KycApplication saved = applicationRepository.save(app);
        kafkaTemplate.send("kyc-events", new KycStatusChangedEvent(app.getUserId(), app.getId(), app.getStatus().toString()));

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public KycApplicationResponse getApplication(String applicationId) {
        return toResponse(findApplication(applicationId));
    }

    @Transactional(readOnly = true)
    public List<KycApplicationResponse> getUserApplications(String userId) {
        return applicationRepository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<KycApplicationResponse> getPendingApplications(Pageable pageable) {
        return applicationRepository.findPendingApplications(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<VerificationCheckResponse> getApplicationChecks(String applicationId) {
        return checkRepository.findByApplicationId(applicationId).stream()
            .map(c -> VerificationCheckResponse.builder()
                .id(c.getId())
                .checkType(c.getCheckType().toString())
                .result(c.getResult().toString())
                .provider(c.getProvider())
                .confidenceScore(c.getConfidenceScore())
                .details(c.getDetails())
                .executedAt(c.getExecutedAt())
                .durationMs(c.getDurationMs())
                .build())
            .toList();
    }

    private KycApplication findApplication(String id) {
        return applicationRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("KYC application not found: " + id));
    }

    private KycApplicationResponse toResponse(KycApplication app) {
        return KycApplicationResponse.builder()
            .id(app.getId())
            .userId(app.getUserId())
            .applicationNumber(app.getApplicationNumber())
            .kycTier(app.getKycTier().toString())
            .status(app.getStatus().toString())
            .complianceScore(app.getComplianceScore())
            .riskLevel(app.getRiskLevel())
            .pepCheckResult(app.getPepCheckResult())
            .sanctionsCheckResult(app.getSanctionsCheckResult())
            .biometricVerified(app.getBiometricVerified())
            .idDocumentVerified(app.getIdDocumentVerified())
            .submittedAt(app.getSubmittedAt())
            .reviewedAt(app.getReviewedAt())
            .approvedAt(app.getApprovedAt())
            .expiresAt(app.getExpiresAt())
            .createdAt(app.getCreatedAt())
            .build();
    }

    // Simple event wrapper
    record KycStatusChangedEvent(String userId, String applicationId, String status) {}
}