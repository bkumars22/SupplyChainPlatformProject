# ⚡ Supply Chain Intelligence Platform (SCIP)

> **AI-native supply chain platform built solo in 4 weeks** · Java 17 · Spring Boot 3 · React 18 · React Native · Python FastAPI · Playwright · Docker · Claude AI

[![CI](https://github.com/bkumars22/SupplyChainPlatformProject/actions/workflows/ci.yml/badge.svg)](https://github.com/bkumars22/SupplyChainPlatformProject/actions)
[![Tests](https://img.shields.io/badge/Playwright-41%20tests-green)](https://github.com/bkumars22/SupplyChainPlatformProject)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

---

## What is SCIP?

SCIP is a production-grade supply chain management platform that combines **enterprise-grade backend**, **AI anomaly detection**, **full mobile support**, and **embedded test intelligence** — built independently to demonstrate that a single AI-augmented engineer can deliver what previously required teams of hundreds.

Most mid-market companies still manage supply chains in spreadsheets. The gap between "Excel on a shared drive" and a SAP implementation is enormous. SCIP fills that gap.

---

## Live Architecture

```
┌─────────────────────────────────────────────────────────────┐
│  Clients                                                     │
│  React 18 Web (port 3000) · React Native iOS/Android        │
│  Data Portal HTML · Test Intelligence Dashboard             │
└──────────────────────┬──────────────────────────────────────┘
                       │ JWT Bearer Token · REST
┌──────────────────────▼──────────────────────────────────────┐
│  Spring Boot API  (port 8089/supchain)                      │
│  80+ endpoints · JWT auth · BCrypt · RBAC · XSS filter      │
│  JPA/Hibernate · H2 (dev) · PostgreSQL (prod)               │
└────────┬─────────────────────────────┬───────────────────────┘
         │ JPA                          │ HTTP
┌────────▼────────┐         ┌──────────▼──────────────────────┐
│  Database        │         │  Python AI Service (port 8001)   │
│  H2 / PostgreSQL │         │  FastAPI · IsolationForest ML    │
│  80+ tables      │         │  Supplier risk scoring           │
│  Flyway migrations│        │  Claude AI alert explanations    │
└──────────────────┘         └─────────────────────────────────┘
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17 · Spring Boot 3 · Maven · JWT (HMAC-SHA256) · BCrypt · JPA/Hibernate |
| Frontend | React 18 · React Router · Axios · CSS-in-JS |
| Mobile | React Native · Expo SDK · Shared JWT auth |
| AI/ML | Python FastAPI · IsolationForest · scikit-learn · Claude AI API |
| Testing | Playwright TypeScript · Custom persistent reporter · Test Intelligence Dashboard |
| DevOps | Docker Compose · GitHub Actions CI/CD · Railway (production) |
| Database | H2 (dev, in-memory) · PostgreSQL (prod) · Flyway migrations |

---

## Modules

### Backend API — `/api/*`

| Module | Endpoints | Description |
|---|---|---|
| Auth | `/api/auth/login` `/api/auth/me` | JWT login, role extraction |
| Item Master | `/api/items` | Components, raw materials, finished goods |
| Bill of Materials | `/api/bom` `/api/bom/{key}` `/api/bom/stats` | Multi-level BOM management |
| Cost Records | `/api/costs` `/api/cost-records` | DRAFT→SUBMIT→APPROVE→REJECT workflow |
| Suppliers | `/api/suppliers` `/api/suppliers/{id}/deliveries` | OTD scoring, tier classification |
| Forecasting | `/api/forecasts` | Demand and supply forecasts |
| Alerts | `/api/alerts/active` | Active supply chain alerts with dismiss |
| Users | `/api/admin/users` `/api/admin/roles` | User management, RBAC |
| Dashboard | `/api/dashboard/summary` | Aggregated KPIs |
| AI | `/api/ai/health` `/api/ai/anomalies` | ML risk scoring, anomaly detection |

### Web Application — `localhost:3000`

| Route | Description |
|---|---|
| `/dashboard` | Live KPIs — alerts, BOMs, pending approvals, at-risk suppliers |
| `/bom` | Bill of Materials browser with detail view |
| `/cost-records` | Cost change workflow with approve/reject |
| `/suppliers` | Supplier scorecard with OTD and tier badges |
| `/alerts` | Active alerts with severity classification and dismiss |
| `/admin/users` | User management — create, edit, disable, set password |
| `/forecasts` | Demand forecasting with variance analysis |
| `/ai` | AI anomaly engine dashboard |
| `/tests` | **Test Intelligence Dashboard** — live test results, API health, known bugs |

### Test Intelligence Dashboard — `/tests`

The test dashboard is embedded inside the application itself — no external tools needed.

- **41 Playwright test cases** grouped by module with PASS/BUG badges
- **14 live API health checks** with HTTP status and response time
- **Known Bugs tab** with P0–P3 severity and exact fix instructions
- **Live Results tab** that auto-refreshes every 10 seconds after a test run
- Custom `persistent-reporter.js` writes `test-results.json` to the React public folder

### Data Portal — `docs/scip_data_portal.html`

Standalone HTML file — open in Chrome with backend running. No install needed.

- Create items, BOMs, cost records, suppliers, users via forms
- Full cost record workflow: DRAFT → SUBMIT → APPROVE → REJECT
- Bulk CSV import for all modules with template download
- API Explorer — test any endpoint with live responses
- DB Console — H2 SQL runner with fix instructions for seed data

---

## Test Results

**41 test cases · 36 passing · 5 known bugs documented**

| Module | Tests | Status |
|---|---|---|
| Authentication | 7 | 6 pass · 1 known bug (P0) |
| Item Master | 6 | 5 pass · 1 known bug (P2) |
| Bill of Materials | 7 | 7 pass |
| Cost Records | 6 | 5 pass · 1 known bug (P1) |
| Suppliers | 3 | 2 pass · 1 known bug (P3) |
| User Management | 6 | 5 pass · 1 known bug (P2) |
| Alerts | 4 | 4 pass |
| Dashboard | 2 | 2 pass |

### Bugs Discovered Through Black Box Testing

| ID | Severity | Description | Fix |
|---|---|---|---|
| BB-AUTH-02 | P0 Critical | Any password accepted — BCrypt check skipped when password is null | `UPDATE PCM_USER SET PASSWORD = BCrypt('kumar123') WHERE USER_ID = 'kumar'` |
| BB-COST-02 | P1 High | POST /api/cost-records returns 405 — no @PostMapping in controller | Add `@PostMapping` to `CostRecordRestController` |
| BB-ITEM-04 | P2 Medium | POST /api/items returns 500 — NullPointerException in service layer | Add null check in `ItemManagementService.createItem()` |
| BB-USR-03 | P2 Medium | POST /api/admin/users returns 500 — roleKey FK resolution fails | Fix `CreateUserRequest` record in `UserManagementController` |
| BB-SUP-03 | P3 Low | Supplier tier doesn't match OTD score in seed data | Call `POST /api/suppliers/recalculate-tiers` after startup |

---

## Quick Start

### Prerequisites

- Java 17+
- Node.js 18+
- Python 3.9+
- Maven 3.9+
- Docker Desktop (optional)

### Option 1 — Run locally

```bash
# Clone
git clone https://github.com/bkumars22/SupplyChainPlatformProject.git
cd SupplyChainPlatformProject

# Build and start backend
mvn package -DskipTests
java -jar target/pcm-0.0.1-SNAPSHOT.war

# Start web app (new terminal)
cd ../scweb
npm install
npm start

# Start Python AI (new terminal)
cd ai-service
pip install -r requirements.txt
uvicorn main:app --port 8001
```

### Option 2 — Docker Compose

```bash
docker compose up -d
```

### Option 3 — Smart Fix Agent (recommended for first run)

```powershell
# Windows PowerShell
Set-ExecutionPolicy Bypass -Scope Process
.\agents\master_fix.ps1
```

The agent automatically fixes build issues, starts all services, and verifies all APIs.

### Service URLs

| Service | URL |
|---|---|
| Web App | http://localhost:3000 |
| Test Dashboard | http://localhost:3000/tests |
| Spring Boot API | http://localhost:8089/supchain |
| Swagger UI | http://localhost:8089/supchain/swagger-ui/index.html |
| H2 Console | http://localhost:8089/supchain/h2-console |
| Python AI | http://localhost:8001 |

---

## Run Tests

```bash
cd playwright-tests

# Run all 41 tests with live dashboard update
npx playwright test tests/scip.spec.ts --reporter=list

# Run with HTML report
npx playwright test tests/scip.spec.ts --reporter=html
npx playwright show-report

# Auto-run every 30 minutes and update dashboard
.\auto_run_tests.ps1 -IntervalMinutes 30
```

---

## Project Structure

```
SupplyChainPlatformProject/
│
├── src/                          # Java Spring Boot backend
│   └── main/java/
│       ├── com/test/pcm/         # Core application
│       └── com/scplatform/api/   # REST controllers, services
│           ├── controller/       # 15 REST controllers
│           ├── service/          # Business logic
│           ├── config/           # Security, JWT, beans
│           └── jwt/              # JWT utilities
│
├── scweb/                        # React 18 web application
│   └── src/
│       ├── pages/                # 9 page components
│       ├── api.js                # All API functions
│       └── App.js                # Routes
│
├── SupplyChainApp/               # React Native mobile app
│   └── screens/                  # 10 screens
│
├── ai-service/                   # Python FastAPI AI microservice
│   ├── main.py                   # FastAPI app
│   └── models/                   # IsolationForest, forecasting
│
├── playwright-tests/             # Playwright TypeScript test suite
│   ├── tests/
│   │   └── scip.spec.ts          # 41 test cases
│   ├── persistent-reporter.js    # Writes results to React public/
│   └── playwright.config.ts      # Headless CI config
│
├── docs/                         # Documentation
│   ├── ARCHITECTURE.md           # Architecture guide with diagrams
│   ├── API_REFERENCE.md          # All 80+ endpoints documented
│   ├── BUSINESS_REQUIREMENTS.md  # BRD
│   ├── USE_CASES.md              # 20 use cases UC-01 to UC-20
│   └── scip_data_portal.html     # Standalone data management portal
│
├── agents/                       # Autonomous fix agents
│   ├── master_fix.ps1            # Smart build + fix + test agent
│   ├── scip_orchestrator.ps1     # Full autonomous pipeline
│   └── scip_agent_live.ps1       # Self-updating agent from GitHub
│
├── .github/workflows/
│   ├── ci.yml                    # CI — test on every push
│   └── cd.yml                    # CD — deploy to Railway
│
└── docker-compose.yml            # 5 services: DB, Redis, API, AI, Web
```

---

## Key Achievements

| Metric | Value |
|---|---|
| Total test cases | 41 |
| API endpoints tested | 14 live health checks |
| Bugs found by testing | 5 (1 P0 security, 1 P1 workflow, 2 P2 crashes, 1 P3 data) |
| Backend endpoints | 80+ |
| Database tables | 80+ |
| Autonomous agents | 25+ |
| Lines of test code | ~540 (scip.spec.ts) |
| Build time | ~35 seconds (Maven, skip tests) |
| Startup time | ~40 seconds (Spring Boot + H2) |

---

## Documentation

| Document | Description |
|---|---|
| [Architecture Guide](docs/ARCHITECTURE.md) | Full system architecture, data flows, auth flow, port reference |
| [API Reference](docs/API_REFERENCE.md) | All endpoints with request/response examples |
| [Business Requirements](docs/BUSINESS_REQUIREMENTS.md) | BRD with 20 use cases |
| [Data Portal](docs/scip_data_portal.html) | Standalone management tool — open in Chrome |

---

## What Makes This Different

**1. AI-native, not AI-bolted-on**
IsolationForest ML scores every supplier and cost change in real time. Claude AI explains findings in plain English. This is not a dashboard widget — it's integrated into every alert.

**2. Test intelligence embedded in the product**
The test dashboard lives at `/tests` inside the app. Persistent reporter writes JSON to the React public folder. Any team member sees live test results without switching tools.

**3. Quality engineering discipline**
5 bugs found through systematic black box testing before they could reach production — including a P0 auth bypass and a missing create endpoint that would have completely blocked the core workflow at go-live.

**4. Autonomous agents**
25+ PowerShell and Python agents handle build fixing, test running, deployment, and health monitoring. The orchestrator agent diagnoses issues, applies fixes, rebuilds, runs tests, commits, and generates reports — unattended.

**5. Solo delivery at team speed**
Built by one engineer in 4 weeks using Claude AI as a force multiplier. All architecture decisions, test strategy, bug analysis, and domain knowledge are independent — AI accelerated execution, not thinking.

---

## Built By

**Kumara Swamy** — Staff SDET · AI Development Lead · Supply Chain Domain Expert

13 years building enterprise supply chain software. This project demonstrates full-stack delivery capability beyond the QA domain.

- LinkedIn: [linkedin.com/in/kumaraswamy7731b020](https://linkedin.com/in/kumaraswamy7731b020)
- GitHub: [github.com/bkumars22](https://github.com/bkumars22)
- Email: swamy.kumar02@gmail.com

---

## License

MIT License — see [LICENSE](LICENSE) for details.

Copyright © 2026 Kumara Swamy — github.com/bkumars22
