package com.microfinance.loan.dto;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RepaymentResponse {
    private String loanId, loanNumber, installmentId, status;
    private BigDecimal paidAmount, outstandingBalance;
    private Instant paidAt;
}