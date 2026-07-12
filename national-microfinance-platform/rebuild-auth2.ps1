$result = mvn install -pl services/auth-service -am -DskipTests 2>&1
$result | Out-File "auth-build2.txt" -Encoding UTF8
$result | Select-String "error:|ERROR"
