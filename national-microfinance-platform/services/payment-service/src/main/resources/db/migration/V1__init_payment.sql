-- ============================================================
-- Payment Service Database Schema
-- V1__init_payment.sql
-- ============================================================

CREATE TABLE IF NOT EXISTS payments (
    id VARCHAR(36) PRIMARY KEY,
    reference VARCHAR(50) NOT NULL UNIQUE,
    user_id VARCHAR(36) NOT NULL,
    wallet_id VARCHAR(36) NOT NULL,
    type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    amount NUMERIC(19,2) NOT NULL,
    fee NUMERIC(19,2) NOT NULL DEFAULT 0,
    currency VARCHAR(5) NOT NULL DEFAULT 'ETB',
    merchant_id VARCHAR(36),
    merchant_name VARCHAR(100),
    biller_id VARCHAR(36),
    biller_name VARCHAR(100),
    account_number VARCHAR(50),
    description VARCHAR(255),
    channel VARCHAR(30),
    payment_method VARCHAR(30),
    provider_reference VARCHAR(100),
    callback_url VARCHAR(500),
    metadata TEXT,
    completed_at TIMESTAMP WITH TIME ZONE,
    failure_reason VARCHAR(255),
    retry_count INTEGER NOT NULL DEFAULT 0,
    next_retry_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_payments_user_id ON payments(user_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_type ON payments(type);
CREATE INDEX idx_payments_created_at ON payments(created_at DESC);
CREATE INDEX idx_payments_merchant_id ON payments(merchant_id);

CREATE TABLE IF NOT EXISTS merchant_payments (
    id VARCHAR(36) PRIMARY KEY,
    payment_id VARCHAR(36) NOT NULL REFERENCES payments(id),
    merchant_id VARCHAR(36) NOT NULL,
    merchant_name VARCHAR(100) NOT NULL,
    merchant_category VARCHAR(50),
    terminal_id VARCHAR(50),
    qr_code_ref VARCHAR(100),
    pos_entry_mode VARCHAR(20),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_merchant_payments_merchant_id ON merchant_payments(merchant_id);

CREATE TABLE IF NOT EXISTS bill_payments (
    id VARCHAR(36) PRIMARY KEY,
    payment_id VARCHAR(36) NOT NULL REFERENCES payments(id),
    biller_id VARCHAR(36) NOT NULL,
    biller_name VARCHAR(100) NOT NULL,
    biller_code VARCHAR(50) NOT NULL,
    bill_account_number VARCHAR(100) NOT NULL,
    bill_due_date DATE,
    bill_period VARCHAR(20),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_bill_payments_biller_id ON bill_payments(biller_id);
CREATE INDEX idx_bill_payments_account ON bill_payments(bill_account_number);
