# ============================================================
# Supply Chain Platform — Test Runner Agent
# Save to: .\run-tests.ps1
# Run with: powershell -ExecutionPolicy Bypass -File .\run-tests.ps1
# ============================================================

$PROJECT   = "."
$SCWEB     = "..\scweb"
$AGENTS    = "..\agents"
$BASE_URL  = "http://localhost:8089/supchain"
$APP_URL   = "http://localhost:3000"
$AI_URL    = "http://localhost:8001"

function Green($t)  { Write-Host $t -ForegroundColor Green }
function Red($t)    { Write-Host $t -ForegroundColor Red }
function Yellow($t) { Write-Host $t -ForegroundColor Yellow }
function Cyan($t)   { Write-Host $t -ForegroundColor Cyan }
function White($t)  { Write-Host $t -ForegroundColor White }

function Header($t) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "  $t" -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
}

# ── Step 1: Get JWT token ─────────────────────────────────────────────────────
function GetToken {
    try {
        $r = Invoke-RestMethod -Uri "$BASE_URL/api/auth/login" `
            -Method POST -ContentType "application/json" `
            -Body '{"username":"kumar","password":"kumar"}' -TimeoutSec 10
        return $r.token
    } catch { return $null }
}

# ── Step 2: Run API tests ─────────────────────────────────────────────────────
function RunAPITests($token) {
    Header "API TESTS"
    $results = @()
    $endpoints = @(
        @{ name="Auth Login";        method="POST"; path="/api/auth/login";          auth=$false; body='{"username":"kumar","password":"kumar"}' },
        @{ name="AI Health";         method="GET";  path="/api/ai/health";           auth=$true },
        @{ name="Supplier Risk";     method="GET";  path="/api/ai/supplier/1/risk";  auth=$true },
        @{ name="AI Insights";       method="GET";  path="/api/ai/insights";         auth=$true },
        @{ name="Dashboard Summary"; method="GET";  path="/api/dashboard/summary";   auth=$true },
        @{ name="Suppliers";         method="GET";  path="/api/suppliers";           auth=$true },
        @{ name="Alerts Active";     method="GET";  path="/api/alerts/active";       auth=$true },
        @{ name="Cost Records";      method="GET";  path="/api/costs";               auth=$true },
        @{ name="BOM List";          method="GET";  path="/api/bom";                 auth=$true },
        @{ name="Python AI Health";  method="GET";  path="";                         auth=$false; fullUrl=$AI_URL+"/health" }
    )

    foreach ($ep in $endpoints) {
        $start = Get-Date
        try {
            $url = if ($ep.fullUrl) { $ep.fullUrl } else { "$BASE_URL$($ep.path)" }
            $headers = @{}
            if ($ep.auth -and $token) { $headers["Authorization"] = "Bearer $token" }

            if ($ep.method -eq "POST") {
                $res = Invoke-RestMethod -Uri $url -Method POST `
                    -ContentType "application/json" -Body $ep.body `
                    -Headers $headers -TimeoutSec 10
            } else {
                $res = Invoke-RestMethod -Uri $url -Method GET `
                    -Headers $headers -TimeoutSec 10
            }
            $ms = [int]((Get-Date) - $start).TotalMilliseconds
            Green "  OK  $($ep.name.PadRight(22)) ${ms}ms"
            $results += @{ name=$ep.name; status="passed"; ms=$ms; error="" }
        } catch {
            $ms = [int]((Get-Date) - $start).TotalMilliseconds
            $err = $_.Exception.Message
            Red "  FAIL $($ep.name.PadRight(22)) $err"
            $results += @{ name=$ep.name; status="failed"; ms=$ms; error=$err }
        }
    }

    $passed = ($results | Where-Object { $_.status -eq "passed" }).Count
    $failed = ($results | Where-Object { $_.status -eq "failed" }).Count
    Write-Host ""
    White "  API Results: " -NoNewline
    Green "$passed passed" -NoNewline
    if ($failed -gt 0) { Red "  $failed failed" }
    Write-Host ""

    return $results
}

# ── Step 3: Run UI (Playwright) tests ────────────────────────────────────────
function RunUITests {
    Header "UI TESTS (Playwright)"

    Set-Location $PROJECT

    # Fix playwright config to output JSON to correct location
    $config = @"
import { defineConfig } from '@playwright/test';
export default defineConfig({
  testDir: './test',
  timeout: 30000,
  retries: 1,
  workers: 2,
  reporter: [
    ['list'],
    ['json', { outputFile: 'test-results/results.json' }],
  ],
  use: {
    baseURL: 'http://localhost:3000',
    browserName: 'chromium',
    headless: true,
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
  },
});
"@
    $config | Out-File -FilePath "playwright.config.ts" -Encoding utf8
    New-Item -ItemType Directory -Path "test-results" -Force | Out-Null

    Yellow "  Running Playwright tests..."
    Yellow "  This takes 1-2 minutes..."
    Write-Host ""

    $proc = Start-Process -FilePath "cmd" `
        -ArgumentList "/c npx playwright test 2>&1" `
        -WorkingDirectory $PROJECT `
        -Wait -PassThru -NoNewWindow

    $exitCode = $proc.ExitCode

    # Parse results
    $resultsFile = "$PROJECT\test-results\results.json"
    $passed = 0; $failed = 0; $skipped = 0

    if (Test-Path $resultsFile) {
        try {
            $json = Get-Content $resultsFile -Raw | ConvertFrom-Json
            function CountTests($suites) {
                foreach ($suite in $suites) {
                    foreach ($spec in $suite.specs) {
                        foreach ($test in $spec.tests) {
                            if ($test.status -eq "passed")  { $script:passed++ }
                            elseif ($test.status -eq "failed") { $script:failed++ }
                            else { $script:skipped++ }
                        }
                    }
                    if ($suite.suites) { CountTests $suite.suites }
                }
            }
            $script:passed = 0; $script:failed = 0; $script:skipped = 0
            CountTests $json.suites
            $passed = $script:passed; $failed = $script:failed; $skipped = $script:skipped
        } catch {}
    }

    Write-Host ""
    White "  UI Results: " -NoNewline
    Green "$passed passed  " -NoNewline
    if ($failed -gt 0) { Red "$failed failed  " -NoNewline }
    if ($skipped -gt 0) { Yellow "$skipped skipped" }
    Write-Host ""

    return @{ passed=$passed; failed=$failed; skipped=$skipped; file=$resultsFile }
}

# ── Step 4: Copy results to React public folder ───────────────────────────────
function CopyResultsToDashboard($uiResults) {
    Header "COPYING RESULTS TO DASHBOARD"

    $dest = "$SCWEB\public\test-results"
    New-Item -ItemType Directory -Path $dest -Force | Out-Null

    if (Test-Path $uiResults.file) {
        Copy-Item $uiResults.file "$dest\results.json" -Force
        Green "  OK  results.json copied to React public folder"
        Green "      $dest\results.json"
    } else {
        Red "  FAIL results.json not found at $($uiResults.file)"
        Yellow "  Creating empty results file..."
        @{ suites=@(); stats=@{ passed=0; failed=0 } } | ConvertTo-Json | Out-File "$dest\results.json" -Encoding utf8
    }
}

# ── Step 5: Save API results as JSON for dashboard ────────────────────────────
function SaveAPIResults($apiResults) {
    $dest = "$SCWEB\public\test-results"
    New-Item -ItemType Directory -Path $dest -Force | Out-Null

    $report = @{
        timestamp = (Get-Date -Format "o")
        type      = "api"
        results   = $apiResults
        summary   = @{
            passed  = ($apiResults | Where-Object { $_.status -eq "passed" }).Count
            failed  = ($apiResults | Where-Object { $_.status -eq "failed" }).Count
            total   = $apiResults.Count
        }
    }

    $report | ConvertTo-Json -Depth 5 | Out-File "$dest\api-results.json" -Encoding utf8
    Green "  OK  api-results.json saved to React public folder"
}

# ── Step 6: Print failure summary ────────────────────────────────────────────
function PrintFailureSummary($apiResults, $uiResults) {
    $apiFailed = $apiResults | Where-Object { $_.status -eq "failed" }
    $uiFailed  = $uiResults.failed

    if ($apiFailed.Count -eq 0 -and $uiFailed -eq 0) {
        Header "ALL TESTS PASSED"
        Green "  Everything is working!"
        Green "  Open http://localhost:3000/tests to see the dashboard"
        return
    }

    Header "FAILURES FOUND"

    if ($apiFailed.Count -gt 0) {
        Red "  API Failures ($($apiFailed.Count)):"
        foreach ($f in $apiFailed) {
            Red "    - $($f.name): $($f.error)"
        }
        Write-Host ""
    }

    if ($uiFailed -gt 0) {
        Red "  UI Failures ($uiFailed):"
        Yellow "    Check http://localhost:3000/tests for details"
        Yellow "    Screenshots in: $PROJECT\test-results\"
        Write-Host ""
    }

    Yellow "  Token-saving tip:"
    Cyan "  Open http://localhost:3000/tests"
    Cyan "  Click any failure -> copy error -> paste to Claude"
}

# ── MAIN ──────────────────────────────────────────────────────────────────────
Header "Supply Chain Platform — Test Runner"
Cyan "  App:  $APP_URL"
Cyan "  API:  $BASE_URL"
Cyan "  AI:   $AI_URL"

# Check services are running
Write-Host ""
Yellow "  Checking services..."
$javaOk  = (Test-NetConnection -ComputerName localhost -Port 8089 -InformationLevel Quiet 2>$null)
$reactOk = (Test-NetConnection -ComputerName localhost -Port 3000 -InformationLevel Quiet 2>$null)
$aiOk    = (Test-NetConnection -ComputerName localhost -Port 8001 -InformationLevel Quiet 2>$null)

if ($javaOk)  { Green "  OK  Java  :8089" } else { Red "  --  Java  :8089 NOT RUNNING" }
if ($reactOk) { Green "  OK  React :3000" } else { Red "  --  React :3000 NOT RUNNING" }
if ($aiOk)    { Green "  OK  AI    :8001" } else { Red "  --  AI    :8001 NOT RUNNING" }

if (-not $javaOk) {
    Red "  Java backend not running — API tests will fail"
    Yellow "  Start it first: .\mvnw spring-boot:run"
    $ans = Read-Host "  Continue anyway? (y/n)"
    if ($ans -ne "y") { exit 1 }
}

# Get token
Yellow "  Getting JWT token..."
$token = GetToken
if ($token) { Green "  OK  Token obtained" }
else        { Yellow "  !! Could not get token — API tests may fail" }

# Run tests
$apiResults = RunAPITests $token
$uiResults  = RunUITests

# Copy results to dashboard
CopyResultsToDashboard $uiResults
SaveAPIResults $apiResults

# Summary
PrintFailureSummary $apiResults $uiResults

Write-Host ""
Cyan "  Dashboard: http://localhost:3000/tests"
Cyan "  Click 'Load UI Results' to see Playwright results"
Write-Host ""
