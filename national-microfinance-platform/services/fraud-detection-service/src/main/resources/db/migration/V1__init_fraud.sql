-- ============================================================
-- Fraud Detection Service Database Schema
-- V1__init_fraud.sql
-- ============================================================

CREATE TABLE IF NOT EXISTS fraud_rules (
    id              VARCHAR(36)     PRIMARY KEY,
    rule_name       VARCHAR(100)    NOT NULL UNIQUE,
    rule_type       VARCHAR(50)     NOT NULL,   -- VELOCITY, AMOUNT, PATTERN, BLACKLIST
    description     TEXT,
    condition_json  TEXT            NOT NULL,   -- JSON rule definition
    action          VARCHAR(30)     NOT NULL DEFAULT 'ALERT', -- ALERT, BLOCK, REVIEW
    severity        VARCHAR(20)     NOT NULL DEFAULT 'MEDIUM', -- LOW, MEDIUM, HIGH, CRITICAL
    enabled         BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS fraud_alerts (
    id                  VARCHAR(36)     PRIMARY KEY,
    transaction_id      VARCHAR(36)     NOT NULL,
    user_id             VARCHAR(36)     NOT NULL,
    rule_id             VARCHAR(36)     REFERENCES fraud_rules(id),
    rule_name           VARCHAR(100),
    alert_type          VARCHAR(50)     NOT NULL,
    severity            VARCHAR(20)     NOT NULL,
    risk_score          INTEGER         NOT NULL DEFAULT 0,
    description         TEXT,
    transaction_amount  NUMERIC(19,2),
    transaction_type    VARCHAR(30),
    status              VARCHAR(20)     NOT NULL DEFAULT 'OPEN', -- OPEN, REVIEWED, RESOLVED, FALSE_POSITIVE
    reviewed_by         VARCHAR(36),
    reviewed_at         TIMESTAMP WITH TIME ZONE,
    resolution_notes    TEXT,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_fraud_alerts_user_id       ON fraud_alerts(user_id);
CREATE INDEX idx_fraud_alerts_status        ON fraud_alerts(status);
CREATE INDEX idx_fraud_alerts_severity      ON fraud_alerts(severity);
CREATE INDEX idx_fraud_alerts_created       ON fraud_alerts(created_at DESC);
CREATE INDEX idx_fraud_alerts_tx_id         ON fraud_alerts(transaction_id);
CREATE INDEX idx_fraud_rules_enabled        ON fraud_rules(enabled);
CREATE INDEX idx_fraud_rules_type           ON fraud_rules(rule_type);

-- Seed default fraud rules
INSERT INTO fraud_rules (id, rule_name, rule_type, description, condition_json, action, severity) VALUES
    ('rule-001', 'High Value Transaction',      'AMOUNT',   'Single transaction over 25,000 ETB',          '{"threshold": 25000}',                     'ALERT',  'HIGH'),
    ('rule-002', 'Velocity - 5 tx per minute',  'VELOCITY', 'More than 5 transactions in 60 seconds',      '{"count": 5, "windowSeconds": 60}',         'BLOCK',  'HIGH'),
    ('rule-003', 'Velocity - 20 tx per hour',   'VELOCITY', 'More than 20 transactions in one hour',       '{"count": 20, "windowSeconds": 3600}',      'ALERT',  'MEDIUM'),
    ('rule-004', 'Midnight Large Transfer',      'PATTERN',  'Large transfer between 00:00 and 04:00',      '{"hourStart": 0, "hourEnd": 4, "minAmount": 10000}', 'REVIEW', 'MEDIUM'),
    ('rule-005', 'Daily Limit Exceeded',         'AMOUNT',   'Daily transactions exceed 50,000 ETB',        '{"dailyThreshold": 50000}',                'BLOCK',  'CRITICAL');
