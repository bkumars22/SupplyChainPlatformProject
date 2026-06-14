# 📋 Business Requirements Document (BRD)
## Supply Chain Intelligence Platform (SCIP)

**Version:** 1.0  
**Author:** Kumara Swamy  
**Date:** June 2026  
**Status:** Active Development

---

## 1. Executive Summary

The **Supply Chain Intelligence Platform (SCIP)** is an enterprise-grade digital supply chain management system designed to give organisations real-time visibility into component costs, Bill of Materials complexity, supplier performance, and supply chain risk — all from a unified web and mobile interface.

SCIP replaces manual spreadsheet-based processes, disconnected ERP data exports, and reactive supplier management with a proactive, data-driven, AI-enhanced platform.

---

## 2. Business Objectives

| ID | Objective | Success Metric |
|---|---|---|
| BO-01 | Reduce cost overrun from untracked component price changes | 100% of cost changes go through approval workflow before ITEM_MASTER update |
| BO-02 | Provide real-time supplier performance visibility | OTD score and delivery history available within 24 hours of delivery record entry |
| BO-03 | Eliminate manual BOM maintenance | BOM changes tracked with version history and component cost roll-up |
| BO-04 | Reduce alert fatigue | AI anomaly engine surfaces only high-confidence alerts (>65% confidence score) |
| BO-05 | Enable mobile supply chain management | All core workflows available on iOS and Android via React Native app |
| BO-06 | Enforce role-based data access | Three-tier role model (ADMIN / BUS_ADMIN / GUEST) enforced at API and UI level |

---

## 3. Stakeholders

| Role | Responsibility | Access Level |
|---|---|---|
| **Supply Chain Manager** | Reviews and approves cost records, monitors supplier scorecards | BUS_ADMIN |
| **Procurement Analyst** | Creates cost records, enters supplier delivery data | BUS_ADMIN |
| **Operations Team** | Views BOMs, monitors alerts, read-only data access | GUEST |
| **System Administrator** | Manages users, roles, system configuration | ADMIN |
| **Executive / C-Suite** | Dashboard KPIs, high-level metrics | GUEST |
| **Supplier Manager** | Manages supplier profiles, delivery records | BUS_ADMIN |

---

## 4. Scope

### In Scope
- JWT-secured REST API backend (Java 21 / Spring Boot 4)
- React 18 web application
- React Native mobile application (iOS + Android)
- Python AI microservice for anomaly detection and demand forecasting
- H2 in-memory database (development), Oracle (production)
- Playwright end-to-end test suite
- Cost record approval workflow (MS3)
- Supplier scorecard and tier classification (MS3)
- BOM viewer with multi-level component drill-down (MS2)
- Alert engine with intelligent prioritisation (MS1)
- AI anomaly detection (partial — MS4)
- Demand forecasting module (planned — MS4)

### Out of Scope
- Direct ERP integration (SAP, Oracle E-Business Suite) — future phase
- EDI supplier data exchange — future phase
- Financial accounting / GL integration — future phase
- Multi-currency support — future phase

---

## 5. Functional Requirements

### FR-01: Authentication & Authorisation
| ID | Requirement | Priority |
|---|---|---|
| FR-01-01 | System shall authenticate users via username/password | Must Have |
| FR-01-02 | System shall issue JWT tokens with 24-hour expiry | Must Have |
| FR-01-03 | System shall validate JWT on every protected API call | Must Have |
| FR-01-04 | System shall support BCrypt password hashing | Must Have |
| FR-01-05 | System shall enforce role-based access (ADMIN / BUS_ADMIN / GUEST) | Must Have |
| FR-01-06 | System shall allow any password if no hash is stored (first login) | Should Have |
| FR-01-07 | Users shall be able to change their own password | Should Have |
| FR-01-08 | Admins shall be able to set/reset passwords for any user | Should Have |

### FR-02: Dashboard
| ID | Requirement | Priority |
|---|---|---|
| FR-02-01 | Dashboard shall display count of active alerts | Must Have |
| FR-02-02 | Dashboard shall display total BOM count | Must Have |
| FR-02-03 | Dashboard shall display count of pending cost record approvals | Must Have |
| FR-02-04 | Dashboard shall display count of at-risk suppliers | Must Have |
| FR-02-05 | Dashboard shall display recent alerts list | Must Have |
| FR-02-06 | Dashboard shall provide quick-action links to all modules | Should Have |

### FR-03: Alert Management
| ID | Requirement | Priority |
|---|---|---|
| FR-03-01 | System shall display all active (non-dismissed) alerts | Must Have |
| FR-03-02 | Users shall be able to dismiss individual alerts | Must Have |
| FR-03-03 | System shall display alert count in dashboard KPI | Must Have |
| FR-03-04 | System shall send email notification for at-risk supplier alerts | Should Have |
| FR-03-05 | Alerts shall include type, label, summary, and creation date | Must Have |

### FR-04: BOM Management
| ID | Requirement | Priority |
|---|---|---|
| FR-04-01 | System shall display paginated list of all Bills of Materials | Must Have |
| FR-04-02 | Users shall be able to search BOMs by key or name | Must Have |
| FR-04-03 | System shall display BOM detail with all component lines | Must Have |
| FR-04-04 | BOM detail shall show item code, description, quantity for each line | Must Have |
| FR-04-05 | System shall support BOM version tracking | Should Have |
| FR-04-06 | Users shall be able to create new BOMs | Could Have |
| FR-04-07 | Users shall be able to edit existing BOM components | Could Have |

### FR-05: Cost Record Workflow (MS3)
| ID | Requirement | Priority |
|---|---|---|
| FR-05-01 | Users shall be able to create DRAFT cost records for any item | Must Have |
| FR-05-02 | Cost records shall track proposed cost, justification, and creator | Must Have |
| FR-05-03 | DRAFT records shall be editable before submission | Must Have |
| FR-05-04 | Users shall be able to submit DRAFT records for approval | Must Have |
| FR-05-05 | Approvers shall be able to approve PENDING_APPROVAL records | Must Have |
| FR-05-06 | On approval, system shall update ITEM_MASTER unit cost automatically | Must Have |
| FR-05-07 | Approvers shall be able to reject records with a reason | Must Have |
| FR-05-08 | System shall send email on approval and rejection | Should Have |
| FR-05-09 | System shall display cost change percentage vs current cost | Should Have |
| FR-05-10 | Mobile app shall support approve/reject actions | Must Have |

### FR-06: Supplier Scorecard (MS3)
| ID | Requirement | Priority |
|---|---|---|
| FR-06-01 | System shall display supplier list with OTD and quality scores | Must Have |
| FR-06-02 | System shall classify suppliers as PREFERRED / CONDITIONAL / PROBATION | Must Have |
| FR-06-03 | Tier shall auto-update based on OTD score thresholds | Must Have |
| FR-06-04 | OTD ≥ 90% = PREFERRED, 70–89% = CONDITIONAL, <70% = PROBATION | Must Have |
| FR-06-05 | System shall flag at-risk suppliers in dashboard | Must Have |
| FR-06-06 | Users shall be able to view delivery history per supplier | Must Have |
| FR-06-07 | Users shall be able to add new delivery records for suppliers | Should Have |
| FR-06-08 | System shall send at-risk supplier email alerts | Should Have |

### FR-07: AI Anomaly Detection
| ID | Requirement | Priority |
|---|---|---|
| FR-07-01 | System shall detect anomalies in cost and delivery data | Must Have |
| FR-07-02 | Each anomaly shall have a severity (CRITICAL / WARNING / INFO) | Must Have |
| FR-07-03 | Each anomaly shall have a confidence score (0–100%) | Must Have |
| FR-07-04 | Users shall be able to filter anomalies by severity and category | Must Have |
| FR-07-05 | Anomaly detail shall show affected items list | Should Have |
| FR-07-06 | Anomaly detail shall show recommended action | Should Have |
| FR-07-07 | AI page shall auto-refresh every 60 seconds | Should Have |

### FR-08: User Management
| ID | Requirement | Priority |
|---|---|---|
| FR-08-01 | Admins shall be able to list all system users | Must Have |
| FR-08-02 | Admins shall be able to create new users with role assignment | Must Have |
| FR-08-03 | Admins shall be able to edit user name, email, and role | Must Have |
| FR-08-04 | Admins shall be able to disable users (soft delete) | Must Have |
| FR-08-05 | Admins shall be able to set or reset user passwords | Must Have |
| FR-08-06 | System shall never hard-delete user records | Must Have |

---

## 6. Non-Functional Requirements

| ID | Category | Requirement |
|---|---|---|
| NFR-01 | Performance | API responses shall complete within 2 seconds under normal load |
| NFR-02 | Security | All API endpoints (except /login) shall require valid JWT token |
| NFR-03 | Security | Passwords shall be stored as BCrypt hashes (min cost factor 10) |
| NFR-04 | Availability | Application shall target 99.5% uptime in production |
| NFR-05 | Scalability | Architecture shall support horizontal scaling via stateless JWT |
| NFR-06 | Compatibility | Web app shall support Chrome, Firefox, and Edge (latest 2 versions) |
| NFR-07 | Compatibility | Mobile app shall support iOS 14+ and Android 11+ |
| NFR-08 | Maintainability | Code shall follow Java naming conventions and REST design patterns |
| NFR-09 | Testability | All business-critical flows shall have Playwright E2E test coverage |
| NFR-10 | Observability | All API errors shall be logged with request context |

---

## 7. Business Rules

| ID | Rule |
|---|---|
| BR-01 | A cost record can only be approved if its status is PENDING_APPROVAL |
| BR-02 | Only approved cost records trigger an ITEM_MASTER unit cost update |
| BR-03 | A cost record cannot be edited once submitted for approval |
| BR-04 | Supplier tier is automatically recalculated nightly at 01:00 AM |
| BR-05 | A supplier with OTD score below 70% is automatically flagged as at-risk |
| BR-06 | Users cannot be hard-deleted — only disabled |
| BR-07 | JWT tokens expire after 24 hours and require re-login |
| BR-08 | An admin cannot delete their own account |
| BR-09 | Password must be minimum 6 characters |
| BR-10 | A user with no stored password hash can log in with any password (first login) |

---

## 8. Data Requirements

### Core Entities

| Entity | Table | Key Fields |
|---|---|---|
| Item | ITEM_MASTER | itemKey, itemName, unitCost, categoryCode |
| BOM | BOM_HEADER | bomKey, bomName, status, item (FK) |
| BOM Line | BOM_LINE_ITEM | bomLineKey, bom (FK), item (FK), itemQty |
| Alert | ALERT_DETAIL | id, alertType, shortSummary, state, created |
| User | PCM_USER | userId, userName, password, role (FK), isEnabled |
| Role | PCM_ROLE | roleKey, roleId, roleName |
| Cost Record | MS3_COST_RECORD | id, item (FK), proposedCost, status, justification, approvedBy |
| Supplier | SUPPLIER_PROFILE | id, supplierName, otdScore, qualityScore, tier |
| Delivery | SUPPLIER_DELIVERY | id, supplier (FK), deliveryDate, onTime, quantityOrdered |
| Forecast | PCM_FORECAST | id, itemKey, forecastType, startPeriod, endPeriod |

---

## 9. Integration Requirements

| System | Direction | Protocol | Purpose |
|---|---|---|---|
| Python AI Service (port 8001) | Java → Python | HTTP REST | Anomaly detection scoring |
| H2 Database | Java → H2 | JDBC / JPA | Development data storage |
| Oracle Database | Java → Oracle | JDBC / JPA | Production data storage (planned) |
| Email (SMTP) | Java → SMTP | JavaMail | Approval and alert notifications |

---

## 10. Acceptance Criteria

| Feature | Acceptance Criteria |
|---|---|
| Login | User can log in with kumar/kumar and receive JWT token |
| Dashboard | KPI cards show correct counts from live database |
| BOM List | All BOMs display with correct name and status |
| Cost Record | Full lifecycle DRAFT → SUBMIT → APPROVE updates ITEM_MASTER |
| Supplier | OTD score drives tier classification automatically |
| Alerts | Dismissed alerts do not reappear |
| AI Engine | Anomalies load with severity badges and confidence bars |
| User Mgmt | Admin can create, edit, and disable users |
| Mobile | All core screens functional on iOS and Android |
| E2E Tests | All 44 Playwright tests pass with backend running |
