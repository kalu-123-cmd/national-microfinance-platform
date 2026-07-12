CREATE TABLE IF NOT EXISTS admin_users (
    id           VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      VARCHAR(36) UNIQUE NOT NULL,
    username     VARCHAR(100) UNIQUE NOT NULL,
    role         VARCHAR(50) NOT NULL,
    department   VARCHAR(100),
    active       BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at TIMESTAMP WITH TIME ZONE,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by   VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS system_configs (
    id           VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid(),
    config_key   VARCHAR(100) UNIQUE NOT NULL,
    config_value TEXT NOT NULL,
    description  TEXT,
    category     VARCHAR(50) NOT NULL DEFAULT 'GENERAL',
    data_type    VARCHAR(20) NOT NULL DEFAULT 'STRING',
    modifiable   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by   VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS admin_audit_actions (
    id                  VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid(),
    admin_user_id       VARCHAR(36) NOT NULL,
    action              VARCHAR(100) NOT NULL,
    target_entity_type  VARCHAR(50),
    target_entity_id    VARCHAR(36),
    details             TEXT,
    ip_address          VARCHAR(45),
    performed_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_admin_audit_user ON admin_audit_actions(admin_user_id);
CREATE INDEX idx_admin_audit_action ON admin_audit_actions(action);
CREATE INDEX idx_system_config_category ON system_configs(category);

-- Seed default system configs
INSERT INTO system_configs (config_key, config_value, description, category, data_type)
VALUES
  ('max_daily_transfer_limit',    '100000',  'Maximum daily transfer amount in ETB',      'LIMITS',   'NUMBER'),
  ('max_single_transaction',      '50000',   'Maximum single transaction amount in ETB',  'LIMITS',   'NUMBER'),
  ('min_wallet_balance',          '10',      'Minimum wallet balance to maintain in ETB', 'LIMITS',   'NUMBER'),
  ('loan_max_interest_rate',      '25',      'Maximum loan interest rate (%)',             'LIMITS',   'NUMBER'),
  ('kyc_required_for_transfer',   'true',    'KYC verification required for transfers',   'SECURITY', 'BOOLEAN'),
  ('otp_expiry_minutes',          '5',       'OTP expiry time in minutes',                'SECURITY', 'NUMBER'),
  ('max_login_attempts',          '5',       'Maximum failed login attempts before lock', 'SECURITY', 'NUMBER'),
  ('account_lock_duration_hours', '24',      'Account lock duration after max attempts',  'SECURITY', 'NUMBER'),
  ('sms_notifications_enabled',   'true',    'Enable SMS notifications platform-wide',    'FEATURES', 'BOOLEAN'),
  ('email_notifications_enabled', 'true',    'Enable email notifications platform-wide',  'FEATURES', 'BOOLEAN'),
  ('agent_banking_enabled',       'true',    'Enable agent banking feature',              'FEATURES', 'BOOLEAN'),
  ('cooperative_enabled',         'true',    'Enable cooperative banking feature',        'FEATURES', 'BOOLEAN'),
  ('transaction_fee_rate',        '0.005',   'Transaction fee rate (0.5%)',               'FEES',     'NUMBER'),
  ('international_transfer_fee',  '150',     'Flat fee for international transfers ETB',  'FEES',     'NUMBER')
ON CONFLICT (config_key) DO NOTHING;
