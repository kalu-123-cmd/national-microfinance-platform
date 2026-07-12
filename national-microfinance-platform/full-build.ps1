Write-Host "=== Building all 28 modules (no clean - skip locked JARs) ===" -ForegroundColor Cyan

# Build WITHOUT clean phase to avoid locked JAR issues
# -DskipTests skips test execution
# The previously compiled JARs in other terminals are fine - we just rebuild what changed
$result = mvn install -DskipTests 2>&1
$result | Out-File "full-build-output.txt" -Encoding UTF8

# Show only the important lines
$result | Select-String "Building jar:|SUCCESS|FAILURE|ERROR"
