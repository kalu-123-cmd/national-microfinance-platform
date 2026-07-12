CREATE TABLE credit_scores (
    id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    score INT NOT NULL,
    grade VARCHAR(10),
    risk_level VARCHAR(20),
    recommended_limit DECIMAL(19,2),
    repayment_history_score INT,
    credit_utilization_score INT,
    transaction_volume_score INT,
    account_age_score INT,
    kyc_compliance_score INT,
    model VARCHAR(20),
    explanation TEXT,
    valid_until TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_cs_user ON credit_scores(user_id);
CREATE INDEX idx_cs_created ON credit_scores(created_at);