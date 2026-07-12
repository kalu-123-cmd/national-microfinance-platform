package com.microfinance.voice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ussd_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UssdSession {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String sessionId;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String currentMenu;

    @Column(columnDefinition = "JSONB")
    private String sessionData;

    @Column(nullable = false)
    private String status;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
