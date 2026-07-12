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
@Table(name = "report_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportSchedule {
    @Id
    private String id;

    @Column(name = "report_definition_id", nullable = false)
    private String reportDefinitionId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String cronExpression;

    @Column(columnDefinition = "JSONB")
    private String parameters;

    @Column(columnDefinition = "TEXT")
    private String recipients; // comma separated emails

    @Column(nullable = false)
    private String status;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
