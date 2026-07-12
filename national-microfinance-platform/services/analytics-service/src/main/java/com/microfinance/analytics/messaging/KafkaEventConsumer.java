package com.microfinance.analytics.messaging;

import com.microfinance.analytics.domain.model.AnalyticsEvent;
import com.microfinance.analytics.domain.repository.AnalyticsEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventConsumer {

    private final AnalyticsEventRepository eventRepository;

    @KafkaListener(topics = {"user-events", "transaction-events", "loan-events", "savings-events"}, groupId = "analytics-service-group")
    public void consumeEvent(Map<String, Object> payload) {
        log.info("Received event for analytics: {}", payload);
        
        try {
            String eventType = (String) payload.getOrDefault("eventType", "UNKNOWN");
            String userId = (String) payload.get("userId");
            String entityId = (String) payload.get("entityId");
            String region = (String) payload.getOrDefault("region", "UNKNOWN");
            
            Object amountObj = payload.get("amount");
            BigDecimal amount = null;
            if (amountObj != null) {
                amount = new BigDecimal(amountObj.toString());
            }

            AnalyticsEvent event = AnalyticsEvent.builder()
                    .id(UUID.randomUUID().toString())
                    .eventType(eventType)
                    .userId(userId)
                    .entityId(entityId)
                    .amount(amount)
                    .region(region)
                    .eventTime(Instant.now())
                    .metadata(payload)
                    .build();

            eventRepository.save(event);
            log.debug("Saved analytics event: {}", eventType);
        } catch (Exception e) {
            log.error("Error processing analytics event", e);
        }
    }
}
