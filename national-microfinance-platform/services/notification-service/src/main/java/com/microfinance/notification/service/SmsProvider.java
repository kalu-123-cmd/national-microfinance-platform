package com.microfinance.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmsProvider {

    @Value("${sms.provider.url:https://api.africas-talking.com/sms}")
    private String providerUrl;

    @Value("${sms.provider.api-key:dev-key}")
    private String apiKey;

    @Value("${sms.sender-id:MICROFINANCE}")
    private String senderId;

    public void send(String phone, String message) {
        // Production: integrate with Africa's Talking, Twilio, or local Ethiopian telco API
        log.info("[SMS-STUB] To: {} | Message: {}", phone, message.substring(0, Math.min(50, message.length())) + "...");
        // TODO: HTTP call to SMS provider
    }
}