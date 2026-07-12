package com.microfinance.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Circuit breaker fallback controller.
 * Returns a user-friendly message when a downstream service is unavailable.
 */
@RestController
public class FallbackController {

    @RequestMapping("/fallback/auth")
    public Mono<ResponseEntity<Map<String, Object>>> authFallback() {
        return fallback("Authentication service is temporarily unavailable. Please try again.");
    }

    @RequestMapping("/fallback/wallet")
    public Mono<ResponseEntity<Map<String, Object>>> walletFallback() {
        return fallback("Wallet service is temporarily unavailable. Your funds are safe.");
    }

    @RequestMapping("/fallback/payment")
    public Mono<ResponseEntity<Map<String, Object>>> paymentFallback() {
        return fallback("Payment service is temporarily unavailable. No transaction was processed.");
    }

    @RequestMapping("/fallback/loan")
    public Mono<ResponseEntity<Map<String, Object>>> loanFallback() {
        return fallback("Loan service is temporarily unavailable. Please try again shortly.");
    }

    @RequestMapping("/fallback/default")
    public Mono<ResponseEntity<Map<String, Object>>> defaultFallback() {
        return fallback("Service is temporarily unavailable. Please try again in a few moments.");
    }

    private Mono<ResponseEntity<Map<String, Object>>> fallback(String message) {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
                Map.of("success", false, "message", message, "retryAfter", "30")));
    }
}
