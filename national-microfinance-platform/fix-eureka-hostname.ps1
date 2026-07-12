# Fix prefer-ip-address: true -> false in all service application.yml files
# On Windows, prefer-ip-address: true causes registration as host.docker.internal
$files = Get-ChildItem -Path "services","infrastructure" -Recurse -Filter "application.yml"
$fixed = 0
foreach ($file in $files) {
    $content = [System.IO.File]::ReadAllText($file.FullName)
    if ($content -match "prefer-ip-address: true") {
        $updated = $content -replace "prefer-ip-address: true", "prefer-ip-address: false"
        [System.IO.File]::WriteAllText($file.FullName, $updated)
        Write-Host "Fixed: $($file.Directory.Parent.Name)/$($file.Directory.Name)"
        $fixed++
    }
}
Write-Host ""
Write-Host "Fixed $fixed files."
