# Deployment Guide - National Digital Microfinance Platform

## Prerequisites

| Tool | Version | Purpose |
|------|---------|---------|
| Java JDK | 21+ | Build Java services |
| Maven | 3.9+ | Build tool |
| Docker | 25+ | Containerization |
| Docker Compose | 2.24+ | Local dev orchestration |
| Kubernetes | 1.29+ | Production orchestration |
| kubectl | 1.29+ | K8s CLI |
| Helm | 3.14+ | K8s package manager |

---

## 1. Local Development (Docker Compose)

### Step 1: Clone and configure
```bash
git clone https://github.com/your-org/national-microfinance-platform.git
cd national-microfinance-platform
cp .env.example .env
# Edit .env with your values
```

### Step 2: Build shared libraries
```bash
cd national-microfinance-platform
mvn clean install -pl shared/common-lib,shared/security-lib,shared/event-lib -am
```

### Step 3: Build all service JARs
```bash
mvn clean package -DskipTests
```

### Step 4: Start infrastructure first
```bash
docker-compose up -d zookeeper kafka redis mongodb postgres-auth postgres-user postgres-wallet postgres-loan
# Wait 30 seconds for DBs to initialize
```

### Step 5: Start Spring Cloud infrastructure
```bash
docker-compose up -d config-server
docker-compose up -d discovery-server
# Wait 20 seconds
docker-compose up -d api-gateway
```

### Step 6: Start microservices
```bash
docker-compose up -d auth-service user-service identity-kyc-service wallet-service
docker-compose up -d payment-service loan-service savings-service
docker-compose up -d notification-service fraud-detection-service credit-scoring-service
```

### Step 7: Start monitoring
```bash
docker-compose up -d prometheus grafana elasticsearch kibana
```

### Verify deployment
```bash
# Check all containers running
docker-compose ps

# Test API Gateway
curl http://localhost:8080/actuator/health

# Test Auth Service
curl http://localhost:8080/api/v1/auth/health

# Test Wallet Service
curl http://localhost:8080/api/v1/wallets/health

# View service registry
open http://localhost:8761  # Eureka Dashboard

# View metrics
open http://localhost:9090  # Prometheus
open http://localhost:3000  # Grafana (admin/admin123)
open http://localhost:5601  # Kibana
```

---

## 2. Kubernetes Deployment

### Step 1: Create namespace and secrets
```bash
kubectl apply -f k8s/namespace.yml

# Create secrets (replace base64 values with real ones)
kubectl apply -f k8s/secrets.yml

kubectl apply -f k8s/configmap.yml
```

### Step 2: Deploy infrastructure
```bash
kubectl apply -f k8s/infrastructure/
kubectl rollout status deployment/redis -n microfinance
kubectl rollout status deployment/zookeeper -n microfinance
kubectl rollout status deployment/kafka -n microfinance
```

### Step 3: Deploy Spring Cloud infra
```bash
# Set your registry
export DOCKER_REGISTRY=ghcr.io/your-org/microfinance
export IMAGE_TAG=latest

# Apply and wait
kubectl apply -f k8s/services/api-gateway.yml
kubectl rollout status deployment/api-gateway -n microfinance --timeout=120s
```

### Step 4: Deploy all microservices
```bash
kubectl apply -f k8s/services/
kubectl rollout status deployment -n microfinance --timeout=300s
```

### Step 5: Configure Ingress
```bash
# Install nginx ingress controller
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml

# Install cert-manager for TLS
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.14.0/cert-manager.yaml

# Apply ingress
kubectl apply -f k8s/services/api-gateway.yml  # Contains Ingress manifest
```

### Step 6: Verify
```bash
kubectl get pods -n microfinance
kubectl get services -n microfinance
kubectl get ingress -n microfinance

# Check logs
kubectl logs -n microfinance deployment/auth-service --tail=50
kubectl logs -n microfinance deployment/wallet-service --tail=50
```

---

## 3. Database Initialization

Each service uses Flyway for automatic schema migration. Migrations run on startup.

### Manual database creation (if not using Docker)
```sql
-- Run these commands in PostgreSQL as superuser
CREATE DATABASE auth_db;
CREATE DATABASE user_db;
CREATE DATABASE kyc_db;
CREATE DATABASE wallet_db;
CREATE DATABASE payment_db;
CREATE DATABASE loan_db;
CREATE DATABASE savings_db;
CREATE DATABASE cooperative_db;
CREATE DATABASE agent_db;
CREATE DATABASE credit_db;
CREATE DATABASE admin_db;
CREATE DATABASE reporting_db;

GRANT ALL PRIVILEGES ON DATABASE auth_db TO postgres;
-- Repeat for each database
```

---

## 4. Environment Variables Reference

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| JWT_SECRET | YES | — | Base64 JWT signing secret |
| DB_PASSWORD | YES | — | PostgreSQL password |
| REDIS_PASSWORD | YES | — | Redis auth password |
| MONGO_PASSWORD | YES | — | MongoDB password |
| SMS_API_KEY | Recommended | dev-key | Africa's Talking API key |
| EMAIL_FROM | Recommended | noreply@... | From email address |
| JUMIO_API_KEY | Phase 2 | — | ID document verification |
| ONFIDO_API_KEY | Phase 2 | — | Biometric verification |

---

## 5. Monitoring Access

| Service | URL | Default Credentials |
|---------|-----|---------------------|
| Eureka | http://host:8761 | None |
| Grafana | http://host:3000 | admin / admin123 |
| Prometheus | http://host:9090 | None |
| Kibana | http://host:5601 | None |
| API Docs | http://host:8080/swagger-ui.html | — |

---

## 6. Rolling Updates

```bash
# Update a single service image
kubectl set image deployment/wallet-service \
  wallet-service=ghcr.io/your-org/microfinance/wallet-service:v1.2.0 \
  -n microfinance

# Monitor rollout
kubectl rollout status deployment/wallet-service -n microfinance

# Rollback if needed
kubectl rollout undo deployment/wallet-service -n microfinance
```

---

## 7. Scaling

```bash
# Manual scaling
kubectl scale deployment wallet-service --replicas=5 -n microfinance

# Auto-scaling is configured via HPA (defined in k8s/services/)
kubectl get hpa -n microfinance
```

---

## 8. Backup Procedures

```bash
# PostgreSQL backup (wallet DB example)
kubectl exec -n microfinance postgres-wallet -- \
  pg_dump -U postgres wallet_db | gzip > wallet_backup_$(date +%Y%m%d).sql.gz

# MongoDB backup
kubectl exec -n microfinance mongodb -- \
  mongodump --db notification_db --out /backup/$(date +%Y%m%d)

# Redis backup
kubectl exec -n microfinance redis -- redis-cli BGSAVE
```
