CREATE TABLE wallets (
    id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL UNIQUE,
    wallet_number VARCHAR(20) NOT NULL UNIQUE,
    balance DECIMAL(19,2) NOT NULL DEFAULT 0,
    reserved_balance DECIMAL(19,2) NOT NULL DEFAULT 0,
    currency VARCHAR(5) NOT NULL DEFAULT 'ETB',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    wallet_type VARCHAR(20) NOT NULL DEFAULT 'PERSONAL',
    daily_limit DECIMAL(19,2),
    monthly_limit DECIMAL(19,2),
    single_tx_limit DECIMAL(19,2),
    daily_spent DECIMAL(19,2) DEFAULT 0,
    monthly_spent DECIMAL(19,2) DEFAULT 0,
    daily_reset_at TIMESTAMP,
    monthly_reset_at TIMESTAMP,
    pin_hash VARCHAR(255),
    last_transaction_at TIMESTAMP,
    total_credited DECIMAL(19,2) DEFAULT 0,
    total_debited DECIMAL(19,2) DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions (
    id VARCHAR(50) PRIMARY KEY,
    reference VARCHAR(50) NOT NULL UNIQUE,
    wallet_id VARCHAR(50) NOT NULL REFERENCES wallets(id),
    user_id VARCHAR(50) NOT NULL,
    counterparty_wallet_id VARCHAR(50),
    counterparty_user_id VARCHAR(50),
    type VARCHAR(30) NOT NULL,
    direction VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    fee DECIMAL(19,2) DEFAULT 0,
    currency VARCHAR(5) NOT NULL DEFAULT 'ETB',
    balance_before DECIMAL(19,2),
    balance_after DECIMAL(19,2),
    description VARCHAR(500),
    narration VARCHAR(500),
    external_reference VARCHAR(100),
    channel VARCHAR(30),
    metadata TEXT,
    ip_address VARCHAR(45),
    device_id VARCHAR(100),
    failure_reason VARCHAR(500),
    reversed_at TIMESTAMP,
    reversed_by VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_wallet_user ON wallets(user_id);
CREATE INDEX idx_wallet_number ON wallets(wallet_number);
CREATE INDEX idx_wallet_status ON wallets(status);
CREATE INDEX idx_tx_wallet ON transactions(wallet_id);
CREATE INDEX idx_tx_user ON transactions(user_id);
CREATE INDEX idx_tx_ref ON transactions(reference);
CREATE INDEX idx_tx_status ON transactions(status);
CREATE INDEX idx_tx_created ON transactions(created_at);