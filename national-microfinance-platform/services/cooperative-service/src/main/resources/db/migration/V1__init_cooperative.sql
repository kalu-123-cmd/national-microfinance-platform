-- ============================================================
-- Cooperative Service Database Schema
-- V1__init_cooperative.sql
-- ============================================================

CREATE TABLE IF NOT EXISTS cooperatives (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    registration_number VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(30) NOT NULL,
    admin_user_id VARCHAR(36) NOT NULL,
    max_members INTEGER NOT NULL,
    membership_fee NUMERIC(19,2) NOT NULL DEFAULT 0,
    monthly_contribution NUMERIC(19,2),
    total_pool_balance NUMERIC(19,2) NOT NULL DEFAULT 0,
    loan_interest_rate NUMERIC(6,4),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    location VARCHAR(255),
    phone VARCHAR(20),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_coops_admin ON cooperatives(admin_user_id);
CREATE INDEX idx_coops_status ON cooperatives(status);

CREATE TABLE IF NOT EXISTS cooperative_members (
    id VARCHAR(36) PRIMARY KEY,
    cooperative_id VARCHAR(36) NOT NULL REFERENCES cooperatives(id),
    user_id VARCHAR(36) NOT NULL,
    member_number VARCHAR(50) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    total_contributed NUMERIC(19,2) NOT NULL DEFAULT 0,
    total_withdrawn NUMERIC(19,2) NOT NULL DEFAULT 0,
    join_date DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_members_coop_id ON cooperative_members(cooperative_id);
CREATE INDEX idx_members_user_id ON cooperative_members(user_id);

CREATE TABLE IF NOT EXISTS contributions (
    id VARCHAR(36) PRIMARY KEY,
    cooperative_id VARCHAR(36) NOT NULL REFERENCES cooperatives(id),
    member_id VARCHAR(36) NOT NULL REFERENCES cooperative_members(id),
    user_id VARCHAR(36) NOT NULL,
    contribution_month VARCHAR(20) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    payment_reference VARCHAR(100) NOT NULL UNIQUE,
    paid_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_contrib_coop_member ON contributions(cooperative_id, member_id);

CREATE TABLE IF NOT EXISTS rosca_cycles (
    id VARCHAR(36) PRIMARY KEY,
    cooperative_id VARCHAR(36) NOT NULL REFERENCES cooperatives(id),
    cycle_number INTEGER NOT NULL,
    beneficiary_user_id VARCHAR(36) NOT NULL,
    beneficiary_member_id VARCHAR(36) NOT NULL REFERENCES cooperative_members(id),
    pot_amount NUMERIC(19,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    scheduled_date DATE NOT NULL,
    disbursed_at TIMESTAMP WITH TIME ZONE,
    disbursed_amount NUMERIC(19,2),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_rosca_coop_status ON rosca_cycles(cooperative_id, status);

CREATE TABLE IF NOT EXISTS group_loans (
    id VARCHAR(36) PRIMARY KEY,
    cooperative_id VARCHAR(36) NOT NULL REFERENCES cooperatives(id),
    applicant_user_id VARCHAR(36) NOT NULL,
    applicant_member_id VARCHAR(36) NOT NULL REFERENCES cooperative_members(id),
    loan_number VARCHAR(50) NOT NULL UNIQUE,
    amount_requested NUMERIC(19,2) NOT NULL,
    amount_approved NUMERIC(19,2),
    interest_rate NUMERIC(6,4) NOT NULL,
    tenure_months INTEGER NOT NULL,
    monthly_repayment NUMERIC(19,2),
    total_repayable NUMERIC(19,2),
    outstanding_balance NUMERIC(19,2),
    purpose VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'APPLIED',
    approved_by VARCHAR(36),
    applied_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    approved_at TIMESTAMP WITH TIME ZONE,
    disbursed_at TIMESTAMP WITH TIME ZONE,
    closed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_gloans_coop_id ON group_loans(cooperative_id);
CREATE INDEX idx_gloans_user_id ON group_loans(applicant_user_id);

CREATE TABLE IF NOT EXISTS group_loan_repayments (
    id VARCHAR(36) PRIMARY KEY,
    loan_id VARCHAR(36) NOT NULL REFERENCES group_loans(id),
    cooperative_id VARCHAR(36) NOT NULL REFERENCES cooperatives(id),
    installment_number INTEGER NOT NULL,
    due_date DATE NOT NULL,
    principal_amount NUMERIC(19,2) NOT NULL,
    interest_amount NUMERIC(19,2) NOT NULL,
    total_due NUMERIC(19,2) NOT NULL,
    amount_paid NUMERIC(19,2) NOT NULL DEFAULT 0,
    penalty_amount NUMERIC(19,2) NOT NULL DEFAULT 0,
    payment_reference VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    paid_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_grepay_loan_id ON group_loan_repayments(loan_id);
CREATE INDEX idx_grepay_status_due ON group_loan_repayments(status, due_date);
