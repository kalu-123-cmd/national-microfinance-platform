package com.microfinance.admin.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "admin_audit_actions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditAction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String adminUserId;

    @Column(nullable = false)
    private String action; // CREATE_CONFIG, UPDATE_CONFIG, DISABLE_USER, APPROVE_LOAN, etc.

    private String targetEntityType;
    private String targetEntityId;

    @Column(columnDefinition = "TEXT")
    private String details;

    private String ipAddress;

    @Column(nullable = false, updatable = false)
    private Instant performedAt = Instant.now();
}
