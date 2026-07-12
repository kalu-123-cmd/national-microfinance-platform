# Sequence Diagrams - Key Workflows

## 1. User Registration & Onboarding Flow

```
Mobile App          API Gateway       Auth Service      User Service      KYC Service       Notification
     |                   |                 |                |                  |                  |
     |-- POST /users ---->|                 |                |                  |                  |
     |                   |-- forward ------>|                |                  |                  |
     |                   |                 |-- createUser -->|                  |                  |
     |                   |                 |                 |-- save user ---->|                  |
     |                   |                 |                 |<-- userId --------|                  |
     |                   |                 |<-- userId -------|                  |                  |
     |<-- 201 userId ----|                 |                 |-- publish event ->|                  |
     |                   |                 |                 |                  |                  |
     |-- POST /auth/register (credentials)>|                 |                  |                  |
     |                   |-- forward ------>|                 |                  |                  |
     |                   |                 |-- hash pwd+PIN ->|                  |                  |
     |                   |                 |-- save creds --->|                  |                  |
     |<-- 201 OK --------|                 |                 |                  |                  |
     |                   |                 |                 |                  |                  |
     |-- POST /auth/login ----------------->|                 |                  |                  |
     |                   |-- forward ------>|                 |                  |                  |
     |                   |                 |-- validate ----->|                  |                  |
     |                   |                 |-- generateJWT    |                  |                  |
     |<-- 200 {accessToken, refreshToken}--|                 |                  |                  |
     |                   |                 |                 |-- notify: welcome ->               |
     |                   |                 |                 |                  |-- SMS welcome --->|
```

---

## 2. Wallet Transfer Flow

```
Mobile App          API Gateway       Wallet Service    Fraud Service     Notification
     |                   |                 |                |                  |
     |-- POST /wallets/transfer ----------->|                |                  |
     |    {from, to, amount, PIN}           |                |                  |
     |                   |-- JWT validate   |                |                  |
     |                   |-- forward ------>|                |                  |
     |                   |                 |-- check balance |                  |
     |                   |                 |-- check limits  |                  |
     |                   |                 |-- debit source  |                  |
     |                   |                 |-- credit dest   |                  |
     |                   |                 |-- publish event >                  |
     |                   |                 |                 |-- analyze txn --->|
     |                   |                 |                 |-- risk score      |
     |<-- 200 {ref, status} <--------------|                 |                  |
     |                   |                 |                 |-- notify sender -->|
     |                   |                 |                 |-- notify receiver->|
```

---

## 3. Loan Application & Disbursement Flow

```
User App            API Gateway       Loan Service      Credit Service    Wallet Service    Notification
     |                   |                 |                |                  |                |
     |-- POST /loans ---------------------->|                |                  |                |
     |    {amount, tenure, purpose}         |                |                  |                |
     |                   |-- forward ------>|                |                  |                |
     |                   |                 |-- save SUBMITTED|                  |                |
     |                   |                 |-- request score >                  |                |
     |                   |                 |                 |-- calculate ----->|                |
     |                   |                 |<-- creditScore --|                  |                |
     |<-- 201 loanId -----|                 |                 |                  |                |
     |                   |                 |                 |                  |                |
     |   [Loan Officer reviews in Admin Dashboard]           |                  |                |
     |                   |                 |                 |                  |                |
     |-- PUT /loans/{id}/approve ---------->|                |                  |                |
     |                   |-- forward ------>|                |                  |                |
     |                   |                 |-- calc EMIs     |                  |                |
     |                   |                 |-- gen schedule  |                  |                |
     |                   |                 |-- status=APPROVED                  |                |
     |<-- 200 approved ---|                 |                 |                  |                |
     |                   |                 |                 |                  |                |
     |-- PUT /loans/{id}/disburse ---------->               |                  |                |
     |                   |-- forward ------>|                |                  |                |
     |                   |                 |-- publish loan-disbursed event ---->|                |
     |                   |                 |                 |  credit wallet -->|                |
     |                   |                 |                 |                  |-- notify user ->|
     |<-- 200 disbursed --|                 |                 |                  |                |
```

---

## 4. KYC Verification Flow

```
User App            API Gateway       KYC Service       External Providers    User Service
     |                   |                 |                    |                  |
     |-- POST /kyc/initiate --------------->|                    |                  |
     |                   |-- forward ------>|                    |                  |
     |                   |                 |-- save DRAFT ------->|                  |
     |<-- 201 applicationId <--------------|                    |                  |
     |                   |                 |                    |                  |
     |-- POST /kyc/{id}/submit ------------>|                    |                  |
     |                   |-- forward ------>|                    |                  |
     |                   |                 |-- status=SUBMITTED  |                  |
     |                   |                 |-- async checks start|                  |
     |<-- 200 SUBMITTED --|                 |                    |                  |
     |                   |                 |-- ID verify ------->| Jumio            |
     |                   |                 |-- liveness check -->| Onfido           |
     |                   |                 |-- PEP screen ------>| Refinitiv        |
     |                   |                 |-- sanctions check ->| Refinitiv        |
     |                   |                 |<-- results ---------|                  |
     |                   |                 |-- evaluate all      |                  |
     |                   |                 |-- if all PASS:      |                  |
     |                   |                 |  status=APPROVED    |                  |
     |                   |                 |-- update user KYC -->                  |
     |                   |                 |-- publish kyc-approved event           |
     |   [user gets SMS notification about KYC result]                             |
```

---

## 5. OTP Authentication Flow

```
User               API Gateway        Auth Service      Notification Service
  |                    |                   |                    |
  |-- POST /auth/otp/send ---------------->|                    |
  |    {recipient, purpose}                |                    |
  |                    |-- forward ------->|                    |
  |                    |                   |-- generate 6-digit OTP                |
  |                    |                   |-- hash with SHA-256 |                  |
  |                    |                   |-- save to DB        |                  |
  |                    |                   |-- publish to kafka ->|                  |
  |                    |                   |                    |-- send SMS ------->|
  |<-- 200 sent -------|                   |                    |                  |
  |                    |                   |                    |                  |
  | [User receives OTP on phone]           |                    |                  |
  |                    |                   |                    |                  |
  |-- POST /auth/otp/verify -------------->|                    |                  |
  |    {recipient, otpCode, purpose}       |                    |                  |
  |                    |-- forward ------->|                    |                  |
  |                    |                   |-- hash input OTP    |                  |
  |                    |                   |-- compare hashes    |                  |
  |                    |                   |-- mark verified     |                  |
  |                    |                   |-- if LOGIN:         |                  |
  |                    |                   |   generate JWT      |                  |
  |<-- 200 {accessToken, refreshToken} ----|                    |                  |
```
