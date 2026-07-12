-- ============================================================
-- Audit Service Database Schema
-- V1__init_audit.sql
-- ============================================================

CREATE TABLE IF NOT EXISTS audit_logs (
    id                VARCHAR(36)     PRIMARY KEY,
    service_name      VARCHAR(60)     NOT NULL,
    action            VARCHAR(100)    NOT NULL,
    user_id           VARCHAR(36),
    resource_id       VARCHAR(36),
    resource_type     VARCHAR(60),
    details           TEXT,
    ip_address        VARCHAR(45),
    user_agent        VARCHAR(255),
    status            VARCHAR(20)     NOT NULL DEFAULT 'SUCCESS',
    error_message     TEXT,
    duration_ms       BIGINT,
    timestamp         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_user_id       ON audit_logs(user_id);
CREATE INDEX idx_audit_service_name  ON audit_logs(service_name);
CREATE INDEX idx_audit_action        ON audit_logs(action);
CREATE INDEX idx_audit_status        ON audit_logs(status);
CREATE INDEX idx_audit_timestamp     ON audit_logs(timestamp DESC);
CREATE INDEX idx_audit_resource_id   ON audit_logs(resource_id);
