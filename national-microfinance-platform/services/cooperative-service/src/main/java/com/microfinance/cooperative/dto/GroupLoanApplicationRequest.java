package com.microfinance.cooperative.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class GroupLoanApplicationRequest {
    @NotNull @DecimalMin("100.00") private BigDecimal amount;
    @Min(1) private int tenureMonths;
    @NotBlank private String purpose;
}
