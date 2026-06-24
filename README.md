# SCIP — Supply Chain Intelligence Platform

> AI-powered full-stack supply chain platform. Now with RAG: ask natural language questions about your suppliers and get answers from real data.

[![Live Demo](https://img.shields.io/badge/Live%20Demo-GitHub%20Pages-blue?style=for-the-badge)](https://bkumars22.github.io/SupplyChainPlatformProject)
[![Tests](https://img.shields.io/badge/Tests-76%20Passing-green?style=for-the-badge)](https://bkumars22.github.io/SupplyChainPlatformProject)
[![Security](https://img.shields.io/badge/Security-OWASP%20Top%2010-orange?style=for-the-badge)](https://github.com/bkumars22/SupplyChainPlatformProject)
[![Built With](https://img.shields.io/badge/Built%20With-Claude%20AI-purple?style=for-the-badge)](https://anthropic.com)
[![RAG](https://img.shields.io/badge/RAG-pgvector%20%2B%20NL%20Queries-8b5cf6?style=for-the-badge)](https://github.com/bkumars22/SupplyChainPlatformProject)

---

## Live Demo

**https://bkumars22.github.io/SupplyChainPlatformProject**

| Email | Password | Role |
|-------|----------|------|
| kumar@scip.io | Kumar@2026 | Admin |
| ops@scip.io | Ops@2026 | Operations |

---

## All Live Projects

| Platform | Description | Live URL |
|----------|-------------|---------|
| **SCIP** | Supply Chain Intelligence | **https://bkumars22.github.io/SupplyChainPlatformProject** |
| **QAIP** | QA Intelligent Platform | https://bkumars22.github.io/QA-Intelligent-Platform |
| **ARIA** | Free AI Tutor (35 languages) | https://bkumars22.github.io/ARIA |
| **ZENTRAVIX** | Org Intelligence Platform | https://bkumars22.github.io/ZENTRAVIX |

---

## What SCIP Does

SCIP is an **enterprise supply chain management system** that combines:

- Real-time supplier risk scoring using **IsolationForest anomaly detection**
- Full procurement workflow (Purchase Orders, Invoices, Inventory)
- **Natural language supplier queries** powered by RAG + Claude/Groq
- AI-generated risk explanations with historical context

### What You Can Ask SCIP AI

```
"Which suppliers are highest risk?"
→ TechParts Ltd (risk: HIGH, anomaly score: 0.89) and GlobalParts Inc
  (risk: HIGH) are currently flagged. TechParts has delivery delays
  averaging 45 days this quarter.

"Any suppliers with quality issues?"
→ 3 suppliers have quality scores below threshold: Shenzhen Electronics
  (12/100), TE Connectivity (9/100), STMicroelectronics (13/100).

"What supply chain disruptions happened recently?"
→ 2 disruption events stored: semiconductor shortage (HIGH severity,
  affecting Murata Manufacturing) and port delay (MEDIUM, resolved).
```

---

## What's New — RAG Supplier Intelligence

SCIP now learns from every analysis run:

```
Supplier Data (live from DB)
          ↓
[score_risk node] — IsolationForest anomaly detection
          ↓
[retrieve_context node] — pgvector: historical analyses for this supplier
          ↓
[generate_explanation node] — Claude uses current + historical context
          ↓
Result auto-ingested into RAG for future queries
          ↓
CEO asks "which supplier is high risk?" → semantic search → real answer
```

### RAG API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `POST /scip/rag/ask` | POST | NL question about suppliers |
| `GET /scip/rag/high-risk` | GET | List all flagged suppliers |
| `POST /scip/rag/ingest-disruption` | POST | Store a disruption event |

---

## Architecture

```
┌────────────────────────────────────────────────────┐
│                  SCIP System                       │
│                                                    │
│  Spring MVC + JSP Frontend                         │
│         (Railway)                                  │
│              │                                     │
│    Spring Boot REST API (Railway)                  │
│              │                                     │
│    PostgreSQL (Railway) + pgvector                 │
│              │                                     │
│    Python AI Engine (FastAPI + LangGraph)          │
│              │                                     │
│    ┌─────────┴────────────┐                        │
│ Anthropic Claude      pgvector RAG                 │
│ (risk explanations)   (384-dim supplier vectors)   │
└────────────────────────────────────────────────────┘
```

### LangGraph Agent Pipeline (5 nodes)

```
fetch_supplier_data   ← Spring Boot API with JWT auth
          │
score_risk            ← IsolationForest on quality/responsiveness/composite
          │
retrieve_context      ← NEW: pgvector pulls historical analyses per supplier
          │
generate_explanation  ← Claude with RAG history context injected
          │
validate_response     ← hallucination check, name/score verification
```

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | Spring MVC, JSP, Bootstrap |
| Backend | Spring Boot 3.3, Java 17, JWT auth |
| AI Engine | Python 3.11, FastAPI, LangGraph |
| Risk Scoring | scikit-learn IsolationForest + StandardScaler |
| Forecasting | Prophet (demand forecasting) |
| LLM | Anthropic Claude Haiku + Groq Llama-3.3-70b |
| RAG | pgvector + sentence-transformers all-MiniLM-L6-v2 |
| Database | PostgreSQL 15 (Railway) + Flyway |
| Testing | Playwright TypeScript (76 tests), JUnit 5 |
| CI/CD | GitHub Actions → Railway |

---

## Environment Variables

```env
DATABASE_URL=postgresql://...       # Railway PostgreSQL
ANTHROPIC_API_KEY=sk-ant-...        # Claude for risk explanations
GROQ_API_KEY=gsk_...               # Groq for NL query synthesis
SCIP_USERNAME=kumar                 # Spring Boot API login
SCIP_PASSWORD=Kumar@2026
EMBED_MODEL=all-MiniLM-L6-v2       # sentence-transformers model
```

---

## Local Development

```bash
# Backend (Spring Boot)
./mvnw spring-boot:run

# AI Service
cd ai-service
pip install -r requirements.txt
uvicorn main:app --reload --port 8000
```

---

## MCP Servers

```json
{
  "mcpServers": {
    "scip": {
      "command": "npx",
      "args": ["@scip/mcp-server"],
      "env": { "SCIP_API_URL": "https://scip-production.up.railway.app" }
    }
  }
}
```
