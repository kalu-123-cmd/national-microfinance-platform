package com.microfinance.payment.dto;

import com.microfinance.payment.domain.model.PaymentGateway;
import com.microfinance.payment.domain.model.PaymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class InitiatePaymentRequest {
    @NotBlank private String walletId;
    @NotNull private PaymentType type;
    @NotNull @DecimalMin("0.01") private BigDecimal amount;
    private String currency = "ETB";
    private String merchantId;
    private String merchantName;
    private String billerId;
    private String billerName;
    private String accountNumber;
    private String description;
    private String channel;
    private PaymentGateway paymentGateway = PaymentGateway.MOCK;
    private String callbackUrl;
    private String metadata;
}
