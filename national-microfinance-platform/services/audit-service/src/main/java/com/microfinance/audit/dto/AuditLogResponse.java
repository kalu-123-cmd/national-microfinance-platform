package com.microfinance.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {
    private String id;
    private String serviceName;
    private String action;
    private String userId;
    private String resourceId;
    private String details;
    private String ipAddress;
    private String status;
    private String errorMessage;
    private Instant timestamp;
}
