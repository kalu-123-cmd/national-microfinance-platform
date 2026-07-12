package com.microfinance.notification.service;

import com.microfinance.notification.domain.model.*;
import com.microfinance.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SmsProvider smsProvider;
    private final EmailProvider emailProvider;

    public void sendSms(String userId, String phone, String body, NotificationType type, String referenceId) {
        log.info("Sending SMS to {} type:{}", phone, type);
        Notification notification = Notification.builder()
            .id(UUID.randomUUID().toString())
            .userId(userId)
            .recipient(phone)
            .type(type)
            .channel(NotificationChannel.SMS)
            .status(NotificationStatus.PENDING)
            .body(body)
            .referenceId(referenceId)
            .retryCount(0)
            .createdAt(Instant.now())
            .build();

        try {
            smsProvider.send(phone, body);
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(Instant.now());
            log.info("SMS sent to {} successfully", phone);
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setFailureReason(e.getMessage());
            log.error("SMS failed to {} : {}", phone, e.getMessage());
        }
        notificationRepository.save(notification);
    }

    public void sendEmail(String userId, String email, String subject, String body,
                          NotificationType type, String referenceId) {
        log.info("Sending email to {} type:{}", email, type);
        Notification notification = Notification.builder()
            .id(UUID.randomUUID().toString())
            .userId(userId)
            .recipient(email)
            .type(type)
            .channel(NotificationChannel.EMAIL)
            .status(NotificationStatus.PENDING)
            .subject(subject)
            .body(body)
            .referenceId(referenceId)
            .retryCount(0)
            .createdAt(Instant.now())
            .build();

        try {
            emailProvider.send(email, subject, body);
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(Instant.now());
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setFailureReason(e.getMessage());
            log.error("Email failed to {} : {}", email, e.getMessage());
        }
        notificationRepository.save(notification);
    }

    public void sendOtp(String userId, String recipient, String otpCode, NotificationChannel channel) {
        String body = "Your verification code is: " + otpCode + ". Valid for 5 minutes. Do not share with anyone.";
        if (channel == NotificationChannel.EMAIL) {
            sendEmail(userId, recipient, "Your OTP Code", body, NotificationType.OTP, null);
        } else {
            sendSms(userId, recipient, body, NotificationType.OTP, null);
        }
    }

    public void sendTransactionAlert(String userId, String phone, String amount, String type, String reference) {
        String body = String.format("Transaction Alert: %s of ETB %s. Ref: %s. If not authorized, call 8282.",
            type, amount, reference);
        sendSms(userId, phone, body, NotificationType.TRANSACTION_ALERT, reference);
    }
}