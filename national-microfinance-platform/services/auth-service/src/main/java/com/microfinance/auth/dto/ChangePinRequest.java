package com.microfinance.auth.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ChangePinRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Current PIN is required")
    @Pattern(regexp = "^\\d{4,6}$", message = "PIN must be 4-6 digits")
    private String currentPin;

    @NotBlank(message = "New PIN is required")
    @Pattern(regexp = "^\\d{4,6}$", message = "New PIN must be 4-6 digits")
    private String newPin;
}
