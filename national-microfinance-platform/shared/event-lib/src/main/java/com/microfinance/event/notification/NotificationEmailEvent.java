package com.microfinance.event.notification;

import com.microfinance.event.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NotificationEmailEvent extends BaseEvent {

    private String userId;
    private String recipient;
    private String subject;
    private String body;
    private String referenceId;

    public NotificationEmailEvent(String userId, String recipient, String subject, String body, String referenceId) {
        super("NOTIFY_EMAIL", "auth-service");
        this.userId = userId;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.referenceId = referenceId;
    }
}
