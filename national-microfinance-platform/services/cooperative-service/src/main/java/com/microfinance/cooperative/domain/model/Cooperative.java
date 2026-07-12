package com.microfinance.cooperative.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name = "cooperatives")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Cooperative {
    @Id private String id;
    @Column(nullable = false) private String name;
    @Column(nullable = false, unique = true) private String registrationNumber;
    private String description;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private CooperativeType type;
    @Column(nullable = false) private String adminUserId;
    private int maxMembers;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal membershipFee;
    @Column(precision = 19, scale = 2) private BigDecimal monthlyContribution;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal totalPoolBalance;
    @Column(nullable = false, precision = 6, scale = 4) private BigDecimal loanInterestRate;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private CooperativeStatus status;
    private String location;
    private String phone;
    @CreatedDate @Column(nullable = false, updatable = false) private Instant createdAt;
    @LastModifiedDate @Column(nullable = false) private Instant updatedAt;
}
