package com.microfinance.payment.service;

import com.microfinance.payment.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;

public interface PaymentService {
    PaymentResponse initiatePayment(InitiatePaymentRequest req, String userId);
    PaymentResponse processMerchantPayment(MerchantPaymentRequest req, String userId);
    PaymentResponse payBill(BillPaymentRequest req, String userId);
    void handleGatewayCallback(GatewayCallbackRequest req);
    PaymentResponse refundPayment(String paymentId, BigDecimal refundAmount);
    Page<PaymentResponse> getPaymentHistory(String userId, Pageable pageable);
    PaymentResponse getPayment(String paymentId);
}
