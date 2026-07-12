package com.microfinance.savings.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateSavingsGoalRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Goal name is required")
    private String goalName;

    private String goalDescription;

    @NotNull @DecimalMin(value = "10.00", message = "Target must be at least 10 ETB")
    private BigDecimal targetAmount;

    private LocalDate targetDate;

    private String category; // EDUCATION, BUSINESS, HOUSING, EMERGENCY, TRAVEL, etc.

    /** Optional: auto-save amount per period */
    @DecimalMin(value = "0.00")
    private BigDecimal autoSaveAmount;

    /** DAILY, WEEKLY, MONTHLY */
    private String autoSaveFrequency;
}
