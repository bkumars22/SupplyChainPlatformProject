# ============================================================
# Supply Chain Intelligence Platform - Start All Services
# Copyright (c) 2026 Kumara Swamy - github.com/bkumars22
# ============================================================
# Usage: powershell -ExecutionPolicy Bypass -File start-all.ps1
# ============================================================

$root = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  Supply Chain Intelligence Platform" -ForegroundColor Cyan
Write-Host "  Starting all 4 services..." -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Service 1 - Java Spring Boot backend (port 8089)
Write-Host "Starting Java backend on port 8089..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command",
    "Write-Host 'Java Backend - port 8089' -ForegroundColor Cyan;
     cd '$root';
     .\mvnw spring-boot:run"

Start-Sleep -Seconds 2

# Service 2 - Python FastAPI AI service (port 8001)
Write-Host "Starting Python AI service on port 8001..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command",
    "Write-Host 'Python AI Service - port 8001' -ForegroundColor Cyan;
     cd '$root\ai-service';
     if (Test-Path '.\venv\Scripts\activate') {
         .\venv\Scripts\activate
     } else {
         python -m venv venv;
         .\venv\Scripts\activate;
         pip install -r requirements.txt
     };
     uvicorn main:app --reload --port 8001"

Start-Sleep -Seconds 2

# Service 3 - React web app (port 3000)
Write-Host "Starting React web app on port 3000..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command",
    "Write-Host 'React Web App - port 3000' -ForegroundColor Cyan;
     cd '$root\scweb';
     npm start"

Start-Sleep -Seconds 2

# Service 4 - React Native Expo mobile app (port 8081)
Write-Host "Starting React Native Expo on port 8081..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command",
    "Write-Host 'React Native Expo - port 8081' -ForegroundColor Cyan;
     cd '$root\SupplyChainApp';
     npx expo start"

Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "  All 4 services starting!" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""
Write-Host "  Web App    : http://localhost:3000" -ForegroundColor White
Write-Host "  Java API   : http://localhost:8089/supchain" -ForegroundColor White
Write-Host "  Python AI  : http://localhost:8001" -ForegroundColor White
Write-Host "  Swagger    : http://localhost:8089/supchain/swagger-ui/index.html" -ForegroundColor White
Write-Host "  H2 Console : http://localhost:8089/supchain/h2-console" -ForegroundColor White
Write-Host "  Mobile     : Scan QR code from Expo window" -ForegroundColor White
Write-Host ""
Write-Host "  Login with: kumar / kumar (dev only)" -ForegroundColor Gray
Write-Host ""
