$env:PGPASSWORD = "postgres"
$psql = "C:\Program Files\PostgreSQL\18\bin\psql.exe"

Write-Host "Dropping and recreating auth_db..."
& $psql -U postgres -c "DROP DATABASE IF EXISTS auth_db;"
& $psql -U postgres -c "CREATE DATABASE auth_db;"
Write-Host "auth_db reset. Flyway will re-run V1 on next startup."
