package com.microfinance.payment.dto;

import com.microfinance.payment.domain.model.Payment;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data @Builder
public class PaymentResponse {
    private String id;
    private String reference;
    private String userId;
    private String walletId;
    private String type;
    private String status;
    private BigDecimal amount;
    private BigDecimal fee;
    private String currency;
    private String merchantId;
    private String merchantName;
    private String billerId;
    private String billerName;
    private String description;
    private String channel;
    private String paymentMethod;
    private String providerReference;
    private Instant completedAt;
    private String failureReason;
    private Instant createdAt;

    public static PaymentResponse from(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .reference(p.getReference())
                .userId(p.getUserId())
                .walletId(p.getWalletId())
                .type(p.getType() != null ? p.getType().name() : null)
                .status(p.getStatus() != null ? p.getStatus().name() : null)
                .amount(p.getAmount())
                .fee(p.getFee())
                .currency(p.getCurrency())
                .merchantId(p.getMerchantId())
                .merchantName(p.getMerchantName())
                .billerId(p.getBillerId())
                .billerName(p.getBillerName())
                .description(p.getDescription())
                .channel(p.getChannel())
                .paymentMethod(p.getPaymentMethod())
                .providerReference(p.getProviderReference())
                .completedAt(p.getCompletedAt())
                .failureReason(p.getFailureReason())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
