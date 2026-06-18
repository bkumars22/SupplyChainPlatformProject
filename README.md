# 🏭 SCIP — Supply Chain Intelligence Platform

> **AI-powered full-stack supply chain platform built independently using Claude AI + GitHub Copilot**

[![Live Demo](https://img.shields.io/badge/Live%20Demo-GitHub%20Pages-blue)](https://bkumars22.github.io/SupplyChainPlatformProject)
[![Tests](https://img.shields.io/badge/Tests-51%20Passing-green)](https://bkumars22.github.io/SupplyChainPlatformProject/tests)
[![Security](https://img.shields.io/badge/Security-OWASP%20Top%2010-orange)](https://github.com/bkumars22/SupplyChainPlatformProject)

---

## 🌐 Live Demo

**URL:** https://bkumars22.github.io/SupplyChainPlatformProject

| Field | Value |
|---|---|
| Username | kumar |
| Password | Kumar@2026 |
| Mode | Demo — realistic mock data |

---

## 🎯 What This Platform Demonstrates

Built solo in 4 weeks using Claude AI and GitHub Copilot as force multipliers. Demonstrates full-stack delivery, AI/ML integration, enterprise security, and automated quality assurance — independently.

| Achievement | Detail |
|---|---|
| 🔴 P0 Auth Bypass Found | BCrypt skipped when password null — discovered via black-box testing |
| 🧪 51 Playwright Tests | 9 modules covered including AI eval pipeline |
| 🤖 IsolationForest ML | Unsupervised supplier risk scoring |
| 💬 Claude AI Integration | Natural language alert explanations |
| 🔗 LangGraph Agent | 5-node StateGraph for AI reliability testing |
| 📊 LLM Eval Harness | Prompt consistency & hallucination measurement |
| 🔐 7-Layer Security | OWASP Top 10, JWT, BCrypt, RBAC, rate limiting |

---

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐
│   React 18 Web  │    │ React Native    │
│   9 Pages       │    │ Mobile 10 Scrns │
└────────┬────────┘    └────────┬────────┘
         │                      │
         └──────────┬───────────┘
                    │ JWT Auth
         ┌──────────▼───────────┐
         │  Spring Boot 3       │
         │  80+ REST Endpoints  │
         │  OWASP Security      │
         └──────────┬───────────┘
                    │
         ┌──────────▼───────────┐
         │  Python FastAPI      │
         │  IsolationForest ML  │
         │  Claude AI API       │
         │  LangGraph Agent     │
         └──────────┬───────────┘
                    │
         ┌──────────▼───────────┐
         │  PostgreSQL          │
         │  JPA/Hibernate       │
         └──────────────────────┘
```

---

## 🚀 Run Locally in 2 Minutes

```bash
git clone https://github.com/bkumars22/SupplyChainPlatformProject.git
cd SupplyChainPlatformProject
docker-compose up
```

Open: http://localhost:3000
Login: kumar / Kumar@2026

---

## 📋 Module Overview

| Module | What It Does |
|---|---|
| Dashboard | Live KPIs — OTD, alerts, cost savings, risk distribution |
| Suppliers | Scorecard with AI risk classification and anomaly detection |
| Alerts | Claude AI natural language explanations of supply chain risks |
| Cost Records | DRAFT → APPROVE workflow for cost management |
| BOM | Bill of Materials viewer with component tracking |
| Test Dashboard | Live 51-test results with auto-refresh |
| Eval Dashboard | LLM prompt scores, consistency rate, hallucination metrics |
| AI Engines | LangGraph agent results and reliability scores |
| Users | Role-based user management — Admin, Manager, Viewer |

---

## 🔐 Security Architecture

- **Authentication:** JWT tokens, 8-hour expiry, strong secret validation
- **Passwords:** BCrypt hashing, null-password protection (P0 fix)
- **Authorization:** RBAC — Admin, Manager, Viewer roles
- **Rate Limiting:** 5 login attempts/min, Bucket4j, 15-min lockout
- **Input Protection:** XSS sanitisation, 1MB request size limits
- **Headers:** HSTS, X-Frame-Options, CSP, X-Content-Type-Options
- **Audit:** OWASP Top 10, SAST scanning, SQL injection audit

---

## 🧪 Test Coverage

```
Auth Module        ✅ 6 tests
Item Master        ✅ 5 tests
BOM                ✅ 4 tests
Cost Records       ✅ 5 tests
Suppliers          ✅ 6 tests
Users              ✅ 5 tests
Alerts             ✅ 5 tests
Dashboard          ✅ 5 tests
Eval Pipeline      ✅ 10 tests
─────────────────────────────
Total              ✅ 51/51 passing
```

---

## 🤖 AI/ML Components

**IsolationForest** — Unsupervised anomaly detection for supplier risk scoring. No labelled data required. Scores each supplier -1 (anomalous) to +1 (normal).

**Claude AI** — Natural language alert explanations via Anthropic API. Converts raw risk scores into actionable procurement recommendations.

**LangGraph Agent** — 5-node StateGraph: fetch_supplier → score_risk → explain_risk → validate_output → log_result. Runs 3x consistency checks.

**deepeval Harness** — LLM evaluation measuring prompt consistency, hallucination rate, and answer relevancy across 5 test cases.

---

## 📬 Contact

**Kumara Swamy B** — Staff SDET & AI Product Builder

- 📧 swamy.kumar02@gmail.com
- 💼 linkedin.com/in/kumara-swamy-7731b020
- 🐙 github.com/bkumars22

> *"AI built the code. I built the product. The decisions, the architecture, the security design, and the bugs found — that was all me."*
