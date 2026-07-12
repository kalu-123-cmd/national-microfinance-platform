package com.microfinance.notification.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "notifications")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Notification {
    @Id private String id;
    private String userId;
    private String recipient;  // phone or email
    private NotificationType type;
    private NotificationChannel channel; // SMS, EMAIL, PUSH
    private NotificationStatus status;
    private String subject;
    private String body;
    private String templateId;
    private String referenceId;
    private String referenceType;
    private int retryCount;
    private String failureReason;
    private Instant sentAt;
    private Instant deliveredAt;
    private Instant createdAt;
}