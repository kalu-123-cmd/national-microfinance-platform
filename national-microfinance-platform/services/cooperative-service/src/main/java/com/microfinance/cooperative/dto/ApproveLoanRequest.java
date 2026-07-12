package com.microfinance.cooperative.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ApproveLoanRequest {
    @NotNull @DecimalMin("1.00") private BigDecimal approvedAmount;
}
