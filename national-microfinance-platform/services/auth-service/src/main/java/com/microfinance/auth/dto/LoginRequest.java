package com.microfinance.auth.dto;

import com.microfinance.auth.domain.model.LoginMethod;
import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Identifier is required")
    private String identifier; // phone, email, or userId

    private String password; // for PASSWORD login
    private String pin;      // for PIN login

    @NotNull(message = "Login method is required")
    private LoginMethod loginMethod;

    private String deviceId;
    private String deviceInfo;
    private String ipAddress;

    // OTP login flow uses separate endpoint
}
