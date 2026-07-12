package com.microfinance.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminUserRequest {
    @NotBlank private String userId;
    @NotBlank private String username;
    @NotBlank private String role;
    private String department;
}
