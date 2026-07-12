package com.microfinance.analytics.domain.repository;

import com.microfinance.analytics.domain.model.AnalyticsEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AnalyticsEventRepository extends MongoRepository<AnalyticsEvent, String> {
    List<AnalyticsEvent> findByEventType(String eventType);
    List<AnalyticsEvent> findByEventTimeBetween(Instant from, Instant to);
    List<AnalyticsEvent> findByRegion(String region);
}