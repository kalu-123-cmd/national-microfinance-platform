# Nuclear clean - delete entire target folder
$targetDir = "services\auth-service\target"
if (Test-Path $targetDir) {
    Remove-Item -Recurse -Force $targetDir -ErrorAction SilentlyContinue
    Write-Host "Deleted target folder."
}
Start-Sleep -Seconds 2

# Build using package (not install to avoid repackage rename issue)
Write-Host "Packaging auth-service..."
mvn clean package -pl services/auth-service -am -DskipTests 2>&1 | Select-String "SUCCESS|FAILURE|ERROR"
