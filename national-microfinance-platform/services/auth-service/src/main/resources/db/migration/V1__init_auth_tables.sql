-- V1__init_auth_tables.sql

-- User Credentials Table
CREATE TABLE IF NOT EXISTS user_credentials (
    id               VARCHAR(50)  PRIMARY KEY,
    user_id          VARCHAR(50)  NOT NULL UNIQUE,
    phone_number     VARCHAR(20)  NOT NULL UNIQUE,
    email            VARCHAR(255) UNIQUE,
    password_hash    VARCHAR(255),          -- nullable: PIN-only accounts allowed
    pin_hash         VARCHAR(255) NOT NULL,
    enabled          BOOLEAN      NOT NULL DEFAULT TRUE,
    failed_login_attempts  INTEGER NOT NULL DEFAULT 0,
    last_failed_login_at   TIMESTAMP,
    account_locked_until   TIMESTAMP,
    last_login_at          TIMESTAMP,
    last_login_ip          VARCHAR(45),
    last_device_id         VARCHAR(255),
    pin_changed_at         TIMESTAMP,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_uc_user_id ON user_credentials(user_id);
CREATE INDEX IF NOT EXISTS idx_uc_phone   ON user_credentials(phone_number);
CREATE INDEX IF NOT EXISTS idx_uc_email   ON user_credentials(email);

-- OTP Records Table
CREATE TABLE IF NOT EXISTS otp_records (
    id                     VARCHAR(50)  PRIMARY KEY,
    recipient              VARCHAR(255) NOT NULL,
    otp_hash               VARCHAR(255) NOT NULL,
    purpose                VARCHAR(50)  NOT NULL,
    expires_at             TIMESTAMP    NOT NULL,
    verified               BOOLEAN      NOT NULL DEFAULT FALSE,
    verified_at            TIMESTAMP,
    verification_attempts  INTEGER      NOT NULL DEFAULT 0,
    ip_address             VARCHAR(45),
    created_at             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_otp_recipient_purpose ON otp_records(recipient, purpose);
CREATE INDEX IF NOT EXISTS idx_otp_expires            ON otp_records(expires_at);

-- Refresh Tokens Table
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id                VARCHAR(50)  PRIMARY KEY,
    user_id           VARCHAR(50)  NOT NULL,
    token_hash        VARCHAR(255) NOT NULL UNIQUE,
    device_id         VARCHAR(255),
    device_info       VARCHAR(500),
    ip_address        VARCHAR(45),
    expires_at        TIMESTAMP    NOT NULL,
    revoked           BOOLEAN      NOT NULL DEFAULT FALSE,
    revoked_at        TIMESTAMP,
    revocation_reason VARCHAR(100),
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_rt_token_hash ON refresh_tokens(token_hash);
CREATE INDEX IF NOT EXISTS idx_rt_user        ON refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_rt_expires     ON refresh_tokens(expires_at);

-- Auto-update updated_at on user_credentials
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_user_credentials_updated_at
    BEFORE UPDATE ON user_credentials
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
