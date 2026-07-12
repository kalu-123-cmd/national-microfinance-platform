# Sprint Plan - National Digital Microfinance Platform

## Team Structure

| Role | Count | Responsibilities |
|------|-------|-----------------|
| Tech Lead / Architect | 1 | Architecture decisions, code reviews, PR approvals |
| Backend Engineers | 4 | Service implementation (Java/Spring Boot) |
| Frontend Engineers | 2 | Mobile app (React Native), Admin dashboard (React) |
| DevOps Engineer | 1 | CI/CD, K8s, monitoring |
| QA Engineer | 1 | Test automation, regression testing |
| Product Manager | 1 | Backlog grooming, stakeholder communication |
| Business Analyst | 1 | Requirements, UAT |

**Total Team Size:** 11 people

---

## Sprint Cadence
- **Sprint Length:** 2 weeks
- **Ceremonies:**
  - Sprint Planning: Monday of week 1 (2 hours)
  - Daily Standup: 15 minutes, 9:00 AM EAT
  - Sprint Review + Retrospective: Last Friday (3 hours)

---

## Sprint 1: Auth & User Foundation (Weeks 1-2)
**Goal:** Working authentication and user management in staging

| Story | Points | Assignee |
|-------|--------|----------|
| Auth service: register, login, OTP, refresh tokens | 8 | BE-1 |
| User service: CRUD, profile management | 8 | BE-2 |
| JWT filter in API Gateway | 5 | BE-1 |
| Rate limiting in API Gateway | 3 | BE-3 |
| Database migrations for auth + user | 3 | BE-4 |
| Docker Compose for local dev | 5 | DevOps |
| Eureka + Config Server setup | 3 | DevOps |
| Postman collection for Auth + User APIs | 3 | QA |

**Total:** 38 points | **Target Velocity:** 40 points

---

## Sprint 2: KYC & Identity (Weeks 3-4)
**Goal:** Users can complete KYC application

| Story | Points | Assignee |
|-------|--------|----------|
| KYC application workflow | 8 | BE-1 |
| Automated verification checks (stub) | 5 | BE-2 |
| Document upload + storage | 5 | BE-3 |
| Manual review queue (admin) | 5 | BE-4 |
| KYC status webhooks | 3 | BE-1 |
| CI/CD GitHub Actions pipeline | 8 | DevOps |
| KYC E2E test suite | 5 | QA |

**Total:** 39 points

---

## Sprint 3: Wallet & Transactions (Weeks 5-6)
**Goal:** Users can hold money and transact

| Story | Points | Assignee |
|-------|--------|----------|
| Wallet creation on user registration | 5 | BE-2 |
| Credit/debit/transfer operations | 13 | BE-1 + BE-2 |
| Transaction history pagination | 5 | BE-3 |
| Transaction limits enforcement | 5 | BE-4 |
| Wallet freeze/unfreeze | 3 | BE-1 |
| Mobile app: wallet screen | 8 | FE-1 |
| Wallet API integration tests | 5 | QA |

**Total:** 44 points

---

## Sprint 4: Payments (Weeks 7-8)
**Goal:** Bill payments and P2P transfers working

| Story | Points | Assignee |
|-------|--------|----------|
| Bill payment service | 8 | BE-1 |
| Merchant payment integration | 8 | BE-2 |
| Airtime purchase | 5 | BE-3 |
| Payment status webhooks | 5 | BE-4 |
| Mobile app: payments screen | 8 | FE-1 |
| Payment reconciliation report | 5 | BE-1 |
| Payment load testing | 5 | QA + DevOps |

**Total:** 44 points

---

## Sprint 5: Loan Service (Weeks 9-10)
**Goal:** Users can apply and receive micro-loans

| Story | Points | Assignee |
|-------|--------|----------|
| Loan application flow | 8 | BE-2 |
| Approval workflow (loan officer) | 8 | BE-1 |
| EMI calculation engine | 5 | BE-3 |
| Repayment schedule generation | 5 | BE-4 |
| Loan disbursement to wallet | 5 | BE-1 |
| Repayment processing | 5 | BE-2 |
| Admin: loan dashboard | 8 | FE-2 |
| Loan E2E tests | 5 | QA |

**Total:** 49 points

---

## Sprint 6: Notifications & Fraud (Weeks 11-12)
**Goal:** All transactions trigger notifications; fraud protection active

| Story | Points | Assignee |
|-------|--------|----------|
| SMS notification via Africa's Talking | 8 | BE-3 |
| Email notifications (AWS SES) | 5 | BE-4 |
| Kafka event listeners (all services) | 8 | BE-1 |
| Fraud detection rules engine | 8 | BE-2 |
| Fraud alert dashboard | 5 | FE-2 |
| Transaction alert SMS template | 3 | BE-3 |
| Fraud detection unit tests | 5 | QA |

**Total:** 42 points

---

## Sprint 7: Savings & Cooperative (Weeks 13-14)
**Goal:** Group savings and individual savings accounts

| Story | Points | Assignee |
|-------|--------|----------|
| Savings accounts CRUD | 8 | BE-3 |
| Fixed deposit products | 5 | BE-4 |
| Interest calculation (daily accrual) | 8 | BE-1 |
| Cooperative group management | 8 | BE-2 |
| ROSCA contribution tracking | 8 | BE-3 |
| Group loan disbursement | 5 | BE-4 |
| Savings mobile screens | 8 | FE-1 |

**Total:** 50 points

---

## Sprint 8: Agent Banking & Credit Scoring (Weeks 15-16)
**Goal:** Agent network live; credit scoring operational

| Story | Points | Assignee |
|-------|--------|----------|
| Agent registration and onboarding | 8 | BE-1 |
| Cash-in / cash-out via agent | 8 | BE-2 |
| Float management | 5 | BE-3 |
| Agent commission calculation | 5 | BE-4 |
| Credit scoring algorithm v1 | 13 | BE-1 + BE-2 |
| Credit score API | 3 | BE-3 |
| Agent mobile app screens | 8 | FE-1 |

**Total:** 50 points

---

## Sprint 9-10: USSD + Voice Banking (Weeks 17-20)
**Goal:** Rural users accessible via feature phones

| Story | Points | Assignee |
|-------|--------|----------|
| USSD gateway integration | 13 | BE-1 + BE-3 |
| USSD balance check | 3 | BE-2 |
| USSD money transfer | 8 | BE-2 |
| USSD bill payment | 5 | BE-4 |
| USSD mini statement | 3 | BE-1 |
| Amharic language support in USSD | 8 | BE-3 |
| IVR integration | 8 | BE-4 |
| Offline sync service | 13 | BE-1 + BE-2 |

**Total:** 61 points over 2 sprints

---

## Sprint 11-12: Analytics, Reporting & Compliance (Weeks 21-24)
**Goal:** NBE regulatory reports; business intelligence dashboard

| Story | Points | Assignee |
|-------|--------|----------|
| Regulatory report generator (NBE format) | 13 | BE-1 + BE-4 |
| Transaction analytics dashboard | 8 | BE-3 |
| User acquisition funnel | 5 | BE-2 |
| Portfolio risk report | 8 | BE-1 |
| AML transaction monitoring report | 8 | BE-4 |
| Grafana dashboards for all services | 8 | DevOps |
| Audit log retention policy | 5 | BE-3 |
| Performance testing suite | 8 | QA + DevOps |

**Total:** 63 points over 2 sprints

---

## Definition of Done (DoD)

A story is DONE when:
- [ ] Code written and peer-reviewed (2 approvals)
- [ ] Unit tests written (min 80% coverage)
- [ ] Integration tests passing
- [ ] API documented in Swagger
- [ ] Database migration tested on clean DB
- [ ] Deployed to staging environment
- [ ] QA sign-off
- [ ] No P0/P1 bugs open
- [ ] Performance acceptable (< 200ms p95 for writes, < 100ms for reads)
