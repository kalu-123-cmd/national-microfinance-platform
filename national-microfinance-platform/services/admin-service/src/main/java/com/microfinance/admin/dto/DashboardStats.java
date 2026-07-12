package com.microfinance.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class DashboardStats {
    private long totalAdminUsers;
    private long activeAdminUsers;
    private long totalSystemConfigs;
    private long totalAuditActions;
    private Map<String, Long> configsByCategory;
    private Map<String, Long> adminUsersByRole;
}
