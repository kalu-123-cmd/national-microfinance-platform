CREATE TABLE loan_applications (
    id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    loan_product_id VARCHAR(50),
    amount DECIMAL(19,2) NOT NULL,
    interest_rate DECIMAL(5,2),
    tenure_months INT,
    purpose VARCHAR(255),
    status VARCHAR(30) DEFAULT 'DRAFT',
    credit_score INT,
    risk_level VARCHAR(20),
    approved_amount DECIMAL(19,2),
    approved_by VARCHAR(50),
    approved_at TIMESTAMP,
    rejected_reason TEXT,
    disbursed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE loan_repayments (
    id VARCHAR(50) PRIMARY KEY,
    loan_id VARCHAR(50) NOT NULL,
    due_date DATE NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    principal_amount DECIMAL(19,2),
    interest_amount DECIMAL(19,2),
    status VARCHAR(30) DEFAULT 'PENDING',
    paid_amount DECIMAL(19,2) DEFAULT 0,
    paid_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_loan_user ON loan_applications(user_id);
CREATE INDEX idx_loan_status ON loan_applications(status);
CREATE INDEX idx_repay_loan ON loan_repayments(loan_id);