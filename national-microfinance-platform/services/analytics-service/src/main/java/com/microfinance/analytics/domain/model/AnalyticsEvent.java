package com.microfinance.analytics.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Document(collection = "analytics_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsEvent {
    @Id
    private String id;
    private String eventType;
    private String entityType;
    private String entityId;
    private String userId;
    private String region;
    private BigDecimal amount;
    private String currency;
    private String channel;
    private String status;
    private Instant eventTime;
    private Map<String, Object> metadata;
}