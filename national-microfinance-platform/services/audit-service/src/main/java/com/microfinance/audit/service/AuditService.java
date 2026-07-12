package com.microfinance.audit.service;

import com.microfinance.audit.dto.AuditLogResponse;
import com.microfinance.audit.dto.AuditSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditService {
    Page<AuditLogResponse> searchAuditLogs(AuditSearchRequest request, Pageable pageable);
}
