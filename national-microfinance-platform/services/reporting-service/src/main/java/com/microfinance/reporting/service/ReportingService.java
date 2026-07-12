package com.microfinance.reporting.service;

import com.microfinance.reporting.dto.ExecuteReportRequest;
import com.microfinance.reporting.dto.ReportExecutionResponse;
import com.microfinance.reporting.dto.ReportResponse;

import java.util.List;

public interface ReportingService {
    List<ReportResponse> getAllReportDefinitions();
    ReportResponse getReportDefinition(String id);
    ReportExecutionResponse executeReport(ExecuteReportRequest request);
    List<ReportExecutionResponse> getReportExecutions(String definitionId);
}
