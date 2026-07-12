package com.microfinance.admin.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "admin_users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String userId; // reference to auth-service userId

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String role; // SUPER_ADMIN, ADMIN, LOAN_OFFICER, COMPLIANCE_OFFICER, SUPPORT

    private String department;
    private boolean active = true;
    private Instant lastLoginAt;

    @Column(updatable = false)
    private Instant createdAt = Instant.now();

    private String createdBy;
}
