package com.microfinance.payment.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Payment {
    @Id private String id;
    @Column(nullable = false, unique = true) private String reference;
    @Column(nullable = false) private String userId;
    @Column(nullable = false) private String walletId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal amount;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal fee;
    private String currency;
    private String merchantId;
    private String merchantName;
    private String billerId;
    private String billerName;
    private String accountNumber;
    private String description;
    private String channel;
    private String paymentMethod;
    private String providerReference;
    private String callbackUrl;
    @Column(columnDefinition = "TEXT") private String metadata;
    private Instant completedAt;
    private String failureReason;
    private int retryCount;
    private Instant nextRetryAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;
}