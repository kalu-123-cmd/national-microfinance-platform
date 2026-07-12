package com.microfinance.loan.dto;
import com.microfinance.loan.domain.model.LoanType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LoanApplicationRequest {
    @NotBlank private String userId;
    @NotBlank private String walletId;
    @NotNull @Positive @DecimalMax("5000000.00") private BigDecimal requestedAmount;
    @NotNull @Min(1) @Max(60) private Integer tenureMonths;
    @NotNull private LoanType loanType;
    @NotBlank private String purpose;
    private String collateralType;
    private String collateralDescription;
    private String guarantorUserId;
}