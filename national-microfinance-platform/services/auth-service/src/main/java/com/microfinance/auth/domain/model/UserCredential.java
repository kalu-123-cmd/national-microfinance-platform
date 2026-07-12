package com.microfinance.auth.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_credentials",
    indexes = {
        @Index(name = "idx_uc_phone",   columnList = "phone_number", unique = true),
        @Index(name = "idx_uc_user_id", columnList = "user_id",      unique = true),
        @Index(name = "idx_uc_email",   columnList = "email",        unique = true)
    })
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserCredential {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @PrePersist
    private void generateId() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
    }

    @Column(name = "user_id",       nullable = false, unique = true) private String userId;
    @Column(name = "phone_number",  nullable = false, unique = true, length = 20) private String phoneNumber;
    @Column(name = "email",         unique = true) private String email;
    @Column(name = "password_hash") private String passwordHash;
    @Column(name = "pin_hash",      nullable = false) private String pinHash;

    @Column(name = "enabled", nullable = false)
    @Builder.Default private boolean enabled = true;

    @Column(name = "failed_login_attempts", nullable = false)
    @Builder.Default private int failedLoginAttempts = 0;

    @Column(name = "last_failed_login_at") private Instant lastFailedLoginAt;
    @Column(name = "account_locked_until") private Instant accountLockedUntil;
    @Column(name = "last_login_at")        private Instant lastLoginAt;
    @Column(name = "last_login_ip", length = 45) private String lastLoginIp;
    @Column(name = "last_device_id", length = 255) private String lastDeviceId;
    @Column(name = "pin_changed_at")  private Instant pinChangedAt;

    @CreationTimestamp @Column(name = "created_at", updatable = false) private Instant createdAt;
    @UpdateTimestamp   @Column(name = "updated_at") private Instant updatedAt;

    public boolean isAccountLocked() {
        return accountLockedUntil != null && Instant.now().isBefore(accountLockedUntil);
    }
}
