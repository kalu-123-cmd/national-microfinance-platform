@echo off
echo ============================================
echo  National Microfinance Platform - Startup
echo ============================================

cd /d "C:\Users\HP\Desktop\National Digital Microfinance Platform\national-microfinance-platform"

echo.
echo [1/2] Building all modules...
call mvn clean install -DskipTests -q
if %ERRORLEVEL% NEQ 0 (
    echo BUILD FAILED! Check errors above.
    pause
    exit /b 1
)
echo BUILD SUCCESS - All JARs ready.
echo.

echo [2/2] Starting services in separate windows...
echo.

echo Starting Config Server (port 8888)...
start "Config Server" cmd /k "java -jar infrastructure\config-server\target\config-server-1.0.0.jar"
timeout /t 20 /nobreak > nul

echo Starting Discovery Server (port 8761)...
start "Discovery Server" cmd /k "java -jar infrastructure\discovery-server\target\discovery-server-1.0.0.jar"
timeout /t 20 /nobreak > nul

echo Starting API Gateway (port 8080)...
start "API Gateway" cmd /k "java -jar infrastructure\api-gateway\target\api-gateway-1.0.0.jar"
timeout /t 20 /nobreak > nul

echo Starting Auth Service (port 8081)...
start "Auth Service" cmd /k "java -jar services\auth-service\target\auth-service-1.0.0.jar"
timeout /t 20 /nobreak > nul

echo.
echo ============================================
echo  All services started!
echo ============================================
echo.
echo  Config Server:    http://localhost:8888/actuator/health
echo  Eureka Dashboard: http://localhost:8761
echo  API Gateway:      http://localhost:8080/actuator/health
echo  Auth Service:     http://localhost:8081/api/v1/auth/health
echo  Auth Swagger UI:  http://localhost:8081/swagger-ui.html
echo.
echo Press any key to test the services...
pause > nul

echo.
echo Testing services...
curl -s http://localhost:8081/api/v1/auth/health
echo.
echo.
echo Done! Check the opened windows for logs.
pause
