package com.microfinance.auth.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Entity
@Table(name = "refresh_tokens",
    indexes = {
        @Index(name = "idx_rt_token_hash", columnList = "token_hash", unique = true),
        @Index(name = "idx_rt_user",       columnList = "user_id"),
        @Index(name = "idx_rt_expires",    columnList = "expires_at")
    })
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RefreshToken {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @PrePersist
    private void generateId() {
        if (this.id == null) this.id = java.util.UUID.randomUUID().toString();
    }
    @Column(name = "user_id", nullable = false) private String userId;
    @Column(name = "token_hash", nullable = false, unique = true) private String tokenHash;
    @Column(name = "device_id", length = 255) private String deviceId;
    @Column(name = "device_info", length = 500) private String deviceInfo;
    @Column(name = "ip_address", length = 45) private String ipAddress;
    @Column(name = "expires_at", nullable = false) private Instant expiresAt;
    @Column(nullable = false) @Builder.Default private boolean revoked = false;
    @Column(name = "revoked_at") private Instant revokedAt;
    @Column(name = "revocation_reason", length = 100) private String revocationReason;
    @CreationTimestamp @Column(name = "created_at", updatable = false) private Instant createdAt;

    public boolean isExpired() { return Instant.now().isAfter(expiresAt); }
    public boolean isValid()   { return !revoked && !isExpired(); }
    public void revoke(String reason) {
        this.revoked = true;
        this.revokedAt = Instant.now();
        this.revocationReason = reason;
    }
}
