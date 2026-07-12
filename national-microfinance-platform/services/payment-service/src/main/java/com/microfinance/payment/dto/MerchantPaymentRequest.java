package com.microfinance.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class MerchantPaymentRequest {
    @NotBlank private String walletId;
    @NotBlank private String merchantId;
    @NotBlank private String merchantName;
    @NotNull @DecimalMin("0.01") private BigDecimal amount;
    private String merchantCategory;
    private String terminalId;
    private String qrCodeRef;
    private String posEntryMode;
    private String description;
    private String channel;
}
