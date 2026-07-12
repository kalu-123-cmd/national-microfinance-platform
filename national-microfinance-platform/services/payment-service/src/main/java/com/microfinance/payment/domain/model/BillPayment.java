package com.microfinance.payment.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "bill_payments")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BillPayment {

    @Id
    private String id;

    @Column(nullable = false)
    private String paymentId;

    @Column(nullable = false)
    private String billerId;

    @Column(nullable = false)
    private String billerName;

    @Column(nullable = false)
    private String billerCode;

    @Column(nullable = false)
    private String billAccountNumber;

    private LocalDate billDueDate;
    private String billPeriod;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
