package com.microfinance.savings.dto;

import com.microfinance.savings.domain.model.CompoundingFrequency;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateFixedDepositRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotNull @DecimalMin(value = "100.00", message = "Minimum fixed deposit is 100 ETB")
    private BigDecimal principalAmount;

    @NotNull @Min(value = 1, message = "Minimum tenure is 1 month")
    @Max(value = 120, message = "Maximum tenure is 120 months (10 years)")
    private Integer tenureMonths;

    private CompoundingFrequency compoundingFreq;
    private boolean autoRenew = false;

    /** Source account/wallet to debit for the deposit */
    private String sourceAccountId;
}
