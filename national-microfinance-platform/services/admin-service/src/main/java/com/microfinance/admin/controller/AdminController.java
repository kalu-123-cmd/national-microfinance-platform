package com.microfinance.admin.controller;

import com.microfinance.admin.domain.model.*;
import com.microfinance.admin.dto.*;
import com.microfinance.admin.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ── Dashboard ──────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStats> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    // ── System Config ──────────────────────────────────────────────────────────

    @GetMapping("/configs")
    public ResponseEntity<List<SystemConfig>> getAllConfigs(
            @RequestParam(required = false) String category) {
        if (category != null) return ResponseEntity.ok(adminService.getConfigsByCategory(category));
        return ResponseEntity.ok(adminService.getAllConfigs());
    }

    @GetMapping("/configs/{key}")
    public ResponseEntity<Map<String, String>> getConfigValue(@PathVariable String key) {
        return ResponseEntity.ok(Map.of("key", key, "value", adminService.getConfigValue(key)));
    }

    @PostMapping("/configs")
    public ResponseEntity<SystemConfig> createConfig(
            @Valid @RequestBody ConfigRequest req,
            @RequestHeader("X-User-Id") String adminId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createConfig(req, adminId));
    }

    @PutMapping("/configs/{key}")
    public ResponseEntity<SystemConfig> updateConfig(
            @PathVariable String key,
            @RequestBody Map<String, String> body,
            @RequestHeader("X-User-Id") String adminId) {
        return ResponseEntity.ok(adminService.updateConfig(key, body.get("value"), adminId));
    }

    // ── Admin Users ────────────────────────────────────────────────────────────

    @GetMapping("/users")
    public ResponseEntity<List<AdminUser>> getAdminUsers(
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        if (activeOnly) return ResponseEntity.ok(adminService.getActiveAdminUsers());
        return ResponseEntity.ok(adminService.getAllAdminUsers());
    }

    @PostMapping("/users")
    public ResponseEntity<AdminUser> createAdminUser(
            @Valid @RequestBody AdminUserRequest req,
            @RequestHeader("X-User-Id") String createdBy) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createAdminUser(req, createdBy));
    }

    @PutMapping("/users/{adminUserId}/role")
    public ResponseEntity<AdminUser> updateRole(
            @PathVariable String adminUserId,
            @RequestBody Map<String, String> body,
            @RequestHeader("X-User-Id") String updatedBy) {
        return ResponseEntity.ok(adminService.updateAdminUserRole(adminUserId, body.get("role"), updatedBy));
    }

    @PutMapping("/users/{adminUserId}/toggle")
    public ResponseEntity<AdminUser> toggle(
            @PathVariable String adminUserId,
            @RequestBody Map<String, Boolean> body,
            @RequestHeader("X-User-Id") String updatedBy) {
        return ResponseEntity.ok(adminService.toggleAdminUser(adminUserId, body.get("active"), updatedBy));
    }

    // ── Audit Trail ────────────────────────────────────────────────────────────

    @GetMapping("/audit")
    public ResponseEntity<Page<AuditAction>> getAuditTrail(Pageable pageable) {
        return ResponseEntity.ok(adminService.getAuditTrail(pageable));
    }

    @GetMapping("/audit/{adminUserId}")
    public ResponseEntity<Page<AuditAction>> getAuditByAdmin(
            @PathVariable String adminUserId, Pageable pageable) {
        return ResponseEntity.ok(adminService.getAuditByAdmin(adminUserId, pageable));
    }
}
