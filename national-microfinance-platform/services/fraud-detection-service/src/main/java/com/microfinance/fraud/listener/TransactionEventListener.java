package com.microfinance.fraud.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.fraud.dto.FraudCheckRequest;
import com.microfinance.fraud.service.FraudDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventListener {

    private final FraudDetectionService fraudDetectionService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "transaction-events", groupId = "fraud-group")
    public void handleTransactionEvent(String message) {
        log.info("Received transaction event for fraud analysis: {}", message);
        try {
            Map<String, Object> event = objectMapper.readValue(message, Map.class);
            
            // Transform event to FraudCheckRequest
            FraudCheckRequest request = FraudCheckRequest.builder()
                    .transactionId(String.valueOf(event.get("transactionId")))
                    .userId(String.valueOf(event.get("userId")))
                    .amount(new BigDecimal(String.valueOf(event.get("amount"))))
                    .currency(String.valueOf(event.get("currency")))
                    .transactionType(String.valueOf(event.get("type")))
                    .build();

            fraudDetectionService.evaluateTransactionAsync(request);
            
        } catch (Exception e) {
            log.error("Error processing transaction event for fraud detection", e);
        }
    }
}
