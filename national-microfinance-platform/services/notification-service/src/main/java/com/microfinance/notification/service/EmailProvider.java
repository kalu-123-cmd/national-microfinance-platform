package com.microfinance.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailProvider {

    @Value("${email.from:noreply@microfinance.et}")
    private String fromEmail;

    public void send(String to, String subject, String body) {
        // Production: use Spring Mail / AWS SES / SendGrid
        log.info("[EMAIL-STUB] To: {} | Subject: {}", to, subject);
        // TODO: SMTP/SES send call
    }
}