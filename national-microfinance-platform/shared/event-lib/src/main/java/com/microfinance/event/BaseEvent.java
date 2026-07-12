package com.microfinance.event;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor
public abstract class BaseEvent {
    private String eventId;
    private String eventType;
    private String sourceService;
    private Instant occurredAt;
    private String correlationId;

    protected BaseEvent(String eventType, String sourceService) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.sourceService = sourceService;
        this.occurredAt = Instant.now();
    }
}
