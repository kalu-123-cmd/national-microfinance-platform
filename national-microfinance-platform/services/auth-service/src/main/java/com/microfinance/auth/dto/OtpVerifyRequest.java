package com.microfinance.auth.dto;

import com.microfinance.auth.domain.model.OtpPurpose;
import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OtpVerifyRequest {

    @NotBlank(message = "Recipient is required")
    private String recipient;

    @NotBlank(message = "OTP code is required")
    @Pattern(regexp = "^\\d{6}$", message = "OTP must be 6 digits")
    private String otpCode;

    @NotNull(message = "Purpose is required")
    private OtpPurpose purpose;

    private String deviceId;
    private String deviceInfo;
}
