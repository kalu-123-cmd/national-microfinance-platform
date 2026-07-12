package com.microfinance.admin.service;

import com.microfinance.admin.domain.model.*;
import com.microfinance.admin.domain.repository.*;
import com.microfinance.admin.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final SystemConfigRepository configRepository;
    private final AdminUserRepository adminUserRepository;
    private final AuditActionRepository auditActionRepository;

    // ── System Config ──────────────────────────────────────────────────────────

    @Transactional
    public SystemConfig createConfig(ConfigRequest req, String createdBy) {
        if (configRepository.existsByConfigKey(req.getConfigKey())) {
            throw new IllegalArgumentException("Config key already exists: " + req.getConfigKey());
        }
        SystemConfig config = SystemConfig.builder()
                .configKey(req.getConfigKey())
                .configValue(req.getConfigValue())
                .description(req.getDescription())
                .category(req.getCategory() != null ? req.getCategory() : "GENERAL")
                .dataType(req.getDataType() != null ? req.getDataType() : "STRING")
                .modifiable(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .updatedBy(createdBy)
                .build();
        config = configRepository.save(config);
        recordAudit(createdBy, "CREATE_CONFIG", "SystemConfig", config.getId(),
                "Created config: " + req.getConfigKey() + " = " + req.getConfigValue());
        log.info("Config created: {} by {}", req.getConfigKey(), createdBy);
        return config;
    }

    @Transactional
    public SystemConfig updateConfig(String configKey, String newValue, String updatedBy) {
        SystemConfig config = configRepository.findByConfigKey(configKey)
                .orElseThrow(() -> new IllegalArgumentException("Config not found: " + configKey));
        if (!config.isModifiable()) {
            throw new IllegalStateException("Config '" + configKey + "' is not modifiable");
        }
        String oldValue = config.getConfigValue();
        config.setConfigValue(newValue);
        config.setUpdatedAt(Instant.now());
        config.setUpdatedBy(updatedBy);
        config = configRepository.save(config);
        recordAudit(updatedBy, "UPDATE_CONFIG", "SystemConfig", config.getId(),
                "Updated " + configKey + ": " + oldValue + " → " + newValue);
        return config;
    }

    public List<SystemConfig> getAllConfigs() {
        return configRepository.findAll();
    }

    public List<SystemConfig> getConfigsByCategory(String category) {
        return configRepository.findByCategory(category);
    }

    public String getConfigValue(String configKey) {
        return configRepository.findByConfigKey(configKey)
                .map(SystemConfig::getConfigValue)
                .orElseThrow(() -> new IllegalArgumentException("Config not found: " + configKey));
    }

    // ── Admin Users ────────────────────────────────────────────────────────────

    @Transactional
    public AdminUser createAdminUser(AdminUserRequest req, String createdBy) {
        if (adminUserRepository.findByUserId(req.getUserId()).isPresent()) {
            throw new IllegalArgumentException("Admin user already exists for userId: " + req.getUserId());
        }
        AdminUser adminUser = AdminUser.builder()
                .userId(req.getUserId())
                .username(req.getUsername())
                .role(req.getRole())
                .department(req.getDepartment())
                .active(true)
                .createdAt(Instant.now())
                .createdBy(createdBy)
                .build();
        adminUser = adminUserRepository.save(adminUser);
        recordAudit(createdBy, "CREATE_ADMIN_USER", "AdminUser", adminUser.getId(),
                "Created admin user " + req.getUsername() + " with role " + req.getRole());
        log.info("Admin user created: {} ({})", req.getUsername(), req.getRole());
        return adminUser;
    }

    @Transactional
    public AdminUser updateAdminUserRole(String adminUserId, String newRole, String updatedBy) {
        AdminUser adminUser = adminUserRepository.findById(adminUserId)
                .orElseThrow(() -> new IllegalArgumentException("Admin user not found: " + adminUserId));
        String oldRole = adminUser.getRole();
        adminUser.setRole(newRole);
        adminUser = adminUserRepository.save(adminUser);
        recordAudit(updatedBy, "UPDATE_ADMIN_ROLE", "AdminUser", adminUserId,
                "Role changed: " + oldRole + " → " + newRole);
        return adminUser;
    }

    @Transactional
    public AdminUser toggleAdminUser(String adminUserId, boolean active, String updatedBy) {
        AdminUser adminUser = adminUserRepository.findById(adminUserId)
                .orElseThrow(() -> new IllegalArgumentException("Admin user not found: " + adminUserId));
        adminUser.setActive(active);
        adminUser = adminUserRepository.save(adminUser);
        recordAudit(updatedBy, active ? "ENABLE_ADMIN_USER" : "DISABLE_ADMIN_USER",
                "AdminUser", adminUserId, (active ? "Enabled" : "Disabled") + " admin user " + adminUser.getUsername());
        return adminUser;
    }

    public List<AdminUser> getAllAdminUsers() {
        return adminUserRepository.findAll();
    }

    public List<AdminUser> getActiveAdminUsers() {
        return adminUserRepository.findByActiveTrue();
    }

    // ── Audit Trail ────────────────────────────────────────────────────────────

    public Page<AuditAction> getAuditTrail(Pageable pageable) {
        return auditActionRepository.findAll(pageable);
    }

    public Page<AuditAction> getAuditByAdmin(String adminUserId, Pageable pageable) {
        return auditActionRepository.findByAdminUserId(adminUserId, pageable);
    }

    // ── Dashboard ──────────────────────────────────────────────────────────────

    public DashboardStats getDashboardStats() {
        List<AdminUser> allAdmins = adminUserRepository.findAll();
        List<SystemConfig> allConfigs = configRepository.findAll();

        Map<String, Long> configsByCategory = allConfigs.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getCategory() != null ? c.getCategory() : "GENERAL",
                        Collectors.counting()));

        Map<String, Long> adminsByRole = allAdmins.stream()
                .collect(Collectors.groupingBy(AdminUser::getRole, Collectors.counting()));

        return DashboardStats.builder()
                .totalAdminUsers(allAdmins.size())
                .activeAdminUsers(allAdmins.stream().filter(AdminUser::isActive).count())
                .totalSystemConfigs(allConfigs.size())
                .totalAuditActions(auditActionRepository.count())
                .configsByCategory(configsByCategory)
                .adminUsersByRole(adminsByRole)
                .build();
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private void recordAudit(String adminUserId, String action, String entityType,
                              String entityId, String details) {
        AuditAction audit = AuditAction.builder()
                .adminUserId(adminUserId)
                .action(action)
                .targetEntityType(entityType)
                .targetEntityId(entityId)
                .details(details)
                .performedAt(Instant.now())
                .build();
        auditActionRepository.save(audit);
    }
}
