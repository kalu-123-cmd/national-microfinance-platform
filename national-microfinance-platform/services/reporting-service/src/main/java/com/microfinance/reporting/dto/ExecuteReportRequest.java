package com.microfinance.reporting.dto;

import lombok.Data;
import java.util.Map;

@Data
public class ExecuteReportRequest {
    private String reportDefinitionId;
    private Map<String, Object> parameters;
}
