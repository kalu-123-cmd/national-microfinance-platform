package com.microfinance.notification.service;

import com.microfinance.notification.domain.model.Notification;
import com.microfinance.notification.domain.model.NotificationChannel;
import com.microfinance.notification.domain.model.NotificationStatus;
import com.microfinance.notification.domain.model.NotificationType;
import com.microfinance.notification.domain.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SmsProvider smsProvider;

    @Mock
    private EmailProvider emailProvider;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(notificationRepository, smsProvider, emailProvider);
    }

    @Test
    void sendSmsMarksNotificationSentAfterProviderSucceeds() {
        notificationService.sendSms(
            "USER-1",
            "+251911111111",
            "Welcome",
            NotificationType.SYSTEM_ALERT,
            "ref-1"
        );

        verify(smsProvider).send("+251911111111", "Welcome");
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification notification = notificationCaptor.getValue();
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(notification.getChannel()).isEqualTo(NotificationChannel.SMS);
        assertThat(notification.getSentAt()).isNotNull();
        assertThat(notification.getRetryCount()).isZero();
    }

    @Test
    void sendEmailStoresFailureWhenProviderThrows() {
        doThrow(new RuntimeException("smtp unavailable"))
            .when(emailProvider).send("user@example.com", "Subject", "Body");

        notificationService.sendEmail(
            "USER-1",
            "user@example.com",
            "Subject",
            "Body",
            NotificationType.SYSTEM_ALERT,
            "ref-2"
        );

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification notification = notificationCaptor.getValue();
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.FAILED);
        assertThat(notification.getChannel()).isEqualTo(NotificationChannel.EMAIL);
        assertThat(notification.getFailureReason()).isEqualTo("smtp unavailable");
    }

    @Test
    void sendOtpRoutesEmailOtpThroughEmailProvider() {
        notificationService.sendOtp("USER-1", "user@example.com", "123456", NotificationChannel.EMAIL);

        verify(emailProvider).send(
            org.mockito.ArgumentMatchers.eq("user@example.com"),
            org.mockito.ArgumentMatchers.eq("Your OTP Code"),
            org.mockito.ArgumentMatchers.contains("123456")
        );
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());
        assertThat(notificationCaptor.getValue().getType()).isEqualTo(NotificationType.OTP);
    }
}
