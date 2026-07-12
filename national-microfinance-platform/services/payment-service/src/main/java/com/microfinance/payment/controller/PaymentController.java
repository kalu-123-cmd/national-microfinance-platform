package com.microfinance.payment.controller;

import com.microfinance.payment.dto.*;
import com.microfinance.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "payment-service"));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentResponse> initiatePayment(
            @Valid @RequestBody InitiatePaymentRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.initiatePayment(request, user.getUsername()));
    }

    @PostMapping("/merchant")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentResponse> merchantPayment(
            @Valid @RequestBody MerchantPaymentRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.processMerchantPayment(request, user.getUsername()));
    }

    @PostMapping("/bill")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentResponse> billPayment(
            @Valid @RequestBody BillPaymentRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.payBill(request, user.getUsername()));
    }

    @PostMapping("/callback")
    public ResponseEntity<Void> gatewayCallback(@RequestBody GatewayCallbackRequest request) {
        paymentService.handleGatewayCallback(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable String paymentId) {
        return ResponseEntity.ok(paymentService.getPayment(paymentId));
    }

    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<PaymentResponse>> getHistory(
            @AuthenticationPrincipal UserDetails user, Pageable pageable) {
        return ResponseEntity.ok(paymentService.getPaymentHistory(user.getUsername(), pageable));
    }

    @PostMapping("/{paymentId}/refund")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentResponse> refund(
            @PathVariable String paymentId,
            @RequestParam(required = false) BigDecimal amount) {
        return ResponseEntity.ok(paymentService.refundPayment(paymentId, amount));
    }
}
