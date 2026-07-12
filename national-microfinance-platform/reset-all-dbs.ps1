$env:PGPASSWORD = "postgres"
$psql = "C:\Program Files\PostgreSQL\18\bin\psql.exe"

# All service databases that need clean Flyway runs
$databases = @(
    "auth_db", "user_db", "kyc_db", "wallet_db",
    "payment_db", "loan_db", "savings_db", "cooperative_db",
    "agent_db", "credit_db", "admin_db", "reporting_db"
)

foreach ($db in $databases) {
    Write-Host "Resetting $db ..."

    # Terminate all active connections to the database
    & $psql -U postgres -c "
        SELECT pg_terminate_backend(pid)
        FROM pg_stat_activity
        WHERE datname = '$db' AND pid <> pg_backend_pid();
    " | Out-Null

    # Drop and recreate
    & $psql -U postgres -c "DROP DATABASE IF EXISTS $db;" 2>&1 | Out-Null
    & $psql -U postgres -c "CREATE DATABASE $db;" 2>&1 | Out-Null
    Write-Host "  $db OK"
}

Write-Host ""
Write-Host "All databases reset. Flyway will run fresh migrations on next startup."
