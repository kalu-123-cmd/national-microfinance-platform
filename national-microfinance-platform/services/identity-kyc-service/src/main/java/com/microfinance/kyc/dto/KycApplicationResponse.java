package com.microfinance.kyc.dto;

import lombok.*;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class KycApplicationResponse {
    private String id;
    private String userId;
    private String applicationNumber;
    private String kycTier;
    private String status;
    private Integer complianceScore;
    private String riskLevel;
    private String pepCheckResult;
    private String sanctionsCheckResult;
    private Boolean biometricVerified;
    private Boolean idDocumentVerified;
    private Instant submittedAt;
    private Instant reviewedAt;
    private Instant approvedAt;
    private Instant expiresAt;
    private Instant createdAt;
}