package com.microfinance.savings.dto;

import com.microfinance.savings.domain.model.AccountType;
import com.microfinance.savings.domain.model.CompoundingFrequency;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateSavingsAccountRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    private String accountName;

    /** Initial deposit amount */
    @DecimalMin(value = "0.00", message = "Initial deposit must be non-negative")
    private BigDecimal initialDeposit;

    private CompoundingFrequency compoundingFreq;

    private String currency = "ETB";
}
