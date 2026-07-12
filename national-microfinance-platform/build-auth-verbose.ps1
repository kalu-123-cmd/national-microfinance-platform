mvn clean install -pl services/auth-service -am -DskipTests 2>&1 | Select-String "error\[|ERROR.*\.java|symbol|cannot find"
