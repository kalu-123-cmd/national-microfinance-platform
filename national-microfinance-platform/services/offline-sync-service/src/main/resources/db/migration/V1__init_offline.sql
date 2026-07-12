-- ============================================================
-- Offline Sync Service Database Schema
-- V1__init_offline.sql
-- ============================================================

CREATE TABLE IF NOT EXISTS sync_requests (
    id VARCHAR(36) PRIMARY KEY,
    device_id VARCHAR(100) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(100) NOT NULL,
    operation VARCHAR(20) NOT NULL, -- CREATE, UPDATE, DELETE
    payload JSONB,
    client_timestamp BIGINT NOT NULL,
    server_timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    status VARCHAR(20) NOT NULL, -- PENDING, PROCESSED, CONFLICT, FAILED
    error_message TEXT,
    retry_count INT DEFAULT 0
);

CREATE INDEX idx_sync_device ON sync_requests(device_id);
CREATE INDEX idx_sync_user ON sync_requests(user_id);
CREATE INDEX idx_sync_status ON sync_requests(status);
