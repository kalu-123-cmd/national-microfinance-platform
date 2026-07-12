package com.microfinance.cooperative.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name = "contributions")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Contribution {
    @Id private String id;
    @Column(nullable = false) private String cooperativeId;
    @Column(nullable = false) private String memberId;
    @Column(nullable = false) private String userId;
    @Column(nullable = false) private String contributionMonth; // YYYY-MM
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal amount;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private ContributionStatus status;
    private Instant paidAt;
    private String paymentReference;
    private String notes;
    @CreatedDate @Column(nullable = false, updatable = false) private Instant createdAt;
}
