# ============================================================
# Supply Chain Intelligence Platform — Local Dev Agent
# Save this file as: .\scip-agent.ps1
# Run with: .\scip-agent.ps1
# ============================================================

$PROJECT   = "."
$AI_DIR    = "$PROJECT\ai-service"
$SCWEB_DIR = "..\scweb"
$JAVA_PORT = 8089
$AI_PORT   = 8001
$WEB_PORT  = 3000
$BASE_URL  = "http://localhost:$JAVA_PORT/supchain"
$AI_URL    = "http://localhost:$AI_PORT"

# Colors
function Green($t)  { Write-Host $t -ForegroundColor Green }
function Red($t)    { Write-Host $t -ForegroundColor Red }
function Yellow($t) { Write-Host $t -ForegroundColor Yellow }
function Cyan($t)   { Write-Host $t -ForegroundColor Cyan }
function White($t)  { Write-Host $t -ForegroundColor White }

function Header {
    Clear-Host
    Cyan "╔══════════════════════════════════════════════════════════╗"
    Cyan "║     Supply Chain Intelligence Platform — Dev Agent       ║"
    Cyan "╚══════════════════════════════════════════════════════════╝"
    Write-Host ""
}

# ── Check if a port is in use ────────────────────────────────
function IsPortInUse($port) {
    $conn = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
    return $conn -ne $null
}

# ── Kill process on a port ───────────────────────────────────
function KillPort($port) {
    $conn = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
    if ($conn) {
        $pid = $conn.OwningProcess | Select-Object -First 1
        Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
        Start-Sleep -Seconds 2
        Green "  Stopped process on port $port"
    }
}

# ── Test HTTP endpoint ───────────────────────────────────────
function TestEndpoint($url, $token = $null) {
    try {
        $headers = @{}
        if ($token) { $headers["Authorization"] = "Bearer $token" }
        $response = Invoke-RestMethod -Uri $url -Headers $headers -TimeoutSec 5
        return $true
    } catch {
        return $false
    }
}

# ── Get JWT token ────────────────────────────────────────────
function GetToken {
    try {
        $body = '{"username":"kumar","password":"kumar"}'
        $response = Invoke-RestMethod -Uri "$BASE_URL/api/auth/login" `
            -Method POST -ContentType "application/json" `
            -Body $body -TimeoutSec 10
        return $response.token
    } catch {
        return $null
    }
}

# ── Status dashboard ─────────────────────────────────────────
function ShowStatus {
    Header
    White "SERVICE STATUS"
    Write-Host "─────────────────────────────────────────" -ForegroundColor DarkGray

    # Python AI
    if (IsPortInUse $AI_PORT) {
        $ok = TestEndpoint "$AI_URL/health"
        if ($ok) { Green "  ✅ Python AI service   → http://localhost:$AI_PORT" }
        else     { Yellow "  ⚠️  Python AI service   → port $AI_PORT in use but not responding" }
    } else {
        Red "  ❌ Python AI service   → NOT running (port $AI_PORT)"
    }

    # Java
    if (IsPortInUse $JAVA_PORT) {
        Yellow "  ⏳ Java Spring Boot    → starting on port $JAVA_PORT..."
        $token = GetToken
        if ($token) {
            $ok = TestEndpoint "$BASE_URL/api/ai/health" $token
            if ($ok) { Green "  ✅ Java Spring Boot    → http://localhost:$JAVA_PORT/supchain" }
            else     { Yellow "  ⚠️  Java Spring Boot    → running but AI endpoint not ready yet" }
        } else {
            Yellow "  ⚠️  Java Spring Boot    → running but login failed"
        }
    } else {
        Red "  ❌ Java Spring Boot    → NOT running (port $JAVA_PORT)"
    }

    # React web
    if (IsPortInUse $WEB_PORT) {
        Green "  ✅ React Web App       → http://localhost:$WEB_PORT"
    } else {
        Red "  ❌ React Web App       → NOT running (port $WEB_PORT)"
    }

    Write-Host ""
}

# ── Start Python AI service ──────────────────────────────────
function StartPythonAI {
    Yellow "  Starting Python AI service on port $AI_PORT..."

    if (IsPortInUse $AI_PORT) {
        Yellow "  Port $AI_PORT already in use — skipping"
        return
    }

    # Check venv exists
    if (-not (Test-Path "$AI_DIR\venv\Scripts\Activate.ps1")) {
        Yellow "  Creating virtual environment..."
        Set-Location $AI_DIR
        python -m venv venv
    }

    # Check main.py exists
    if (-not (Test-Path "$AI_DIR\main.py")) {
        Red "  ERROR: main.py not found in $AI_DIR"
        Red "  Please create main.py first"
        return
    }

    # Start in new window
    Start-Process powershell -ArgumentList @(
        "-NoExit",
        "-Command",
        "Set-Location '$AI_DIR'; .\venv\Scripts\Activate.ps1; uvicorn main:app --reload --port $AI_PORT"
    ) -WindowStyle Normal

    # Wait for it to start
    $tries = 0
    while ($tries -lt 15) {
        Start-Sleep -Seconds 2
        if (TestEndpoint "$AI_URL/health") {
            Green "  ✅ Python AI service started successfully"
            return
        }
        $tries++
        Write-Host "  Waiting... ($tries/15)" -ForegroundColor DarkGray
    }
    Yellow "  Python AI service is starting — may take a few more seconds"
}

# ── Start Java Spring Boot ───────────────────────────────────
function StartJava {
    Yellow "  Starting Java Spring Boot on port $JAVA_PORT..."

    if (IsPortInUse $JAVA_PORT) {
        Green "  ✅ Java already running on port $JAVA_PORT"
        return
    }

    if (-not (Test-Path "$PROJECT\mvnw.cmd")) {
        Red "  ERROR: mvnw.cmd not found in $PROJECT"
        return
    }

    Start-Process powershell -ArgumentList @(
        "-NoExit",
        "-Command",
        "Set-Location '$PROJECT'; .\mvnw spring-boot:run"
    ) -WindowStyle Normal

    Yellow "  Java is starting — this takes 30-60 seconds..."
    $tries = 0
    while ($tries -lt 30) {
        Start-Sleep -Seconds 3
        if (IsPortInUse $JAVA_PORT) {
            Start-Sleep -Seconds 5
            Green "  ✅ Java Spring Boot started on port $JAVA_PORT"
            return
        }
        $tries++
        Write-Host "  Waiting for Java... ($tries/30)" -ForegroundColor DarkGray
    }
    Yellow "  Java is still starting — check the Java terminal window"
}

# ── Start React web ──────────────────────────────────────────
function StartReact {
    Yellow "  Starting React web app on port $WEB_PORT..."

    if (IsPortInUse $WEB_PORT) {
        Green "  ✅ React already running on port $WEB_PORT"
        return
    }

    if (-not (Test-Path $SCWEB_DIR)) {
        Red "  ERROR: scweb directory not found at $SCWEB_DIR"
        return
    }

    Start-Process powershell -ArgumentList @(
        "-NoExit",
        "-Command",
        "Set-Location '$SCWEB_DIR'; `$env:PORT=3000; `$env:BROWSER='none'; node node_modules\react-scripts\bin\react-scripts.js start"
    ) -WindowStyle Normal

    $tries = 0
    while ($tries -lt 20) {
        Start-Sleep -Seconds 3
        if (IsPortInUse $WEB_PORT) {
            Green "  ✅ React web app started on port $WEB_PORT"
            return
        }
        $tries++
        Write-Host "  Waiting for React... ($tries/20)" -ForegroundColor DarkGray
    }
    Yellow "  React is still starting — check the React terminal window"
}

# ── Restart a service ────────────────────────────────────────
function RestartService($service) {
    switch ($service) {
        "python" {
            Yellow "  Restarting Python AI service..."
            KillPort $AI_PORT
            StartPythonAI
        }
        "java" {
            Yellow "  Restarting Java Spring Boot..."
            KillPort $JAVA_PORT
            Start-Sleep -Seconds 2
            StartJava
        }
        "react" {
            Yellow "  Restarting React web app..."
            KillPort $WEB_PORT
            StartReact
        }
        "all" {
            Yellow "  Restarting ALL services..."
            KillPort $AI_PORT
            KillPort $JAVA_PORT
            KillPort $WEB_PORT
            Start-Sleep -Seconds 3
            StartPythonAI
            StartJava
            StartReact
        }
    }
}

# ── Run full test suite ──────────────────────────────────────
function RunTests {
    Header
    White "RUNNING ENDPOINT TESTS"
    Write-Host "─────────────────────────────────────────" -ForegroundColor DarkGray
    Write-Host ""

    # Test Python AI directly
    Yellow "  Testing Python AI service..."
    if (TestEndpoint "$AI_URL/health") {
        Green "  ✅ GET $AI_URL/health"
    } else {
        Red "  ❌ GET $AI_URL/health — Python AI not running"
    }

    # Test Java login
    Yellow "  Getting JWT token..."
    $token = GetToken
    if ($token) {
        Green "  ✅ Login successful — token obtained"
    } else {
        Red "  ❌ Login failed — Java backend not running or credentials wrong"
        return
    }

    # Test Java AI health
    if (TestEndpoint "$BASE_URL/api/ai/health" $token) {
        Green "  ✅ GET $BASE_URL/api/ai/health"
    } else {
        Red "  ❌ GET $BASE_URL/api/ai/health"
    }

    # Test supplier risk
    Yellow "  Testing supplier risk analysis..."
    try {
        $risk = Invoke-RestMethod -Uri "$BASE_URL/api/ai/supplier/1/risk" `
            -Headers @{Authorization="Bearer $token"} -TimeoutSec 10
        Green "  ✅ GET /api/ai/supplier/1/risk"
        White "     Risk Score : $($risk.riskScore)/100"
        White "     Risk Level : $($risk.riskLevel)"
        White "     Explanation: $($risk.explanation)"
    } catch {
        Red "  ❌ GET /api/ai/supplier/1/risk — $($_.Exception.Message)"
    }

    # Test insights
    try {
        $insights = Invoke-RestMethod -Uri "$BASE_URL/api/ai/insights" `
            -Headers @{Authorization="Bearer $token"} -TimeoutSec 10
        $count = if ($insights -is [Array]) { $insights.Count } else { 0 }
        Green "  ✅ GET /api/ai/insights — $count suppliers"
    } catch {
        Red "  ❌ GET /api/ai/insights"
    }

    # Test existing supplier endpoint
    try {
        $suppliers = Invoke-RestMethod -Uri "$BASE_URL/api/supplier" `
            -Headers @{Authorization="Bearer $token"} -TimeoutSec 10
        Green "  ✅ GET /api/supplier — existing endpoint working"
    } catch {
        Yellow "  ⚠️  GET /api/supplier — $($_.Exception.Message)"
    }

    # Test existing alert endpoint  
    try {
        $alerts = Invoke-RestMethod -Uri "$BASE_URL/api/alert" `
            -Headers @{Authorization="Bearer $token"} -TimeoutSec 10
        Green "  ✅ GET /api/alert — existing endpoint working"
    } catch {
        Yellow "  ⚠️  GET /api/alert — may need different path"
    }

    Write-Host ""
    Green "  Tests complete!"
    Write-Host ""
}

# ── Fix common errors ────────────────────────────────────────
function AutoFix {
    Header
    White "AUTO-FIX MODE"
    Write-Host "─────────────────────────────────────────" -ForegroundColor DarkGray
    Write-Host ""

    # Fix 1: Python venv not activated
    Yellow "  Checking Python venv..."
    if (-not (Test-Path "$AI_DIR\venv\Scripts\Activate.ps1")) {
        Yellow "  Creating Python venv..."
        Set-Location $AI_DIR
        python -m venv venv
        Green "  ✅ venv created"
    } else {
        Green "  ✅ venv exists"
    }

    # Fix 2: Python packages missing
    Yellow "  Checking Python packages..."
    $testImport = & "$AI_DIR\venv\Scripts\python.exe" -c "import fastapi, uvicorn, sklearn, pandas, sqlalchemy; print('ok')" 2>&1
    if ($testImport -eq "ok") {
        Green "  ✅ Python packages installed"
    } else {
        Yellow "  Installing missing Python packages..."
        & "$AI_DIR\venv\Scripts\pip.exe" install fastapi uvicorn scikit-learn pandas sqlalchemy psycopg2-binary python-dotenv statsmodels
        Green "  ✅ Python packages installed"
    }

    # Fix 3: .env file missing
    Yellow "  Checking .env file..."
    if (-not (Test-Path "$AI_DIR\.env")) {
        Yellow "  Creating .env file..."
        @"
DATABASE_URL=postgresql://${DB_USERNAME}:${DB_PASSWORD}@localhost:5432/pcm
APP_HOST=0.0.0.0
APP_PORT=8001
JAVA_BACKEND_URL=http://localhost:8089
"@ | Out-File -FilePath "$AI_DIR\.env" -Encoding utf8
        Green "  ✅ .env file created"
    } else {
        Green "  ✅ .env file exists"
    }

    # Fix 4: application-local.properties missing ai.service.url
    Yellow "  Checking ai.service.url property..."
    $propsFile = "$PROJECT\src\main\resources\application-local.properties"
    $content = Get-Content $propsFile -Raw
    if ($content -notmatch "ai.service.url") {
        Add-Content $propsFile "`nai.service.url=http://localhost:8001"
        Green "  ✅ ai.service.url added to application-local.properties"
    } else {
        Green "  ✅ ai.service.url already configured"
    }

    # Fix 5: PowerShell execution policy
    Yellow "  Checking PowerShell execution policy..."
    $policy = Get-ExecutionPolicy -Scope CurrentUser
    if ($policy -eq "Restricted") {
        Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser -Force
        Green "  ✅ Execution policy fixed"
    } else {
        Green "  ✅ Execution policy OK ($policy)"
    }

    Write-Host ""
    Green "  All fixes applied!"
    Write-Host ""
}

# ── Open all URLs in browser ─────────────────────────────────
function OpenBrowser {
    Start-Process "http://localhost:3000"
    Start-Sleep -Milliseconds 500
    Start-Process "http://localhost:8001/docs"
    Start-Sleep -Milliseconds 500
    Start-Process "http://localhost:8089/supchain/swagger-ui.html"
    Green "  ✅ Opened all URLs in browser"
}

# ── Show API token ───────────────────────────────────────────
function ShowToken {
    Yellow "  Getting JWT token..."
    $token = GetToken
    if ($token) {
        Green "  ✅ Token obtained!"
        Write-Host ""
        White "  Copy this token for Swagger/Postman:"
        Write-Host ""
        Yellow "  $token"
        Write-Host ""
        White "  PowerShell variable (for testing):"
        Cyan '  $token = "' + $token + '"'
        Write-Host ""

        # Save to clipboard
        $token | Set-Clipboard
        Green "  ✅ Token copied to clipboard!"
    } else {
        Red "  ❌ Could not get token — is Java running?"
    }
}

# ── Main menu ────────────────────────────────────────────────
function MainMenu {
    while ($true) {
        Header
        ShowStatus
        Write-Host "─────────────────────────────────────────" -ForegroundColor DarkGray
        White "ACTIONS"
        Write-Host ""
        Cyan "  [1] Start ALL services"
        Cyan "  [2] Restart ALL services"
        Cyan "  [3] Restart Python AI only"
        Cyan "  [4] Restart Java only"
        Cyan "  [5] Restart React only"
        Cyan "  [6] Run all endpoint tests"
        Cyan "  [7] Auto-fix common errors"
        Cyan "  [8] Get JWT token (copied to clipboard)"
        Cyan "  [9] Open all URLs in browser"
        Cyan "  [0] Exit"
        Write-Host ""
        $choice = Read-Host "  Enter choice"

        switch ($choice) {
            "1" {
                Header
                White "STARTING ALL SERVICES"
                Write-Host ""
                StartPythonAI
                StartJava
                StartReact
                Write-Host ""
                Green "All services started! Press Enter to continue..."
                Read-Host
            }
            "2" { RestartService "all"; Read-Host "Press Enter to continue" }
            "3" { RestartService "python"; Read-Host "Press Enter to continue" }
            "4" { RestartService "java"; Read-Host "Press Enter to continue" }
            "5" { RestartService "react"; Read-Host "Press Enter to continue" }
            "6" { RunTests; Read-Host "Press Enter to continue" }
            "7" { AutoFix; Read-Host "Press Enter to continue" }
            "8" { ShowToken; Read-Host "Press Enter to continue" }
            "9" { OpenBrowser; Read-Host "Press Enter to continue" }
            "0" { Green "Goodbye!"; exit }
            default { Yellow "Invalid choice — try again" }
        }
    }
}

# ── Entry point ──────────────────────────────────────────────
MainMenu
