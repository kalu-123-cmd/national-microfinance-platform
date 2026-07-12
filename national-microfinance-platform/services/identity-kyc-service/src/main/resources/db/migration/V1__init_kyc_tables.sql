CREATE TABLE IF NOT EXISTS kyc_applications (
    id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    application_number VARCHAR(30) NOT NULL UNIQUE,
    kyc_tier VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    submission_notes TEXT,
    reviewer_id VARCHAR(50),
    review_notes TEXT,
    rejection_reason TEXT,
    compliance_score INTEGER,
    risk_level VARCHAR(20),
    pep_check_result VARCHAR(20),
    sanctions_check_result VARCHAR(20),
    adverse_media_result VARCHAR(20),
    biometric_verified BOOLEAN,
    liveness_check_passed BOOLEAN,
    id_document_verified BOOLEAN,
    address_verified BOOLEAN,
    phone_verified BOOLEAN,
    email_verified BOOLEAN,
    submitted_at TIMESTAMP,
    reviewed_at TIMESTAMP,
    approved_at TIMESTAMP,
    expires_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS verification_checks (
    id VARCHAR(50) PRIMARY KEY,
    application_id VARCHAR(50) NOT NULL REFERENCES kyc_applications(id),
    user_id VARCHAR(50) NOT NULL,
    check_type VARCHAR(50) NOT NULL,
    result VARCHAR(30) NOT NULL,
    provider VARCHAR(100),
    provider_reference VARCHAR(100),
    confidence_score DOUBLE PRECISION,
    raw_response TEXT,
    details TEXT,
    error_code VARCHAR(50),
    error_message TEXT,
    executed_at TIMESTAMP NOT NULL,
    duration_ms BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_kyc_user ON kyc_applications(user_id);
CREATE INDEX idx_kyc_status ON kyc_applications(status);
CREATE INDEX idx_kyc_app_num ON kyc_applications(application_number);
CREATE INDEX idx_vc_app ON verification_checks(application_id);
CREATE INDEX idx_vc_user ON verification_checks(user_id);
CREATE INDEX idx_vc_type ON verification_checks(check_type);