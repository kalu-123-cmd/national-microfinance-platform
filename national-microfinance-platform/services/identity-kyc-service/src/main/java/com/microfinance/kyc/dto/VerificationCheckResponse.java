package com.microfinance.kyc.dto;

import lombok.*;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class VerificationCheckResponse {
    private String id;
    private String checkType;
    private String result;
    private String provider;
    private Double confidenceScore;
    private String details;
    private Instant executedAt;
    private Long durationMs;
}