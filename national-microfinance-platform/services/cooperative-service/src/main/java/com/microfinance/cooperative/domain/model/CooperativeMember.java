package com.microfinance.cooperative.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name = "cooperative_members")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CooperativeMember {
    @Id private String id;
    @Column(nullable = false) private String cooperativeId;
    @Column(nullable = false) private String userId;
    @Column(nullable = false, unique = true) private String memberNumber;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private MemberRole role;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private MemberStatus status;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal totalContributed;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal totalWithdrawn;
    @Column(nullable = false) private LocalDate joinDate;
    private LocalDate exitDate;
    private String exitReason;
    @CreatedDate @Column(nullable = false, updatable = false) private Instant createdAt;
}
