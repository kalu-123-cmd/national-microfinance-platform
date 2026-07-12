package com.microfinance.payment.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "merchant_payments")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MerchantPayment {

    @Id
    private String id;

    @Column(nullable = false)
    private String paymentId;

    @Column(nullable = false)
    private String merchantId;

    @Column(nullable = false)
    private String merchantName;

    private String merchantCategory;
    private String terminalId;
    private String qrCodeRef;
    private String posEntryMode;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
