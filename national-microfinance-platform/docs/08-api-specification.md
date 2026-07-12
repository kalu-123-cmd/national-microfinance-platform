# API Specification - National Digital Microfinance Platform

## Base URL
- **Production:** `https://api.microfinance.et`
- **Staging:** `https://staging-api.microfinance.et`
- **Local:** `http://localhost:8080`

## Authentication
All endpoints (except auth) require a Bearer JWT token:
```
Authorization: Bearer <access_token>
```

---

## 1. Auth Service (port 8081) — `/api/v1/auth`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/register` | Register user credentials | No |
| POST | `/login` | Login with password or PIN | No |
| POST | `/otp/send` | Send OTP to phone/email | No |
| POST | `/otp/verify` | Verify OTP and get tokens | No |
| POST | `/refresh` | Refresh access token | No |
| POST | `/password/change` | Change password | Yes |
| POST | `/pin/change` | Change PIN | Yes |
| POST | `/logout` | Logout and revoke token | Yes |
| POST | `/sessions/revoke-all` | Revoke all sessions | Yes |

### Login Request
```json
{
  "identifier": "+251911234567",
  "password": "SecurePass123!",
  "loginMethod": "PASSWORD",
  "deviceId": "device-uuid",
  "deviceInfo": "iPhone 15 iOS 17.0"
}
```

### Auth Response
```json
{
  "userId": "USER-2026-000001",
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "issuedAt": "2026-07-05T10:00:00Z",
  "expiresAt": "2026-07-05T11:00:00Z"
}
```

---

## 2. User Service (port 8082) — `/api/v1/users`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/` | Create user profile | Yes |
| GET | `/{userId}` | Get user by ID | Yes |
| GET | `/phone/{phoneNumber}` | Get user by phone | Yes |
| PUT | `/{userId}` | Update user profile | Yes |
| GET | `?page=0&size=20` | List all users (admin) | Admin |
| GET | `/search?query=abel` | Search users | Admin |
| PUT | `/{userId}/status` | Update user status | Admin |
| PUT | `/{userId}/activate` | Activate user | Admin |
| POST | `/{userId}/documents` | Upload KYC document | Yes |
| GET | `/{userId}/documents` | Get user documents | Yes |
| PUT | `/documents/{docId}/verify` | Verify document | KYC Officer |
| PUT | `/documents/{docId}/reject` | Reject document | KYC Officer |

---

## 3. KYC Service (port 8083) — `/api/v1/kyc`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/initiate` | Start KYC application | Yes |
| POST | `/{applicationId}/submit` | Submit for review | Yes |
| GET | `/{applicationId}` | Get application | Yes |
| GET | `/user/{userId}` | Get user applications | Yes |
| GET | `/pending` | Pending review queue | KYC Officer |
| POST | `/{applicationId}/review` | Manual review decision | KYC Officer |
| GET | `/{applicationId}/checks` | Get verification checks | Yes |

---

## 4. Wallet Service (port 8084) — `/api/v1/wallets`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/` | Create wallet | Yes |
| GET | `/user/{userId}` | Get wallet by user | Yes |
| GET | `/number/{walletNumber}` | Get wallet by number | Yes |
| POST | `/credit` | Credit wallet | Internal |
| POST | `/debit` | Debit wallet | Internal |
| POST | `/transfer` | Transfer between wallets | Yes |
| GET | `/{walletId}/transactions` | Transaction history | Yes |
| POST | `/{walletId}/freeze` | Freeze wallet | Admin |
| POST | `/{walletId}/unfreeze` | Unfreeze wallet | Admin |

### Transfer Request
```json
{
  "fromWalletId": "wallet-uuid",
  "toWalletId": "wallet-uuid-2",
  "amount": 500.00,
  "description": "Rent payment",
  "channel": "MOBILE"
}
```

---

## 5. Payment Service (port 8085) — `/api/v1/payments`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/bill` | Pay a bill | Yes |
| POST | `/merchant` | Merchant payment | Yes |
| POST | `/airtime` | Buy airtime | Yes |
| GET | `/{paymentId}` | Get payment | Yes |
| GET | `/user/{userId}` | User payment history | Yes |

### Bill Payment Request
```json
{
  "userId": "USER-2026-000001",
  "walletId": "wallet-uuid",
  "billerId": "ETHIO_ELECTRIC",
  "billerName": "Ethiopian Electric Utility",
  "accountNumber": "5678901234",
  "amount": 350.00,
  "description": "Electricity bill - June 2026",
  "channel": "MOBILE"
}
```

---

## 6. Loan Service (port 8086) — `/api/v1/loans`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/` | Apply for loan | Yes |
| GET | `/{loanId}` | Get loan details | Yes |
| GET | `/user/{userId}` | User loans | Yes |
| PUT | `/{loanId}/approve` | Approve loan | Loan Officer |
| PUT | `/{loanId}/reject` | Reject loan | Loan Officer |
| PUT | `/{loanId}/disburse` | Disburse loan | Loan Officer |
| POST | `/repay` | Make repayment | Yes |
| GET | `/{loanId}/schedule` | Repayment schedule | Yes |

### Loan Application Request
```json
{
  "userId": "USER-2026-000001",
  "walletId": "wallet-uuid",
  "requestedAmount": 10000.00,
  "tenureMonths": 12,
  "loanType": "PERSONAL",
  "purpose": "Small business working capital"
}
```

### Loan Response
```json
{
  "id": "loan-uuid",
  "loanNumber": "LOAN-1720123456789",
  "status": "ACTIVE",
  "requestedAmount": 10000.00,
  "approvedAmount": 10000.00,
  "interestRate": 18.0,
  "tenureMonths": 12,
  "totalRepayable": 11969.43,
  "totalInterest": 1969.43,
  "outstandingBalance": 11969.43,
  "firstRepaymentDate": "2026-08-05",
  "maturityDate": "2027-07-05"
}
```

---

## 7. Notification Service (port 8090) — `/api/v1/notifications`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/user/{userId}` | User notifications | Yes |
| POST | `/send` | Send notification (internal) | Internal |

---

## 8. Fraud Detection Service (port 8091) — `/api/v1/fraud`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/alerts` | Get all open alerts | Admin |
| GET | `/alerts/user/{userId}` | User fraud alerts | Admin |
| PUT | `/alerts/{alertId}/resolve` | Resolve alert | Admin |
| PUT | `/alerts/{alertId}/false-positive` | Mark false positive | Admin |

---

## Error Responses

All errors follow this format:
```json
{
  "success": false,
  "message": "User not found with ID: USER-2026-999999",
  "errorCode": "RESOURCE_NOT_FOUND",
  "timestamp": "2026-07-05T10:30:00Z",
  "path": "/api/v1/users/USER-2026-999999"
}
```

### HTTP Status Codes
| Code | Meaning |
|------|---------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request / Validation Error |
| 401 | Unauthorized / Invalid Token |
| 403 | Forbidden / Insufficient Permission |
| 404 | Not Found |
| 409 | Conflict (duplicate) |
| 422 | Unprocessable Entity |
| 429 | Too Many Requests (rate limited) |
| 500 | Internal Server Error |
| 503 | Service Unavailable |

---

## Rate Limits
| Endpoint Group | Limit |
|----------------|-------|
| Auth endpoints | 5 requests/minute |
| User endpoints | 100 requests/minute |
| Wallet/Payment | 60 requests/minute |
| General API | 200 requests/minute |
| Anonymous | 20 requests/minute |
