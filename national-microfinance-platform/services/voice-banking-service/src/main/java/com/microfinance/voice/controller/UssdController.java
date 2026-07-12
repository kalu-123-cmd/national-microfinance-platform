package com.microfinance.voice.controller;

import com.microfinance.voice.dto.UssdRequest;
import com.microfinance.voice.dto.UssdResponse;
import com.microfinance.voice.service.UssdMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ussd")
@RequiredArgsConstructor
public class UssdController {

    private final UssdMenuService ussdMenuService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "voice-banking-service"));
    }

    // Telco networks typically send requests either as JSON POST or URL-encoded GET/POST.
    @PostMapping("/callback")
    public ResponseEntity<String> handleUssdCallback(
            @RequestParam String sessionId,
            @RequestParam String serviceCode,
            @RequestParam String phoneNumber,
            @RequestParam String text) {
        
        UssdRequest request = new UssdRequest();
        request.setSessionId(sessionId);
        request.setServiceCode(serviceCode);
        request.setPhoneNumber(phoneNumber);
        request.setText(text);

        UssdResponse response = ussdMenuService.handleUssdRequest(request);
        
        // Format for Africa's Talking or similar gateways
        String responseText = (response.isEndSession() ? "END " : "CON ") + response.getMessage();
        return ResponseEntity.ok(responseText);
    }
}
