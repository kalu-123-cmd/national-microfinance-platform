package com.microfinance.kyc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ManualReviewRequest {
    @NotBlank private String reviewerId;
    private boolean approved;
    private String notes;
    private String rejectionReason;
}