# Stop all running Java processes (services)
Stop-Process -Name "java" -Force -ErrorAction SilentlyContinue
Write-Host "Java processes stopped."
Start-Sleep -Seconds 3

# Rebuild auth-service
$output = mvn clean install -pl services/auth-service -am -DskipTests 2>&1
$output | Select-String "SUCCESS|FAILURE|error:"
