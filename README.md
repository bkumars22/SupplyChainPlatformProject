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

### Demo Access
Use the live dashboard directly — no signup required.
For full admin access: swamy.kumar02@gmail.com---

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

                  SCIP System                       
                                                    
  Spring MVC + JSP Frontend                         
         (Railway)                                  
                                                   
    Spring Boot REST API (Railway)                  
                                                   
    PostgreSQL (Railway) + pgvector                 
                                                   
    Python AI Engine (FastAPI + LangGraph)          
                                                   
                            
 Anthropic Claude      pgvector RAG                 
 (risk explanations)   (384-dim supplier vectors)   

```

### LangGraph Agent Pipeline (6 nodes, conditional)

```
fetch_supplier_data   ← Spring Boot API with JWT auth
          
score_risk            ← IsolationForest on quality/responsiveness/composite
          
     [route_by_risk] — all suppliers in this run LOW risk?
          │                              │
         no                             yes
          ↓                              ↓
retrieve_context             skip_low_risk (skips the 3 nodes below entirely)
          ↓
generate_explanation  ← Claude with RAG history context injected
          ↓
validate_response     ← hallucination check, name/score verification
```

See "Conditional Risk-Based Routing" below — mirrored in the sequential (no-LangGraph) fallback path too.

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

---

## Phase 6 — Small Business UX (Simple Dashboard + CSV Upload)

Built for non-technical small-business users who have no ERP or API integration and need a simple way to onboard supplier data and monitor risk.

**Simple Dashboard** (`/simple-dashboard`)

| Feature | Detail |
|---------|--------|
| 3-Tier Risk Labels | Converts `compositeScore` to Healthy / Needs a check-in / Needs attention with green/yellow/red dot |
| Header Summary | "X suppliers need your attention" count derived from the API's `atRisk` boolean |
| Stat Cards | 3 summary cards: Total Suppliers, Needing Attention, Healthy |
| At-Risk Cards | Clickable cards with plain-English summary sentence, tier dot, and supplier name |
| Healthy Collapse | Healthy suppliers in a collapsible section (expand/collapse toggle) |
| Route | `/simple-dashboard` — visible in sidebar as "Simple Dashboard" |

**Supplier Detail (Simple)** (`/simple-dashboard/:supplierId`)

| Feature | Detail |
|---------|--------|
| Parallel Fetch | Fetches scorecard + delivery history in parallel |
| Plain-English Narrative | "What changed" section: sorts 5 most recent deliveries, counts late ones, averages delay days |
| 4 InfoCards | Orders tracked, On-time %, Late shipments, Supplier tier |
| Action Checklist | 3 client-side checkboxes: Contact supplier / Line up backup / Mark as resolved |
| Link to Technical Page | "View detailed breakdown" → existing `/suppliers/:supplierId` |
| Back Button | Returns to `/simple-dashboard` |

**CSV Upload** (`/csv-upload`)

| Feature | Detail |
|---------|--------|
| Template Download | `GET /api/suppliers/import/template` — CSV with comment notes + example row |
| Drag-and-Drop Upload | File drop zone or click-to-browse, .csv only |
| Server-Side Parsing | RFC 4180 quoted-field parser — no third-party CSV library |
| Required Columns | `supplier_id`, `supplier_name`, `promised_date`, `actual_date` |
| Forgiving Defaults | `po_number` → CSV-{row}, `item_code` → IMPORTED, `qty_ordered` → 1, `quality_score` → null |
| Date Formats | Accepts `yyyy-MM-dd`, `MM/dd/yyyy`, `dd/MM/yyyy`, `M/d/yyyy` |
| Row-Level Errors | Skipped rows shown in a table with row number, supplier ID, and plain-English reason |
| Auto-Redirect | Redirects to `/simple-dashboard` automatically after a clean import |
| Supplier Upsert | Creates `SupplierProfile` if new; skips update if existing (preserves scoring data) |
| Delivery Records | Creates `SupplierDelivery` rows with auto-calculated `delayDays` and ON_TIME/LATE status |
| Unit Tests | 30 JUnit 5 tests: empty file, null input, missing required fields, malformed dates, forgiving defaults, RFC 4180 quoted fields, UTF-8 BOM handling, a real messy Excel-style export fixture |

---

## Multi-Tier Supplier Risk Intelligence

Supplier risk scoring only ever looked at a supplier's own metrics, which
misses a real gap: a supplier can look healthy in isolation while depending
entirely on a struggling upstream source for a critical component. These
four additions — built and verified against the running app, not just
unit-tested — close that gap.

### Dependency Graph + Cascading Risk

New `SUPPLIER_DEPENDENCY` table (who sources what from whom, criticality
0–1, sole-source flag) and `SupplierRiskCascadeService`: a hop-decayed graph
traversal (BFS, cycle-safe — a node is only ever expanded once) that
computes an **effective risk** alongside the existing direct risk, never
replacing it, plus explicit single-point-of-failure flagging.

| Endpoint | Description |
|---|---|
| `POST /api/suppliers/dependencies` | Record a dependency edge |
| `GET /api/suppliers/dependencies` | List all dependency edges |
| `GET /api/suppliers/{id}/cascaded-risk` | Direct risk, effective risk, active sole-source flags |
| `GET /api/suppliers/{id}/structural-risk` | Sole-source mapping independent of current risk levels |

**Verified live:** Taiwan Semiconductors' direct risk (26%) vs. effective
risk (46.8%, from its sole-sourced dependency on an at-risk supplier) —
matches hand-calculated decay math exactly. 7 unit tests, including an
explicit circular-dependency case confirming the traversal terminates.

### ESG Risk, Scenario Simulation, Action Suggestions

- `SupplierEsgService` — ESG scoring that reports `esgDataAvailable: false`
  honestly when there's no profile on file, rather than fabricating a
  neutral score. A profile with some fields missing computes a real score
  from what exists and lists exactly which fields were defaulted.
- `ScenarioSimulationService` — read-only "what if Supplier X's risk rose to
  0.9" projections, reusing the same cascade algorithm unmodified. The
  response is unmistakably flagged `isSimulation: true` (no setter).
  Verified both in tests (Mockito: `save`/`delete` never called) and live —
  a real factory-shutdown-style simulation projected a 0.549 → 0.673 risk
  jump for a downstream supplier, then confirmed the real stored data was
  byte-identical before and after.
- `ActionSuggestionService` — human-approved suggestions
  (`requiresHumanApproval: true`, no setter) that cite the specific
  supplier/component/figure behind each one; nothing here acts
  autonomously.

| Endpoint | Description |
|---|---|
| `POST /api/suppliers/{id}/esg-profile` | Set/update ESG data |
| `GET /api/suppliers/{id}/esg-risk` | ESG score, honest about missing data |
| `POST /api/scenarios/simulate` | Read-only what-if projection |
| `GET /api/suppliers/{id}/action-suggestions` | Cited, human-approved suggestions |

### Demo Scenario

Two seeded suppliers (`SUPP-011` Meridian Assemblies, healthy on paper;
`SUPP-012` Coastal Components, its sole-sourced and actually struggling
upstream dependency) walk through the exact gap this feature closes. Full
walkthrough script with live-verified numbers: [`docs/DEMO_SCENARIO.md`](docs/DEMO_SCENARIO.md).

---

## Voice Commands

Client-side regex/keyword intent matcher over the Web Speech API (Chrome/
Edge only) — navigate, create records, dismiss/submit/approve, and query
data by voice, no server round-trip. Recently hardened after finding two
concrete bug classes, both verified live:

- Several `\bword\b` patterns never matched their own plural suggestion
  chips — `/\bsupplier\b/` doesn't match "suppliers" (no word boundary
  before the trailing "s"), so "Show suppliers", "Show reports", and "Show
  users" were silently unrecognized. Every navigation pattern now accepts
  both singular and plural.
- Chrome's speech recognition reliably mishears the acronym **"BOM"** as
  the word **"bomb"** — confirmed live (a stray "bomb laptop assembly"
  transcript was correctly rejected under the old pattern). Both the
  navigation and create-BOM patterns now deliberately accept bomb/bombs.

Also added voice routes to two pages that had none (Simple Dashboard, CSV
Upload).

---

## AI Engine — Recent ML Additions (Verified Locally)

Four additions to `ai-service`, each locally verified end-to-end against a
real `ankane/pgvector` Postgres container (not just unit-tested against
placeholders). Numbers below are real output from that verification run,
not estimates.

### HNSW Vector Index (upgraded from ivfflat)

`rag/vector_store.py`'s index is now `USING hnsw (embedding vector_cosine_ops)`
with `m = 16, ef_construction = 64` (query-time `hnsw.ef_search` tunable per
call, default 40 via `HNSW_EF_SEARCH` env var).

Verified with `EXPLAIN ANALYZE` at 8,150 rows:

| Plan | Execution time |
|------|-----------------|
| `Index Scan using rag_documents_hnsw_idx`, `Order By: embedding <=> ...` | **1.25 ms** |
| Same query with the index disabled (forced seq/bitmap scan) | 27.98 ms |

At small table sizes (~150 rows) Postgres' planner correctly prefers an
exact sort over the ANN index — HNSW's advantage only shows up at real
scale, which is expected and was confirmed, not assumed.

### Conditional Risk-Based Routing

`agents/supplier_agent.py`'s LangGraph pipeline gained a real conditional
edge (`route_by_risk`) after `score_risk`: when every supplier in a run is
LOW risk, execution routes straight to a new `skip_low_risk` node and skips
`retrieve_context` + `generate_explanation` + `validate_response` entirely,
instead of running them and discarding the output in-node. Mirrored in the
sequential (no-LangGraph) fallback path too. Verified live — a mixed-risk
synthetic batch correctly reviewed 9/10 suppliers and skipped the LOW one;
an all-LOW batch correctly routed to `skip`.

### Conformal Prediction — Confidence Bounds on Risk Scores

New module `calibration/conformal_predictor.py`. `score_risk` now emits a
continuous `riskScoreContinuous` (0–1) per supplier alongside the existing
categorical `riskLevel`, and attaches `riskLower` / `riskUpper` /
`riskConfidence` via split conformal calibration.

**Honest limitation:** SCIP doesn't yet persist historical ground-truth
outcomes (e.g. "was this supplier actually late next quarter?"), so there
is no true label to calibrate against yet. This calibrates the continuous
score against the same quality-score threshold that already produces the
categorical label — it flags when the ML score and the rule disagree
sharply, but it is not the same guarantee as calibrating against real
future deliveries. Wiring real outcome tracking is the next step.

### Statistical Significance — Proving RAG Improves Explanation Quality

New module `significance/statistical_proof.py` (p-value + Cohen's d) plus
`significance/prove_rag_improvement.py`, which compares
`validate_response` pass rate for explanations generated with real
retrieved RAG history vs. with no context. Run it directly:

```bash
cd ai-service
python significance/prove_rag_improvement.py
```

**Honest limitation:** without ingested historical supplier analyses in
the RAG store, "with RAG" has nothing to retrieve and correctly reports
"no effect to test yet" rather than a fabricated result — verified this
is exactly what it prints against an empty store.

---

## BCT — Behavioral Contract Testing Suite (AI Red-Teaming)

New module `ai-service/bct/`. Tests whether the AI systems in this repo
hold up under adversarial pressure, across 5 categories: does the AI
follow its own stated rules (**behavioral contract** — e.g. the
Socratic-tutor contract in `prompt_library.ARIA_SOCRATIC`, and SCIP's own
RAG-assistant system prompt), can untrusted input override its
instructions (**prompt injection** — including a SCIP-specific case via
the `/scip/rag/ingest-disruption` → `/scip/rag/ask` pipeline), does it
leak system prompts/credentials/other users' data (**data leakage**),
do outputs unfairly diverge on identity signals alone (**bias &
fairness**), and do rules erode across a multi-turn conversation
(**multi-turn escalation**).

```bash
cd ai-service
python bct/runner.py                 # reference target — offline, no API key
python bct/runner.py --target live   # real SCIP RAG assistant, needs GROQ_API_KEY
```

| Method | Path | Description |
|---|---|---|
| GET | `/bct/health` | Judge mode, per-category test-case counts |
| GET | `/bct/run?target=reference\|live` | Run the full suite, return the report |
| GET | `/bct/results` | Latest saved run |

**Honest status:** every judge is real rule-based pattern matching
(`bct/judges.py`) — verified it correctly fails on deliberately
injected/leaked/caved-under-pressure/biased responses, not just
rubber-stamping everything — with an LLM-judge upgrade path via the
existing `prompt_library.BCT_COMPLIANCE_JUDGE` prompt when
`ANTHROPIC_API_KEY` is set. The default `reference` target is a small
rule-based guard used to prove the judges discriminate correctly, not a
production LLM; point `--target live` at SCIP's real Groq-backed
assistant for a genuine adversarial result. Toxicity resistance (a 6th
category) is not yet implemented. Full details: [`ai-service/bct/README.md`](ai-service/bct/README.md).

