package com.microfinance.loan.dto;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RepaymentRequest {
    @NotBlank private String loanId;
    @NotNull @Positive private BigDecimal amount;
    private String paymentReference;
    private String channel;
}