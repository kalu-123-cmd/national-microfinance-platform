Write-Host "=== Building all microservices (skipping already-running infrastructure) ===" -ForegroundColor Cyan

# Build shared libs first
mvn install -pl shared/common-lib,shared/security-lib,shared/event-lib -DskipTests 2>&1 | Select-String "SUCCESS|FAILURE|ERROR"

# Build all services (not infrastructure - those are already running)
$services = @(
    "services/auth-service",
    "services/user-service",
    "services/identity-kyc-service",
    "services/wallet-service",
    "services/payment-service",
    "services/loan-service",
    "services/savings-service",
    "services/cooperative-service",
    "services/agent-banking-service",
    "services/notification-service",
    "services/fraud-detection-service",
    "services/credit-scoring-service",
    "services/ai-recommendation-service",
    "services/audit-service",
    "services/reporting-service",
    "services/analytics-service",
    "services/offline-sync-service",
    "services/financial-literacy-service",
    "services/voice-banking-service",
    "services/document-management-service",
    "services/admin-service"
)

$pl = $services -join ","
Write-Host "Building 21 services..." -ForegroundColor Yellow

$result = mvn install -pl $pl -am -DskipTests 2>&1
$result | Out-File "services-build-output.txt" -Encoding UTF8
$result | Select-String "SUCCESS|FAILURE|ERROR|Building Authentication|Building User|Building Wallet|Building Loan"

Write-Host ""
Write-Host "=== Build Summary ===" -ForegroundColor Cyan
$result | Select-String "BUILD SUCCESS|BUILD FAILURE"
