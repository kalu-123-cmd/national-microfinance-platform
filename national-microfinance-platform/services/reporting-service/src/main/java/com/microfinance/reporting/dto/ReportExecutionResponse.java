package com.microfinance.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportExecutionResponse {
    private String id;
    private String reportDefinitionId;
    private String scheduleId;
    private String status;
    private String filePath;
    private String errorMessage;
    private Instant startedAt;
    private Instant completedAt;
}
