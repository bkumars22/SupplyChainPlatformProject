# SCIP - Supply Chain Intelligence Platform

> AI-powered full-stack supply chain platform built independently using Claude AI + GitHub Copilot

[![Live Demo](https://img.shields.io/badge/Live%20Demo-GitHub%20Pages-blue)](https://bkumars22.github.io/SupplyChainPlatformProject)
[![Tests](https://img.shields.io/badge/Tests-51%20Passing-green)](https://bkumars22.github.io/SupplyChainPlatformProject)
[![Security](https://img.shields.io/badge/Security-OWASP%20Top%2010-orange)](https://github.com/bkumars22/SupplyChainPlatformProject)
[![Built With](https://img.shields.io/badge/Built%20With-Claude%20AI-purple)](https://anthropic.com)

---

## Live Demo

**URL:** https://bkumars22.github.io/SupplyChainPlatformProject

| Field | Value |
|---|---|
| Username | kumar |
| Password | Kumar@2026 |
| Mode | Demo - realistic mock data, no setup required |

> Demo data resets daily at midnight IST. Feel free to create, edit, and delete anything.

---

## What This Platform Demonstrates

Built solo in 4 weeks using Claude AI and GitHub Copilot as force multipliers. Demonstrates full-stack delivery, AI/ML integration, enterprise security, and automated quality assurance - independently.

| Achievement | Detail |
|---|---|
| P0 Auth Bypass Found | BCrypt skipped when password null - discovered via black-box testing |
| 51 Playwright Tests | 9 modules covered including AI eval pipeline |
| IsolationForest ML | Unsupervised supplier risk scoring |
| Claude AI Integration | Natural language alert explanations via Anthropic API |
| LangGraph Agent | 5-node StateGraph for AI reliability testing |
| LLM Eval Harness | Prompt consistency and hallucination rate measurement |
| 7-Layer Security | OWASP Top 10, JWT, BCrypt, RBAC, rate limiting |
| Solo in 4 Weeks | Full stack - Spring Boot + React + React Native + Python AI |

---

## Architecture

```
+------------------+    +------------------+
|   React 18 Web   |    |  React Native    |
|   9 Pages        |    |  Mobile 10 Scrns |
+--------+---------+    +--------+---------+
         |                       |
         +----------+------------+
                    | JWT Auth
         +----------v-----------+
         |   Spring Boot 3      |
         |   80+ REST Endpoints |
         |   OWASP Security     |
         +----------+-----------+
                    |
         +----------v-----------+
         |   Python FastAPI     |
         |   IsolationForest ML |
         |   Claude AI API      |
         |   LangGraph Agent    |
         +----------+-----------+
                    |
         +----------v-----------+
         |   PostgreSQL         |
         |   JPA / Hibernate    |
         +----------------------+
```

---

## Run Locally in 2 Minutes

```bash
git clone https://github.com/bkumars22/SupplyChainPlatformProject.git
cd SupplyChainPlatformProject
docker-compose up
```

Open: http://localhost:3000  
Login: kumar / Kumar@2026

### Manual Setup (without Docker)

**Backend (Spring Boot):**
```bash
cd LearningProject
mvn spring-boot:run
```
Runs on: http://localhost:8089

**Frontend (React):**
```bash
cd scweb
npm install
npm start
```
Runs on: http://localhost:3000

**AI Service (Python FastAPI):**
```bash
cd LearningProject/ai-service
pip install -r requirements.txt
uvicorn main:app --port 8001
```
Runs on: http://localhost:8001

**Mobile (React Native):**
```bash
cd SupplyChainApp
npm install
npx expo start
```

### React App Scripts

This frontend was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

```bash
npm start          # Run in development mode at http://localhost:3000
npm test           # Launch interactive test runner
npm run build      # Build for production to the build/ folder
npm run deploy     # Build and deploy to GitHub Pages
npm run eject      # Eject CRA config (one-way operation, use with caution)
```

### Environment Variables

| Variable | Dev (.env.local) | Production (.env.production) |
|---|---|---|
| REACT_APP_DEMO_MODE | false | true |
| REACT_APP_API_URL | http://localhost:8089 | https://your-app.railway.app/supchain |
| REACT_APP_BASENAME | (empty) | /SupplyChainPlatformProject |

---

## Module Overview

| Module | What It Does |
|---|---|
| Dashboard | Live KPIs - OTD averages, active alerts, cost savings, risk distribution |
| Suppliers | AI risk scorecard with IsolationForest anomaly detection |
| Alerts | Claude AI natural language explanations of supply chain risks |
| Cost Records | DRAFT to APPROVE workflow for cost management |
| BOM | Bill of Materials viewer with component tracking |
| Test Dashboard | Live 51-test results with auto-refresh every 30 seconds |
| Eval Dashboard | LLM prompt scores, consistency rate, hallucination metrics |
| AI Engines | LangGraph agent results and reliability scores |
| Users | Role-based user management - Admin, Manager, Viewer |

---

## Security Architecture

- **Authentication:** JWT tokens, 8-hour expiry, strong secret validation at startup
- **Passwords:** BCrypt hashing, null-password protection (P0 auth bypass fixed)
- **Authorization:** RBAC - Admin, Manager, Viewer roles with endpoint-level control
- **Rate Limiting:** 5 login attempts per minute per IP, Bucket4j, 15-minute lockout
- **Input Protection:** XSS sanitisation, 1MB request size limits
- **Headers:** HSTS, X-Frame-Options, CSP, X-Content-Type-Options
- **Audit:** OWASP Top 10, SAST scanning across 10 modules, SQL injection audit
- **Errors:** Global exception handler - no stack traces exposed in production

---

## Test Coverage

```
Auth Module        6 tests  - PASSING
Item Master        5 tests  - PASSING
BOM                4 tests  - PASSING
Cost Records       5 tests  - PASSING
Suppliers          6 tests  - PASSING
Users              5 tests  - PASSING
Alerts             5 tests  - PASSING
Dashboard          5 tests  - PASSING
Eval Pipeline     10 tests  - PASSING
--------------------------------------
Total             51 / 51   - ALL PASSING
```

---

## AI / ML Components

**IsolationForest**
Unsupervised anomaly detection for supplier risk scoring. No labelled data required.
Scores each supplier from -1 (anomalous) to +1 (normal). Contamination parameter
set to 0.1 - expects approximately 10% of suppliers to be anomalous.

**Claude AI (Anthropic)**
Natural language alert explanations via Anthropic API. Converts raw IsolationForest
risk scores into plain English procurement recommendations with specific actions.

**DistilBERT (WiseTech Global - Production)**
Fine-tuned on 949 real CI build failures for 7-class root cause classification.
Delivers failure diagnosis in under 2 seconds. Used in production at WiseTech Global.

**LangGraph Agent**
5-node StateGraph pipeline:
fetch_supplier_data > score_risk > explain_risk > validate_output > log_result
Runs 3x consistency checks per supplier. Flags explanation length variance above 30%.

**deepeval Harness**
LLM evaluation measuring prompt consistency, hallucination rate, and answer relevancy
across 5 test cases. Consistency rate: 94.2%.

---

## Technology Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3, JWT, BCrypt, RBAC, JPA/Hibernate |
| Frontend | React 18, React Router, Tailwind CSS, 9 pages |
| Mobile | React Native + Expo, 10 screens, Android and iOS |
| AI Service | Python FastAPI, IsolationForest, Claude AI API, LangGraph |
| Testing | Playwright TypeScript, 51 tests, custom reporter, CI/CD |
| LLM Eval | deepeval, prompt consistency, hallucination rate |
| DevOps | Docker Compose, GitHub Actions, Railway, GitHub Pages |
| Database | PostgreSQL, H2 (testing), JPA/Hibernate ORM |
| Security | Bucket4j rate limiting, HTTPS, HSTS, OWASP SAST |

---

## Project Structure

```
SupplyChainPlatformProject/
├── LearningProject/          # Spring Boot backend + Python AI service
│   ├── src/main/java/        # Java Spring Boot source
│   ├── ai-service/           # Python FastAPI + IsolationForest + Claude AI
│   ├── test/                 # Playwright TypeScript tests (51 tests)
│   ├── agents/               # 25+ Python automation agents
│   └── docs/                 # Architecture guide, API reference, BRD
├── scweb/                    # React 18 web frontend (this repo)
│   ├── src/pages/            # 9 page components
│   ├── src/services/         # Mock data for demo mode
│   ├── public/               # Static assets + help page
│   └── docs/                 # User guide and documentation
└── SupplyChainApp/           # React Native mobile app
    └── src/                  # 10 mobile screens
```

---

## Key Bugs Found Through Testing

| ID | Severity | Description | How Found |
|---|---|---|---|
| BUG-001 | P0 | Auth bypass - any password accepted when stored hash is null | Black-box security testing |
| BUG-002 | P1 | Missing POST endpoint for cost records - returns 405 | API contract testing |
| BUG-003 | P2 | NullPointerException on item create with missing category | Boundary value testing |
| BUG-004 | P2 | User create crash with missing role field | Boundary value testing |
| BUG-005 | P3 | Supplier OTD 45% incorrectly classified as Gold tier | Business rule validation |

---

## Demo Data Reset

The demo database automatically resets every day at midnight IST.
All data is restored to the clean seed state below.

**Admin reset:** POST /api/admin/reset-demo (requires admin / Admin@2026)

**Seed data includes:**
- 5 suppliers (2 critical/at-risk, 2 gold, 1 silver)
- 4 cost records (2 approved, 1 draft, 1 pending)
- 3 active supply chain alerts
- 3 BOM items
- 3 user accounts (admin, manager, viewer)

---

## Documentation

| Document | Description |
|---|---|
| [User Guide (PDF)](docs/SCIP_User_Guide.pdf) | Complete end-user guide for all modules |
| [User Guide (DOCX)](docs/SCIP_User_Guide.docx) | Editable version of the user guide |
| [Help Page](https://bkumars22.github.io/SupplyChainPlatformProject/help.html) | Interactive in-app help |

---

> "AI built the code. I built the product. The decisions, the architecture,
> the security design, and the bugs found - that was all me."

---

## 📊 Diagrams

### 1. System Architecture

Full-stack overview — how the web, mobile, backend, AI service, and database connect.

```mermaid
graph TD
    subgraph Clients ["Client Layer"]
        Web["🌐 React 18 Web\n9 pages · GitHub Pages"]
        Mobile["📱 React Native\nExpo · 10 screens\nAndroid + iOS"]
    end

    subgraph Backend ["Backend — Spring Boot 3 · Java 17"]
        AuthAPI["Auth Controller\n/api/auth/**"]
        SupplierAPI["Suppliers API\n/api/suppliers/**"]
        AlertAPI["Alerts API\n/api/alerts/**"]
        CostAPI["Cost Records\n/api/costs/**"]
        BOMAPI["BOM API\n/api/bom/**"]
        UserAPI["Users API\n/api/users/**"]
        Security["RBAC + JWT Filter\nBucket4j Rate Limiting"]
    end

    subgraph AIService ["AI Service — Python FastAPI"]
        IsoForest["IsolationForest ML\nSupplier Risk Scoring"]
        ClaudeAI["Claude AI (Anthropic)\nNL Alert Explanations"]
        LangGraph["LangGraph Agent\n5-node StateGraph"]
        DeepEval["deepeval Harness\nLLM Evaluation"]
    end

    subgraph DB ["Database — PostgreSQL"]
        DBUsers[("USERS")]
        DBSuppliers[("SUPPLIERS")]
        DBAlerts[("ALERTS")]
        DBCost[("COST_RECORDS")]
        DBBOM[("BOM_ITEMS")]
    end

    Web --> AuthAPI
    Web --> SupplierAPI
    Web --> AlertAPI
    Web --> CostAPI
    Web --> BOMAPI
    Web --> UserAPI

    Mobile --> AuthAPI
    Mobile --> SupplierAPI
    Mobile --> AlertAPI

    Security --> AuthAPI
    Security --> SupplierAPI
    Security --> AlertAPI
    Security --> CostAPI
    Security --> BOMAPI
    Security --> UserAPI

    SupplierAPI --> IsoForest
    AlertAPI --> ClaudeAI
    IsoForest --> LangGraph
    ClaudeAI --> LangGraph
    LangGraph --> DeepEval

    AuthAPI --> DBUsers
    SupplierAPI --> DBSuppliers
    AlertAPI --> DBAlerts
    CostAPI --> DBCost
    BOMAPI --> DBBOM
```

---

### 2. AI / ML Pipeline

How raw supplier data becomes a plain-English procurement recommendation.

```mermaid
flowchart LR
    subgraph Input
        Data["📦 Supplier Metrics\n(OTD %, quality, cost,\nlead time, defect rate)"]
    end

    subgraph ML ["IsolationForest ML"]
        Score["Anomaly Score\n-1.0 → anomalous\n+1.0 → normal"]
        Tier["Risk Tier\n🔴 Critical  🟡 Silver  🟢 Gold"]
    end

    subgraph Agent ["LangGraph 5-Node Agent"]
        N1["① fetch_supplier_data"]
        N2["② score_risk"]
        N3["③ explain_risk"]
        N4["④ validate_output\n(3× consistency checks)"]
        N5["⑤ log_result"]
    end

    subgraph LLM ["Claude AI (Anthropic)"]
        Claude["Natural Language\nProcurement Recommendation\nSpecific actions to take"]
    end

    subgraph Eval ["deepeval Harness"]
        Metrics["Consistency: 94.2%\nHallucination rate\nAnswer relevancy score"]
    end

    subgraph Output
        Alert["⚠️ Supply Chain Alert\n(plain English on Dashboard)"]
        KPI["📊 Risk Dashboard\nKPIs + Scorecard"]
    end

    Data --> N1
    N1 --> N2
    N2 --> Score
    Score --> Tier
    Tier --> N3
    N3 --> Claude
    Claude --> N4
    N4 --> Metrics
    N4 --> N5
    N5 --> Alert
    N5 --> KPI
```

---

### 3. Security Architecture — 7 Layers

```mermaid
flowchart TD
    Req["Incoming HTTP Request"]

    Req --> L1

    subgraph L1 ["Layer 1 — Rate Limiting"]
        RL{"Bucket4j check\n5 login attempts/min\n15-min lockout"}
        RL -->|Over limit| E1["429 Too Many Requests"]
        RL -->|OK| L2
    end

    subgraph L2 ["Layer 2 — JWT Authentication"]
        JWT{"Bearer token\nvalid · not expired\n8-hour TTY"}
        JWT -->|Invalid| E2["401 Unauthorized"]
        JWT -->|Valid| L3
    end

    subgraph L3 ["Layer 3 — RBAC Authorization"]
        RBAC{"Role allowed?\nAdmin · Manager · Viewer"}
        RBAC -->|Denied| E3["403 Forbidden"]
        RBAC -->|Granted| L4
    end

    subgraph L4 ["Layer 4 — Input Protection"]
        Input["XSS Sanitisation\n1 MB request size limit\nSQL injection audit (OWASP)"]
    end

    L4 --> L5

    subgraph L5 ["Layer 5 — Password Security"]
        PW["BCrypt hashing\nNull-password guard (P0 fix)\nStrong secret validation at startup"]
    end

    L5 --> L6

    subgraph L6 ["Layer 6 — Security Headers"]
        Headers["HSTS · X-Frame-Options\nCSP · X-Content-Type-Options\nNo stack traces in production"]
    end

    L6 --> L7

    subgraph L7 ["Layer 7 — Audit & Monitoring"]
        Audit["OWASP Top 10 audit\nSAST scanning · 10 modules\nGlobal exception handler"]
    end

    L7 --> Resp["✅ Authorised Response"]
```

---

### 4. Module & Role Access Map

Which roles can access which modules.

```mermaid
graph LR
    subgraph Roles
        Admin["🛡️ Admin"]
        Manager["👔 Manager"]
        Viewer["👁️ Viewer"]
    end

    subgraph Modules
        Dash["📊 Dashboard\nLive KPIs"]
        Suppliers["📦 Suppliers\nML Risk Scorecard"]
        Alerts["⚠️ Alerts\nClaude AI Explanations"]
        Costs["💰 Cost Records\nApproval Workflow"]
        BOM["🔩 Bill of Materials"]
        Users["👥 User Management"]
        Tests["🧪 Test Dashboard\n51 Playwright Tests"]
        Eval["🤖 AI Eval Dashboard\nLLM Metrics"]
        AIEng["🧠 AI Engines\nLangGraph Results"]
    end

    Admin --> Dash
    Admin --> Suppliers
    Admin --> Alerts
    Admin --> Costs
    Admin --> BOM
    Admin --> Users
    Admin --> Tests
    Admin --> Eval
    Admin --> AIEng

    Manager --> Dash
    Manager --> Suppliers
    Manager --> Alerts
    Manager --> Costs
    Manager --> BOM

    Viewer --> Dash
    Viewer --> Suppliers
    Viewer --> Alerts
```

---

### 5. Alert Generation — End to End

How a supplier risk alert is created from raw data to dashboard display.

```mermaid
sequenceDiagram
    participant FE  as React Frontend
    participant BE  as Spring Boot
    participant AI  as Python FastAPI
    participant ML  as IsolationForest
    participant LLM as Claude AI
    participant DB  as PostgreSQL

    FE->>BE: GET /api/suppliers (JWT)
    BE->>DB: Fetch supplier metrics
    DB-->>BE: OTD%, quality, cost, lead time

    BE->>AI: POST /score-risk (supplier data)
    AI->>ML: Run IsolationForest
    ML-->>AI: Anomaly score (-1 to +1)
    AI->>AI: Map score → Critical / Silver / Gold

    AI->>LLM: Prompt with risk score + supplier data
    LLM-->>AI: Plain-English recommendation

    rect rgb(240, 255, 240)
        note over AI: LangGraph — 3x consistency check
        AI->>AI: validate_output
    end

    AI-->>BE: Risk tier + NL explanation
    BE->>DB: Save alert record
    BE-->>FE: Supplier list + risk scores
    FE-->>FE: Display ⚠️ alert with\nClaude AI explanation
```
