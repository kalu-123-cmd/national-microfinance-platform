package com.microfinance.offline.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "sync_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncRequest {
    @Id
    private String id;

    @Column(nullable = false)
    private String deviceId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private String entityId;

    @Column(nullable = false)
    private String operation;

    @Column(columnDefinition = "JSONB")
    private String payload;

    @Column(nullable = false)
    private Long clientTimestamp;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant serverTimestamp;

    @Column(nullable = false)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column
    private Integer retryCount;
}
