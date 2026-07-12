package com.microfinance.reporting.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "report_executions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportExecution {
    @Id
    private String id;

    @Column(name = "schedule_id")
    private String scheduleId;

    @Column(name = "report_definition_id", nullable = false)
    private String reportDefinitionId;

    @Column(nullable = false)
    private String status;

    @Column
    private String filePath;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant startedAt;

    @Column
    private Instant completedAt;
}
