# Docker Setup Guide - National Microfinance Platform

This guide will help you run the entire platform using Docker and Docker Compose.

---

## Prerequisites

- **Docker Desktop** 24.0+ (Windows/Mac) or **Docker Engine** 24.0+ (Linux)
- **Docker Compose** 2.20+
- **Git** (to clone the repository)
- **8GB+ RAM** recommended (for running all services)
- **20GB+ free disk space**

### Check Prerequisites

```bash
docker --version
# Docker version 24.0.0 or higher

docker compose version
# Docker Compose version 2.20.0 or higher
```

---

## Quick Start

### 1. Clone & Setup

```bash
# Clone the repository
cd "c:\Users\HP\Desktop\National Digital Microfinance Platform\national-microfinance-platform"

# Copy environment file
copy .env.example .env

# Edit .env if needed (optional for local dev)
notepad .env
```

### 2. Start Infrastructure Only (Recommended First)

```bash
# Start databases and message queue
docker compose up -d postgres mongodb redis zookeeper kafka

# Check if infrastructure is running
docker compose ps

# View logs
docker compose logs -f postgres
```

Wait for all services to be healthy (check with `docker compose ps`).

### 3. Start Core Infrastructure Services

```bash
# Start config server, discovery server, and API gateway
docker compose up -d config-server discovery-server api-gateway

# Check health
docker compose ps

# View logs
docker compose logs -f config-server discovery-server api-gateway
```

Wait ~60 seconds for services to register with Eureka.

### 4. Start Individual Microservices

```bash
# Start a single service for testing
docker compose up -d payment-service

# Check logs
docker compose logs -f payment-service

# Test the service
curl http://localhost:8088/api/v1/payments/health
```

### 5. Start All Services

```bash
# Start everything
docker compose up -d

# Check status
docker compose ps

# View logs for all services
docker compose logs -f
```

---

## Service URLs

### Infrastructure
- **PostgreSQL**: `localhost:5432` (user: postgres, pass: postgres)
- **MongoDB**: `localhost:27017` (user: admin, pass: admin123)
- **Redis**: `localhost:6379` (pass: redis123)
- **Kafka**: `localhost:9092`
- **Config Server**: http://localhost:8888
- **Discovery Server (Eureka)**: http://localhost:8761
- **API Gateway**: http://localhost:8080

### Core Services
- **Auth Service**: http://localhost:8081
- **User Service**: http://localhost:8082
- **KYC Service**: http://localhost:8083
- **Wallet Service**: http://localhost:8084

### Financial Services
- **Payment Service**: http://localhost:8088
- **Loan Service**: http://localhost:8086
- **Savings Service**: http://localhost:8087
- **Cooperative Service**: http://localhost:8089

---

## Useful Commands

### Managing Services

```bash
# Start all services
docker compose up -d

# Start specific services
docker compose up -d postgres redis kafka

# Stop all services
docker compose down

# Stop and remove volumes (clean slate)
docker compose down -v

# Restart a service
docker compose restart payment-service

# View logs
docker compose logs -f payment-service

# View logs for multiple services
docker compose logs -f payment-service loan-service

# Check running containers
docker compose ps

# Check service health
docker compose ps --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}"
```

### Database Access

```bash
# PostgreSQL
docker exec -it microfinance-postgres psql -U postgres -d payment_db

# MongoDB
docker exec -it microfinance-mongodb mongosh -u admin -p admin123

# Redis
docker exec -it microfinance-redis redis-cli -a redis123
```

### Debugging

```bash
# Enter a running container
docker exec -it payment-service sh

# View container resource usage
docker stats

# Inspect a service
docker inspect payment-service

# View network
docker network inspect national-microfinance-platform_microfinance-network
```

### Rebuilding Services

```bash
# Rebuild a single service
docker compose build payment-service

# Rebuild and restart
docker compose up -d --build payment-service

# Rebuild all services
docker compose build

# Force rebuild without cache
docker compose build --no-cache
```

---

## Testing the Platform

### 1. Check Infrastructure

```bash
# Check Eureka dashboard
start http://localhost:8761

# Should show all registered services
```

### 2. Test Individual Services

```bash
# Payment Service
curl http://localhost:8088/api/v1/payments/health

# Loan Service
curl http://localhost:8086/actuator/health

# Savings Service
curl http://localhost:8087/api/v1/savings/health

# Cooperative Service
curl http://localhost:8089/api/v1/cooperatives/health
```

### 3. Test Through API Gateway

```bash
# All requests should route through gateway
curl http://localhost:8080/api/v1/payments/health
curl http://localhost:8080/api/v1/loans/health
curl http://localhost:8080/api/v1/savings/health
```

---

## Troubleshooting

### Services Won't Start

**Problem**: Service fails with "connection refused" or "unable to connect to database"

**Solution**:
```bash
# Check if infrastructure is running
docker compose ps postgres mongodb redis kafka

# Wait for health checks
docker compose ps

# Check logs
docker compose logs postgres
```

### Out of Memory

**Problem**: Docker runs out of memory

**Solution**:
- Increase Docker Desktop memory limit (Settings → Resources → Memory)
- Start services incrementally instead of all at once
- Stop unused services: `docker compose stop service-name`

### Port Already in Use

**Problem**: "port is already allocated"

**Solution**:
```bash
# Find what's using the port (example for 8080)
netstat -ano | findstr :8080

# Kill the process or change port in docker-compose.yml
```

### Database Connection Errors

**Problem**: "Connection refused" from services to PostgreSQL

**Solution**:
```bash
# Ensure PostgreSQL is healthy
docker compose ps postgres

# Check PostgreSQL logs
docker compose logs postgres

# Restart PostgreSQL
docker compose restart postgres

# Recreate if needed
docker compose up -d --force-recreate postgres
```

### Kafka Connection Issues

**Problem**: Services can't connect to Kafka

**Solution**:
```bash
# Ensure Zookeeper and Kafka are running
docker compose ps zookeeper kafka

# Check Kafka logs
docker compose logs kafka

# Recreate Kafka
docker compose up -d --force-recreate zookeeper kafka
```

### Services Not Registering with Eureka

**Problem**: Services don't appear in Eureka dashboard

**Solution**:
```bash
# Check discovery server logs
docker compose logs discovery-server

# Restart the service
docker compose restart payment-service

# Check service logs for Eureka connection errors
docker compose logs payment-service | findstr eureka
```

---

## Performance Tuning

### For Development

```yaml
# In docker-compose.yml, reduce resources for specific services:
services:
  payment-service:
    deploy:
      resources:
        limits:
          cpus: '0.5'
          memory: 512M
```

### For Production

1. **Use external managed databases** (AWS RDS, Azure Database, etc.)
2. **Use external Kafka** (Confluent Cloud, AWS MSK, etc.)
3. **Enable Redis persistence** and clustering
4. **Configure proper resource limits** for each service
5. **Use Docker Swarm or Kubernetes** for orchestration
6. **Enable SSL/TLS** for all connections
7. **Use secrets management** (HashiCorp Vault, AWS Secrets Manager)

---

## Cleanup

### Remove All Containers and Volumes

```bash
# Stop and remove everything
docker compose down -v

# Remove images (optional)
docker compose down --rmi all

# Prune unused Docker resources
docker system prune -a --volumes
```

**WARNING**: This will delete all data!

---

## Next Steps

1. **Run Integration Tests**: Test end-to-end workflows
2. **Add Monitoring**: Setup Prometheus + Grafana
3. **Add Logging**: Setup ELK Stack (Elasticsearch + Logstash + Kibana)
4. **Security Hardening**: Enable SSL, rotate secrets, add firewall rules
5. **Kubernetes Deployment**: Migrate to K8s for production

---

## Support

For issues or questions:
- Email: support@microfinance.et
- Documentation: `/docs` folder
- GitHub Issues: [Create an issue](https://github.com/your-org/national-microfinance-platform/issues)

---

**Happy Deploying! 🚀**
