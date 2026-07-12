package com.microfinance.audit.controller;

import com.microfinance.audit.dto.AuditLogResponse;
import com.microfinance.audit.dto.AuditSearchRequest;
import com.microfinance.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "audit-service"));
    }

    @PostMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('AUDIT_ADMIN')")
    public ResponseEntity<Page<AuditLogResponse>> searchAuditLogs(
            @RequestBody AuditSearchRequest request,
            Pageable pageable) {
        return ResponseEntity.ok(auditService.searchAuditLogs(request, pageable));
    }
}
