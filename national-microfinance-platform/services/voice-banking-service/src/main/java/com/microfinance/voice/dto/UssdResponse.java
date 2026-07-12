package com.microfinance.voice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UssdResponse {
    private String message; // The text to show on screen
    private boolean endSession; // If true, session ends (e.g., END or CON in some USSD APIs)
}
