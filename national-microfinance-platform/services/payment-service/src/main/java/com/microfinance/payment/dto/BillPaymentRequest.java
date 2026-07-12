package com.microfinance.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BillPaymentRequest {
    @NotBlank private String walletId;
    @NotBlank private String billerId;
    @NotBlank private String billerName;
    @NotBlank private String billerCode;
    @NotBlank private String billAccountNumber;
    @NotNull @DecimalMin("0.01") private BigDecimal amount;
    private LocalDate billDueDate;
    private String billPeriod;
    private String description;
    private String channel;
}
