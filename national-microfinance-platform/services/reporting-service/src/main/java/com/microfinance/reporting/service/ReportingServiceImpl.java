package com.microfinance.reporting.service;

import com.microfinance.reporting.domain.model.ReportDefinition;
import com.microfinance.reporting.domain.model.ReportExecution;
import com.microfinance.reporting.domain.repository.ReportDefinitionRepository;
import com.microfinance.reporting.domain.repository.ReportExecutionRepository;
import com.microfinance.reporting.dto.ExecuteReportRequest;
import com.microfinance.reporting.dto.ReportExecutionResponse;
import com.microfinance.reporting.dto.ReportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportingServiceImpl implements ReportingService {

    private final ReportDefinitionRepository definitionRepository;
    private final ReportExecutionRepository executionRepository;

    @Override
    public List<ReportResponse> getAllReportDefinitions() {
        return definitionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReportResponse getReportDefinition(String id) {
        ReportDefinition def = definitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Report definition not found: " + id));
        return toResponse(def);
    }

    @Override
    @Transactional
    public ReportExecutionResponse executeReport(ExecuteReportRequest request) {
        ReportDefinition def = definitionRepository.findById(request.getReportDefinitionId())
                .orElseThrow(() -> new IllegalArgumentException("Report definition not found"));

        ReportExecution execution = ReportExecution.builder()
                .id(UUID.randomUUID().toString())
                .reportDefinitionId(def.getId())
                .status("PROCESSING")
                .startedAt(Instant.now())
                .build();
        
        execution = executionRepository.save(execution);

        // Async execution would go here (e.g. JasperReports generation)
        // For now, we simulate completion
        
        try {
            // Simulate generation
            Thread.sleep(100);
            execution.setStatus("COMPLETED");
            execution.setFilePath("/reports/" + execution.getId() + ".pdf");
            execution.setCompletedAt(Instant.now());
        } catch (Exception e) {
            execution.setStatus("FAILED");
            execution.setErrorMessage(e.getMessage());
            execution.setCompletedAt(Instant.now());
        }

        execution = executionRepository.save(execution);
        return toExecutionResponse(execution);
    }

    @Override
    public List<ReportExecutionResponse> getReportExecutions(String definitionId) {
        return executionRepository.findByReportDefinitionIdOrderByStartedAtDesc(definitionId).stream()
                .map(this::toExecutionResponse)
                .collect(Collectors.toList());
    }

    private ReportResponse toResponse(ReportDefinition def) {
        return ReportResponse.builder()
                .id(def.getId())
                .name(def.getName())
                .description(def.getDescription())
                .type(def.getType())
                .createdAt(def.getCreatedAt())
                .build();
    }

    private ReportExecutionResponse toExecutionResponse(ReportExecution exec) {
        return ReportExecutionResponse.builder()
                .id(exec.getId())
                .reportDefinitionId(exec.getReportDefinitionId())
                .scheduleId(exec.getScheduleId())
                .status(exec.getStatus())
                .filePath(exec.getFilePath())
                .errorMessage(exec.getErrorMessage())
                .startedAt(exec.getStartedAt())
                .completedAt(exec.getCompletedAt())
                .build();
    }
}
