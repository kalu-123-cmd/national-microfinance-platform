package com.microfinance.payment.gateway;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data @Builder
public class GatewayRequest {
    private String reference;
    private String walletId;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String gateway;
    private String description;
    private String callbackUrl;
}
