-- ============================================================
-- Savings Service Database Schema
-- V1__init_savings.sql
-- ============================================================

CREATE TABLE IF NOT EXISTS savings_accounts (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    account_number VARCHAR(30) NOT NULL UNIQUE,
    account_name VARCHAR(100),
    account_type VARCHAR(30) NOT NULL,
    balance NUMERIC(19,2) NOT NULL DEFAULT 0,
    minimum_balance NUMERIC(19,2) NOT NULL DEFAULT 10,
    interest_rate NUMERIC(6,4) NOT NULL,
    compounding_freq VARCHAR(20) NOT NULL DEFAULT 'MONTHLY',
    currency VARCHAR(5) NOT NULL DEFAULT 'ETB',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    last_interest_date DATE,
    last_transaction_at TIMESTAMP WITH TIME ZONE,
    closed_at TIMESTAMP WITH TIME ZONE,
    close_reason VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_savings_accounts_user_id ON savings_accounts(user_id);
CREATE INDEX idx_savings_accounts_status ON savings_accounts(status);
CREATE INDEX idx_savings_accounts_type ON savings_accounts(account_type);

CREATE TABLE IF NOT EXISTS fixed_deposits (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    account_id VARCHAR(36) NOT NULL REFERENCES savings_accounts(id),
    deposit_number VARCHAR(30) NOT NULL UNIQUE,
    principal_amount NUMERIC(19,2) NOT NULL,
    interest_rate NUMERIC(6,4) NOT NULL,
    tenure_months INTEGER NOT NULL,
    compounding_freq VARCHAR(20) NOT NULL DEFAULT 'MONTHLY',
    maturity_amount NUMERIC(19,2) NOT NULL,
    maturity_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    auto_renew BOOLEAN NOT NULL DEFAULT FALSE,
    penalty_rate NUMERIC(6,4) NOT NULL DEFAULT 0.0200,
    interest_earned NUMERIC(19,2) NOT NULL DEFAULT 0,
    source_account_id VARCHAR(36),
    matured_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_fixed_deposits_user_id ON fixed_deposits(user_id);
CREATE INDEX idx_fixed_deposits_status ON fixed_deposits(status);
CREATE INDEX idx_fixed_deposits_maturity_date ON fixed_deposits(maturity_date);

CREATE TABLE IF NOT EXISTS savings_goals (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    account_id VARCHAR(36) NOT NULL REFERENCES savings_accounts(id),
    goal_name VARCHAR(100) NOT NULL,
    goal_description VARCHAR(500),
    target_amount NUMERIC(19,2) NOT NULL,
    current_amount NUMERIC(19,2) NOT NULL DEFAULT 0,
    target_date DATE,
    category VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    auto_save_amount NUMERIC(19,2),
    auto_save_frequency VARCHAR(20),
    last_auto_save_at TIMESTAMP WITH TIME ZONE,
    achieved_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_savings_goals_user_id ON savings_goals(user_id);
CREATE INDEX idx_savings_goals_status ON savings_goals(status);

CREATE TABLE IF NOT EXISTS savings_transactions (
    id VARCHAR(36) PRIMARY KEY,
    account_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    transaction_type VARCHAR(40) NOT NULL,
    reference VARCHAR(50) NOT NULL UNIQUE,
    amount NUMERIC(19,2) NOT NULL,
    balance_before NUMERIC(19,2) NOT NULL,
    balance_after NUMERIC(19,2) NOT NULL,
    fee NUMERIC(19,2) NOT NULL DEFAULT 0,
    channel VARCHAR(30),
    description VARCHAR(255),
    related_entity_id VARCHAR(36),
    related_entity_type VARCHAR(30),
    initiated_by VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_savings_tx_account_id ON savings_transactions(account_id);
CREATE INDEX idx_savings_tx_user_id ON savings_transactions(user_id);
CREATE INDEX idx_savings_tx_created_at ON savings_transactions(created_at DESC);

CREATE TABLE IF NOT EXISTS interest_accrual_logs (
    id VARCHAR(36) PRIMARY KEY,
    account_id VARCHAR(36) NOT NULL,
    accrual_date DATE NOT NULL,
    opening_balance NUMERIC(19,2) NOT NULL,
    daily_rate NUMERIC(12,10) NOT NULL,
    interest_amount NUMERIC(19,4) NOT NULL,
    posted BOOLEAN NOT NULL DEFAULT FALSE,
    posted_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE(account_id, accrual_date)
);

CREATE INDEX idx_accrual_account_id ON interest_accrual_logs(account_id);
CREATE INDEX idx_accrual_posted ON interest_accrual_logs(posted);
