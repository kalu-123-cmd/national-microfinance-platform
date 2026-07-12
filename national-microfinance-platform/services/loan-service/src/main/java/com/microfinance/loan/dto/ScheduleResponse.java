package com.microfinance.loan.dto;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ScheduleResponse {
    private String id, status;
    private Integer installmentNumber;
    private LocalDate dueDate;
    private BigDecimal totalAmount, principalAmount, interestAmount, paidAmount, outstandingAmount;
    private Instant paidAt;
}