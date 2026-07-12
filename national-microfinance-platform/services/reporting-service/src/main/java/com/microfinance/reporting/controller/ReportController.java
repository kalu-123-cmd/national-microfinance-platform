package com.microfinance.reporting.controller;

import com.microfinance.reporting.dto.ExecuteReportRequest;
import com.microfinance.reporting.dto.ReportExecutionResponse;
import com.microfinance.reporting.dto.ReportResponse;
import com.microfinance.reporting.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportingService reportingService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "reporting-service"));
    }

    @GetMapping("/definitions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReportResponse>> getReportDefinitions() {
        return ResponseEntity.ok(reportingService.getAllReportDefinitions());
    }

    @GetMapping("/definitions/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReportResponse> getReportDefinition(@PathVariable String id) {
        return ResponseEntity.ok(reportingService.getReportDefinition(id));
    }

    @PostMapping("/execute")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('REPORT_ADMIN')")
    public ResponseEntity<ReportExecutionResponse> executeReport(@RequestBody ExecuteReportRequest request) {
        return ResponseEntity.ok(reportingService.executeReport(request));
    }

    @GetMapping("/definitions/{id}/executions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReportExecutionResponse>> getReportExecutions(@PathVariable String id) {
        return ResponseEntity.ok(reportingService.getReportExecutions(id));
    }
}
