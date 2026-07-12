package com.microfinance.event.user;

import com.microfinance.event.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserSuspendedEvent extends BaseEvent {

    private String userId;
    private String phoneNumber;
    private String email;
    private String reason;

    public UserSuspendedEvent(String userId, String phoneNumber, String email, String reason) {
        super("USER_SUSPENDED", "user-service");
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.reason = reason;
    }
}
