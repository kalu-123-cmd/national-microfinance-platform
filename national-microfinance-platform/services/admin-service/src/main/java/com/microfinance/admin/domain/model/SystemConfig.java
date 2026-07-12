package com.microfinance.admin.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "system_configs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String configKey;

    @Column(nullable = false)
    private String configValue;

    private String description;
    private String category; // LIMITS, FEES, FEATURES, SECURITY, etc.
    private String dataType; // STRING, NUMBER, BOOLEAN, JSON
    private boolean modifiable = true;
    
    @Column(updatable = false)
    private Instant createdAt = Instant.now();
    
    private Instant updatedAt = Instant.now();
    private String updatedBy;
}
