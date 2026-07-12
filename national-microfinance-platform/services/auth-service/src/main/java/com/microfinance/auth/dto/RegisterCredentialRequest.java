package com.microfinance.auth.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RegisterCredentialRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+251[79]\\d{8}$", message = "Phone number must be in format +251XXXXXXXXX")
    private String phoneNumber;

    @Email(message = "Invalid email format")
    private String email;

    // Password is optional — users can authenticate with PIN only.
    // If provided it must be strong.
    @Size(min = 8, max = 128, message = "Password must be 8-128 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$",
        message = "Password must contain uppercase, lowercase, digit, and special character"
    )
    private String password;

    @NotBlank(message = "PIN is required")
    @Pattern(regexp = "^\\d{4,6}$", message = "PIN must be 4-6 digits")
    private String pin;

    private String deviceId;
    private String deviceInfo;
}
