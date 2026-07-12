-- ============================================================
-- Savings Service - Full Database Schema
-- V1: Savings accounts, transactions, fixed deposits, goals,
--     interest accrual log, scheduled operations
-- ============================================================

-- Savings account types: REGULAR, FIXED_DEPOSIT, SAVINGS_GOAL, CHILDREN, PENSION
CREATE TABLE savings_accounts (
    id                  VARCHAR(50) PRIMARY KEY,
    user_id             VARCHAR(50) NOT NULL,
    account_number      VARCHAR(30) UNIQUE NOT NULL,
    account_name        VARCHAR(100),
    account_type        VARCHAR(30) NOT NULL DEFAULT 'REGULAR',
    balance             DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    minimum_balance     DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    interest_rate       DECIMAL(6,4) NOT NULL DEFAULT 0.00,  -- annual rate e.g. 0.0750 = 7.5%
    compounding_freq    VARCHAR(20) NOT NULL DEFAULT 'MONTHLY', -- DAILY, MONTHLY, QUARTERLY, ANNUALLY
    currency            VARCHAR(5) NOT NULL DEFAULT 'ETB',
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, DORMANT, FROZEN, CLOSED
    last_interest_date  DATE,
    last_transaction_at TIMESTAMP,
    closed_at           TIMESTAMP,
    close_reason        VARCHAR(255),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Fixed deposit (term deposit) products
CREATE TABLE fixed_deposits (
    id                  VARCHAR(50) PRIMARY KEY,
    user_id             VARCHAR(50) NOT NULL,
    account_id          VARCHAR(50) NOT NULL REFERENCES savings_accounts(id),
    deposit_number      VARCHAR(30) UNIQUE NOT NULL,
    principal_amount    DECIMAL(19,2) NOT NULL,
    interest_rate       DECIMAL(6,4) NOT NULL,           -- annual rate at time of creation
    tenure_months       INTEGER NOT NULL,
    compounding_freq    VARCHAR(20) NOT NULL DEFAULT 'MONTHLY',
    maturity_amount     DECIMAL(19,2),                   -- pre-calculated at creation
    maturity_date       DATE NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, MATURED, PREMATURELY_CLOSED
    auto_renew          BOOLEAN NOT NULL DEFAULT FALSE,
    penalty_rate        DECIMAL(6,4) NOT NULL DEFAULT 0.0200, -- 2% early withdrawal penalty
    interest_earned     DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    source_account_id   VARCHAR(50),                     -- wallet or savings account that funded this
    matured_at          TIMESTAMP,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Savings goals (target-based savings)
CREATE TABLE savings_goals (
    id                  VARCHAR(50) PRIMARY KEY,
    user_id             VARCHAR(50) NOT NULL,
    account_id          VARCHAR(50) NOT NULL REFERENCES savings_accounts(id),
    goal_name           VARCHAR(100) NOT NULL,
    goal_description    VARCHAR(500),
    target_amount       DECIMAL(19,2) NOT NULL,
    current_amount      DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    target_date         DATE,
    category            VARCHAR(50),                      -- EDUCATION, BUSINESS, HOUSING, EMERGENCY, etc.
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, ACHIEVED, ABANDONED
    auto_save_amount    DECIMAL(19,2),                    -- recurring auto-save per period
    auto_save_frequency VARCHAR(20),                     -- DAILY, WEEKLY, MONTHLY
    last_auto_save_at   TIMESTAMP,
    achieved_at         TIMESTAMP,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- All savings transactions (deposits, withdrawals, interest credits, etc.)
CREATE TABLE savings_transactions (
    id                  VARCHAR(50) PRIMARY KEY,
    account_id          VARCHAR(50) NOT NULL REFERENCES savings_accounts(id),
    user_id             VARCHAR(50) NOT NULL,
    transaction_type    VARCHAR(40) NOT NULL,  -- DEPOSIT, WITHDRAWAL, INTEREST_CREDIT, FD_MATURITY, GOAL_CONTRIBUTION, TRANSFER_IN, TRANSFER_OUT, FEE_DEBIT
    reference           VARCHAR(50) UNIQUE NOT NULL,
    amount              DECIMAL(19,2) NOT NULL,
    balance_before      DECIMAL(19,2) NOT NULL,
    balance_after       DECIMAL(19,2) NOT NULL,
    fee                 DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    channel             VARCHAR(30),           -- MOBILE_APP, USSD, AGENT, BANK_TRANSFER
    description         VARCHAR(500),
    related_entity_id   VARCHAR(50),           -- goal_id, fd_id, etc.
    related_entity_type VARCHAR(30),           -- GOAL, FIXED_DEPOSIT, WALLET
    initiated_by        VARCHAR(50),           -- user_id or SYSTEM
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Interest accrual log — daily record of interest earned per account
CREATE TABLE interest_accrual_log (
    id                  VARCHAR(50) PRIMARY KEY,
    account_id          VARCHAR(50) NOT NULL REFERENCES savings_accounts(id),
    accrual_date        DATE NOT NULL,
    opening_balance     DECIMAL(19,2) NOT NULL,
    daily_rate          DECIMAL(10,8) NOT NULL,           -- annual_rate / 365
    interest_amount     DECIMAL(19,4) NOT NULL,
    compounded_balance  DECIMAL(19,2),
    posted             BOOLEAN NOT NULL DEFAULT FALSE,   -- TRUE when credited to account
    posted_at          TIMESTAMP,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(account_id, accrual_date)
);

-- Indexes
CREATE INDEX idx_savings_user_id       ON savings_accounts(user_id);
CREATE INDEX idx_savings_status        ON savings_accounts(status);
CREATE INDEX idx_savings_type          ON savings_accounts(account_type);
CREATE INDEX idx_fd_user_id            ON fixed_deposits(user_id);
CREATE INDEX idx_fd_account_id         ON fixed_deposits(account_id);
CREATE INDEX idx_fd_maturity_date      ON fixed_deposits(maturity_date);
CREATE INDEX idx_fd_status             ON fixed_deposits(status);
CREATE INDEX idx_goal_user_id          ON savings_goals(user_id);
CREATE INDEX idx_goal_account_id       ON savings_goals(account_id);
CREATE INDEX idx_goal_status           ON savings_goals(status);
CREATE INDEX idx_savings_tx_account    ON savings_transactions(account_id);
CREATE INDEX idx_savings_tx_user       ON savings_transactions(user_id);
CREATE INDEX idx_savings_tx_type       ON savings_transactions(transaction_type);
CREATE INDEX idx_savings_tx_created    ON savings_transactions(created_at DESC);
CREATE INDEX idx_interest_log_account  ON interest_accrual_log(account_id);
CREATE INDEX idx_interest_log_date     ON interest_accrual_log(accrual_date);
CREATE INDEX idx_interest_log_posted   ON interest_accrual_log(posted) WHERE posted = FALSE;