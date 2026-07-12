package com.microfinance.cooperative.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class MakeRepaymentRequest {
    @NotNull @DecimalMin("1.00") private BigDecimal amount;
    @NotBlank private String paymentReference;
}
