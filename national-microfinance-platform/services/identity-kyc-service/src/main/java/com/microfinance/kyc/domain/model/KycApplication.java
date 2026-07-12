package com.microfinance.kyc.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.Instant;

@Entity
@Table(name = "kyc_applications")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class KycApplication {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "application_number", nullable = false, unique = true, length = 30)
    private String applicationNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_tier", nullable = false, length = 20)
    private KycTier kycTier;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private KycApplicationStatus status;

    @Column(name = "submission_notes", columnDefinition = "TEXT")
    private String submissionNotes;

    @Column(name = "reviewer_id", length = 50)
    private String reviewerId;

    @Column(name = "review_notes", columnDefinition = "TEXT")
    private String reviewNotes;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "compliance_score")
    private Integer complianceScore; // 0-100

    @Column(name = "risk_level", length = 20)
    private String riskLevel; // LOW, MEDIUM, HIGH, VERY_HIGH

    @Column(name = "pep_check_result", length = 20)
    private String pepCheckResult; // CLEAR, MATCH, POTENTIAL_MATCH

    @Column(name = "sanctions_check_result", length = 20)
    private String sanctionsCheckResult; // CLEAR, MATCH, POTENTIAL_MATCH

    @Column(name = "adverse_media_result", length = 20)
    private String adverseMediaResult; // CLEAR, FOUND

    @Column(name = "biometric_verified")
    private Boolean biometricVerified;

    @Column(name = "liveness_check_passed")
    private Boolean livenessCheckPassed;

    @Column(name = "id_document_verified")
    private Boolean idDocumentVerified;

    @Column(name = "address_verified")
    private Boolean addressVerified;

    @Column(name = "phone_verified")
    private Boolean phoneVerified;

    @Column(name = "email_verified")
    private Boolean emailVerified;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public boolean isApproved() {
        return status == KycApplicationStatus.APPROVED;
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public boolean requiresManualReview() {
        return status == KycApplicationStatus.PENDING_REVIEW ||
               "HIGH".equals(riskLevel) || "VERY_HIGH".equals(riskLevel);
    }
}