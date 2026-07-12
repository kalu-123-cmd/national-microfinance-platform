package com.microfinance.reporting.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "report_definitions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDefinition {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String type; // FINANCIAL, AUDIT, TRANSACTIONAL, OPERATIONAL

    @Column
    private String templatePath;

    @Column(columnDefinition = "TEXT")
    private String queryConfig;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
