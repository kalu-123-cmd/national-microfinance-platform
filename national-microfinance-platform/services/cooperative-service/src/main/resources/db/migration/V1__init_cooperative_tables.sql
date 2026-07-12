-- ============================================================
-- Cooperative Service - Full Database Schema
-- Covers: cooperatives, members, contributions, ROSCA cycles,
--         group loans and their repayment schedules
-- ============================================================

-- Master cooperative (savings & credit association)
CREATE TABLE cooperatives (
    id                  VARCHAR(50) PRIMARY KEY,
    name                VARCHAR(150) NOT NULL,
    registration_number VARCHAR(50) UNIQUE NOT NULL,
    description         VARCHAR(500),
    type                VARCHAR(30) NOT NULL DEFAULT 'SAVINGS_CREDIT', -- SAVINGS_CREDIT, ROSCA, MULTIPURPOSE
    admin_user_id       VARCHAR(50) NOT NULL,           -- founding admin
    max_members         INTEGER NOT NULL DEFAULT 30,
    membership_fee      DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    monthly_contribution DECIMAL(19,2),                -- required monthly contribution
    total_pool_balance  DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    loan_interest_rate  DECIMAL(6,4) NOT NULL DEFAULT 0.1200, -- 12% annual
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, SUSPENDED, DISSOLVED
    location            VARCHAR(255),
    phone               VARCHAR(20),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Members of cooperatives
CREATE TABLE cooperative_members (
    id                  VARCHAR(50) PRIMARY KEY,
    cooperative_id      VARCHAR(50) NOT NULL REFERENCES cooperatives(id),
    user_id             VARCHAR(50) NOT NULL,
    member_number       VARCHAR(30) UNIQUE NOT NULL,
    role                VARCHAR(20) NOT NULL DEFAULT 'MEMBER', -- ADMIN, TREASURER, SECRETARY, MEMBER
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, SUSPENDED, EXITED
    total_contributed   DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    total_withdrawn     DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    join_date           DATE NOT NULL DEFAULT CURRENT_DATE,
    exit_date           DATE,
    exit_reason         VARCHAR(255),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(cooperative_id, user_id)
);

-- Contribution records (monthly/periodic savings to the pool)
CREATE TABLE contributions (
    id                  VARCHAR(50) PRIMARY KEY,
    cooperative_id      VARCHAR(50) NOT NULL REFERENCES cooperatives(id),
    member_id           VARCHAR(50) NOT NULL REFERENCES cooperative_members(id),
    user_id             VARCHAR(50) NOT NULL,
    contribution_month  VARCHAR(7) NOT NULL,           -- YYYY-MM format
    amount              DECIMAL(19,2) NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, PAID, LATE, WAIVED
    paid_at             TIMESTAMP,
    payment_reference   VARCHAR(50),
    notes               VARCHAR(255),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(cooperative_id, member_id, contribution_month)
);

-- ROSCA cycles (Rotating Savings and Credit Association)
-- Each cycle, one member receives the pot
CREATE TABLE rosca_cycles (
    id                  VARCHAR(50) PRIMARY KEY,
    cooperative_id      VARCHAR(50) NOT NULL REFERENCES cooperatives(id),
    cycle_number        INTEGER NOT NULL,
    beneficiary_user_id VARCHAR(50) NOT NULL,          -- who gets the pot this round
    beneficiary_member_id VARCHAR(50) NOT NULL REFERENCES cooperative_members(id),
    pot_amount          DECIMAL(19,2) NOT NULL,        -- total pot = monthly_contribution * member_count
    status              VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED', -- SCHEDULED, ACTIVE, DISBURSED, COMPLETED
    scheduled_date      DATE NOT NULL,
    disbursed_at        TIMESTAMP,
    disbursed_amount    DECIMAL(19,2),
    notes               VARCHAR(255),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(cooperative_id, cycle_number)
);

-- Group loans issued by the cooperative
CREATE TABLE group_loans (
    id                  VARCHAR(50) PRIMARY KEY,
    cooperative_id      VARCHAR(50) NOT NULL REFERENCES cooperatives(id),
    applicant_user_id   VARCHAR(50) NOT NULL,
    applicant_member_id VARCHAR(50) NOT NULL REFERENCES cooperative_members(id),
    loan_number         VARCHAR(30) UNIQUE NOT NULL,
    amount_requested    DECIMAL(19,2) NOT NULL,
    amount_approved     DECIMAL(19,2),
    interest_rate       DECIMAL(6,4) NOT NULL,         -- annual rate
    tenure_months       INTEGER NOT NULL,
    monthly_repayment   DECIMAL(19,2),
    total_repayable     DECIMAL(19,2),
    outstanding_balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    status              VARCHAR(20) NOT NULL DEFAULT 'APPLIED', -- APPLIED, APPROVED, REJECTED, DISBURSED, ACTIVE, CLOSED, DEFAULTED
    purpose             VARCHAR(255),
    guarantor_user_ids  TEXT,                          -- JSON array of guarantor user IDs
    applied_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_at         TIMESTAMP,
    approved_by         VARCHAR(50),
    disbursed_at        TIMESTAMP,
    closed_at           TIMESTAMP,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Group loan repayment schedule
CREATE TABLE group_loan_repayments (
    id                  VARCHAR(50) PRIMARY KEY,
    loan_id             VARCHAR(50) NOT NULL REFERENCES group_loans(id),
    cooperative_id      VARCHAR(50) NOT NULL,
    installment_number  INTEGER NOT NULL,
    due_date            DATE NOT NULL,
    principal_amount    DECIMAL(19,2) NOT NULL,
    interest_amount     DECIMAL(19,2) NOT NULL,
    total_due           DECIMAL(19,2) NOT NULL,
    amount_paid         DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, PAID, PARTIAL, OVERDUE, WAIVED
    paid_at             TIMESTAMP,
    payment_reference   VARCHAR(50),
    penalty_amount      DECIMAL(19,2) NOT NULL DEFAULT 0.00,    -- late payment penalty
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(loan_id, installment_number)
);

-- Indexes
CREATE INDEX idx_coop_admin        ON cooperatives(admin_user_id);
CREATE INDEX idx_coop_status       ON cooperatives(status);
CREATE INDEX idx_member_coop       ON cooperative_members(cooperative_id);
CREATE INDEX idx_member_user       ON cooperative_members(user_id);
CREATE INDEX idx_member_status     ON cooperative_members(status);
CREATE INDEX idx_contrib_coop      ON contributions(cooperative_id);
CREATE INDEX idx_contrib_member    ON contributions(member_id);
CREATE INDEX idx_contrib_month     ON contributions(contribution_month);
CREATE INDEX idx_rosca_coop        ON rosca_cycles(cooperative_id);
CREATE INDEX idx_rosca_status      ON rosca_cycles(status);
CREATE INDEX idx_gloan_coop        ON group_loans(cooperative_id);
CREATE INDEX idx_gloan_applicant   ON group_loans(applicant_user_id);
CREATE INDEX idx_gloan_status      ON group_loans(status);
CREATE INDEX idx_gloan_repay_loan  ON group_loan_repayments(loan_id);
CREATE INDEX idx_gloan_repay_due   ON group_loan_repayments(due_date);
CREATE INDEX idx_gloan_repay_status ON group_loan_repayments(status);
