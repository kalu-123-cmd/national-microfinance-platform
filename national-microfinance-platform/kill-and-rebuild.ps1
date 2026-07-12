# Kill any process using auth-service port 8081 or holding the JAR
$ports = @(8081, 8082, 8083, 8084, 8085, 8086, 8087, 8088, 8889, 8761, 8080)
foreach ($port in $ports) {
    $conn = netstat -ano | Select-String ":$port " | Select-String "LISTENING"
    if ($conn) {
        $procId = ($conn -split "\s+")[-1]
        if ($procId -match "^\d+$" -and $procId -ne "0") {
            Stop-Process -Id $procId -Force -ErrorAction SilentlyContinue
            Write-Host "Killed PID $procId on port $port"
        }
    }
}

# Also kill all java processes
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Write-Host "All Java processes killed."
Start-Sleep -Seconds 3

# Delete locked artifacts
Remove-Item -Force "services\auth-service\target\*.jar" -ErrorAction SilentlyContinue
Remove-Item -Force "services\auth-service\target\*.jar.original" -ErrorAction SilentlyContinue
Write-Host "Cleaned target JARs."

# Rebuild
Write-Host "Building auth-service..."
mvn install -pl services/auth-service -am -DskipTests 2>&1 | Select-String "SUCCESS|FAILURE|error:"
