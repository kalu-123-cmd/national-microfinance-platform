# Database Schema - National Digital Microfinance Platform

## Database Strategy: DB-per-Service
Each microservice owns its database. No cross-service JOINs. Data consistency through events.

---

## 1. Auth Database (`auth_db` - PostgreSQL)

### `user_credentials`
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Auto-generated UUID |
| user_id | VARCHAR(50) | NOT NULL, UNIQUE | Reference to user-service |
| phone_number | VARCHAR(20) | NOT NULL, UNIQUE | +251XXXXXXXXX |
| email | VARCHAR(255) | UNIQUE | Optional |
| password_hash | VARCHAR(255) | NOT NULL | BCrypt(12) |
| pin_hash | VARCHAR(255) | NOT NULL | BCrypt(12) |
| enabled | BOOLEAN | NOT NULL DEFAULT TRUE | Account enabled |
| failed_login_attempts | INT | DEFAULT 0 | Lockout counter |
| last_failed_login_at | TIMESTAMP | | Last failure timestamp |
| account_locked_until | TIMESTAMP | | Lock expiry |
| last_login_at | TIMESTAMP | | Last successful login |

### `otp_records`
| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PK |
| recipient | VARCHAR(255) | NOT NULL (phone or email) |
| otp_hash | VARCHAR(255) | NOT NULL (SHA-256) |
| purpose | VARCHAR(50) | LOGIN/REGISTRATION/PASSWORD_RESET |
| expires_at | TIMESTAMP | NOT NULL |
| verified | BOOLEAN | DEFAULT FALSE |
| verification_attempts | INT | DEFAULT 0 (max 3) |

### `refresh_tokens`
| Column | Type | Description |
|--------|------|-------------|
| id | UUID | PK |
| user_id | VARCHAR(50) | Owner |
| token_hash | VARCHAR(255) | SHA-256 of raw token |
| device_id | VARCHAR(255) | Device identifier |
| expires_at | TIMESTAMP | 30-day expiry |
| revoked | BOOLEAN | Rotation flag |
| revocation_reason | VARCHAR(100) | USER_LOGOUT/TOKEN_ROTATED/ALL_SESSIONS_REVOKED |

---

## 2. User Database (`user_db` - PostgreSQL)

### `users`
| Column | Type | Description |
|--------|------|-------------|
| id | VARCHAR(50) | PK, format: USER-YYYY-XXXXXX |
| phone_number | VARCHAR(20) | UNIQUE, +251XXXXXXXXX |
| email | VARCHAR(255) | UNIQUE |
| first_name | VARCHAR(100) | NOT NULL |
| last_name | VARCHAR(100) | NOT NULL |
| date_of_birth | DATE | |
| gender | VARCHAR(10) | MALE/FEMALE/OTHER |
| national_id | VARCHAR(50) | UNIQUE |
| status | VARCHAR(20) | PENDING_VERIFICATION/ACTIVE/SUSPENDED/BLOCKED/CLOSED |
| kyc_status | VARCHAR(20) | NOT_STARTED/PENDING/APPROVED/REJECTED/EXPIRED |
| account_type | VARCHAR(20) | BASIC/STANDARD/PREMIUM/MERCHANT/AGENT |
| region | VARCHAR(100) | Ethiopian region |
| preferred_language | VARCHAR(10) | am/en/or/ti |

### `user_documents`
| Column | Type | Description |
|--------|------|-------------|
| id | VARCHAR(50) | PK |
| user_id | VARCHAR(50) | FK → users.id |
| document_type | VARCHAR(50) | NATIONAL_ID/PASSPORT/DRIVERS_LICENSE/etc. |
| status | VARCHAR(20) | PENDING/APPROVED/REJECTED/EXPIRED |
| file_path | VARCHAR(500) | Relative storage path |
| verified_by | VARCHAR(50) | KYC officer ID |
| verified_at | TIMESTAMP | |
| UNIQUE(user_id, document_type) | | One doc per type per user |

---

## 3. KYC Database (`kyc_db` - PostgreSQL)

### `kyc_applications`
| Column | Type | Description |
|--------|------|-------------|
| id | VARCHAR(50) | PK |
| user_id | VARCHAR(50) | NOT NULL |
| application_number | VARCHAR(30) | UNIQUE, format: KYC-XXXXXX |
| kyc_tier | VARCHAR(20) | TIER_1/TIER_2/TIER_3 |
| status | VARCHAR(30) | DRAFT/SUBMITTED/UNDER_REVIEW/PENDING_REVIEW/APPROVED/REJECTED |
| compliance_score | INT | 0-100 |
| risk_level | VARCHAR(20) | LOW/MEDIUM/HIGH/VERY_HIGH |
| pep_check_result | VARCHAR(20) | CLEAR/MATCH/POTENTIAL_MATCH |
| sanctions_check_result | VARCHAR(20) | CLEAR/MATCH/POTENTIAL_MATCH |
| biometric_verified | BOOLEAN | |
| expires_at | TIMESTAMP | 2-year validity |

### `verification_checks`
| Column | Type | Description |
|--------|------|-------------|
| id | VARCHAR(50) | PK |
| application_id | VARCHAR(50) | FK → kyc_applications |
| check_type | VARCHAR(50) | ID_DOCUMENT/FACE_MATCH/LIVENESS/PEP_SCREENING/etc. |
| result | VARCHAR(30) | PASSED/FAILED/PENDING/ERROR/MANUAL_REVIEW |
| provider | VARCHAR(100) | JUMIO/ONFIDO/REFINITIV/INTERNAL |
| confidence_score | DOUBLE | 0.0-1.0 |
| duration_ms | BIGINT | Response time in ms |

---

## 4. Wallet Database (`wallet_db` - PostgreSQL)

### `wallets`
| Column | Type | Description |
|--------|------|-------------|
| id | VARCHAR(50) | PK |
| user_id | VARCHAR(50) | UNIQUE - one wallet per user |
| wallet_number | VARCHAR(20) | UNIQUE, 13-digit number |
| balance | DECIMAL(19,2) | Current balance |
| reserved_balance | DECIMAL(19,2) | Funds on hold |
| currency | VARCHAR(5) | ETB (Ethiopian Birr) |
| status | VARCHAR(20) | ACTIVE/FROZEN/SUSPENDED/CLOSED |
| daily_limit | DECIMAL(19,2) | Default: 50,000 ETB |
| monthly_limit | DECIMAL(19,2) | Default: 500,000 ETB |
| single_tx_limit | DECIMAL(19,2) | Default: 20,000 ETB |

### `transactions`
| Column | Type | Description |
|--------|------|-------------|
| id | VARCHAR(50) | PK |
| reference | VARCHAR(50) | UNIQUE, format: CRD/DBT/TRF-XXXXXXX |
| wallet_id | VARCHAR(50) | FK → wallets |
| type | VARCHAR(30) | DEPOSIT/WITHDRAWAL/TRANSFER/PAYMENT/LOAN_DISBURSEMENT/etc. |
| direction | VARCHAR(10) | CREDIT/DEBIT |
| status | VARCHAR(20) | PENDING/COMPLETED/FAILED/REVERSED |
| amount | DECIMAL(19,2) | |
| balance_before | DECIMAL(19,2) | Pre-transaction balance |
| balance_after | DECIMAL(19,2) | Post-transaction balance |
| channel | VARCHAR(30) | MOBILE/USSD/AGENT/API |

---

## 5. Loan Database (`loan_db` - PostgreSQL)

### `loan_applications`
| Column | Type | Description |
|--------|------|-------------|
| id | VARCHAR(50) | PK |
| loan_number | VARCHAR(30) | UNIQUE, format: LOAN-XXXXXXXXXX |
| user_id | VARCHAR(50) | Borrower |
| requested_amount | DECIMAL(19,2) | |
| approved_amount | DECIMAL(19,2) | May differ from requested |
| interest_rate | DECIMAL(5,2) | Annual rate % |
| tenure_months | INT | 1-60 months |
| loan_type | VARCHAR(20) | PERSONAL/BUSINESS/MICRO/etc. |
| status | VARCHAR(30) | SUBMITTED/APPROVED/ACTIVE/OVERDUE/CLOSED |
| outstanding_balance | DECIMAL(19,2) | Remaining amount |
| total_repayable | DECIMAL(19,2) | Principal + Interest |
| maturity_date | DATE | |

### `repayment_schedules`
| Column | Type | Description |
|--------|------|-------------|
| id | VARCHAR(50) | PK |
| loan_id | VARCHAR(50) | FK → loan_applications |
| installment_number | INT | 1..N |
| due_date | DATE | |
| total_amount | DECIMAL(19,2) | EMI |
| principal_amount | DECIMAL(19,2) | |
| interest_amount | DECIMAL(19,2) | |
| status | VARCHAR(20) | PENDING/PAID/PARTIAL/OVERDUE |

---

## Entity Relationship Summary

```
users (user-service)
  ├── user_credentials (auth-service) -- linked by userId
  ├── user_documents (user-service)
  ├── kyc_applications (kyc-service) -- linked by userId
  ├── wallets (wallet-service) -- 1:1 linked by userId
  │     └── transactions
  ├── loan_applications (loan-service)
  │     └── repayment_schedules
  ├── savings_accounts (savings-service)
  ├── payments (payment-service)
  └── credit_scores (credit-scoring-service)
```

---

## Indexes Strategy

Critical indexes on all foreign keys, status columns, phone numbers, dates (created_at, due_date, expires_at), and any field used in WHERE clauses or ORDER BY.

Composite indexes for common query patterns:
- `(user_id, status)` - most common query pattern
- `(status, created_at)` - admin dashboards
- `(wallet_id, direction, created_at)` - transaction reports
