package com.microfinance.notification.listener;

import com.microfinance.event.KafkaTopics;
import com.microfinance.notification.domain.model.NotificationType;
import com.microfinance.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "user-events", groupId = "notification-service")
    public void onUserEvent(Map<String, Object> event) {
        try {
            String type = (String) event.get("eventType");
            String userId = (String) event.get("userId");
            String phone = (String) event.get("phoneNumber");
            if ("USER_REGISTERED".equals(type) && phone != null) {
                notificationService.sendSms(userId, phone,
                    "Welcome to Ethiopia National Microfinance Platform! Your account has been created.",
                    NotificationType.WELCOME, userId);
            }
        } catch (Exception e) {
            log.error("Failed to process user event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "wallet-events", groupId = "notification-service")
    public void onWalletEvent(Map<String, Object> event) {
        try {
            String userId = (String) event.get("userId");
            String amount  = (String) event.get("amount");
            String ref = (String) event.get("reference");
            // Lookup phone from user-service via feign (simplified here)
            log.info("Wallet event for user {}: {} ETB ref:{}", userId, amount, ref);
        } catch (Exception e) {
            log.error("Failed to process wallet event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "loan-events", groupId = "notification-service")
    public void onLoanEvent(Map<String, Object> event) {
        log.info("Received loan event: {}", event);
    }

    @KafkaListener(topics = "kyc-events", groupId = "notification-service")
    public void onKycEvent(Map<String, Object> event) {
        log.info("Received KYC event: {}", event);
    }

    @KafkaListener(topics = KafkaTopics.NOTIFY_SMS, groupId = "notification-service")
    public void onSmsNotificationEvent(Map<String, Object> event) {
        try {
            String userId = (String) event.get("userId");
            String recipient = (String) event.get("recipient");
            String body = (String) event.get("body");
            String referenceId = (String) event.get("referenceId");
            if (recipient != null && body != null) {
                notificationService.sendSms(userId, recipient, body, NotificationType.OTP, referenceId);
            } else {
                log.warn("Invalid SMS notification event payload: {}", event);
            }
        } catch (Exception e) {
            log.error("Failed to process SMS notification event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = KafkaTopics.NOTIFY_EMAIL, groupId = "notification-service")
    public void onEmailNotificationEvent(Map<String, Object> event) {
        try {
            String userId = (String) event.get("userId");
            String recipient = (String) event.get("recipient");
            String subject = (String) event.get("subject");
            String body = (String) event.get("body");
            String referenceId = (String) event.get("referenceId");
            if (recipient != null && subject != null && body != null) {
                notificationService.sendEmail(userId, recipient, subject, body, NotificationType.OTP, referenceId);
            } else {
                log.warn("Invalid email notification event payload: {}", event);
            }
        } catch (Exception e) {
            log.error("Failed to process email notification event: {}", e.getMessage());
        }
    }
}
