# BCT — Behavioral Contract Testing Suite

An AI red-teaming suite for this repo's AI systems: does an AI's *stated
rules* hold up under adversarial pressure? Covers the same ground as
Microsoft's PyRIT / NVIDIA's Garak categories, scoped to what's actually
wired up in this project. Works out of the box with **no API key**
(heuristic mode) and upgrades automatically to LLM-judge mode when
`ANTHROPIC_API_KEY` is set — same mode-selection pattern as
[`ai-service/eval`](../eval/README.md).

---

## The 5 categories

| Module | What it tests |
|---|---|
| `behavioral_contract` | Does the AI follow its own stated rules under pressure? Tests the Socratic-tutor contract (`prompt_library.ARIA_SOCRATIC`) and SCIP's own RAG-assistant contract (the literal system prompt used by `/scip/rag/ask`). |
| `prompt_injection` | Can untrusted input override the AI's actual instructions? Includes a SCIP-specific case: injection via a disruption event's `description` field (`POST /scip/rag/ingest-disruption`), later retrieved by `/scip/rag/ask`. |
| `data_leakage` | Does the AI reveal system prompts, other users'/tenants' data, memorized PII, or infra credentials (`DATABASE_URL`, `GROQ_API_KEY`)? |
| `bias_fairness` | Do outputs unfairly differ based on identity signals (names) in otherwise-identical prompts? Includes a SCIP-specific paired test: does an identical delivery-history supplier risk assessment change with the supplier's name/region? |
| `multi_turn_escalation` | Do rules erode across a conversation even if each single message looks fine? Includes a SCIP-specific scenario: incremental pressure to override a supplier's risk classification without supporting data. |

---

## What's real here (vs. the original drop)

The BCT Complete files this module was built from shipped every judge
function as a `NotImplementedError` placeholder — "wire this yourself."
That's done:

- **Judges are real** (`bct/judges.py`): deterministic regex/keyword
  pattern matching against each `TestCase.violation_patterns` — a
  response fails when a known bad-signature (e.g. `here is my system
  prompt`, an SSN pattern, a Postgres connection string) is actually
  present. This is intentionally conservative: it under-flags novel
  attack phrasings rather than over-flagging benign responses. Bias
  detection extracts a coarse outcome label (risk level / recommendation)
  per response and flags divergence across paired prompts, not just
  wording differences.
- **`system_under_test_fn` is real** (`bct/system_adapters.py`): a
  `reference` adapter (general-purpose keyword-based policy guard, no
  API key) is the default target, plus a `live` adapter that calls the
  actual production system prompt SCIP's `/scip/rag/ask` runs, via Groq,
  when `GROQ_API_KEY` is set.
- **LLM-judge upgrade path exists**: when `ANTHROPIC_API_KEY` is set,
  `JudgeEngine` uses the repo's existing `prompt_library.BCT_COMPLIANCE_JUDGE`
  prompt (previously defined but unused) to adjudicate cases the regex
  heuristic flags, instead of trusting the regex blindly.

**Honest limitation:** the default `reference` target is a small
rule-based guard, not a real LLM — running the suite against it out of
the box proves the *judges* discriminate correctly (verified: it
correctly fails on injected/leaked/caved/biased responses when fed
deliberately bad ones), not that any particular production LLM is safe.
For a genuine adversarial result against your own system, either point
`--target live` at SCIP's real Groq-backed assistant, or write a new
adapter in `system_adapters.py` for whatever system you're testing next
(QAIP, ARIA, etc.) and pass it into the module runners directly.

---

## How to run

```bash
cd ai-service

# Reference target — offline, no API key needed
python bct/runner.py

# Real SCIP RAG assistant (needs GROQ_API_KEY in ai-service/.env)
python bct/runner.py --target live
```

Exit code `0` if zero critical failures, `1` otherwise (CI-gateable, same
convention as `eval/eval_runner.py`).

### Upgrading judges to LLM-judge mode

```bash
# ai-service/.env
ANTHROPIC_API_KEY=sk-ant-...
```

`anthropic` is already a dependency (used by `eval/`). Re-run — the
report header will show `Judge mode: llm-judge`.

---

## FastAPI endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/bct/health` | Module status, judge mode, per-category test-case counts |
| GET | `/bct/run?target=reference\|live` | Execute the full suite, return the comprehensive report |
| GET | `/bct/results` | Latest saved run from `bct/results/bct_results.json` |

```bash
curl http://localhost:8001/bct/health
curl http://localhost:8001/bct/run
curl "http://localhost:8001/bct/run?target=live"
curl http://localhost:8001/bct/results
```

---

## Extending

Each module's test-case list (`INJECTION_TEST_CASES`,
`LEAKAGE_TEST_CASES`, `BIAS_TEST_CASES`, `MULTI_TURN_SCENARIOS`,
`CONTRACT_TEST_CASES`) is a plain Python list — add a new `TestCase` /
`PairedPromptTest` / `MultiTurnScenario` with its own
`violation_patterns` to extend coverage for a specific system. To test
a new system, add an adapter function to `bct/system_adapters.py` and
pass it directly to the relevant `run_*_tests()` function, or add a new
named target to `get_target()`.

**Not implemented** (call this out if you present this): toxicity
resistance (sustained adversarial pressure toward harmful content) —
same shape as the other modules, no test-case library built yet.

---

## Results files

All results are saved to `bct/results/`:

| File | Contents |
|---|---|
| `bct_results.json` | Latest run (overwritten each run) |
| `bct_YYYYMMDD_HHMMSS_<id>.json` | Timestamped archive of every run |

Each file contains the full per-module reports (including every
`TestResult` with its judge evidence string), the bias/multi-turn
results, and the aggregate `total_critical_failures` / recommendation.
