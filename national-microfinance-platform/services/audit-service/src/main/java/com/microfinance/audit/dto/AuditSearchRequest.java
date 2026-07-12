package com.microfinance.audit.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class AuditSearchRequest {
    private String serviceName;
    private String action;
    private String userId;
    private String status;
    private Instant fromDate;
    private Instant toDate;
}
