mvn install -pl services/auth-service -DskipTests 2>&1 | Out-File "auth-errors.txt" -Encoding UTF8
Get-Content "auth-errors.txt" | Select-String "ERROR|error\[" | Select-Object -First 30
