package com.microfinance.kyc.dto;

import com.microfinance.kyc.domain.model.KycTier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class InitiateKycRequest {
    @NotBlank private String userId;
    @NotNull  private KycTier kycTier;
    private String notes;
}