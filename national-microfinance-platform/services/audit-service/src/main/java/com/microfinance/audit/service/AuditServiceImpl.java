package com.microfinance.audit.service;

import com.microfinance.audit.domain.model.AuditLog;
import com.microfinance.audit.domain.repository.AuditLogRepository;
import com.microfinance.audit.dto.AuditLogResponse;
import com.microfinance.audit.dto.AuditSearchRequest;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public Page<AuditLogResponse> searchAuditLogs(AuditSearchRequest request, Pageable pageable) {
        Specification<AuditLog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (request.getServiceName() != null && !request.getServiceName().isEmpty()) {
                predicates.add(cb.equal(root.get("serviceName"), request.getServiceName()));
            }
            if (request.getAction() != null && !request.getAction().isEmpty()) {
                predicates.add(cb.equal(root.get("action"), request.getAction()));
            }
            if (request.getUserId() != null && !request.getUserId().isEmpty()) {
                predicates.add(cb.equal(root.get("userId"), request.getUserId()));
            }
            if (request.getStatus() != null && !request.getStatus().isEmpty()) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }
            if (request.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("timestamp"), request.getFromDate()));
            }
            if (request.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("timestamp"), request.getToDate()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return auditLogRepository.findAll(spec, pageable).map(this::toResponse);
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .serviceName(log.getServiceName())
                .action(log.getAction())
                .userId(log.getUserId())
                .resourceId(log.getResourceId())
                .details(log.getDetails())
                .ipAddress(log.getIpAddress())
                .status(log.getStatus())
                .errorMessage(log.getErrorMessage())
                .timestamp(log.getTimestamp())
                .build();
    }
}
