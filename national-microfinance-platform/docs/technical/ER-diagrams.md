# Entity-Relationship Diagrams

## Core Banking Entities

### User Service
```
User
- id: UUID (PK)
- username: String (UNIQUE)
- email: String (UNIQUE)
- phone: String
- role: Enum (CUSTOMER, AGENT, ADMIN)
- status: Enum (ACTIVE, SUSPENDED, CLOSED)
- created_at: Timestamp
- updated_at: Timestamp

UserProfile
- id: UUID (PK)
- user_id: UUID (FK -> User)
- first_name: String
- last_name: String
- date_of_birth: Date
- address: Text
- occupation: String
```

### Identity-KYC Service
```
KYCDocument
- id: UUID (PK)
- user_id: UUID
- document_type: Enum (NATIONAL_ID, PASSPORT, DRIVERS_LICENSE)
- document_number: String
- verification_status: Enum (PENDING, VERIFIED, REJECTED)
- verified_at: Timestamp
- verified_by: UUID

BiometricData
- id: UUID (PK)
- user_id: UUID
- fingerprint_hash: String
- face_encoding: Binary
- created_at: Timestamp
```

### Wallet Service
```
Wallet
- id: UUID (PK)
- user_id: UUID
- account_number: String (UNIQUE)
- balance: Decimal(18,2)
- currency: String (default: KES)
- status: Enum (ACTIVE, FROZEN, CLOSED)
- created_at: Timestamp

WalletTransaction
- id: UUID (PK)
- wallet_id: UUID (FK -> Wallet)
- transaction_type: Enum (CREDIT, DEBIT)
- amount: Decimal(18,2)
- balance_before: Decimal(18,2)
- balance_after: Decimal(18,2)
- reference: String
- description: Text
- created_at: Timestamp
```

### Payment Service
```
Payment
- id: UUID (PK)
- source_wallet_id: UUID
- destination_wallet_id: UUID
- amount: Decimal(18,2)
- currency: String
- payment_method: Enum (WALLET, MOBILE_MONEY, BANK)
- status: Enum (PENDING, COMPLETED, FAILED, REVERSED)
- transaction_reference: String (UNIQUE)
- initiated_at: Timestamp
- completed_at: Timestamp

PaymentGatewayLog
- id: UUID (PK)
- payment_id: UUID (FK -> Payment)
- gateway: String (MPESA, AIRTEL_MONEY)
- request_payload: JSONB
- response_payload: JSONB
- status_code: Integer
- created_at: Timestamp
```

### Loan Service
```
LoanProduct
- id: UUID (PK)
- name: String
- description: Text
- min_amount: Decimal(18,2)
- max_amount: Decimal(18,2)
- interest_rate: Decimal(5,2)
- term_months: Integer
- collateral_required: Boolean

LoanApplication
- id: UUID (PK)
- user_id: UUID
- product_id: UUID (FK -> LoanProduct)
- amount_requested: Decimal(18,2)
- term_months: Integer
- purpose: Text
- status: Enum (PENDING, APPROVED, DISBURSED, REJECTED)
- applied_at: Timestamp
- approved_at: Timestamp

Loan
- id: UUID (PK)
- application_id: UUID (FK -> LoanApplication)
- user_id: UUID
- principal: Decimal(18,2)
- interest_rate: Decimal(5,2)
- total_amount: Decimal(18,2)
- outstanding_balance: Decimal(18,2)
- status: Enum (ACTIVE, PAID, DEFAULTED)
- disbursed_at: Timestamp
- maturity_date: Date

LoanRepayment
- id: UUID (PK)
- loan_id: UUID (FK -> Loan)
- amount: Decimal(18,2)
- principal_paid: Decimal(18,2)
- interest_paid: Decimal(18,2)
- balance_after: Decimal(18,2)
- payment_reference: String
- paid_at: Timestamp
```

### Savings Service
```
SavingsAccount
- id: UUID (PK)
- user_id: UUID
- account_number: String (UNIQUE)
- product_id: UUID (FK -> SavingsProduct)
- balance: Decimal(18,2)
- interest_rate: Decimal(5,2)
- status: Enum (ACTIVE, DORMANT, CLOSED)
- opened_at: Timestamp

SavingsProduct
- id: UUID (PK)
- name: String
- description: Text
- min_balance: Decimal(18,2)
- interest_rate: Decimal(5,2)
- calculation_method: Enum (DAILY, MONTHLY, QUARTERLY)

SavingsTransaction
- id: UUID (PK)
- account_id: UUID (FK -> SavingsAccount)
- transaction_type: Enum (DEPOSIT, WITHDRAWAL, INTEREST)
- amount: Decimal(18,2)
- balance_before: Decimal(18,2)
- balance_after: Decimal(18,2)
- reference: String
- created_at: Timestamp
```

### Cooperative Service
```
Cooperative
- id: UUID (PK)
- name: String
- registration_number: String (UNIQUE)
- address: Text
- total_members: Integer
- total_shares: Decimal(18,2)
- status: Enum (ACTIVE, SUSPENDED, DISSOLVED)
- registered_at: Timestamp

CooperativeMember
- id: UUID (PK)
- cooperative_id: UUID (FK -> Cooperative)
- user_id: UUID
- member_number: String
- shares_owned: Decimal(18,2)
- total_contributions: Decimal(18,2)
- joined_at: Timestamp
- status: Enum (ACTIVE, SUSPENDED, EXITED)

CooperativeLoan
- id: UUID (PK)
- cooperative_id: UUID (FK -> Cooperative)
- member_id: UUID (FK -> CooperativeMember)
- amount: Decimal(18,2)
- interest_rate: Decimal(5,2)
- outstanding_balance: Decimal(18,2)
- status: Enum (ACTIVE, PAID, DEFAULTED)
```

### Credit Scoring Service
```
CreditScore
- id: UUID (PK)
- user_id: UUID
- score: Integer (300-850)
- grade: Enum (EXCELLENT, GOOD, FAIR, POOR)
- factors: JSONB
- calculated_at: Timestamp

CreditHistory
- id: UUID (PK)
- user_id: UUID
- event_type: Enum (LOAN_TAKEN, REPAYMENT_MADE, DEFAULT)
- amount: Decimal(18,2)
- impact_score: Integer
- event_date: Timestamp
```

### Agent Banking Service
```
Agent
- id: UUID (PK)
- user_id: UUID
- agent_code: String (UNIQUE)
- business_name: String
- location: JSONB (lat, lng)
- commission_rate: Decimal(5,2)
- status: Enum (ACTIVE, SUSPENDED, TERMINATED)
- registered_at: Timestamp

AgentTransaction
- id: UUID (PK)
- agent_id: UUID (FK -> Agent)
- customer_id: UUID
- transaction_type: Enum (DEPOSIT, WITHDRAWAL, LOAN_DISBURSEMENT)
- amount: Decimal(18,2)
- commission: Decimal(18,2)
- reference: String
- created_at: Timestamp

AgentFloat
- id: UUID (PK)
- agent_id: UUID (FK -> Agent)
- balance: Decimal(18,2)
- min_balance: Decimal(18,2)
- max_balance: Decimal(18,2)
- updated_at: Timestamp
```

### Fraud Detection Service
```
FraudCase
- id: UUID (PK)
- user_id: UUID
- transaction_id: UUID
- fraud_type: Enum (ACCOUNT_TAKEOVER, IDENTITY_THEFT, TRANSACTION_FRAUD)
- risk_score: Decimal(5,2)
- status: Enum (FLAGGED, INVESTIGATING, CONFIRMED, DISMISSED)
- flagged_at: Timestamp

FraudRule
- id: UUID (PK)
- name: String
- description: Text
- rule_expression: Text
- threshold: Decimal(5,2)
- action: Enum (FLAG, BLOCK, NOTIFY)
- active: Boolean
```

### Notification Service (MongoDB)
```
Notification (MongoDB)
- _id: ObjectId
- user_id: String
- channel: String (SMS, EMAIL, PUSH, IN_APP)
- template: String
- subject: String
- body: Text
- status: String (PENDING, SENT, FAILED, DELIVERED)
- sent_at: Date
- delivered_at: Date
- metadata: Object
```

### Audit Service
```
AuditLog
- id: UUID (PK)
- user_id: UUID
- service_name: String
- action: String
- entity_type: String
- entity_id: UUID
- old_value: JSONB
- new_value: JSONB
- ip_address: String
- user_agent: String
- created_at: Timestamp
```

### Offline Sync Service
```
SyncQueue
- id: UUID (PK)
- device_id: String
- user_id: UUID
- operation: Enum (CREATE, UPDATE, DELETE)
- entity_type: String
- entity_data: JSONB
- status: Enum (PENDING, SYNCED, FAILED, CONFLICT)
- created_at: Timestamp
- synced_at: Timestamp

DeviceRegistration
- id: UUID (PK)
- device_id: String (UNIQUE)
- user_id: UUID
- device_type: String
- last_sync_at: Timestamp
- status: Enum (ACTIVE, REVOKED)
```

### Financial Literacy Service (MongoDB)
```
Course (MongoDB)
- _id: ObjectId
- title: String
- description: String
- level: String (BEGINNER, INTERMEDIATE, ADVANCED)
- duration_minutes: Number
- created_at: Date
- lessons: Array[Lesson]

UserProgress (MongoDB)
- _id: ObjectId
- user_id: String
- course_id: String
- completed_lessons: Array[String]
- quiz_scores: Array[Object]
- progress_percentage: Number
- started_at: Date
- completed_at: Date
```

### Document Management Service (MongoDB)
```
Document (MongoDB)
- _id: ObjectId
- user_id: String
- document_type: String
- file_name: String
- file_path: String
- file_size: Number
- mime_type: String
- status: String (PENDING, VERIFIED, REJECTED)
- uploaded_at: Date
- verified_at: Date
- verified_by: String
- verification_notes: String
```

### Admin Service
```
SystemConfig
- id: UUID (PK)
- config_key: String (UNIQUE)
- config_value: String
- data_type: Enum (STRING, NUMBER, BOOLEAN, JSON)
- description: Text
- updated_at: Timestamp

AdminUser
- id: UUID (PK)
- username: String (UNIQUE)
- email: String (UNIQUE)
- role: Enum (SUPER_ADMIN, ADMIN, SUPPORT, AUDITOR)
- permissions: JSONB
- status: Enum (ACTIVE, SUSPENDED)
- last_login_at: Timestamp

AuditAction
- id: UUID (PK)
- admin_user_id: UUID (FK -> AdminUser)
- action: String
- entity_type: String
- entity_id: UUID
- details: JSONB
- ip_address: String
- created_at: Timestamp
```

## Relationships Summary

- **User** has many **Wallets**, **Loans**, **SavingsAccounts**, **CooperativeMemberships**
- **Wallet** has many **WalletTransactions**
- **Payment** references **Wallet** (source/destination)
- **Loan** belongs to **LoanProduct** and **User**, has many **LoanRepayments**
- **SavingsAccount** belongs to **SavingsProduct** and **User**, has many **SavingsTransactions**
- **Cooperative** has many **CooperativeMembers** and **CooperativeLoans**
- **Agent** has **AgentFloat** and many **AgentTransactions**
- **FraudCase** references **User** and transaction
- **CreditScore** belongs to **User**, influenced by **CreditHistory**
