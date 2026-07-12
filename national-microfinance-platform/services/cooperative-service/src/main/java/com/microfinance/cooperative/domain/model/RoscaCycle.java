package com.microfinance.cooperative.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name = "rosca_cycles")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class RoscaCycle {
    @Id private String id;
    @Column(nullable = false) private String cooperativeId;
    @Column(nullable = false) private int cycleNumber;
    @Column(nullable = false) private String beneficiaryUserId;
    @Column(nullable = false) private String beneficiaryMemberId;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal potAmount;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private RoscaStatus status;
    @Column(nullable = false) private LocalDate scheduledDate;
    private Instant disbursedAt;
    @Column(precision = 19, scale = 2) private BigDecimal disbursedAmount;
    private String notes;
    @CreatedDate @Column(nullable = false, updatable = false) private Instant createdAt;
}
