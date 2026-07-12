package com.microfinance.auth.dto;

import com.microfinance.auth.domain.model.OtpPurpose;
import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OtpSendRequest {

    @NotBlank(message = "Recipient is required")
    private String recipient; // phone or email

    @NotNull(message = "Purpose is required")
    private OtpPurpose purpose;
}
