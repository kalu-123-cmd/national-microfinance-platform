-- ============================================================
-- Voice Banking Service Database Schema
-- V1__init_voice.sql
-- ============================================================

CREATE TABLE IF NOT EXISTS ussd_sessions (
    id VARCHAR(36) PRIMARY KEY,
    session_id VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(20) NOT NULL,
    current_menu VARCHAR(50) NOT NULL,
    session_data JSONB,
    status VARCHAR(20) NOT NULL, -- ACTIVE, COMPLETED, TIMEOUT
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_ussd_phone ON ussd_sessions(phone_number);
CREATE INDEX idx_ussd_status ON ussd_sessions(status);
