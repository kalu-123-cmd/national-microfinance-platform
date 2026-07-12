$files = Get-ChildItem -Path "services" -Recurse -Filter "*Application.java"
foreach ($file in $files) {
    $content = [System.IO.File]::ReadAllText($file.FullName)
    if ($content -match "EnableEurekaClient") {
        $updated = $content `
            -replace "import org.springframework.cloud.netflix.eureka.EnableEurekaClient;", "import org.springframework.cloud.client.discovery.EnableDiscoveryClient;" `
            -replace "@EnableEurekaClient", "@EnableDiscoveryClient" `
            -replace 'scanBasePackages = \{"com\.microfinance\.[^"]+","com\.microfinance\.common","com\.microfinance\.security"\}', 'scanBasePackages = "com.microfinance"'
        [System.IO.File]::WriteAllText($file.FullName, $updated)
        Write-Host "Fixed: $($file.Name)"
    }
}
Write-Host "All done."
