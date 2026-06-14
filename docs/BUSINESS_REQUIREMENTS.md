# 📋 Business Requirements Document (BRD)
## Supply Chain Intelligence Platform (SCIP)

**Version:** 1.0 | **Author:** Kumara Swamy | **Status:** Active Development

---

## 1. Executive Summary

The **Supply Chain Intelligence Platform (SCIP)** is an enterprise-grade supply chain management system providing real-time visibility into component costs, Bill of Materials complexity, supplier performance, and supply chain risk — from a unified web and mobile interface backed by AI anomaly detection.

---

## 2. Business Objectives

| ID | Objective | Success Metric |
|---|---|---|
| BO-01 | Eliminate untracked component price changes | 100% of cost changes go through approval workflow before unit cost update |
| BO-02 | Real-time supplier performance visibility | OTD score updated within 24 hours of each delivery record |
| BO-03 | Structured BOM lifecycle management | BOM changes tracked with version history and cost roll-up |
| BO-04 | Reduce alert fatigue | AI engine surfaces only high-confidence anomalies (>65% score) |
| BO-05 | Enable mobile supply chain management | All core workflows available on iOS and Android |
| BO-06 | Enforce role-based data access | Three-tier role model enforced at API and UI level |

---

## 3. Stakeholders

| Role | Responsibility | Access Level |
|---|---|---|
| Supply Chain Manager | Approves cost records, monitors supplier scorecards | BUS_ADMIN |
| Procurement Analyst | Creates cost records, enters delivery data | BUS_ADMIN |
| Operations Team | Views BOMs, monitors alerts, read-only access | GUEST |
| System Administrator | Manages users, roles, system configuration | ADMIN |
| Executive / C-Suite | Dashboard KPIs, high-level metrics | GUEST |
| Supplier Manager | Manages supplier profiles and delivery records | BUS_ADMIN |

---

## 4. Scope

**In Scope**
- JWT-secured REST API (Java 17 / Spring Boot 3)
- React 18 web application
- React Native mobile app (iOS + Android via Expo)
- Python FastAPI AI microservice (IsolationForest ML + Claude AI)
- Cost record approval workflow (MS3)
- Supplier scorecard and tier auto-classification (MS3)
- BOM viewer with multi-level component drill-down (MS2)
- Alert engine with AI prioritisation (MS1)
- Demand forecasting module (MS4 — planned)

**Out of Scope**
- Direct ERP integration (SAP, Oracle EBS) — future phase
- EDI supplier data exchange — future phase
- Multi-currency support — future phase

---

## 5. Key Functional Requirements

### Authentication & Security
- JWT token-based authentication with configurable expiry
- BCrypt password hashing for all stored credentials
- Role-based access control (ADMIN / BUS_ADMIN / GUEST) enforced at API level
- Users can change their own password; admins can reset any user's password

### Dashboard
- KPI summary cards: active alerts, BOM count, pending approvals, at-risk suppliers
- Recent alerts feed with quick-action navigation to all modules

### Alert Management
- Active alert list with dismiss capability
- Email notifications for critical supply chain events
- Alert count badge in dashboard KPI

### BOM Management
- Paginated BOM list with search by key or name
- BOM detail view with full component line items (item, quantity, lead time)
- BOM version tracking

### Cost Record Workflow (MS3)
- Full lifecycle: DRAFT → PENDING_APPROVAL → APPROVED / REJECTED
- On approval, ITEM_MASTER unit cost updates automatically
- Rejection requires reason; original creator notified via email
- Mobile app supports approve/reject actions

### Supplier Scorecard (MS3)
- OTD score, quality score, delivery history per supplier
- Auto-tier classification: PREFERRED ≥90% / CONDITIONAL 70–89% / PROBATION <70%
- At-risk flag surfaces in dashboard; email alerts triggered
- Manual delivery record entry with automatic OTD recalculation

### AI Anomaly Detection
- IsolationForest ML model scores supplier and cost anomalies
- Severity classification: CRITICAL / WARNING / INFO
- Confidence score (0–100%) per anomaly
- Filters by severity and category; auto-refresh every 60 seconds

### User Management (Admin)
- Create, edit, and disable user accounts
- Assign roles; set or reset passwords
- Soft-delete only — user records are never permanently removed

---

## 6. Non-Functional Requirements

| Category | Requirement |
|---|---|
| Performance | API responses within 2 seconds under normal load |
| Security | All endpoints (except login) require valid JWT |
| Security | Passwords stored as BCrypt hashes |
| Availability | 99.5% uptime target in production |
| Scalability | Horizontal scaling supported via stateless JWT architecture |
| Compatibility | Web: Chrome, Firefox, Edge (latest 2 versions) |
| Compatibility | Mobile: iOS 14+, Android 11+ |
| Testability | All business-critical flows covered by Playwright E2E tests |

---

## 7. Business Rules

| ID | Rule |
|---|---|
| BR-01 | A cost record can only be approved when status is PENDING_APPROVAL |
| BR-02 | Only approved cost records trigger an ITEM_MASTER unit cost update |
| BR-03 | Cost records cannot be edited after submission for approval |
| BR-04 | Supplier tiers are automatically recalculated nightly |
| BR-05 | Suppliers with OTD below 70% are automatically flagged as at-risk |
| BR-06 | Users are never hard-deleted — disable only |
| BR-07 | JWT tokens expire after 24 hours |
| BR-08 | Passwords must be minimum 6 characters |

---

## 8. Core Data Entities

| Entity | Table | Purpose |
|---|---|---|
| Item | ITEM_MASTER | Components, FGs, raw materials with unit cost |
| BOM | BOM_HEADER | Bill of Materials header — name, item, version, status |
| BOM Line | BOM_LINE_ITEM | Component lines — quantity, item reference, lead time |
| Alert | ALERT_DETAIL | System alerts — type, summary, state, creation date |
| User | PCM_USER | System users — login, role, enabled status |
| Cost Record | MS3_COST_RECORD | Cost change workflow — proposed cost, status, approver |
| Supplier | SUPPLIER_PROFILE | Supplier scorecard — OTD score, quality, tier |
| Delivery | SUPPLIER_DELIVERY | Delivery history — date, on-time, quantities |
| Forecast | PCM_FORECAST | Demand forecasts — item, type, period range |
