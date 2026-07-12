$output = mvn clean install -pl services/auth-service -am -DskipTests 2>&1
$output | Out-File "build-auth-output.txt"
$output | Select-String "ERROR|error"
