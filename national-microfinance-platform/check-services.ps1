$ports = @{
    8888 = "config-server"
    8761 = "discovery-server"
    8080 = "api-gateway"
    8081 = "auth-service"
    8082 = "user-service"
    8083 = "identity-kyc-service"
    8084 = "wallet-service"
    8085 = "payment-service"
    8086 = "loan-service"
}

Write-Host "`n=== Service Health Check ===" -ForegroundColor Cyan
foreach ($port in $ports.Keys | Sort-Object) {
    $name = $ports[$port]
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:$port/actuator/health" -TimeoutSec 3 -ErrorAction Stop
        Write-Host "  [UP]   $name (port $port)" -ForegroundColor Green
    } catch {
        # Try basic TCP connection
        $tcp = New-Object System.Net.Sockets.TcpClient
        try {
            $tcp.Connect("localhost", $port)
            Write-Host "  [UP]   $name (port $port) - TCP open" -ForegroundColor Yellow
            $tcp.Close()
        } catch {
            Write-Host "  [DOWN] $name (port $port)" -ForegroundColor Red
        }
    }
}
