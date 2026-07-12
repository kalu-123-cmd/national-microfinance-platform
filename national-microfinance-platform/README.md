# National Digital Microfinance Platform 🇪🇹

> A production-ready, cloud-native microservices platform for digital financial inclusion in Ethiopia — connecting the unbanked to savings, loans, payments, and insurance.

---

## Overview

This platform provides a complete digital banking infrastructure for microfinance institutions, cooperatives, and fintech operators in Ethiopia. Built on modern cloud-native technologies with full offline support for rural areas.

### Key Features
- **Digital Wallet** — Hold, send, receive Ethiopian Birr (ETB)
- **Micro-Loans** — Apply, approve, disburse, and repay loans automatically
- **Savings Products** — Individual and group (ROSCA) savings
- **Bill Payments** — Pay utilities, school fees, insurance via mobile
- **Agent Banking** — Cash-in/out through a network of field agents
- **USSD Banking** — Works on any phone, even without internet (*805#)
- **KYC Verification** — Automated biometric and document verification
- **Credit Scoring** — Behavioral credit scores for the unbanked
- **Fraud Detection** — Real-time rule-based fraud protection
- **Multi-language** — Amharic, Oromo, Tigrinya, English

---

## Architecture

```
                           ┌─────────────────────────┐
                           │      API Gateway          │
                           │  (JWT Auth, Rate Limit)   │
                           └───────────┬───────────────┘
                                       │
            ┌──────────────────────────┼──────────────────────────┐
            │                          │                           │
     ┌──────▼──────┐           ┌───────▼──────┐          ┌───────▼──────┐
     │   Auth      │           │    User      │          │   KYC        │
     │  Service    │           │   Service    │          │  Service     │
     │  port:8081  │           │  port:8082   │          │  port:8083   │
     └─────────────┘           └──────────────┘          └──────────────┘
            │                          │
     ┌──────▼──────┐           ┌───────▼──────┐
     │   Wallet    │           │   Loan       │
     │  Service    │           │  Service     │
     │  port:8084  │           │  port:8086   │
     └─────────────┘           └──────────────┘
```

**27 microservices** | **Java 21 + Spring Boot 3.2** | **PostgreSQL + MongoDB + Redis** | **Apache Kafka** | **Kubernetes**

---

## Service Catalog

| # | Service | Port | Database | Description |
|---|---------|------|----------|-------------|
| 1 | config-server | 8888 | — | Centralized config |
| 2 | discovery-server | 8761 | — | Eureka service registry |
| 3 | api-gateway | 8080 | Redis | API gateway + JWT auth |
| 4 | **auth-service** | 8081 | PostgreSQL | Login, OTP, PIN, JWT |
| 5 | **user-service** | 8082 | PostgreSQL | User profiles, documents |
| 6 | **identity-kyc-service** | 8083 | PostgreSQL | KYC verification |
| 7 | **wallet-service** | 8084 | PostgreSQL | Wallets, transactions |
| 8 | **payment-service** | 8088 | PostgreSQL | Bills, merchants, P2P |
| 9 | **loan-service** | 8086 | PostgreSQL | Loan lifecycle |
| 10 | **savings-service** | 8087 | PostgreSQL | Savings accounts |
| 11 | **cooperative-service** | 8089 | PostgreSQL | Group savings/loans |
| 12 | **agent-banking-service** | 8089 | PostgreSQL | Agent network |
| 13 | **notification-service** | 8090 | MongoDB | SMS/Email/Push |
| 14 | **fraud-detection-service** | 8091 | MongoDB | Real-time fraud detection |
| 15 | **credit-scoring-service** | 8092 | PostgreSQL | Credit scores |
| 16 | **ai-recommendation-service** | 8093 | MongoDB | Product recommendations |
| 17 | **audit-service** | 8094 | MongoDB | Audit trail |
| 18 | **reporting-service** | 8095 | PostgreSQL | Regulatory reports |
| 19 | **analytics-service** | 8096 | MongoDB | Business intelligence |
| 20 | **offline-sync-service** | 8097 | MongoDB | Offline transaction queue |
| 21 | **financial-literacy-service** | 8098 | MongoDB | Financial education |
| 22 | **voice-banking-service** | 8099 | MongoDB | USSD/IVR |
| 23 | **document-management-service** | 8100 | MongoDB | Document OCR/storage |
| 24 | **admin-service** | 8101 | PostgreSQL | Admin backend |

---

## Quick Start

### Prerequisites
- Docker 25+ and Docker Compose 2.24+
- Java 21+ and Maven 3.9+
- 8GB+ RAM (for all services locally)

```bash
# 1. Clone
git clone https://github.com/your-org/national-microfinance-platform.git
cd national-microfinance-platform

# 2. Configure
cp .env.example .env
# Edit .env with your secrets

# 3. Build
cd national-microfinance-platform
mvn clean install -pl shared/common-lib,shared/security-lib,shared/event-lib -am
mvn clean package -DskipTests

# 4. Run (full stack)
docker-compose up -d

# 5. Verify
curl http://localhost:8080/api/v1/auth/health
# {"success":true,"data":"OK","message":"Auth service is healthy"}
```

---

## Tech Stack

| Category | Technology |
|----------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 3.2.5, Spring Cloud 2023.0.1 |
| API Gateway | Spring Cloud Gateway |
| Service Discovery | Netflix Eureka |
| Config | Spring Cloud Config |
| Database (ACID) | PostgreSQL 16 |
| Database (Documents) | MongoDB 7.0 |
| Cache | Redis 7.2 |
| Messaging | Apache Kafka 7.5 |
| Auth | JWT (HS256), BCrypt |
| ORM | Spring Data JPA + Hibernate |
| Migration | Flyway |
| Container | Docker |
| Orchestration | Kubernetes |
| CI/CD | GitHub Actions |
| Monitoring | Prometheus + Grafana |
| Logging | ELK Stack (Elasticsearch + Logstash + Kibana) |
| API Docs | SpringDoc OpenAPI (Swagger UI) |

---

## Documentation

| Document | Description |
|----------|-------------|
| [Problem Statement](docs/01-problem-statement.md) | Why this platform exists |
| [Business Objectives](docs/02-business-objectives.md) | OKRs and revenue model |
| [System Architecture](docs/06-system-architecture.md) | Technical architecture |
| [Microservice Architecture](docs/07-microservice-architecture.md) | Service design patterns |
| [API Specification](docs/08-api-specification.md) | All REST endpoints |
| [Database Schema](docs/09-database-schema.md) | ER diagrams and tables |
| [Sequence Diagrams](docs/10-sequence-diagrams.md) | Key workflow flows |
| [Development Roadmap](docs/11-development-roadmap.md) | 6-month plan |
| [Risk Analysis](docs/12-risk-analysis.md) | Identified risks + mitigations |
| [Deployment Guide](docs/13-deployment-guide.md) | How to deploy |
| [Sprint Plan](docs/14-sprint-plan.md) | Agile sprint breakdown |

---

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit changes: `git commit -m 'feat: add your feature'`
4. Push to branch: `git push origin feature/your-feature`
5. Open a Pull Request

### Commit Convention
- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation
- `refactor:` Code refactoring
- `test:` Tests
- `chore:` Build/config changes

---

## License

Copyright © 2026 National Digital Microfinance Platform. All rights reserved.

---

*Built with ❤️ for financial inclusion in Ethiopia*
