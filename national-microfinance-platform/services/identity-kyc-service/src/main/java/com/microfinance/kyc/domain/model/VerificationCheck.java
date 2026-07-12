package com.microfinance.kyc.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.Instant;

@Entity
@Table(name = "verification_checks")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class VerificationCheck {

    @Id
    private String id;

    @Column(name = "application_id", nullable = false, length = 50)
    private String applicationId;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "check_type", nullable = false, length = 50)
    private CheckType checkType;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false, length = 30)
    private CheckResult result;

    @Column(name = "provider", length = 100)
    private String provider; // External provider name

    @Column(name = "provider_reference", length = 100)
    private String providerReference;

    @Column(name = "confidence_score")
    private Double confidenceScore; // 0.0 - 1.0

    @Column(name = "raw_response", columnDefinition = "TEXT")
    private String rawResponse; // JSON response from provider

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "error_code", length = 50)
    private String errorCode;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "executed_at", nullable = false)
    private Instant executedAt;

    @Column(name = "duration_ms")
    private Long durationMs;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}