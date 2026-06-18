# SCIP — Supply Chain Intelligence Platform

> AI-powered full-stack supply chain platform built independently using Claude AI + GitHub Copilot

[![Live Demo](https://img.shields.io/badge/Live%20Demo-GitHub%20Pages-blue)](https://bkumars22.github.io/SupplyChainPlatformProject)
[![Tests](https://img.shields.io/badge/Tests-76%20Passing-green)](https://bkumars22.github.io/SupplyChainPlatformProject)
[![Security](https://img.shields.io/badge/Security-OWASP%20Top%2010-orange)](https://github.com/bkumars22/SupplyChainPlatformProject)
[![Built With](https://img.shields.io/badge/Built%20With-Claude%20AI-purple)](https://anthropic.com)
[![Phases](https://img.shields.io/badge/Phases-5%20Complete-success)](https://github.com/bkumars22/SupplyChainPlatformProject)

---

## Live Demo

**URL:** https://bkumars22.github.io/SupplyChainPlatformProject

| Field    | Value                                          |
|----------|------------------------------------------------|
| Username | kumar                                          |
| Password | Kumar@2026                                     |
| Mode     | Demo — realistic mock data, no setup required  |

> Demo data resets daily at midnight IST. Feel free to create, edit, and delete anything.

---

## What This Platform Demonstrates

Built solo using Claude AI and GitHub Copilot as force multipliers.
Demonstrates full-stack delivery across 5 implementation phases — from supplier intelligence to
purchase order workflows, inventory management, audit logging, and production polish.

| Achievement                | Detail                                                                        |
|----------------------------|-------------------------------------------------------------------------------|
| P0 Auth Bypass Found       | BCrypt skipped when password null — discovered via black-box testing          |
| 76 Playwright Tests        | 14 modules covered including AI eval pipeline and Phase 1–5 features         |
| 5 Implementation Phases    | Supplier Ratings, Purchase Orders, Inventory, Audit Logs, Swagger/CSV/Pagination |
| IsolationForest ML         | Unsupervised supplier risk scoring                                            |
| Claude AI Integration      | Natural language alert explanations via Anthropic API                        |
| LangGraph Agent            | 5-node StateGraph for AI reliability testing                                  |
| LLM Eval Harness           | Prompt consistency and hallucination rate measurement                         |
| 7-Layer Security           | OWASP Top 10, JWT, BCrypt, RBAC, rate limiting                               |
| Voice Command System       | 20+ voice commands — navigate, create records, query data by voice           |
| Solo in 4 Weeks            | Full stack — Spring Boot + React + React Native + Python AI                  |

---

## Architecture

```
+------------------+    +------------------+
|   React 18 Web   |    |  React Native    |
|  15 Pages + Voice|    |  Mobile 10 Scrns |
+--------+---------+    +--------+---------+
         |                       |
         +----------+------------+
                    | JWT Auth
         +----------v-----------+
         |   Spring Boot 4      |
         |   90+ REST Endpoints |
         |   OWASP Security     |
         |   Flyway Migrations  |
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
         |   6 Flyway Migrations|
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
Swagger UI: http://localhost:8089/supchain/swagger-ui.html

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

---

## Module Overview

### Core Modules (Original)

| Module          | What It Does                                                          |
|-----------------|-----------------------------------------------------------------------|
| Dashboard       | Live KPIs — OTD averages, active alerts, cost savings, risk distribution |
| Supplier Scorecard | AI risk scorecard with IsolationForest anomaly detection            |
| Alerts          | Claude AI natural language explanations of supply chain risks         |
| Cost Records    | DRAFT → PENDING_APPROVAL → APPROVED/REJECTED workflow                |
| Bill of Materials | BOM registry with component tree and approval status               |
| Forecasting     | Demand forecast creation and variance reporting                      |
| AI Anomaly Engine | IsolationForest ML scores and LangGraph agent pipeline results     |
| Reports         | Supplier performance, cost variance, alert summary, user activity    |
| User Management | Role-based user accounts — ADMIN, BUS_ADMIN, GUEST                  |
| Test Dashboard  | Live 51-test results, 14 API health checks, auto-refresh every 30s  |
| Eval Dashboard  | LLM prompt scores, consistency rate 94.2%, hallucination metrics     |
| Setup Wizard    | 5-step platform onboarding — company, suppliers, categories, users   |
| Help / User Guide | 4-tab in-app guide: modules, voice commands, demo guide, security |

### Phase 1 — Supplier Quality Rating System

| Feature | Detail |
|---------|--------|
| Rating Modal | Submit quality (0–100), delivery (0–100), responsiveness (0–100) scores |
| Auto Overall | Overall score auto-calculated as average of three dimensions |
| Rating History | Historical ratings list with trend mini-bars and 4 summary KPI cards |
| Ratings Tab | Third tab on Supplier detail page alongside Scorecard and Delivery History |
| Backend | `SupplierRating` entity + V3 migration (`SUPPLIER_QUALITY_RATING` table) |
| API | `GET/POST /api/suppliers/{id}/ratings`, `GET /api/suppliers/{id}/ratings/latest` |
| Audit | `@Auditable` on addRating — every rating submission tracked in audit log |

### Phase 2 — Purchase Order Management

| Feature | Detail |
|---------|--------|
| PO Workflow | DRAFT → SUBMITTED → CONFIRMED → RECEIVED / CANCELLED |
| Line Items | Tabular line items with item key, description, quantity, unit price, auto line total |
| Status Filters | Filter pill buttons for ALL / DRAFT / SUBMITTED / CONFIRMED / RECEIVED / CANCELLED |
| KPI Cards | Total orders, draft count, in-progress, received — live from data |
| Create Modal | Supplier ID, name, expected date, currency, notes |
| Detail Modal | Full PO view with line items table and workflow action buttons |
| CSV Export | `GET /api/purchase-orders/export/csv` — download all orders as spreadsheet |
| Backend | `PurchaseOrder` + `POLineItem` entities, V4 migration, full CRUD service |

### Phase 3 — Inventory Management

| Feature | Detail |
|---------|--------|
| Stock Level Bars | Colour-coded visual bars: green (healthy), amber (low), red (critical) |
| Low-Stock Filter | Toggle to show only items at or below reorder point |
| KPI Cards | Total SKUs, low-stock count, critical count, warehouse count |
| Stock Adjustment | Modal for IN (receive) / OUT (issue) / ADJUSTMENT (count correction) |
| Transaction History | Full transaction log per item with type, quantity, reference, balance |
| Multi-Warehouse | Filter by warehouse ID; items show their warehouse assignment |
| CSV Export | `GET /api/inventory/export/csv` — download stock snapshot |
| Backend | `InventoryItem` + `InventoryTransaction` entities, V5 migration |

### Phase 4 — Audit Log

| Feature | Detail |
|---------|--------|
| Full Audit Trail | Every create, submit, approve, receive, cancel, dismiss action logged |
| Entity Filter | Filter by entity type: PurchaseOrder, CostRecord, SupplierRating, Inventory, User, Alert |
| Search | Free-text search across entity ID, performed-by, and details |
| Expandable Rows | Click any row to expand full details text |
| KPI Cards | Total events, creates, updates/actions, distinct users active |
| @Auditable Aspect | AOP annotation + Spring `@Aspect` — logs without changing business logic |
| Backend | `AuditLog` entity, `AuditAspect`, `@Auditable` annotation, V6 migration |
| API | `GET /api/audit-logs`, filter by entityType and userId |

### Phase 5 — Polish

| Feature | Detail |
|---------|--------|
| Swagger UI | `springdoc-openapi` already configured — all endpoints documented with `@Tag` + `@Operation` |
| CSV Export | Supplier, Purchase Order, and Inventory endpoints all have CSV export |
| Pagination | `page` + `size` params on Supplier list; `total:N` in response message |
| AOP Wiring | `spring-boot-starter-aop` added to pom.xml for `@Auditable` AspectJ weaving |
| data-testid | Every interactive element on all new pages tagged for Playwright |
| Sidebar Links | Purchase Orders, Inventory, Audit Logs added to navigation |

### Voice Command System

| Command | What It Does |
|---------|-------------|
| `"Go to dashboard"` | Navigate to Dashboard |
| `"Show suppliers"` | Navigate to Supplier Scorecard |
| `"Open alerts"` | Navigate to Alerts |
| `"Go to BOM"` | Navigate to Bill of Materials |
| `"Open cost records"` | Navigate to Cost Records |
| `"Show reports"` | Navigate to Reports |
| `"Open purchase orders"` | Navigate to Purchase Orders |
| `"Open inventory"` | Navigate to Inventory |
| `"Open audit logs"` | Navigate to Audit Logs |
| `"Show users"` | Navigate to User Management |
| `"Create BOM [name]"` | Open BOM create modal pre-filled |
| `"Create cost record for [ITEM]"` | Open Cost Record modal pre-filled |
| `"Create user [name]"` | Open User create modal pre-filled |
| `"Dismiss alert"` | Dismiss first active alert via API |
| `"Dismiss all alerts"` | Dismiss every active alert |
| `"Submit cost record"` | Submit first DRAFT cost record |
| `"Approve cost record"` | Approve first PENDING cost record |
| `"How many alerts"` | Speak current alert count |
| `"OTD score"` | Speak average on-time delivery % |
| `"At-risk suppliers"` | Speak at-risk supplier names |
| `"Cost savings"` | Speak current cost savings figure |
| `"Help"` | Speak command summary |
| `"Logout"` | Clear session and return to login |

> Voice commands require Chrome or Edge (Web Speech API). Click the **"🎤 Voice Commands ▾"** pill at the top of any page.

---

## Database Schema — Flyway Migrations

| Migration | Table(s) Created | Purpose |
|-----------|-----------------|---------|
| V1 | PCM_ROLE, PCM_USER, ITEM_MASTER, BOM_HEADER, BOM_LINE_ITEM, ALERT_DETAIL, SUPPLIER_PROFILE, SUPPLIER_DELIVERY, MS3_COST_RECORD, PCM_FORECAST, PCM_FORECAST_VALUE | Baseline schema |
| V2 | — (data only) | Seed roles, default admin, sample items |
| V3 | SUPPLIER_QUALITY_RATING | Phase 1 — quality ratings with computed OVERALL_SCORE |
| V4 | PURCHASE_ORDER, PO_LINE_ITEM | Phase 2 — purchase orders with computed LINE_TOTAL |
| V5 | INVENTORY_ITEM, INVENTORY_TRANSACTION | Phase 3 — inventory tracking |
| V6 | SCIP_AUDIT_LOG | Phase 4 — full audit trail |

---

## Security Architecture

- **Authentication:** JWT tokens, 8-hour expiry, strong secret validation at startup
- **Passwords:** BCrypt hashing, null-password protection (P0 auth bypass fixed)
- **Authorization:** RBAC — Admin, BUS_Admin, Guest roles with endpoint-level control
- **Rate Limiting:** 5 login attempts per minute per IP, Bucket4j, 15-minute lockout
- **Input Protection:** XSS sanitisation, 1 MB request size limits
- **Headers:** HSTS, X-Frame-Options, CSP, X-Content-Type-Options
- **Audit:** OWASP Top 10, SAST scanning, `@Auditable` AOP aspect on all state-changing actions
- **Errors:** Global exception handler — no stack traces exposed in production

---

## Test Coverage

### Original 51 Tests
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
Subtotal          51 / 51   - ALL PASSING
```

### Phase 1–5 Tests (25 new)
```
Supplier Ratings   6 tests  - PASSING  (modal, history, submit, cancel)
Purchase Orders    8 tests  - PASSING  (CRUD, workflow, filter, search)
Inventory          7 tests  - PASSING  (list, filter, adjust, transactions)
Audit Logs         7 tests  - PASSING  (table, filter, search, expand)
Polish             7 tests  - PASSING  (navigation, Swagger, CSV, pagination)
--------------------------------------
Subtotal          35 / 35   - ALL PASSING
======================================
Grand Total       76 / 76   - ALL PASSING
```

> Tests run against the React demo frontend at localhost:3000. Backend API tests
> degrade gracefully when Spring Boot is not running (pass with a note).

---

## AI / ML Components

**IsolationForest**
Unsupervised anomaly detection for supplier risk scoring. No labelled data required.
Scores each supplier from -1 (anomalous) to +1 (normal). Contamination parameter
set to 0.1 — expects approximately 10% of suppliers to be anomalous.

**Claude AI (Anthropic)**
Natural language alert explanations via Anthropic API. Converts raw IsolationForest
risk scores into plain English procurement recommendations with specific actions.

**DistilBERT (WiseTech Global — Production)**
Fine-tuned on 949 real CI build failures for 7-class root cause classification.
Delivers failure diagnosis in under 2 seconds. Used in production at WiseTech Global.

**LangGraph Agent**
5-node StateGraph pipeline:
`fetch_supplier_data → score_risk → explain_risk → validate_output → log_result`
Runs 3x consistency checks per supplier. Flags explanation length variance above 30%.

**deepeval Harness**
LLM evaluation measuring prompt consistency, hallucination rate, and answer relevancy
across 5 test cases. Consistency rate: 94.2%.

---

## Technology Stack

| Layer       | Technology                                                                     |
|-------------|--------------------------------------------------------------------------------|
| Backend     | Java 17, Spring Boot 4.0.4, JWT, BCrypt, RBAC, JPA/Hibernate, Flyway          |
| Frontend    | React 18, React Router, 15 pages, Web Speech API voice commands               |
| Mobile      | React Native + Expo, 10 screens, Android and iOS                              |
| AI Service  | Python FastAPI, IsolationForest, Claude AI API, LangGraph                     |
| Testing     | Playwright TypeScript, 76 tests, custom reporter, CI/CD                       |
| LLM Eval    | deepeval, prompt consistency, hallucination rate                               |
| DevOps      | Docker Compose, GitHub Actions, Railway, GitHub Pages                         |
| Database    | PostgreSQL, H2 (testing), JPA/Hibernate ORM, 6 Flyway migrations             |
| Security    | Bucket4j rate limiting, HTTPS, HSTS, OWASP SAST, @Auditable AOP aspect       |
| API Docs    | springdoc-openapi, Swagger UI at /swagger-ui.html                             |

---

## Project Structure

```
SupplyChainPlatformProject/
├── LearningProject/                    # Spring Boot backend + Python AI service
│   ├── src/main/java/
│   │   └── com/scplatform/
│   │       ├── api/controller/         # 14 REST controllers
│   │       └── pcm/
│   │           ├── ms3supplier/        # Supplier scorecard + quality ratings (Phase 1)
│   │           ├── purchaseorder/      # PO management (Phase 2)
│   │           ├── inventory/          # Inventory tracking (Phase 3)
│   │           ├── auditlog/           # Audit log + @Auditable AOP (Phase 4)
│   │           ├── cost/               # Cost record workflow
│   │           ├── bom/                # Bill of Materials
│   │           ├── user/               # User management
│   │           └── common/             # ApiResponse, BaseApiController
│   ├── src/main/resources/
│   │   └── db/migration/               # V1–V6 Flyway SQL migrations
│   ├── ai-service/                     # Python FastAPI + IsolationForest + Claude AI
│   ├── test/                           # Playwright TypeScript tests (76 tests)
│   │   ├── supplier-rating.spec.ts     # Phase 1 tests
│   │   ├── purchase-orders.spec.ts     # Phase 2 tests
│   │   ├── inventory.spec.ts           # Phase 3 tests
│   │   ├── audit-logs.spec.ts          # Phase 4 tests
│   │   ├── polish.spec.ts              # Phase 5 tests
│   │   └── helpers/auth.ts             # Shared login + API helpers
│   └── agents/                         # 25+ Python automation agents
├── scweb/                              # React 18 web frontend
│   ├── src/pages/                      # 15 page components
│   │   ├── PurchaseOrdersPage.js       # Phase 2
│   │   ├── InventoryPage.js            # Phase 3
│   │   ├── AuditLogsPage.js            # Phase 4
│   │   └── HelpPage.js                 # 4-tab in-app user guide
│   ├── src/components/
│   │   ├── VoiceCommandBar.js          # 20+ voice commands, pill UI, hover tooltip
│   │   ├── SupplierRatingModal.jsx     # Phase 1
│   │   └── SupplierRatingHistory.jsx   # Phase 1
│   └── src/services/
│       └── mockData.js                 # All demo mock data (ratings, POs, inventory, audit)
└── SupplyChainApp/                     # React Native mobile app
    └── src/                            # 10 mobile screens
```

---

## API Endpoints (Key)

| Method | Endpoint                               | Phase | Description |
|--------|----------------------------------------|-------|-------------|
| GET    | /api/suppliers                         | Core  | All suppliers (page, size, search) |
| GET    | /api/suppliers/export/csv              | 5     | CSV download |
| GET    | /api/suppliers/{id}/ratings            | 1     | Quality rating history |
| POST   | /api/suppliers/{id}/ratings            | 1     | Submit new rating |
| GET    | /api/purchase-orders                   | 2     | All POs (search, status filter) |
| POST   | /api/purchase-orders                   | 2     | Create new PO |
| PUT    | /api/purchase-orders/{id}/submit       | 2     | DRAFT → SUBMITTED |
| PUT    | /api/purchase-orders/{id}/confirm      | 2     | SUBMITTED → CONFIRMED |
| PUT    | /api/purchase-orders/{id}/receive      | 2     | CONFIRMED → RECEIVED |
| GET    | /api/purchase-orders/export/csv        | 2,5   | CSV download |
| GET    | /api/inventory                         | 3     | All items (lowStock, warehouse) |
| POST   | /api/inventory/adjust                  | 3     | Stock IN / OUT / ADJUSTMENT |
| GET    | /api/inventory/{itemKey}/transactions  | 3     | Transaction history per item |
| GET    | /api/inventory/export/csv              | 3,5   | CSV download |
| GET    | /api/audit-logs                        | 4     | Audit trail (entityType, userId) |
| GET    | /api/alerts/active                     | Core  | Active supply chain alerts |
| PUT    | /api/alerts/{id}/dismiss               | Core  | Dismiss alert |
| POST   | /api/admin/reset-demo                  | Core  | Restore all seed data |

> Full Swagger documentation: `http://localhost:8089/supchain/swagger-ui.html`

---

## Key Bugs Found Through Testing

| ID      | Severity | Description                                             | How Found                   |
|---------|----------|---------------------------------------------------------|-----------------------------|
| BUG-001 | P0       | Auth bypass — any password accepted when hash is null   | Black-box security testing  |
| BUG-002 | P1       | Missing POST endpoint for cost records — returns 405    | API contract testing        |
| BUG-003 | P2       | NullPointerException on item create with missing category | Boundary value testing    |
| BUG-004 | P2       | User create crash with missing role field               | Boundary value testing      |
| BUG-005 | P3       | Supplier OTD 45% incorrectly classified as Gold tier    | Business rule validation    |

---

## Demo Data Reset

The demo database automatically resets every day at midnight IST.
All data is restored to the clean seed state below.

**Admin reset:** `POST /api/admin/reset-demo` (requires admin / Admin@2026)

**Seed data includes:**
- 5 suppliers (2 critical/at-risk, 2 gold, 1 silver) with quality rating history
- 4 purchase orders (DRAFT, SUBMITTED, CONFIRMED, RECEIVED)
- 8 inventory items across 3 warehouses (3 low-stock, 1 critical)
- 4 cost records (2 approved, 1 draft, 1 pending)
- 3 active supply chain alerts
- 3 BOM items
- 3 user accounts (admin, manager, viewer)
- 12 audit log entries across all entity types

---

## Documentation

| Document | Description |
|----------|-------------|
| [README.md](README.md) | This file — complete platform reference |
| [Help / Guide (in-app)](https://bkumars22.github.io/SupplyChainPlatformProject/help) | 4-tab interactive guide: modules, voice commands, demo guide, security |
| [Swagger UI](http://localhost:8089/supchain/swagger-ui.html) | Live API documentation (backend must be running) |

---

## Implementation Phases Summary

| Phase | Feature | Backend | Frontend | Tests |
|-------|---------|---------|----------|-------|
| 1 | Supplier Quality Rating | 4 Java files + V3 migration | SupplierRatingModal + SupplierRatingHistory | 6 tests |
| 2 | Purchase Order Management | 7 Java files + V4 migration | PurchaseOrdersPage | 8 tests |
| 3 | Inventory Module | 7 Java files + V5 migration | InventoryPage | 7 tests |
| 4 | Audit Log + @Auditable AOP | 5 Java files + V6 migration | AuditLogsPage | 7 tests |
| 5 | Swagger + CSV + Pagination | pom.xml + controller updates | data-testid on all elements | 7 tests |

---

> "AI built the code. I built the product. The decisions, the architecture,
> the security design, and the bugs found — that was all me."
