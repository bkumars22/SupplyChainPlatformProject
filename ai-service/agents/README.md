# Module 2 — LangGraph Agent Test Framework

Supply Chain Intelligence Platform · AI Service

---

## What is a LangGraph State Graph?

LangGraph is a framework for building **stateful, multi-step AI agents** as directed graphs.
Each node is a Python function that reads from a shared `AgentState` dict and returns an updated
copy of that state. Edges define the execution order. Unlike a simple function pipeline, LangGraph
can support branching, loops, and conditional routing — and every node's output is automatically
passed forward as the next node's input.

This module models the supplier risk analysis pipeline as a 4-node linear graph:

```
fetch_supplier_data → score_risk → generate_explanation → validate_response → END
```

---

## The 4 Nodes

### Node 1: `fetch_supplier_data`
- **What**: Calls `GET /api/suppliers` on the Spring Boot backend with a JWT token.
- **Why**: Real supplier data (OTD rates, quality scores) is the ground truth for risk scoring.
  Falls back to 10 synthetic suppliers if the API is unreachable.
- **Output**: `state["suppliers"]` — list of supplier dicts with OTD, quality, responsiveness.

### Node 2: `score_risk`
- **What**: Runs `sklearn.IsolationForest` on the supplier feature matrix (OTD, quality, responsiveness).
- **Why**: IsolationForest detects statistical outliers without labeled training data — ideal for
  supply chain anomaly detection where "normal" shifts over time.
- **Output**: `state["risk_scores"]` — each supplier tagged HIGH / MEDIUM / LOW + anomaly score.

### Node 3: `generate_explanation`
- **What**: For every HIGH or MEDIUM risk supplier, generates a plain-English explanation.
  Uses Claude Haiku via Anthropic API if `ANTHROPIC_API_KEY` is set; otherwise uses a
  heuristic template that still passes the validator.
- **Why**: LLM-generated explanations help procurement teams act quickly without reading raw scores.
- **Output**: `state["explanations"]` — list of `{supplierName, riskLevel, otdRate, explanation}`.

### Node 4: `validate_response`
- **What**: Quality-gates each explanation against 4 checks:
  1. **contains_supplier_name** — explanation mentions the supplier name
  2. **contains_otd_score** — explanation contains a numeric OTD value
  3. **length_in_range** — explanation is between 50–500 characters
  4. **no_hallucinated_names** — no unknown company names introduced by the LLM
- **Why**: Prevents hallucinated or incomplete explanations from reaching end users.
- **Output**: `state["validations"]` — pass/fail per explanation with per-check breakdown.

---

## How to Run

```bash
# From ai-service/ directory
cd ai-service

# Install dependencies
pip install -r requirements.txt

# Run the agent test suite (default: hits localhost:8089)
python agents/agent_test_runner.py

# Run against a different backend
python agents/agent_test_runner.py --backend http://localhost:8089/supchain

# Via FastAPI endpoints (while uvicorn is running on port 8001)
curl http://localhost:8001/agents/health
curl http://localhost:8001/agents/run
curl http://localhost:8001/agents/results
```

---

## Sample Output

```
============================================================
  Supply Chain Intelligence Platform
  Module 2 — LangGraph Agent Test Suite
============================================================
  Backend : http://localhost:8089/supchain
  Scenarios: 10 (5 HIGH / 3 MEDIUM / 2 LOW)
============================================================

[1/3] Running agent (full pass for scenario analysis)...
      Suppliers fetched  : 10
      Risk scores        : 10
      Explanations       : 8
      Validations        : 8

[2/3] Measuring reliability across 3 runs...
      Run 1: ✓  312ms  validations 8/8
      Run 2: ✓  287ms  validations 8/8
      Run 3: ✓  301ms  validations 8/8

[3/3] Measuring explanation consistency (3 runs)...
      Consistency score  : 0.8450

============================================================
  RESULTS SUMMARY
============================================================
  Agent Reliability Score    : 100%
  Response Consistency Score : 0.8450
  Hallucination Rate         : 0%
  Validation Pass Rate       : 100%
  Avg Latency                : 300 ms
  Scenario Accuracy          : 80% (8/10)

  Node Success Rates:
    fetch_supplier_data            100%
    score_risk                     100%
    generate_explanation           100%
    validate_response              100%

  Scenario Coverage:
    ✓ S-01  Delta Electronics India          expected=HIGH  actual=HIGH
    ✓ S-02  Foxconn Technology Group         expected=HIGH  actual=HIGH
    ✓ S-03  TE Connectivity                  expected=HIGH  actual=HIGH
    ✓ S-04  STMicroelectronics               expected=HIGH  actual=HIGH
    ✓ S-05  Shenzhen Electronics Co.         expected=HIGH  actual=HIGH
    ✓ S-06  Vishay Intertechnology           expected=MEDIUM actual=MEDIUM
    ✓ S-07  Samsung SDI Korea                expected=MEDIUM actual=MEDIUM
    ✗ S-08  Taiwan Semiconductors Ltd        expected=MEDIUM actual=HIGH
    ✓ S-09  Murata Manufacturing             expected=LOW   actual=LOW
    ✗ S-10  Infineon Technologies            expected=LOW   actual=MEDIUM

  Results saved → agents/results/agent_test_results.json
============================================================
```

---

## How This Differs from Module 1 (Eval Harness)

| Dimension | Module 1 — LLM Eval Harness | Module 2 — LangGraph Agent |
|-----------|----------------------------|---------------------------|
| **Input** | Static prompt/response pairs from `test_cases.py` | Live supplier data fetched from Spring Boot API |
| **Execution** | Single evaluator function per test case | 4-node stateful graph with chained state |
| **ML** | Heuristic keyword scoring + optional LLM judge | IsolationForest anomaly detection on live features |
| **Output** | Relevancy / hallucination / consistency scores | Risk levels + validated plain-English explanations |
| **Agent framework** | None (plain Python) | LangGraph StateGraph with typed state dict |
| **Fallback** | Heuristic mode (no API key needed) | Sequential execution + synthetic data fallback |
| **Results file** | `eval/results/eval_results.json` | `agents/results/agent_test_results.json` |
| **FastAPI routes** | `/eval/run`, `/eval/results`, `/eval/health` | `/agents/run`, `/agents/results`, `/agents/health` |

---

## Key Metrics Explained

- **agent_reliability_score** — % of runs where all 4 nodes completed without fatal error
- **response_consistency_score** — Jaccard token overlap across 3 runs on identical input (1.0 = identical)
- **hallucination_rate** — % of explanations containing unknown company names not in input data
- **validation_pass_rate** — % of explanations passing all 4 quality checks
- **scenario_accuracy** — % of 10 test scenarios correctly risk-classified by the agent
