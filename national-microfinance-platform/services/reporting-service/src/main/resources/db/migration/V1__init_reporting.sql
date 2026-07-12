-- ============================================================
-- Reporting Service Database Schema
-- V1__init_reporting.sql
-- ============================================================

CREATE TABLE IF NOT EXISTS report_definitions (
    id              VARCHAR(36)     PRIMARY KEY,
    name            VARCHAR(150)    NOT NULL UNIQUE,
    description     TEXT,
    type            VARCHAR(50)     NOT NULL,   -- FINANCIAL, AUDIT, TRANSACTIONAL, OPERATIONAL, REGULATORY
    template_path   VARCHAR(255),
    query_config    TEXT,                       -- JSON config for dynamic queries
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS report_executions (
    id                      VARCHAR(36)     PRIMARY KEY,
    report_definition_id    VARCHAR(36)     NOT NULL REFERENCES report_definitions(id),
    schedule_id             VARCHAR(36),
    status                  VARCHAR(20)     NOT NULL DEFAULT 'PENDING',  -- PENDING, PROCESSING, COMPLETED, FAILED
    parameters              TEXT,                                         -- JSON execution params
    file_path               VARCHAR(500),
    file_size_bytes         BIGINT,
    error_message           TEXT,
    started_at              TIMESTAMP WITH TIME ZONE,
    completed_at            TIMESTAMP WITH TIME ZONE,
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS report_schedules (
    id                      VARCHAR(36)     PRIMARY KEY,
    report_definition_id    VARCHAR(36)     NOT NULL REFERENCES report_definitions(id),
    name                    VARCHAR(150)    NOT NULL,
    cron_expression         VARCHAR(100)    NOT NULL,
    enabled                 BOOLEAN         NOT NULL DEFAULT TRUE,
    recipients              TEXT,           -- JSON array of email recipients
    last_run_at             TIMESTAMP WITH TIME ZONE,
    next_run_at             TIMESTAMP WITH TIME ZONE,
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_report_exec_definition ON report_executions(report_definition_id);
CREATE INDEX idx_report_exec_status     ON report_executions(status);
CREATE INDEX idx_report_exec_created    ON report_executions(created_at DESC);
CREATE INDEX idx_report_sched_enabled   ON report_schedules(enabled);
CREATE INDEX idx_report_sched_next_run  ON report_schedules(next_run_at);

-- Seed default regulatory report definitions
INSERT INTO report_definitions (id, name, description, type) VALUES
    ('rpt-001', 'Daily Transaction Summary',    'Summary of all transactions per day',                     'FINANCIAL'),
    ('rpt-002', 'Monthly Loan Portfolio',       'Portfolio analysis: disbursements, repayments, NPL',      'FINANCIAL'),
    ('rpt-003', 'KYC Compliance Report',        'KYC verification status and PEP/sanctions hits',          'REGULATORY'),
    ('rpt-004', 'Active User Report',           'Count of active users per service per period',            'OPERATIONAL'),
    ('rpt-005', 'Fraud Detection Summary',      'Flagged transactions and fraud rules triggered',          'AUDIT'),
    ('rpt-006', 'Agent Banking Activity',       'Cash-in / cash-out by agent and region',                  'OPERATIONAL'),
    ('rpt-007', 'Interest Accrual Report',      'Interest earned on savings and fixed deposits',           'FINANCIAL'),
    ('rpt-008', 'Cooperative ROSCA Report',     'ROSCA cycle disbursements and contribution rates',        'FINANCIAL');
