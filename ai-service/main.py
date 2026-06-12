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