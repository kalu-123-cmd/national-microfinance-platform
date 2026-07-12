package com.microfinance.savings.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawRequest {

    @NotBlank(message = "Account ID is required")
    private String accountId;

    @NotNull @DecimalMin(value = "1.00", message = "Minimum withdrawal is 1 ETB")
    private BigDecimal amount;

    private String channel;
    private String description;
    private String pin; // optional PIN verification
}
