package com.microfinance.payment.gateway;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class GatewayResponse {
    private boolean success;
    private String providerReference;
    private String failureReason;
}
