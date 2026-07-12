-- ============================================================
-- Agent Banking Service Database Schema
-- V1__init_agent.sql
-- ============================================================

CREATE TABLE IF NOT EXISTS agents (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL UNIQUE,
    business_name VARCHAR(100) NOT NULL,
    business_id VARCHAR(50) NOT NULL UNIQUE,
    phone_number VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    region VARCHAR(50) NOT NULL,
    woreda VARCHAR(50),
    kebele VARCHAR(50),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING_APPROVAL',
    daily_limit NUMERIC(19,2) NOT NULL DEFAULT 50000.00,
    monthly_limit NUMERIC(19,2) NOT NULL DEFAULT 500000.00,
    daily_processed NUMERIC(19,2) NOT NULL DEFAULT 0.00,
    monthly_processed NUMERIC(19,2) NOT NULL DEFAULT 0.00,
    balance NUMERIC(19,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_agents_status ON agents(status);
CREATE INDEX idx_agents_region ON agents(region);
CREATE INDEX idx_agents_user_id ON agents(user_id);

CREATE TABLE IF NOT EXISTS cash_transactions (
    id VARCHAR(36) PRIMARY KEY,
    agent_id VARCHAR(36) NOT NULL REFERENCES agents(id),
    user_id VARCHAR(36) NOT NULL,
    wallet_id VARCHAR(36),
    transaction_type VARCHAR(30) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    fee NUMERIC(19,2) NOT NULL DEFAULT 0.00,
    commission NUMERIC(19,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL,
    reference VARCHAR(100) NOT NULL UNIQUE,
    channel VARCHAR(30),
    failure_reason VARCHAR(255),
    processed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_cash_tx_agent ON cash_transactions(agent_id);
CREATE INDEX idx_cash_tx_user ON cash_transactions(user_id);
CREATE INDEX idx_cash_tx_processed ON cash_transactions(processed_at DESC);
