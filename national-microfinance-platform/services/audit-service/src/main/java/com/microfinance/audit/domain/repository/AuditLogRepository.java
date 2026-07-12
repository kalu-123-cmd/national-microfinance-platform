package com.microfinance.audit.domain.repository;

import com.microfinance.audit.domain.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, String>, JpaSpecificationExecutor<AuditLog> {
    List<AuditLog> findByUserIdOrderByTimestampDesc(String userId);
    List<AuditLog> findByServiceNameOrderByTimestampDesc(String serviceName);
    List<AuditLog> findByTimestampBetweenOrderByTimestampDesc(Instant start, Instant end);
}
