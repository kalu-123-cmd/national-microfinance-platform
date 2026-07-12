package com.microfinance.reporting.domain.repository;

import com.microfinance.reporting.domain.model.ReportSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportScheduleRepository extends JpaRepository<ReportSchedule, String> {
    List<ReportSchedule> findByStatus(String status);
}
