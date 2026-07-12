package com.microfinance.loan.dto;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LoanResponse {
    private String id, loanNumber, userId, loanType, status, purpose;
    private BigDecimal requestedAmount, approvedAmount, disbursedAmount, interestRate;
    private Integer tenureMonths;
    private BigDecimal outstandingBalance, totalRepayable, totalInterest;
    private LocalDate firstRepaymentDate, maturityDate;
    private Instant disbursedAt, approvedAt, createdAt;
}