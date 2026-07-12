package com.microfinance.auth.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Entity
@Table(name = "otp_records",
    indexes = {
        @Index(name = "idx_otp_recipient_purpose", columnList = "recipient,purpose"),
        @Index(name = "idx_otp_expires",           columnList = "expires_at")
    })
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OtpRecord {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @PrePersist
    private void generateId() {
        if (this.id == null) this.id = java.util.UUID.randomUUID().toString();
    }

    @Column(name = "recipient", nullable = false, length = 255)
    private String recipient;

    @Column(name = "otp_hash", nullable = false)
    private String otpHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OtpPurpose purpose;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "verified", nullable = false)
    @Builder.Default private boolean verified = false;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(name = "verification_attempts", nullable = false)
    @Builder.Default private int verificationAttempts = 0;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    public boolean isExpired() { return Instant.now().isAfter(expiresAt); }
    public boolean isValid()   { return !verified && !isExpired() && verificationAttempts < 3; }
}
