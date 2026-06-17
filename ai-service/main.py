# Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
# Supply Chain Intelligence Platform
# Licensed under MIT License — see LICENSE file for details

from fastapi import FastAPI, HTTPException, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from typing import List, Optional
import pandas as pd
import numpy as np
from sklearn.ensemble import IsolationForest
from sklearn.preprocessing import StandardScaler
import sqlalchemy, os, time, threading

try:
    from statsmodels.tsa.holtwinters import ExponentialSmoothing
    _STATSMODELS = True
except ImportError:
    _STATSMODELS = False
from dotenv import load_dotenv

load_dotenv()
app = FastAPI(title="Supply Chain AI Service", version="1.0.0")

from agent_framework import router as _agent_framework_router
app.include_router(_agent_framework_router)

app.add_middleware(CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"])

# ── Rate limiting (in-memory sliding window, no Redis needed) ─────────────────
_rate_windows: dict = {}   # ip -> deque of timestamps
_rate_lock = threading.Lock()

def _check_rate(ip: str, max_requests: int, window_sec: int) -> tuple[bool, int]:
    """Returns (allowed, remaining). Thread-safe sliding window."""
    import collections
    now = time.time()
    cutoff = now - window_sec
    with _rate_lock:
        if ip not in _rate_windows:
            _rate_windows[ip] = {}
        key = f"{max_requests}/{window_sec}"
        if key not in _rate_windows[ip]:
            _rate_windows[ip][key] = collections.deque()
        dq = _rate_windows[ip][key]
        while dq and dq[0] < cutoff:
            dq.popleft()
        remaining = max_requests - len(dq)
        if len(dq) >= max_requests:
            return False, 0
        dq.append(now)
        return True, remaining - 1

def _rate_limit_response(limit: int, window_sec: int, endpoint: str, ip: str):
    import logging
    logging.getLogger(__name__).warning("[RATE-LIMIT] %s exceeded %d req/%ds from %s", endpoint, limit, window_sec, ip)
    return JSONResponse(
        status_code=429,
        content={"error": "Rate limit exceeded — too many requests", "code": "RATE_LIMITED"},
        headers={
            "X-RateLimit-Limit": str(limit),
            "X-RateLimit-Remaining": "0",
            "Retry-After": str(window_sec),
        }
    )

# ── Global Anthropic API cost guard ──────────────────────────────────────────
_claude_call_count = 0
_claude_window_start = time.time()
_claude_lock = threading.Lock()
CLAUDE_HOURLY_CAP = 100

def _check_claude_budget() -> bool:
    global _claude_call_count, _claude_window_start
    with _claude_lock:
        now = time.time()
        if now - _claude_window_start > 3600:
            _claude_call_count = 0
            _claude_window_start = now
        if _claude_call_count >= CLAUDE_HOURLY_CAP:
            return False
        _claude_call_count += 1
        return True

DATABASE_URL = os.getenv("DATABASE_URL")

class DeliveryRecord(BaseModel):
    supplierId: int
    leadTimeDays: float
    onTime: bool
    defectRate: Optional[float] = 0.0

class SupplierAnalysisRequest(BaseModel):
    supplierId: int
    deliveries: List[DeliveryRecord]

@app.get("/health")
def health():
    return {"status": "ok", "service": "Supply Chain AI", "port": 8001}

@app.post("/analyze-supplier")
def analyze_supplier(req: SupplierAnalysisRequest, request: Request):
    ip = request.client.host if request.client else "unknown"
    allowed, _ = _check_rate(ip, max_requests=20, window_sec=60)
    if not allowed:
        return _rate_limit_response(20, 60, "/analyze-supplier", ip)
    if len(req.deliveries) < 3:
        return {
            "supplierId": req.supplierId,
            "riskScore": 0,
            "riskLevel": "Unknown",
            "explanation": "Not enough delivery data"
        }

    df = pd.DataFrame([d.dict() for d in req.deliveries])

    X = df[["leadTimeDays", "defectRate"]].fillna(0)
    scaler = StandardScaler()
    X_scaled = scaler.fit_transform(X)
    iso = IsolationForest(contamination=0.15, random_state=42)
    df["anomaly"] = iso.fit_predict(X_scaled)
    anomalies = df[df["anomaly"] == -1]

    on_time_rate = df["onTime"].mean()
    avg_lead_time = df["leadTimeDays"].mean()
    lead_time_std = df["leadTimeDays"].std()

    anomaly_score = min(40, len(anomalies) / len(df) * 100)
    late_score = min(40, (1 - on_time_rate) * 100)
    variance_score = min(20, lead_time_std * 2)
    risk_score = int(anomaly_score + late_score + variance_score)

    if risk_score >= 60:
        risk_level = "High"
    elif risk_score >= 30:
        risk_level = "Medium"
    else:
        risk_level = "Low"

    return {
        "supplierId": req.supplierId,
        "riskScore": risk_score,
        "riskLevel": risk_level,
        "explanation": f"{len(anomalies)} anomalies in {len(df)} deliveries. On-time: {on_time_rate:.0%}. Avg lead time: {avg_lead_time:.1f} days.",
        "anomalyCount": int(len(anomalies)),
        "onTimeRate": round(float(on_time_rate) * 100, 1),
        "avgLeadTimeDays": round(float(avg_lead_time), 1)
    }

@app.get("/forecast/{item_id}")
def forecast_demand(item_id: str, weeks: int = 4):
    if not _STATSMODELS:
        raise HTTPException(status_code=503, detail="statsmodels not installed — forecast unavailable in this environment")
    try:
        engine = sqlalchemy.create_engine(DATABASE_URL)
        df = pd.read_sql(
            f"SELECT order_date AS ds, SUM(quantity) AS y FROM orders WHERE item_id = '{item_id}' GROUP BY order_date ORDER BY order_date",
            engine)
        if len(df) < 6:
            return {"error": "Not enough data — need at least 6 data points"}
        df["ds"] = pd.to_datetime(df["ds"])
        df = df.set_index("ds")
        model = ExponentialSmoothing(df["y"], trend="add", seasonal=None)
        fit = model.fit()
        forecast = fit.forecast(weeks)
        result = []
        for date, value in forecast.items():
            result.append({
                "ds": str(date.date()),
                "yhat": round(float(value), 2),
                "yhat_lower": round(float(value) * 0.85, 2),
                "yhat_upper": round(float(value) * 1.15, 2)
            })
        return {"itemId": item_id, "forecastWeeks": weeks, "forecast": result}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/eval/health")
def eval_health():
    """Returns the eval module status and whether LLM-judge mode is active."""
    import os
    has_key = bool(os.getenv("ANTHROPIC_API_KEY", "").strip())
    try:
        from eval.test_cases import SUPPLY_CHAIN_TEST_CASES
        case_count = len(SUPPLY_CHAIN_TEST_CASES)
    except Exception as e:
        return {"status": "error", "detail": str(e)}
    return {
        "status": "ok",
        "eval_module": "loaded",
        "test_cases": case_count,
        "mode": "llm-judge" if has_key else "heuristic",
        "llm_judge_active": has_key,
    }


@app.get("/eval/run")
def eval_run(request: Request):
    """Run all 20 test cases and return full evaluation results."""
    ip = request.client.host if request.client else "unknown"
    allowed, remaining = _check_rate(ip, max_requests=5, window_sec=60)
    if not allowed:
        return _rate_limit_response(5, 60, "/eval/run", ip)
    if not _check_claude_budget():
        return JSONResponse(status_code=503,
            content={"error": "AI service temporarily unavailable — hourly limit reached"})
    try:
        from eval.prompt_evaluator import LLMEvaluator
        from eval.test_cases import SUPPLY_CHAIN_TEST_CASES
    except ImportError as e:
        raise HTTPException(status_code=500, detail=f"Eval module import error: {e}")

    evaluator = LLMEvaluator()
    results = evaluator.evaluate_batch(SUPPLY_CHAIN_TEST_CASES)
    report  = evaluator.generate_report(results)
    return {"report": report, "results": results}


@app.get("/eval/results")
def eval_results():
    """Return the latest saved evaluation results from disk."""
    import json
    from pathlib import Path
    results_file = Path(__file__).parent / "eval" / "results" / "eval_results.json"
    if not results_file.exists():
        raise HTTPException(
            status_code=404,
            detail="No results found. Call GET /eval/run first."
        )
    with open(results_file, encoding="utf-8") as f:
        return json.load(f)


@app.get("/agents/health")
def agents_health():
    """Returns agent module status and LangGraph availability."""
    try:
        from langgraph.graph import StateGraph
        langgraph_available = True
    except ImportError:
        langgraph_available = False
    try:
        from agents.supplier_agent import LANGGRAPH_AVAILABLE, NODES
        agent_loaded = True
    except Exception as e:
        agent_loaded = False
    return {
        "status": "ok",
        "agent_module": "loaded" if agent_loaded else "error",
        "langgraph_available": langgraph_available,
        "nodes": ["fetch_supplier_data", "score_risk", "generate_explanation", "validate_response"],
        "mode": "langgraph" if langgraph_available else "sequential-fallback",
    }


@app.get("/agents/run")
def agents_run(request: Request, backend: str = "http://localhost:8089/supchain"):
    """Run the full LangGraph agent test suite and return results."""
    ip = request.client.host if request.client else "unknown"
    allowed, _ = _check_rate(ip, max_requests=5, window_sec=60)
    if not allowed:
        return _rate_limit_response(5, 60, "/agents/run", ip)
    if not _check_claude_budget():
        return JSONResponse(status_code=503,
            content={"error": "AI service temporarily unavailable — hourly limit reached"})
    import sys, os
    sys.path.insert(0, os.path.dirname(__file__))
    try:
        from agents.agent_test_runner import run_suite
    except ImportError as e:
        raise HTTPException(status_code=500, detail=f"Agent module import error: {e}")
    try:
        report = run_suite(backend)
        return report
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/agents/results")
def agents_results():
    """Return the latest saved agent test results from disk."""
    import json
    from pathlib import Path
    results_file = Path(__file__).parent / "agents" / "results" / "agent_test_results.json"
    if not results_file.exists():
        raise HTTPException(
            status_code=404,
            detail="No results found. Call GET /agents/run first."
        )
    with open(results_file, encoding="utf-8") as f:
        return json.load(f)


@app.get("/all-supplier-risks")
def all_supplier_risks():
    try:
        engine = sqlalchemy.create_engine(DATABASE_URL)
        df = pd.read_sql("""
            SELECT s.id as supplier_id, s.name as supplier_name,
                   d.lead_time_days, d.on_time
            FROM supplier_profile s
            JOIN supplier_delivery d ON s.id = d.supplier_id
        """, engine)
        results = []
        for sid, group in df.groupby("supplier_id"):
            on_time_rate = group["on_time"].mean()
            risk_score = int((1 - on_time_rate) * 100)
            results.append({
                "supplierId": int(sid),
                "supplierName": str(group["supplier_name"].iloc[0]),
                "riskScore": risk_score,
                "riskLevel": "High" if risk_score > 60 else "Medium" if risk_score > 30 else "Low",
                "onTimeRate": round(float(on_time_rate) * 100, 1),
                "explanation": f"Based on {len(group)} deliveries"
            })
        return sorted(results, key=lambda x: x["riskScore"], reverse=True)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))