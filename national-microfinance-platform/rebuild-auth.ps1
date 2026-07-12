# Force delete the locked JAR, then rebuild
$jar = "services\auth-service\target\auth-service-1.0.0.jar"
if (Test-Path $jar) {
    Remove-Item -Force $jar -ErrorAction SilentlyContinue
    Write-Host "Deleted locked JAR."
}

# Rebuild
$result = mvn install -pl services/auth-service -am -DskipTests 2>&1
$result | Select-String "SUCCESS|FAILURE|error:"
