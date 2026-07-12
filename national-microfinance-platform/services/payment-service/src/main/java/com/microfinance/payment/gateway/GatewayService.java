package com.microfinance.payment.gateway;

import java.math.BigDecimal;

public interface GatewayService {
    GatewayResponse initiatePayment(GatewayRequest request);
    GatewayResponse refundPayment(String providerReference, BigDecimal amount);
}
