$ports = @(8888, 8761, 8080, 8081, 8082, 8083, 8084, 8085, 8086, 8087, 8088, 8089, 8090, 8091, 8092, 8093, 8094, 8095, 8096, 8097, 8098, 8099, 8100, 8101)

Write-Host "Killing processes on all service ports..." -ForegroundColor Cyan

foreach ($port in $ports) {
    $result = netstat -ano | Select-String ":$port\s.*LISTENING"
    if ($result) {
        $procId = ($result -split "\s+")[-1].Trim()
        if ($procId -match "^\d+$" -and $procId -ne "0") {
            try {
                Stop-Process -Id $procId -Force -ErrorAction Stop
                Write-Host "  Killed PID $procId on port $port" -ForegroundColor Green
            } catch {
                Write-Host "  Could not kill PID $procId on port $port - try closing the terminal window manually" -ForegroundColor Yellow
            }
        }
    }
}

Start-Sleep -Seconds 3

Write-Host ""
Write-Host "Verifying all ports are free..." -ForegroundColor Cyan
$busy = @()
foreach ($port in @(8888, 8761, 8080, 8081)) {
    $check = netstat -ano | Select-String ":$port\s.*LISTENING"
    if ($check) {
        $busy += $port
        Write-Host "  Port $port still in use!" -ForegroundColor Red
    } else {
        Write-Host "  Port $port is FREE" -ForegroundColor Green
    }
}

if ($busy.Count -eq 0) {
    Write-Host ""
    Write-Host "All ports clear. Ready to start services." -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "Some ports still busy. Close all terminal windows that started Java services, then re-run this script." -ForegroundColor Red
}
