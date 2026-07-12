package com.microfinance.event.notification;

import com.microfinance.event.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NotificationSmsEvent extends BaseEvent {

    private String userId;
    private String recipient;
    private String body;
    private String referenceId;

    public NotificationSmsEvent(String userId, String recipient, String body, String referenceId) {
        super("NOTIFY_SMS", "auth-service");
        this.userId = userId;
        this.recipient = recipient;
        this.body = body;
        this.referenceId = referenceId;
    }
}
