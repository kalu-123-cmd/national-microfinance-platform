package com.microfinance.credit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "credit_scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private Integer score; // e.g., 300 to 850

    @Column(nullable = false)
    private String rating; // POOR, FAIR, GOOD, EXCELLENT

    @Column(nullable = false)
    private Double defaultProbability; // Percentage 0.0 to 100.0

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
