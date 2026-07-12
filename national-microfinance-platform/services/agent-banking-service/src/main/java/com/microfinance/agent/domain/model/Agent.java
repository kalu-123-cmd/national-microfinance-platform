package com.microfinance.agent.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "agents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agent {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String businessName;

    @Column(nullable = false, unique = true)
    private String businessId;

    @Column(nullable = false)
    private String phoneNumber;

    @Column
    private String email;

    @Column(nullable = false)
    private String region;

    @Column
    private String woreda;

    @Column
    private String kebele;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(nullable = false)
    private String status;

    @Column
    private BigDecimal dailyLimit;

    @Column
    private BigDecimal monthlyLimit;

    @Column
    private BigDecimal dailyProcessed;

    @Column
    private BigDecimal monthlyProcessed;

    @Column
    private BigDecimal balance;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}