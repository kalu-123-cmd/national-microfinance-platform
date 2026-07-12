package com.microfinance.payment.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Mock gateway service simulating Telebirr/CBEBirr/MPESA responses.
 * Introduces a configurable failure rate for realistic integration testing.
 */
@Service
@Slf4j
public class MockGatewayServiceImpl implements GatewayService {

    @Value("${payment.gateway.simulate-failure-rate:0.1}")
    private double failureRate;

    private final Random random = new Random();

    @Override
    public GatewayResponse initiatePayment(GatewayRequest request) {
        log.info("MockGateway: Processing {} payment of {} {} for wallet {}",
                request.getGateway(), request.getAmount(), request.getCurrency(), request.getWalletId());

        // Simulate network latency
        try { Thread.sleep(100 + random.nextInt(300)); } catch (InterruptedException ignored) {}

        boolean fail = random.nextDouble() < failureRate;
        if (fail) {
            log.warn("MockGateway: Simulated failure for reference {}", request.getReference());
            return GatewayResponse.builder()
                    .success(false)
                    .providerReference(null)
                    .failureReason("Simulated gateway timeout")
                    .build();
        }

        String providerRef = "GW-" + System.currentTimeMillis() + "-" + request.getGateway();
        log.info("MockGateway: Success - providerRef={}", providerRef);
        return GatewayResponse.builder()
                .success(true)
                .providerReference(providerRef)
                .failureReason(null)
                .build();
    }

    @Override
    public GatewayResponse refundPayment(String providerReference, java.math.BigDecimal amount) {
        log.info("MockGateway: Refunding {} for providerRef={}", amount, providerReference);
        return GatewayResponse.builder()
                .success(true)
                .providerReference("REFUND-" + providerReference)
                .build();
    }
}
