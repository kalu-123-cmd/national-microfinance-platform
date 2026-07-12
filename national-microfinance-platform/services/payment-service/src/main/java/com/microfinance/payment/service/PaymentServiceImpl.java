package com.microfinance.payment.service;

import com.microfinance.payment.domain.model.*;
import com.microfinance.payment.domain.repository.*;
import com.microfinance.payment.dto.*;
import com.microfinance.payment.gateway.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private static final BigDecimal FEE_RATE = new BigDecimal("0.005"); // 0.5%
    private static final int MAX_RETRIES = 3;

    private final PaymentRepository paymentRepository;
    private final MerchantPaymentRepository merchantPaymentRepository;
    private final BillPaymentRepository billPaymentRepository;
    private final GatewayService gatewayService;
    private final KafkaOperations<String, Object> kafkaTemplate;

    // =========================================================================
    // Generic Payment
    // =========================================================================

    @Override
    @Transactional
    public PaymentResponse initiatePayment(InitiatePaymentRequest req, String userId) {
        BigDecimal fee = req.getAmount().multiply(FEE_RATE).setScale(2, java.math.RoundingMode.HALF_UP);
        String reference = generateReference("PAY");

        Payment payment = Payment.builder()
                .id(UUID.randomUUID().toString())
                .reference(reference)
                .userId(userId)
                .walletId(req.getWalletId())
                .type(req.getType())
                .status(PaymentStatus.PROCESSING)
                .amount(req.getAmount())
                .fee(fee)
                .currency(req.getCurrency() != null ? req.getCurrency() : "ETB")
                .merchantId(req.getMerchantId())
                .merchantName(req.getMerchantName())
                .billerId(req.getBillerId())
                .billerName(req.getBillerName())
                .accountNumber(req.getAccountNumber())
                .description(req.getDescription())
                .channel(req.getChannel())
                .paymentMethod(req.getPaymentGateway() != null ? req.getPaymentGateway().name() : "MOCK")
                .callbackUrl(req.getCallbackUrl())
                .metadata(req.getMetadata())
                .retryCount(0)
                .build();

        payment = paymentRepository.save(payment);

        // Call gateway
        GatewayResponse gwResp = gatewayService.initiatePayment(GatewayRequest.builder()
                .reference(reference)
                .walletId(req.getWalletId())
                .userId(userId)
                .amount(req.getAmount())
                .currency(payment.getCurrency())
                .gateway(payment.getPaymentMethod())
                .description(req.getDescription())
                .callbackUrl(req.getCallbackUrl())
                .build());

        if (gwResp.isSuccess()) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setProviderReference(gwResp.getProviderReference());
            payment.setCompletedAt(Instant.now());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(gwResp.getFailureReason());
            payment.setNextRetryAt(Instant.now().plusSeconds(60));
        }

        payment = paymentRepository.save(payment);
        publishEvent(payment);
        log.info("Payment {} -> {} [{}]", reference, payment.getStatus(), payment.getAmount());
        return PaymentResponse.from(payment);
    }

    // =========================================================================
    // Merchant Payment
    // =========================================================================

    @Override
    @Transactional
    public PaymentResponse processMerchantPayment(MerchantPaymentRequest req, String userId) {
        InitiatePaymentRequest base = new InitiatePaymentRequest();
        base.setWalletId(req.getWalletId());
        base.setType(PaymentType.MERCHANT_PAYMENT);
        base.setAmount(req.getAmount());
        base.setMerchantId(req.getMerchantId());
        base.setMerchantName(req.getMerchantName());
        base.setDescription(req.getDescription() != null ? req.getDescription()
                : "Payment to " + req.getMerchantName());
        base.setChannel(req.getChannel());
        PaymentResponse resp = initiatePayment(base, userId);

        if (PaymentStatus.COMPLETED.name().equals(resp.getStatus())) {
            MerchantPayment mp = MerchantPayment.builder()
                    .id(UUID.randomUUID().toString())
                    .paymentId(resp.getId())
                    .merchantId(req.getMerchantId())
                    .merchantName(req.getMerchantName())
                    .merchantCategory(req.getMerchantCategory())
                    .terminalId(req.getTerminalId())
                    .qrCodeRef(req.getQrCodeRef())
                    .posEntryMode(req.getPosEntryMode())
                    .build();
            merchantPaymentRepository.save(mp);
        }
        return resp;
    }

    // =========================================================================
    // Bill Payment
    // =========================================================================

    @Override
    @Transactional
    public PaymentResponse payBill(BillPaymentRequest req, String userId) {
        InitiatePaymentRequest base = new InitiatePaymentRequest();
        base.setWalletId(req.getWalletId());
        base.setType(PaymentType.BILL_PAYMENT);
        base.setAmount(req.getAmount());
        base.setBillerId(req.getBillerId());
        base.setBillerName(req.getBillerName());
        base.setAccountNumber(req.getBillAccountNumber());
        base.setDescription(req.getDescription() != null ? req.getDescription()
                : "Bill payment - " + req.getBillerName());
        base.setChannel(req.getChannel());
        PaymentResponse resp = initiatePayment(base, userId);

        if (PaymentStatus.COMPLETED.name().equals(resp.getStatus())) {
            BillPayment bp = BillPayment.builder()
                    .id(UUID.randomUUID().toString())
                    .paymentId(resp.getId())
                    .billerId(req.getBillerId())
                    .billerName(req.getBillerName())
                    .billerCode(req.getBillerCode())
                    .billAccountNumber(req.getBillAccountNumber())
                    .billDueDate(req.getBillDueDate())
                    .billPeriod(req.getBillPeriod())
                    .build();
            billPaymentRepository.save(bp);
        }
        return resp;
    }

    // =========================================================================
    // Gateway Callback (Webhook)
    // =========================================================================

    @Override
    @Transactional
    public void handleGatewayCallback(GatewayCallbackRequest req) {
        Payment payment = paymentRepository.findByReference(req.getReference())
                .orElse(null);
        if (payment == null) {
            log.warn("Callback for unknown reference: {}", req.getReference());
            return;
        }
        if (payment.getStatus() == PaymentStatus.COMPLETED || payment.getStatus() == PaymentStatus.REFUNDED) {
            log.info("Idempotent callback ignored - payment {} already {}", req.getReference(), payment.getStatus());
            return;
        }
        if ("SUCCESS".equalsIgnoreCase(req.getStatus())) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setProviderReference(req.getProviderReference());
            payment.setCompletedAt(Instant.now());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(req.getFailureReason());
        }
        paymentRepository.save(payment);
        publishEvent(payment);
        log.info("Gateway callback processed: {} -> {}", req.getReference(), payment.getStatus());
    }

    // =========================================================================
    // Refund
    // =========================================================================

    @Override
    @Transactional
    public PaymentResponse refundPayment(String paymentId, BigDecimal refundAmount) {
        Payment original = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
        if (original.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Only COMPLETED payments can be refunded");
        }
        BigDecimal amount = refundAmount != null ? refundAmount : original.getAmount();
        if (amount.compareTo(original.getAmount()) > 0) {
            throw new IllegalStateException("Refund amount exceeds original payment amount");
        }
        GatewayResponse gwResp = gatewayService.refundPayment(original.getProviderReference(), amount);
        Payment refund = Payment.builder()
                .id(UUID.randomUUID().toString())
                .reference(generateReference("REF"))
                .userId(original.getUserId())
                .walletId(original.getWalletId())
                .type(PaymentType.REFUND)
                .status(gwResp.isSuccess() ? PaymentStatus.COMPLETED : PaymentStatus.FAILED)
                .amount(amount)
                .fee(BigDecimal.ZERO)
                .currency(original.getCurrency())
                .description("Refund for " + original.getReference())
                .providerReference(gwResp.getProviderReference())
                .completedAt(gwResp.isSuccess() ? Instant.now() : null)
                .failureReason(gwResp.getFailureReason())
                .retryCount(0)
                .build();
        refund = paymentRepository.save(refund);

        if (gwResp.isSuccess()) {
            boolean isPartial = amount.compareTo(original.getAmount()) < 0;
            original.setStatus(isPartial ? PaymentStatus.PARTIALLY_REFUNDED : PaymentStatus.REFUNDED);
            paymentRepository.save(original);
        }
        log.info("Refund {} ETB for payment {} -> {}", amount, original.getReference(), refund.getStatus());
        return PaymentResponse.from(refund);
    }

    // =========================================================================
    // Queries
    // =========================================================================

    @Override
    public Page<PaymentResponse> getPaymentHistory(String userId, Pageable pageable) {
        return paymentRepository.findByUserId(userId, pageable).map(PaymentResponse::from);
    }

    @Override
    public PaymentResponse getPayment(String paymentId) {
        return PaymentResponse.from(paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId)));
    }

    // =========================================================================
    // Retry Scheduler
    // =========================================================================

    @Scheduled(cron = "0 */5 * * * *") // every 5 minutes
    @Transactional
    public void retryFailedPayments() {
        List<Payment> retryable = paymentRepository.findRetryable(Instant.now(), MAX_RETRIES);
        if (retryable.isEmpty()) return;
        log.info("Retrying {} failed payments", retryable.size());
        for (Payment payment : retryable) {
            try {
                GatewayResponse gwResp = gatewayService.initiatePayment(GatewayRequest.builder()
                        .reference(payment.getReference())
                        .walletId(payment.getWalletId())
                        .userId(payment.getUserId())
                        .amount(payment.getAmount())
                        .currency(payment.getCurrency())
                        .gateway(payment.getPaymentMethod())
                        .build());
                payment.setRetryCount(payment.getRetryCount() + 1);
                if (gwResp.isSuccess()) {
                    payment.setStatus(PaymentStatus.COMPLETED);
                    payment.setProviderReference(gwResp.getProviderReference());
                    payment.setCompletedAt(Instant.now());
                    payment.setNextRetryAt(null);
                } else {
                    long backoffSeconds = (long) Math.pow(2, payment.getRetryCount()) * 60;
                    payment.setNextRetryAt(Instant.now().plusSeconds(backoffSeconds));
                    if (payment.getRetryCount() >= MAX_RETRIES) {
                        payment.setStatus(PaymentStatus.FAILED);
                        payment.setFailureReason("Max retries exceeded");
                    }
                }
                paymentRepository.save(payment);
                publishEvent(payment);
            } catch (Exception e) {
                log.error("Retry error for payment {}: {}", payment.getReference(), e.getMessage());
            }
        }
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private void publishEvent(Payment payment) {
        String topic = PaymentStatus.COMPLETED == payment.getStatus()
                ? "payment.completed" : "payment.failed";
        kafkaTemplate.send(topic, payment.getId(), Map.of(
                "paymentId", payment.getId(),
                "reference", payment.getReference(),
                "userId", payment.getUserId(),
                "amount", payment.getAmount(),
                "status", payment.getStatus().name()
        ));
    }

    private String generateReference(String prefix) {
        return prefix + "-" + System.currentTimeMillis() + "-"
                + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
