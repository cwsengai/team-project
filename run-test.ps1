#!/usr/bin/env pwsh

# Run CompanyRepositoryTestSuite
$ErrorActionPreference = "Stop"

Write-Host "Running CompanyRepositoryTestSuite..." -ForegroundColor Cyan

# Build classpath from Maven
Write-Host "Building classpath..." -ForegroundColor Gray
$classpathOutput = mvn dependency:build-classpath -DincludeScope=runtime 2>&1 | Out-String
$classpathLine = ($classpathOutput -split "`n" | Where-Object { $_ -match "\.jar" -and $_ -notmatch "\[INFO\]" } | Select-Object -First 1).Trim()

# Add target/classes
$classpath = "target/classes;$classpathLine"

Write-Host "Classpath: $classpath" -ForegroundColor Gray

# Run the test
java -cp "$classpath" data_access.CompanyRepositoryTestSuite

Write-Host "`nTest run complete!" -ForegroundColor Green
