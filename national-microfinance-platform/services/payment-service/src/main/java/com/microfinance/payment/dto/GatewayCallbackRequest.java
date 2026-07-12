package com.microfinance.payment.dto;

import lombok.Data;

@Data
public class GatewayCallbackRequest {
    private String reference;
    private String providerReference;
    private String status;  // SUCCESS or FAILED
    private String failureReason;
    private String timestamp;
    private String signature;
}
