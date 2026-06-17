# Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
# Supply Chain Intelligence Platform
# Licensed under MIT License — see LICENSE file for details

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Optional
import pandas as pd
import numpy as np
from sklearn.ensemble import IsolationForest
from sklearn.preprocessing import StandardScaler
from statsmodels.tsa.holtwinters import ExponentialSmoothing
import sqlalchemy, os
from dotenv import load_dotenv

load_dotenv()
app = FastAPI(title="Supply Chain AI Service", version="1.0.0")

app.add_middleware(CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"])

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
def analyze_supplier(req: SupplierAnalysisRequest):
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
def eval_run():
    """Run all 20 test cases and return full evaluation results."""
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
def agents_run(backend: str = "http://localhost:8089/supchain"):
    """Run the full LangGraph agent test suite and return results."""
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