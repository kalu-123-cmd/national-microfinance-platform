-- V1__init_user_tables.sql

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(50) PRIMARY KEY,
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(255),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    date_of_birth DATE,
    gender VARCHAR(10),
    marital_status VARCHAR(20),
    national_id VARCHAR(50),
    passport_number VARCHAR(50),
    occupation VARCHAR(100),
    monthly_income BIGINT,
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    region VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING_VERIFICATION',
    kyc_status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    preferred_language VARCHAR(10) DEFAULT 'en',
    account_type VARCHAR(20) NOT NULL,
    profile_picture_url VARCHAR(500),
    emergency_contact_name VARCHAR(200),
    emergency_contact_phone VARCHAR(20),
    emergency_contact_relationship VARCHAR(50),
    terms_accepted BOOLEAN,
    privacy_policy_accepted BOOLEAN,
    marketing_consent BOOLEAN DEFAULT FALSE,
    last_login_at TIMESTAMP,
    last_activity_at TIMESTAMP,
    device_tokens TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    
    CONSTRAINT uk_email UNIQUE (email),
    CONSTRAINT uk_national_id UNIQUE (national_id),
    CONSTRAINT uk_passport_number UNIQUE (passport_number),
    CONSTRAINT chk_gender CHECK (gender IN ('MALE', 'FEMALE', 'OTHER', 'PREFER_NOT_TO_SAY')),
    CONSTRAINT chk_marital_status CHECK (marital_status IN ('SINGLE', 'MARRIED', 'DIVORCED', 'WIDOWED', 'SEPARATED', 'UNKNOWN')),
    CONSTRAINT chk_status CHECK (status IN ('PENDING_VERIFICATION', 'ACTIVE', 'SUSPENDED', 'DEACTIVATED', 'BLOCKED', 'CLOSED')),
    CONSTRAINT chk_kyc_status CHECK (kyc_status IN ('NOT_STARTED', 'PENDING', 'APPROVED', 'REJECTED', 'EXPIRED', 'PARTIAL')),
    CONSTRAINT chk_account_type CHECK (account_type IN ('BASIC', 'STANDARD', 'PREMIUM', 'MERCHANT', 'AGENT'))
);

-- User Documents Table
CREATE TABLE IF NOT EXISTS user_documents (
    id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    document_number VARCHAR(100),
    document_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    mime_type VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    expiry_date DATE,
    issued_date DATE,
    issuing_authority VARCHAR(255),
    verification_notes TEXT,
    verified_by VARCHAR(50),
    verified_at TIMESTAMP,
    rejection_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_user_documents_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_document_type CHECK (document_type IN (
        'NATIONAL_ID', 'PASSPORT', 'DRIVERS_LICENSE', 'VOTER_ID',
        'UTILITY_BILL', 'BANK_STATEMENT', 'RENTAL_AGREEMENT', 'GOVERNMENT_LETTER',
        'SALARY_CERTIFICATE', 'EMPLOYMENT_LETTER', 'BUSINESS_REGISTRATION', 'TAX_RETURN',
        'PROFILE_PHOTO', 'SIGNATURE',
        'BIRTH_CERTIFICATE', 'MARRIAGE_CERTIFICATE', 'EDUCATIONAL_CERTIFICATE',
        'BUSINESS_LICENSE', 'TAX_ID_CERTIFICATE', 'MEMORANDUM_OF_ASSOCIATION'
    )),
    CONSTRAINT chk_document_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'EXPIRED')),
    CONSTRAINT uk_user_document_type UNIQUE (user_id, document_type)
);

-- Indexes for better query performance
CREATE INDEX idx_users_phone ON users(phone_number);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_kyc_status ON users(kyc_status);
CREATE INDEX idx_users_account_type ON users(account_type);
CREATE INDEX idx_users_region ON users(region);
CREATE INDEX idx_users_created_at ON users(created_at);
CREATE INDEX idx_users_national_id ON users(national_id);
CREATE INDEX idx_users_passport ON users(passport_number);

CREATE INDEX idx_documents_user_id ON user_documents(user_id);
CREATE INDEX idx_documents_type ON user_documents(document_type);
CREATE INDEX idx_documents_status ON user_documents(status);
CREATE INDEX idx_documents_expiry ON user_documents(expiry_date);

-- Update timestamp trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Triggers for updated_at
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_user_documents_updated_at
    BEFORE UPDATE ON user_documents
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();