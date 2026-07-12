mvn clean install -pl services/auth-service -am -DskipTests 2>&1 | Select-String "SUCCESS|FAILURE|error:"
