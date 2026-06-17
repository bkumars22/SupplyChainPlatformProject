# Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
# Supply Chain Intelligence Platform — Module 3: Single-Supplier LangGraph Agent Framework
"""
LangGraph agent framework for per-supplier risk analysis with reliability testing.

Nodes  : fetch_supplier_data → score_risk → explain_risk → validate_output → log_result
Edges  : linear chain as above

FastAPI : POST /agent/run   — run agent on one supplier, returns scored + explained result
         GET  /agent/results — last 10 persisted results from agent_results.json

JWT    : both endpoints require "Authorization: Bearer <token>" issued by the Java backend
         (HMAC-SHA256, secret read from JWT_SECRET env var; if unset, auth is skipped in
          dev mode with a warning — never skip in prod)

CLI    : python agent_framework.py  →  runs 3 test suppliers and prints a results table
"""
import sys, io
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8", errors="replace")
sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding="utf-8", errors="replace")

import json
import os
import re
import statistics
import time
import uuid
from datetime import datetime, timezone
from pathlib import Path
from typing import Optional, TypedDict, List

import numpy as np
from dotenv import load_dotenv
from fastapi import APIRouter, Depends, HTTPException
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from pydantic import BaseModel
from sklearn.ensemble import IsolationForest
from sklearn.preprocessing import StandardScaler

load_dotenv()

# ── LangGraph (graceful fallback) ─────────────────────────────────────────────
try:
    from langgraph.graph import StateGraph, END
    _LANGGRAPH = True
except ImportError:
    _LANGGRAPH = False

# ── Persistence ───────────────────────────────────────────────────────────────
_RESULTS_DIR  = Path(__file__).parent / "agent_results"
_RESULTS_FILE = _RESULTS_DIR / "agent_results.json"
_RESULTS_DIR.mkdir(parents=True, exist_ok=True)

# ── JWT auth dependency ───────────────────────────────────────────────────────
_JWT_SECRET = os.getenv("JWT_SECRET", "").strip()
_bearer     = HTTPBearer(auto_error=False)


def _require_jwt(creds: Optional[HTTPAuthorizationCredentials] = Depends(_bearer)):
    """
    Validate the Bearer token issued by the Spring Boot backend.
    Uses HMAC-SHA256 (HS256).  If JWT_SECRET is not set in the environment,
    auth is skipped with a log warning (dev convenience only).
    """
    if not _JWT_SECRET:
        # Dev mode — no secret configured, pass through
        return {"sub": "dev", "role": "ADMIN"}

    if creds is None or not creds.credentials:
        raise HTTPException(status_code=401, detail="Authorization header required")

    token = creds.credentials
    try:
        import hmac, hashlib, base64
        header_b64, payload_b64, sig_b64 = token.split(".")

        # Verify signature
        signing_input = f"{header_b64}.{payload_b64}".encode()
        expected_sig  = base64.urlsafe_b64encode(
            hmac.new(_JWT_SECRET.encode(), signing_input, hashlib.sha256).digest()
        ).rstrip(b"=")
        actual_sig    = sig_b64.encode()
        if not hmac.compare_digest(expected_sig, actual_sig):
            raise HTTPException(status_code=401, detail="Invalid token signature")

        # Decode payload
        padding = 4 - len(payload_b64) % 4
        payload = json.loads(base64.urlsafe_b64decode(payload_b64 + "=" * padding))

        # Check expiry
        exp = payload.get("exp")
        if exp and time.time() > exp:
            raise HTTPException(status_code=401, detail="Token expired")

        return payload
    except HTTPException:
        raise
    except Exception:
        raise HTTPException(status_code=401, detail="Malformed token")


# ── Pydantic models ───────────────────────────────────────────────────────────

class SupplierInput(BaseModel):
    name:               str
    otd_score:          float   # on-time delivery 0-100
    defect_rate:        float   # defects per 1000 units, 0-100
    lead_time_days:     float   # average lead time
    quality_score:      Optional[float] = None   # composite quality 0-100; derived if omitted
    responsiveness:     Optional[float] = 50.0   # 0-100

class AgentRunResponse(BaseModel):
    supplier_name:      str
    risk_score:         float       # 0-100; higher = riskier
    risk_level:         str         # HIGH / MEDIUM / LOW
    anomaly_flag:       bool
    explanation:        str
    validation_passed:  bool
    consistency_score:  float       # Jaccard similarity 0-1 across 3 runs
    run_id:             str
    latency_ms:         float
    mode:               str         # "langgraph" | "sequential"


# ── LangGraph state ───────────────────────────────────────────────────────────

class FrameworkState(TypedDict):
    supplier:           dict        # raw input
    risk_score:         float
    risk_level:         str
    anomaly_flag:       bool
    explanation:        str
    validation_passed:  bool
    validation_checks:  dict
    errors:             List[str]
    node_timings:       dict


# ── Node 1: fetch_supplier_data ───────────────────────────────────────────────

def fetch_supplier_data(state: FrameworkState) -> FrameworkState:
    """Normalise and validate the incoming supplier dict."""
    t0 = time.perf_counter()
    supplier = dict(state.get("supplier", {}))
    errors   = list(state.get("errors", []))

    # Derive quality_score from OTD + defect_rate if not provided
    if not supplier.get("quality_score"):
        otd     = float(supplier.get("otd_score", 50.0))
        defect  = float(supplier.get("defect_rate", 0.0))
        supplier["quality_score"] = round(max(0.0, otd - defect * 0.5), 2)

    timings = dict(state.get("node_timings", {}))
    timings["fetch_ms"] = round((time.perf_counter() - t0) * 1000, 2)
    return {**state, "supplier": supplier, "errors": errors, "node_timings": timings}


# ── Node 2: score_risk ────────────────────────────────────────────────────────

# Reference population of 10 known suppliers used as IsolationForest training set.
# The live supplier is appended to this population so the model has enough variance
# to produce meaningful anomaly scores on a single input.
_REFERENCE_POPULATION = [
    [88.0, 2.0,  92.0],   # Murata — LOW risk baseline
    [78.0, 5.0,  80.0],   # Samsung SDI — LOW
    [65.0, 8.0,  70.0],   # Vishay — MEDIUM
    [55.0, 12.0, 60.0],   # Taiwan Semi — MEDIUM
    [48.0, 18.0, 50.0],   # Infineon — MEDIUM
    [45.0, 22.0, 40.0],   # Shenzhen Electronics — HIGH
    [40.0, 25.0, 35.0],   # STMicro — HIGH
    [35.0, 28.0, 30.0],   # TE Connectivity — HIGH
    [30.0, 32.0, 20.0],   # Foxconn — HIGH
    [20.0, 40.0, 15.0],   # Delta Electronics — HIGH (worst)
]

def score_risk(state: FrameworkState) -> FrameworkState:
    t0      = time.perf_counter()
    s       = state["supplier"]
    errors  = list(state.get("errors", []))

    try:
        otd      = float(s.get("otd_score", 50.0))
        defect   = float(s.get("defect_rate", 0.0))
        quality  = float(s.get("quality_score", 50.0))

        # Build feature matrix: [otd, defect_rate, quality_score]
        live_row  = [otd, defect, quality]
        all_rows  = _REFERENCE_POPULATION + [live_row]
        X         = np.array(all_rows, dtype=float)

        scaler    = StandardScaler()
        X_scaled  = scaler.fit_transform(X)

        iso             = IsolationForest(contamination=0.3, random_state=42)
        labels          = iso.fit_predict(X_scaled)
        scores          = iso.decision_function(X_scaled)   # negative = more anomalous

        live_label      = labels[-1]
        live_score      = float(scores[-1])
        is_anomaly      = bool(live_label == -1)

        # Risk score: 0-100, higher = riskier
        # Normalise: decision_function range roughly -0.5 to +0.5
        # Invert so higher anomaly → higher risk score
        normalised_risk = round(max(0.0, min(100.0, (0.5 - live_score) * 100)), 1)

        # Risk level thresholds
        if is_anomaly or normalised_risk >= 65:
            risk_level = "HIGH"
        elif normalised_risk >= 40:
            risk_level = "MEDIUM"
        else:
            risk_level = "LOW"

    except Exception as exc:
        errors.append(f"score_risk: {exc}")
        normalised_risk = 50.0
        is_anomaly      = False
        risk_level      = "MEDIUM"

    timings = dict(state.get("node_timings", {}))
    timings["score_ms"] = round((time.perf_counter() - t0) * 1000, 2)

    return {
        **state,
        "risk_score":   normalised_risk,
        "risk_level":   risk_level,
        "anomaly_flag": is_anomaly,
        "errors":       errors,
        "node_timings": timings,
    }


# ── Node 3: explain_risk ──────────────────────────────────────────────────────

def _try_anthropic_client():
    api_key = os.getenv("ANTHROPIC_API_KEY", "").strip()
    if not api_key:
        return None
    try:
        import anthropic
        return anthropic.Anthropic(api_key=api_key)
    except ImportError:
        return None


def explain_risk(state: FrameworkState) -> FrameworkState:
    t0         = time.perf_counter()
    s          = state["supplier"]
    risk_level = state.get("risk_level", "UNKNOWN")
    risk_score = state.get("risk_score", 0.0)
    is_anomaly = state.get("anomaly_flag", False)
    errors     = list(state.get("errors", []))

    name    = s.get("name", "Unknown Supplier")
    otd     = s.get("otd_score", 0.0)
    defect  = s.get("defect_rate", 0.0)
    lead    = s.get("lead_time_days", 0.0)
    quality = s.get("quality_score", 50.0)

    client = _try_anthropic_client()

    if client:
        prompt = (
            f"Supplier '{name}' has: OTD score {otd}%, defect rate {defect} per 1000 units, "
            f"lead time {lead} days, quality score {quality:.1f}/100. "
            f"IsolationForest risk score: {risk_score:.1f}/100 ({risk_level}). "
            f"Anomaly detected: {is_anomaly}. "
            f"In 2-3 sentences: explain the risk for {name} and give one specific recommendation. "
            f"Mention the supplier name and OTD score."
        )
        try:
            msg = client.messages.create(
                model="claude-haiku-4-5-20251001",
                max_tokens=160,
                messages=[{"role": "user", "content": prompt}],
            )
            explanation = msg.content[0].text.strip()
        except Exception as exc:
            errors.append(f"explain_risk LLM: {exc}")
            explanation = _heuristic_explanation(name, otd, defect, lead, risk_level, is_anomaly)
    else:
        explanation = _heuristic_explanation(name, otd, defect, lead, risk_level, is_anomaly)

    timings = dict(state.get("node_timings", {}))
    timings["explain_ms"] = round((time.perf_counter() - t0) * 1000, 2)

    return {**state, "explanation": explanation, "errors": errors, "node_timings": timings}


def _heuristic_explanation(name, otd, defect, lead, risk_level, is_anomaly):
    anomaly_note = " IsolationForest flagged this supplier as a statistical outlier." if is_anomaly else ""
    if risk_level == "HIGH":
        action = "Recommend dual-sourcing immediately and issuing a formal performance notice."
    elif risk_level == "MEDIUM":
        action = "Recommend scheduling a performance review and increasing monitoring frequency."
    else:
        action = "Continue standard monitoring; consider this supplier for strategic expansion."
    return (
        f"{name} has an OTD score of {otd}% with {defect} defects per 1000 units "
        f"and an average lead time of {lead} days.{anomaly_note} "
        f"Risk level: {risk_level}. {action}"
    )


# ── Node 4: validate_output ───────────────────────────────────────────────────

def validate_output(state: FrameworkState) -> FrameworkState:
    t0          = time.perf_counter()
    explanation = state.get("explanation", "")
    name        = state["supplier"].get("name", "")
    errors      = list(state.get("errors", []))

    checks = {
        "not_empty":             len(explanation.strip()) > 0,
        "mentions_supplier":     name.lower() in explanation.lower() or any(
                                     part.lower() in explanation.lower()
                                     for part in name.split() if len(part) > 3
                                 ),
        "contains_recommendation": any(
                                     kw in explanation.lower()
                                     for kw in ("recommend", "action", "review", "monitor",
                                                "dual-sourc", "performance notice", "sourcing",
                                                "consider", "continue", "increase", "notice",
                                                "schedule", "expand", "strategic")
                                 ),
        "length_in_range":       50 <= len(explanation) <= 600,
    }
    passed = all(checks.values())

    timings = dict(state.get("node_timings", {}))
    timings["validate_ms"] = round((time.perf_counter() - t0) * 1000, 2)

    return {
        **state,
        "validation_passed": passed,
        "validation_checks": checks,
        "errors":            errors,
        "node_timings":      timings,
    }


# ── Node 5: log_result ────────────────────────────────────────────────────────

def log_result(state: FrameworkState) -> FrameworkState:
    t0     = time.perf_counter()
    errors = list(state.get("errors", []))

    record = {
        "run_id":            str(uuid.uuid4())[:8],
        "timestamp":         datetime.now(timezone.utc).isoformat() + "Z",
        "supplier_name":     state["supplier"].get("name", "Unknown"),
        "risk_score":        state.get("risk_score", 0.0),
        "risk_level":        state.get("risk_level", "UNKNOWN"),
        "anomaly_flag":      state.get("anomaly_flag", False),
        "explanation":       state.get("explanation", ""),
        "validation_passed": state.get("validation_passed", False),
        "validation_checks": state.get("validation_checks", {}),
        "node_timings":      state.get("node_timings", {}),
        "errors":            errors,
    }

    try:
        existing = []
        if _RESULTS_FILE.exists():
            with open(_RESULTS_FILE, encoding="utf-8") as f:
                existing = json.load(f)
        existing.append(record)
        with open(_RESULTS_FILE, "w", encoding="utf-8") as f:
            json.dump(existing, f, indent=2, ensure_ascii=False)
    except Exception as exc:
        errors.append(f"log_result: {exc}")

    timings = dict(state.get("node_timings", {}))
    timings["log_ms"] = round((time.perf_counter() - t0) * 1000, 2)

    return {**state, "_log_record": record, "errors": errors, "node_timings": timings}


# ── Graph builder ─────────────────────────────────────────────────────────────

def _build_graph():
    if not _LANGGRAPH:
        return None
    g = StateGraph(FrameworkState)
    g.add_node("fetch_supplier_data", fetch_supplier_data)
    g.add_node("score_risk",          score_risk)
    g.add_node("explain_risk",        explain_risk)
    g.add_node("validate_output",     validate_output)
    g.add_node("log_result",          log_result)

    g.set_entry_point("fetch_supplier_data")
    g.add_edge("fetch_supplier_data", "score_risk")
    g.add_edge("score_risk",          "explain_risk")
    g.add_edge("explain_risk",        "validate_output")
    g.add_edge("validate_output",     "log_result")
    g.add_edge("log_result",          END)

    return g.compile()


_COMPILED_GRAPH = None

def _get_graph():
    global _COMPILED_GRAPH
    if _COMPILED_GRAPH is None:
        _COMPILED_GRAPH = _build_graph()
    return _COMPILED_GRAPH


def _run_once(supplier_dict: dict) -> FrameworkState:
    """Run all 5 nodes on one supplier. Uses LangGraph if available, else sequential."""
    initial: FrameworkState = {
        "supplier":          supplier_dict,
        "risk_score":        0.0,
        "risk_level":        "UNKNOWN",
        "anomaly_flag":      False,
        "explanation":       "",
        "validation_passed": False,
        "validation_checks": {},
        "errors":            [],
        "node_timings":      {},
    }
    graph = _get_graph()
    if graph is not None:
        return graph.invoke(initial)
    # Sequential fallback
    s = fetch_supplier_data(initial)
    s = score_risk(s)
    s = explain_risk(s)
    s = validate_output(s)
    s = log_result(s)
    return s


# ── Reliability testing ───────────────────────────────────────────────────────

def _measure_consistency(supplier_dict: dict, runs: int = 3) -> tuple[float, list[FrameworkState]]:
    """
    Run the agent `runs` times on the same supplier.

    Returns
    -------
    consistency_score : float
        Jaccard similarity of explanation tokens across the 3 run pairs.
        Range 0-1; > 0.6 is considered consistent.
    states : list of FrameworkState
        All run states (used for the primary result selection).
    """
    states       = []
    explanations = []

    for _ in range(runs):
        state = _run_once(supplier_dict)
        states.append(state)
        explanations.append(state.get("explanation", ""))

    # Flag runs with empty or error output
    valid_runs = [e for e in explanations if len(e.strip()) > 20]
    if len(valid_runs) < len(explanations):
        # Some runs produced empty/error output — consistency is 0
        return 0.0, states

    # Pairwise Jaccard similarity
    jaccard_scores = []
    for i in range(len(explanations)):
        for j in range(i + 1, len(explanations)):
            a = set(explanations[i].lower().split())
            b = set(explanations[j].lower().split())
            if a or b:
                jaccard_scores.append(len(a & b) / len(a | b))

    score = round(statistics.mean(jaccard_scores), 4) if jaccard_scores else 1.0

    # Length variance check: flag if any explanation is >30% shorter than the longest
    lengths = [len(e) for e in explanations if e]
    if lengths:
        max_len = max(lengths)
        if any(l < max_len * 0.70 for l in lengths):
            # High variance in length — penalise consistency score
            score = round(score * 0.75, 4)

    return score, states


# ── FastAPI router ────────────────────────────────────────────────────────────

router = APIRouter(prefix="/agent", tags=["Agent Framework"])


@router.post("/run", response_model=AgentRunResponse)
def agent_run(
    payload: SupplierInput,
    _claims: dict = Depends(_require_jwt),
):
    """
    Run the 5-node LangGraph agent on a single supplier.
    Executes 3 times internally to compute consistency_score.
    """
    t0             = time.perf_counter()
    supplier_dict  = payload.model_dump()
    supplier_dict["name"] = supplier_dict.get("name", "Unknown")

    consistency_score, states = _measure_consistency(supplier_dict, runs=3)

    # Use the first run as the canonical result (all runs use same deterministic
    # IsolationForest seed=42; explanation may vary if LLM mode is active)
    primary = states[0]

    total_ms = round((time.perf_counter() - t0) * 1000, 1)

    return AgentRunResponse(
        supplier_name     = supplier_dict["name"],
        risk_score        = primary.get("risk_score", 0.0),
        risk_level        = primary.get("risk_level", "UNKNOWN"),
        anomaly_flag      = primary.get("anomaly_flag", False),
        explanation       = primary.get("explanation", ""),
        validation_passed = primary.get("validation_passed", False),
        consistency_score = consistency_score,
        run_id            = primary.get("_log_record", {}).get("run_id", str(uuid.uuid4())[:8]),
        latency_ms        = total_ms,
        mode              = "langgraph" if _LANGGRAPH else "sequential",
    )


@router.get("/results")
def agent_results(_claims: dict = Depends(_require_jwt)):
    """Return the last 10 agent run results from agent_results.json."""
    if not _RESULTS_FILE.exists():
        raise HTTPException(status_code=404, detail="No results yet. Call POST /agent/run first.")
    with open(_RESULTS_FILE, encoding="utf-8") as f:
        all_results = json.load(f)
    return {
        "total":   len(all_results),
        "results": all_results[-10:],
    }


# ── Register on main FastAPI app at import time ───────────────────────────────
# main.py can do:  from agent_framework import router as agent_framework_router
#                  app.include_router(agent_framework_router)
# Both /agent/run and /agent/results are then live.


# ── CLI: run 3 test suppliers and print a results table ──────────────────────

_CLI_TEST_SUPPLIERS = [
    SupplierInput(name="Delta Electronics India",  otd_score=20.0, defect_rate=40.0, lead_time_days=22.0),
    SupplierInput(name="Murata Manufacturing",     otd_score=88.0, defect_rate=2.0,  lead_time_days=8.0),
    SupplierInput(name="Taiwan Semiconductors Ltd",otd_score=55.0, defect_rate=12.0, lead_time_days=14.0),
]


def _cli_run():
    W = 60
    print(f"\n{'='*W}")
    print("  Supply Chain Intelligence Platform")
    print("  Module 3 — Agent Framework  (LangGraph: " + ("YES" if _LANGGRAPH else "NO — sequential fallback") + ")")
    print("  JWT secret: " + ("configured" if _JWT_SECRET else "not set (dev mode)"))
    print(f"{'='*W}\n")

    rows = []
    for inp in _CLI_TEST_SUPPLIERS:
        print(f"  Running: {inp.name} ...", flush=True)
        t0 = time.perf_counter()
        consistency, states = _measure_consistency(inp.model_dump(), runs=3)
        primary  = states[0]
        total_ms = round((time.perf_counter() - t0) * 1000, 1)

        rows.append({
            "name":        inp.name,
            "risk_score":  primary.get("risk_score", 0.0),
            "risk_level":  primary.get("risk_level", "?"),
            "anomaly":     primary.get("anomaly_flag", False),
            "val_pass":    primary.get("validation_passed", False),
            "consistency": consistency,
            "ms":          total_ms,
            "errors":      len(primary.get("errors", [])),
        })

    # Table header
    print(f"\n{'='*W}")
    print("  RESULTS TABLE")
    print(f"{'='*W}")
    fmt = "  {:<30}  {:>6}  {:>7}  {:>7}  {:>5}  {:>5}  {:>8}"
    print(fmt.format("Supplier", "Score", "Level", "Anomaly", "Valid", "Err", "ms"))
    print("  " + "-" * (W - 2))
    for r in rows:
        print(fmt.format(
            r["name"][:30],
            f"{r['risk_score']:.1f}",
            r["risk_level"],
            "YES" if r["anomaly"] else "no",
            "PASS" if r["val_pass"] else "FAIL",
            str(r["errors"]) if r["errors"] else "0",
            f"{r['ms']:.0f}",
        ))
    print("  " + "-" * (W - 2))

    avg_consistency = round(statistics.mean(r["consistency"] for r in rows), 4)
    all_passed      = all(r["val_pass"] for r in rows)
    print(f"\n  Avg consistency : {avg_consistency:.4f}")
    print(f"  All validated   : {'YES' if all_passed else 'NO'}")
    print(f"  Results file    : {_RESULTS_FILE}")
    print(f"{'='*W}\n")


if __name__ == "__main__":
    _cli_run()
