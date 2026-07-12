package com.microfinance.loan.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.math.BigDecimal;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ApproveLoanRequest {
    @NotBlank private String approvedBy;
    private BigDecimal approvedAmount;
    private BigDecimal interestRate;
    private String notes;
}