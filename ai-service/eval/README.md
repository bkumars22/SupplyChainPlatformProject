# LLM Evaluation Harness — Module 1

Evaluates AI-generated supply chain explanations across four quality dimensions.
Works out of the box with **no API key** (heuristic mode) and upgrades automatically
to LLM-judge mode when `ANTHROPIC_API_KEY` is set.

---

## Metrics

| Metric | Range | Goal | Description |
|---|---|---|---|
| `answer_relevancy` | 0–1 | Higher | Does the response address the prompt and contain expected domain keywords? |
| `hallucination_score` | 0–1 | **Lower** | Does the response contain unsupported or fabricated facts? |
| `consistency_score` | 0–1 | Higher | In LLM-judge mode: do three independent runs agree? In heuristic mode: does the response have proper structure? |
| `response_latency_ms` | ms | Lower | Wall-clock milliseconds to evaluate one case |
| `overall_score` | 0–1 | > 0.70 | `(relevancy + (1 − hallucination) + consistency) / 3` |

### Scoring modes

**Heuristic mode** (no API key — default)
- `answer_relevancy` — keyword hit-rate from `expected_keywords` + length signal
- `hallucination_score` — penalises novel numbers in the response not present in the prompt
- `consistency_score` — structural signals: capitalisation, punctuation, length

**LLM-judge mode** (set `ANTHROPIC_API_KEY`)
- Claude Haiku acts as an independent judge on each dimension
- `consistency_score` — sends the same prompt 3× and measures pairwise token overlap (Jaccard similarity)
- Blended 55/35/10 weighting between LLM score, keyword score, and length

---

## How to run

```bash
# From ai-service/ directory
cd ai-service

# One-shot evaluation of all 20 test cases
python eval/eval_runner.py

# With LLM-judge mode (optional)
set ANTHROPIC_API_KEY=sk-ant-...     # Windows PowerShell
python eval/eval_runner.py
```

### CI/CD integration

```yaml
- name: Run LLM evaluation
  run: python eval/eval_runner.py
  # Exits 0 if avg overall_score > 0.70, exits 1 if below threshold
```

---

## FastAPI endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/eval/health` | Module status, mode, test case count |
| GET | `/eval/run` | Execute all 20 cases, return full results |
| GET | `/eval/results` | Latest saved results from `eval/results/eval_results.json` |

```bash
# Check eval module health
curl http://localhost:8001/eval/health

# Run full evaluation via API
curl http://localhost:8001/eval/run

# Fetch latest results
curl http://localhost:8001/eval/results
```

---

## Test case categories

| Category | Cases | Coverage |
|---|---|---|
| `supplier_risk` | SR-001 – SR-005 | Late deliveries, new supplier, sole-source, geopolitical, anomaly cluster |
| `anomaly_detection` | AD-001 – AD-005 | Lead time spike, cost jump, volume spike, correlated anomaly, seasonal FP |
| `demand_forecast` | DF-001 – DF-005 | Q4 surge, new product, declining demand, supply disruption, promo event |
| `cost_record` | CR-001 – CR-005 | Raw material increase, freight cost, FX impact, workflow gap, YoY summary |

---

## Sample output

```
========================================================================
  SCIP LLM Evaluation Harness — Module 1
========================================================================
  Date     : 2026-06-16 12:00:00 UTC
  Mode     : heuristic
  Cases    : 20
  Threshold: 0.7
========================================================================

ID        Category              Relevancy    Hallucin.    Consistency    Overall   Latency ms
--------------------------------------------------------------------------------------------
SR-001    supplier_risk             0.893        0.960          0.900      0.918       0.8
SR-002    supplier_risk             0.875        0.960          0.900      0.912       0.5
...
CR-005    cost_record               0.916        0.960          0.900      0.925       0.6
--------------------------------------------------------------------------------------------
AVG                                 0.891        0.955          0.900      0.912       0.6

  Category Breakdown
  ------------------------------------------------
  anomaly_detection       0.908  ████████████████████████████░░
  cost_record             0.921  ███████████████████████████░░░
  demand_forecast         0.909  ████████████████████████████░░
  supplier_risk           0.912  ████████████████████████████░░

========================================================================
  Verdict      : ✓  PASS
  Avg Score    : 0.912  (threshold: 0.7)
  Wall time    : 0.04s
  Results file : eval_20260616_120000_a1b2c3d4.json
========================================================================
```

---

## Results files

All results are saved to `eval/results/`:

| File | Contents |
|---|---|
| `eval_results.json` | Latest run (overwritten each run) |
| `eval_YYYYMMDD_HHMMSS_<id>.json` | Timestamped archive of every run |

Each result file contains:
```json
{
  "run_id": "a1b2c3d4",
  "generated_at": "2026-06-16T12:00:00Z",
  "mode": "heuristic",
  "report": { "avg_overall_score": 0.912, "pass": true, ... },
  "results": [
    {
      "test_case_id": "SR-001",
      "category": "supplier_risk",
      "scores": {
        "answer_relevancy": 0.893,
        "hallucination_score": 0.040,
        "consistency_score": 0.900,
        "overall_score": 0.918,
        "response_latency_ms": 0.8,
        "mode": "heuristic"
      }
    }
  ]
}
```

---

## Upgrading to LLM-judge mode

1. Obtain an Anthropic API key from [console.anthropic.com](https://console.anthropic.com)
2. Add to `ai-service/.env`:
   ```
   ANTHROPIC_API_KEY=sk-ant-...
   ```
3. Install the Anthropic SDK:
   ```bash
   pip install anthropic>=0.40.0
   ```
4. Re-run: `python eval/eval_runner.py` — mode will show `llm-judge`

LLM-judge mode sends three prompts for consistency checking (9 additional API calls
per evaluation run, ~3 seconds total). Haiku pricing is negligible for 20 cases.
