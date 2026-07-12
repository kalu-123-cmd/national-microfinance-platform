package com.microfinance.reporting.domain.repository;

import com.microfinance.reporting.domain.model.ReportDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportDefinitionRepository extends JpaRepository<ReportDefinition, String> {
}
