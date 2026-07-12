package com.microfinance.voice.dto;

import lombok.Data;

@Data
public class UssdRequest {
    private String sessionId;
    private String serviceCode;
    private String phoneNumber;
    private String text; // The user input (e.g. "1*2")
}
