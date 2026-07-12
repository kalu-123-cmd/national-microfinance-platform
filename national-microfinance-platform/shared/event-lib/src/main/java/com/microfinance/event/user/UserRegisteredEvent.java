package com.microfinance.event.user;

import com.microfinance.event.BaseEvent;
import lombok.*;

@Data @NoArgsConstructor @EqualsAndHashCode(callSuper = true)
public class UserRegisteredEvent extends BaseEvent {
    private String userId;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String region;
    private String userType;

    public UserRegisteredEvent(String userId, String phone, String firstName, String lastName) {
        super("USER_REGISTERED", "user-service");
        this.userId = userId; this.phoneNumber = phone;
        this.firstName = firstName; this.lastName = lastName;
    }
}
