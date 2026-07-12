-- ============================================================
-- Admin Service Database Schema
-- V1__init_admin.sql
-- ============================================================

CREATE TABLE IF NOT EXISTS system_configs (
    id              VARCHAR(36)     PRIMARY KEY,
    config_key      VARCHAR(100)    NOT NULL UNIQUE,
    config_value    TEXT            NOT NULL,
    description     TEXT,
    category        VARCHAR(50)     NOT NULL DEFAULT 'GENERAL',  -- LIMITS, FEES, FEATURES, SECURITY, GENERAL
    data_type       VARCHAR(20)     NOT NULL DEFAULT 'STRING',   -- STRING, NUMBER, BOOLEAN, JSON
    modifiable      BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by      VARCHAR(36)
);

CREATE TABLE IF NOT EXISTS admin_users (
    id              VARCHAR(36)     PRIMARY KEY,
    user_id         VARCHAR(36)     NOT NULL UNIQUE,
    username        VARCHAR(100)    NOT NULL UNIQUE,
    role            VARCHAR(50)     NOT NULL,   -- SUPER_ADMIN, ADMIN, LOAN_OFFICER, COMPLIANCE_OFFICER, VIEWER
    department      VARCHAR(100),
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    last_login_at   TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(36)
);

CREATE TABLE IF NOT EXISTS audit_actions (
    id                  VARCHAR(36)     PRIMARY KEY,
    admin_user_id       VARCHAR(36)     NOT NULL,
    action              VARCHAR(100)    NOT NULL,
    target_entity_type  VARCHAR(60),
    target_entity_id    VARCHAR(36),
    details             TEXT,
    performed_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_syscfg_category       ON system_configs(category);
CREATE INDEX idx_syscfg_key            ON system_configs(config_key);
CREATE INDEX idx_admin_user_id         ON admin_users(user_id);
CREATE INDEX idx_admin_role            ON admin_users(role);
CREATE INDEX idx_admin_active          ON admin_users(active);
CREATE INDEX idx_audit_action_admin    ON audit_actions(admin_user_id);
CREATE INDEX idx_audit_action_time     ON audit_actions(performed_at DESC);
CREATE INDEX idx_audit_action_entity   ON audit_actions(target_entity_type, target_entity_id);

-- Seed default system configuration values
INSERT INTO system_configs (id, config_key, config_value, description, category, data_type, modifiable) VALUES
    ('cfg-001', 'MAX_DAILY_TRANSACTION_LIMIT',  '50000.00',  'Maximum single-day transaction total per user (ETB)',  'LIMITS',   'NUMBER',  true),
    ('cfg-002', 'MAX_SINGLE_TRANSACTION',       '25000.00',  'Maximum amount per single transaction (ETB)',          'LIMITS',   'NUMBER',  true),
    ('cfg-003', 'MIN_WALLET_BALANCE',           '10.00',     'Minimum wallet balance required (ETB)',                'LIMITS',   'NUMBER',  true),
    ('cfg-004', 'TRANSACTION_FEE_PERCENTAGE',   '0.005',     'Platform fee charged per transaction (0.5%)',          'FEES',     'NUMBER',  true),
    ('cfg-005', 'WITHDRAWAL_FEE_FLAT',          '5.00',      'Flat fee for wallet withdrawals (ETB)',                'FEES',     'NUMBER',  true),
    ('cfg-006', 'DEFAULT_LOAN_INTEREST_RATE',   '0.18',      'Default annual loan interest rate (18%)',              'LIMITS',   'NUMBER',  true),
    ('cfg-007', 'OTP_EXPIRY_MINUTES',           '5',         'OTP validity window in minutes',                       'SECURITY', 'NUMBER',  true),
    ('cfg-008', 'MAX_LOGIN_ATTEMPTS',           '5',         'Failed login attempts before account lock',            'SECURITY', 'NUMBER',  true),
    ('cfg-009', 'ACCOUNT_LOCK_DURATION_MINUTES','30',        'Duration to lock account after max failed logins',     'SECURITY', 'NUMBER',  true),
    ('cfg-010', 'FEATURE_OTP_ENABLED',          'true',      'Enable OTP-based authentication',                      'FEATURES', 'BOOLEAN', true),
    ('cfg-011', 'FEATURE_BIOMETRIC_ENABLED',    'true',      'Enable biometric authentication',                      'FEATURES', 'BOOLEAN', true),
    ('cfg-012', 'FEATURE_USSD_ENABLED',         'false',     'Enable USSD banking channel',                          'FEATURES', 'BOOLEAN', true),
    ('cfg-013', 'SUPPORTED_CURRENCIES',         'ETB',       'Comma-separated list of supported currencies',         'GENERAL',  'STRING',  false),
    ('cfg-014', 'DEFAULT_CURRENCY',             'ETB',       'Default platform currency',                            'GENERAL',  'STRING',  false),
    ('cfg-015', 'PLATFORM_TIMEZONE',            'Africa/Addis_Ababa', 'Platform timezone',                           'GENERAL',  'STRING',  false);
