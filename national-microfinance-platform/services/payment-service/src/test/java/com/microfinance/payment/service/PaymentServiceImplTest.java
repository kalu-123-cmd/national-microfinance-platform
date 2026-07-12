package com.microfinance.payment.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaOperations;

import com.microfinance.payment.domain.model.Payment;
import com.microfinance.payment.domain.model.PaymentStatus;
import com.microfinance.payment.domain.model.PaymentType;
import com.microfinance.payment.domain.repository.BillPaymentRepository;
import com.microfinance.payment.domain.repository.MerchantPaymentRepository;
import com.microfinance.payment.domain.repository.PaymentRepository;
import com.microfinance.payment.dto.InitiatePaymentRequest;
import com.microfinance.payment.gateway.GatewayResponse;
import com.microfinance.payment.gateway.GatewayService;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    PaymentRepository paymentRepository;
    @Mock
    MerchantPaymentRepository merchantPaymentRepository;
    @Mock
    BillPaymentRepository billPaymentRepository;
    @Mock
    GatewayService gatewayService;
    @Mock
    KafkaOperations<String, Object> kafkaTemplate;

    @InjectMocks
    PaymentServiceImpl service;

    @Test
    void initiatePayment_success_setsCompletedAndProviderRef() {
        InitiatePaymentRequest req = new InitiatePaymentRequest();
        req.setWalletId("W1");
        req.setType(PaymentType.WALLET_TO_WALLET);
        req.setAmount(new BigDecimal("100.00"));

        when(gatewayService.initiatePayment(any())).thenReturn(GatewayResponse.builder()
                .success(true)
                .providerReference("GW-123")
                .build());
        // echo saved entity
        doAnswer(inv -> inv.getArgument(0)).when(paymentRepository).save(any());

        var resp = service.initiatePayment(req, "user-1");

        assertThat(resp).isNotNull();
        assertThat(resp.getStatus()).isEqualTo(PaymentStatus.COMPLETED.name());
        assertThat(resp.getProviderReference()).isEqualTo("GW-123");
    }

    @Test
    void handleGatewayCallback_unknownReference_noException() {
        when(paymentRepository.findByReference("unknown")).thenReturn(Optional.empty());
        service.handleGatewayCallback(new com.microfinance.payment.dto.GatewayCallbackRequest(){
            { setReference("unknown"); setStatus("FAILED"); }
        });
    }

    @Test
    void refundPayment_success_createsRefundAndMarksOriginalRefunded() {
        Payment original = Payment.builder()
                .id("p1")
                .reference("REF-1")
                .status(PaymentStatus.COMPLETED)
                .amount(new BigDecimal("50.00"))
                .providerReference("GW-abc")
                .createdAt(Instant.now())
                .build();

        when(paymentRepository.findById("p1")).thenReturn(Optional.of(original));
        when(gatewayService.refundPayment("GW-abc", new BigDecimal("50.00")))
                .thenReturn(GatewayResponse.builder().success(true).providerReference("REF-GW-abc").build());
        doAnswer(inv -> inv.getArgument(0)).when(paymentRepository).save(any());

        var resp = service.refundPayment("p1", null);

        assertThat(resp).isNotNull();
        assertThat(resp.getStatus()).isEqualTo(PaymentStatus.COMPLETED.name());
        assertThat(original.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
    }
}
