# 🏭 Supply Chain Intelligence Platform (SCIP)

> A full-stack enterprise supply chain management system built with Java 21, Spring Boot 4, React 18, and React Native — covering cost management, BOM visibility, supplier scorecards, intelligent alerts, and AI-powered anomaly detection.

---

## 📌 Table of Contents

- [Project Overview](#-project-overview)
- [Business Problem](#-business-problem-solved)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Modules](#-modules)
- [Key Features](#-key-features)
- [Project Stats](#-project-stats)
- [Quick Start](#-quick-start)
- [Use Cases](#-use-cases)
- [API Reference](#-api-reference)
- [Roadmap](#-roadmap)
- [License](#-license)

---

## 🎯 Project Overview

The **Supply Chain Intelligence Platform** is an enterprise-grade, full-stack system comparable to SAP Ariba, Oracle SCM, and Blue Yonder — built from scratch to demonstrate real-world supply chain domain knowledge combined with modern full-stack engineering.

The platform manages the entire supply chain intelligence lifecycle:

- **Cost governance** — track, approve, and enforce component cost changes
- **BOM management** — visualise multi-level Bills of Materials
- **Supplier performance** — OTD scoring, tier classification, delivery tracking
- **Intelligent alerts** — prioritised supply chain event notifications
- **AI anomaly detection** — DistilBERT-powered cost and delivery anomaly analysis

---

## 💼 Business Problem Solved

| Problem | Impact | How SCIP Solves It |
|---|---|---|
| **Cost Visibility** | Price spikes go unnoticed until they impact margins | Structured cost change workflow with approval gates and automatic ITEM_MASTER update |
| **BOM Complexity** | Multi-level BOMs with 100s of components are hard to manage | Hierarchical BOM viewer with component drill-down and cost roll-up |
| **Supplier Risk** | OTD failures not surfaced early enough | Real-time supplier scorecard with PREFERRED / CONDITIONAL / PROBATION tier auto-classification |
| **Alert Fatigue** | Hundreds of alerts daily — hard to prioritise | AI-powered anomaly engine surfaces only high-confidence, high-impact alerts |
| **Mobile Access** | Supply chain managers need data on the go | Full React Native mobile app with iOS + Android support |

---

## 🏗️ Architecture

```
┌──────────────────────────────────────────────────────────────┐
│              SUPPLY CHAIN INTELLIGENCE PLATFORM              │
│                    Full Stack Architecture                    │
└──────────────────────────────────────────────────────────────┘

PRESENTATION LAYER
┌─────────────────────────┐   ┌──────────────────────────────┐
│  React Web App          │   │  React Native Mobile App     │
│  port 3000              │   │  Expo SDK 51                 │
│  ─────────────          │   │  ─────────────────────       │
│  Dashboard              │   │  Dashboard Screen            │
│  Alerts                 │   │  Alerts Screen               │
│  BOM List / Detail      │   │  BOM List / Detail           │
│  Cost Records    [MS3]  │   │  Cost Records    [MS3]       │
│  Supplier Scorecard     │   │  Suppliers Screen            │
│  AI Anomaly Engine      │   │                              │
│  User Management        │   │                              │
└────────┬────────────────┘   └─────────────┬────────────────┘
         │  HTTP + JWT Bearer Token          │
         ▼                                   ▼
┌──────────────────────────────────────────────────────────────┐
│  Spring Boot 4 REST API  (port 8089/supchain)                │
│  ──────────────────────────────────────────────────────      │
│  JwtAuthFilter → SecurityConfig → RestControllers            │
│  ├── AlertRestController    /api/alerts                      │
│  ├── BomRestController      /api/bom                         │
│  ├── CostRecordRestController /api/costs                     │
│  ├── SupplierRestController /api/suppliers                   │
│  ├── DashboardController    /api/dashboard                   │
│  ├── AiController           /api/ai                          │
│  ├── MobileAuthController   /api/auth                        │
│  ├── UserManagementController /api/admin/users               │
│  └── PasswordManagementController /api/auth/change-password  │
└──────────────────────┬───────────────────────────────────────┘
                       │  JPA / Hibernate
                       ▼
┌──────────────────────────────────────────────────────────────┐
│  H2 In-Memory Database (dev) → Oracle (production)          │
│  ─────────────────────────────────────────────────────────   │
│  ITEM_MASTER · BOM_HEADER · BOM_LINE_ITEM · ALERT_DETAIL    │
│  PCM_USER · PCM_ROLE · SUPPLIER_PROFILE · SUPPLIER_DELIVERY  │
│  MS3_COST_RECORD · BUSINESS_ENTITY · PCM_FORECAST (MS4)     │
└──────────────────────────────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│  Python FastAPI AI Service  (port 8001)                      │
│  DistilBERT anomaly detection model                          │
│  Prophet demand forecasting                                  │
└──────────────────────────────────────────────────────────────┘
```

---

## ⚙️ Tech Stack

| Layer | Technology | Version | Purpose |
|---|---|---|---|
| **Backend** | Java | 21.0.11 | Core language |
| **Backend** | Spring Boot | 4.0.4 | Web framework, dependency injection |
| **Backend** | Spring Security + JWT | — | Authentication & authorisation |
| **Backend** | JPA / Hibernate | — | ORM — 80+ database tables |
| **Backend** | H2 Database | — | In-memory dev database |
| **Backend** | Maven | 3.9.12 | Build tool |
| **Backend** | Swagger / OpenAPI | — | API documentation |
| **Web** | React | 18 | Web UI framework |
| **Web** | React Router | 6 | Client-side routing |
| **Web** | Axios | — | HTTP client |
| **Mobile** | React Native | — | Cross-platform mobile |
| **Mobile** | Expo | SDK 51 | Mobile development toolchain |
| **AI** | Python FastAPI | — | AI microservice |
| **AI** | DistilBERT | — | Anomaly detection NLP model |
| **AI** | Prophet | — | Demand forecasting |
| **Testing** | Playwright | — | E2E browser automation |

---

## 📦 Modules

### ✅ MS1 — Core Platform
- JWT authentication (login / logout / token refresh)
- Role-based access control (ADMIN, BUS_ADMIN, GUEST)
- Dashboard with KPI summary cards
- Alert engine with active alert list and dismiss
- H2 database with 80+ auto-created tables

### ✅ MS2 — BOM Management
- Bill of Materials list with search and pagination
- BOM detail view with multi-level component tree
- Item master integration (ITEM_MASTER table)
- BOM version tracking

### ✅ MS3 — Cost Intelligence & Supplier Scorecard
- **Cost Record Workflow**: DRAFT → PENDING_APPROVAL → APPROVED / REJECTED
- Automatic ITEM_MASTER unit cost update on approval
- Cost change percentage indicators
- **Supplier Scorecard**: OTD score, quality score, delivery history
- Supplier tier auto-classification (PREFERRED / CONDITIONAL / PROBATION)
- At-risk supplier identification and alerting

### 🔄 MS4 — Forecasting & Rebates (In Progress)
- Demand forecasting using Prophet model
- PcmForecast / PcmForecastValue entities
- Rebate program management (PcmRebateProgram / PcmRebateRule)

### 🔄 AI Engine — Anomaly Detection (In Progress)
- DistilBERT-powered anomaly scoring
- Confidence score per anomaly
- Affected item linkage
- Severity classification (CRITICAL / WARNING / INFO)

---

## ✨ Key Features

### Backend
- 815 Java source files across `com.test.pcm` and `com.scplatform` packages
- 84 JPA repository interfaces auto-wired by Spring Data
- JWT tokens with 24-hour expiry, BCrypt password hashing
- `@EnableScheduling` for nightly supplier tier recalculation
- Full Swagger UI at `/supchain/swagger-ui/index.html`
- H2 console at `/supchain/h2-console` for database inspection

### Web App (React 18)
- 8 fully implemented pages with protected routes
- Axios interceptor auto-attaches JWT Bearer token
- Real-time data with pull-to-refresh patterns
- Cost record edit modal for DRAFT records
- User management admin panel (create / edit / disable users)
- AI anomaly detection page with severity filters and detail modals

### Mobile App (React Native + Expo)
- 7 screens mirroring the web app
- Tab navigation (Dashboard, Alerts, BOM, Costs, Suppliers)
- Cost record approve / reject with confirmation modal
- Supplier detail screen with scorecard KPIs and delivery history
- Pull-to-refresh on all data screens

### Testing
- 44 Playwright E2E test cases across 7 spec files
- API integration tests covering all endpoints
- Custom HTML test dashboard reporter
- Fixtures for auth token management

---

## 📊 Project Stats

| Metric | Count |
|---|---|
| Java Source Files | 815 |
| JPA Repository Interfaces | 84 |
| Database Tables | 80+ |
| REST API Endpoints | 30+ |
| Web Pages | 8 |
| Mobile Screens | 7 |
| E2E Test Cases | 44 |
| Tech Layers | 4 (Java / React / React Native / Python AI) |

---

## 🚀 Quick Start

### Prerequisites

| Tool | Version | Download |
|---|---|---|
| Java JDK | 21+ | [adoptium.net](https://adoptium.net) |
| Maven | 3.9+ | [maven.apache.org](https://maven.apache.org) |
| Node.js | 18+ | [nodejs.org](https://nodejs.org) |
| Expo Go | Latest | App Store / Play Store |

### Start All Services

```powershell
# Option A: Use the SCIP Agent (recommended)
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process
.\start-all.ps1

# Option B: Manual — 4 terminals

# Terminal 1 — Backend
cd supply-chain-intelligence-platform
java -jar target\pcm-0.0.1-SNAPSHOT.war
# → http://localhost:8089/supchain

# Terminal 2 — Web App
cd scweb
npm start
# → http://localhost:3000

# Terminal 3 — Mobile
cd SupplyChainApp
npx expo start

# Terminal 4 — AI Service
cd supply-chain-intelligence-platform\ai-service
.\venv\Scripts\activate
uvicorn main:app --reload --port 8001
```

### Default Login
```
Username: kumar
Password: kumar
```

### Key URLs
| Service | URL |
|---|---|
| Web App | http://localhost:3000 |
| Java API | http://localhost:8089/supchain |
| Swagger UI | http://localhost:8089/supchain/swagger-ui/index.html |
| H2 Console | http://localhost:8089/supchain/h2-console |
| Python AI | http://localhost:8001 |

---

## 🗺️ Roadmap

| Sprint | Theme | Status |
|---|---|---|
| Sprint 1 | Fix & Stabilise — fix E2E tests, verify all features | 🔥 Current |
| Sprint 2 | MS3 Complete — edit drafts, mobile approve/reject, ITEM_MASTER wiring | ⏳ Next |
| Sprint 3 | MS4 Forecasting — demand charts, variance analysis, web + mobile | 📋 Planned |
| Sprint 4 | AI Engine Complete — anomaly cards, confidence scores, mobile screen | 📋 Planned |
| Sprint 5 | User Management & Security — full user CRUD, password management | 📋 Planned |
| Sprint 6 | Production Readiness — Oracle DB, Docker, GitHub Actions CI/CD | 📋 Planned |

---

## 📄 Documents

| Document | Description |
|---|---|
| [Business Requirements](./docs/BUSINESS_REQUIREMENTS.md) | Full BRD with stakeholders, business rules, and acceptance criteria |
| [Use Cases](./docs/USE_CASES.md) | 20 detailed use cases covering all platform workflows |
| [API Reference](./docs/API_REFERENCE.md) | All REST endpoints with request/response examples |
| [Developer Guide](./docs/DEVELOPER_GUIDE.html) | Complete developer guide with architecture, setup, and code examples |

---

## 📜 License

MIT License © 2026 Kumara Swamy — [github.com/bkumars22](https://github.com/bkumars22)
