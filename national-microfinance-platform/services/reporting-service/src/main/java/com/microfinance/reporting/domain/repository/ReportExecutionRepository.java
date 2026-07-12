package com.microfinance.reporting.domain.repository;

import com.microfinance.reporting.domain.model.ReportExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportExecutionRepository extends JpaRepository<ReportExecution, String> {
    List<ReportExecution> findByReportDefinitionIdOrderByStartedAtDesc(String reportDefinitionId);
}
