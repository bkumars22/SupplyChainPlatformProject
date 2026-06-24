"""SCIP RAG ingest pipeline.

Ingestion paths:
  1. ingest_supplier_analysis  — store a completed supplier risk analysis
  2. ingest_disruption_event   — store a supply-chain disruption event
  3. ingest_forecast_result    — store a demand forecast result

These are retrieved when the user asks natural-language questions like
"which supplier is high risk?" or "what disruptions happened recently?"
"""

from __future__ import annotations

import logging
from typing import Any

from .embedder import embed
from .vector_store import upsert

logger = logging.getLogger("rag.ingest")


def _safe_upsert(content: str, source_type: str, source_id: str, metadata: dict) -> bool:
    try:
        embedding = embed(content)
        result = upsert(
            content=content,
            embedding=embedding,
            source_type=source_type,
            source_id=source_id,
            project_id="SCIP",
            metadata=metadata,
        )
        return result is not None
    except Exception as exc:
        logger.warning("SCIP ingest %s/%s failed: %s", source_type, source_id, exc)
        return False


def ingest_supplier_analysis(
    supplier_name: str,
    risk_level: str,
    risk_score: float,
    on_time_delivery: float,
    quality_rating: float,
    recommendation: str,
    explanation: str,
    run_id: str = "",
) -> bool:
    """Store a supplier risk analysis result.

    Embedded text includes all supplier attributes so semantic search
    can find suppliers matching "high risk", "late deliveries", etc.
    """
    content = (
        f"Supplier: {supplier_name}\n"
        f"Risk level: {risk_level}\n"
        f"Risk score: {risk_score:.3f}\n"
        f"On-time delivery: {on_time_delivery:.1f}%\n"
        f"Quality rating: {quality_rating:.1f}/5.0\n"
        f"Recommendation: {recommendation}\n"
        f"Analysis: {explanation[:800]}"
    )
    source_id = f"supplier:{supplier_name.lower().replace(' ', '_')}"
    metadata: dict[str, Any] = {
        "supplier_name": supplier_name,
        "risk_level": risk_level,
        "risk_score": round(risk_score, 4),
        "on_time_delivery": on_time_delivery,
        "quality_rating": quality_rating,
        "run_id": run_id,
    }
    return _safe_upsert(content, "supplier_analysis", source_id, metadata)


def ingest_disruption_event(
    event_id: str,
    supplier_name: str,
    event_type: str,
    severity: str,
    description: str,
    impact: str = "",
    resolution: str = "",
) -> bool:
    """Store a supply-chain disruption event."""
    content_parts = [
        f"Disruption event: {event_type}",
        f"Supplier: {supplier_name}",
        f"Severity: {severity}",
        f"Description: {description[:600]}",
    ]
    if impact:
        content_parts.append(f"Impact: {impact[:300]}")
    if resolution:
        content_parts.append(f"Resolution: {resolution[:300]}")

    content = "\n".join(content_parts)
    source_id = f"disruption:{event_id}"
    metadata: dict[str, Any] = {
        "event_id": event_id,
        "supplier_name": supplier_name,
        "event_type": event_type,
        "severity": severity,
    }
    return _safe_upsert(content, "disruption_event", source_id, metadata)


def ingest_forecast_result(
    item_id: str,
    item_name: str,
    forecast_horizon_days: int,
    predicted_demand: float,
    confidence: float,
    trend: str,
    notes: str = "",
) -> bool:
    """Store a demand forecast for NL queries like 'demand for component X'."""
    content = (
        f"Item: {item_name} (ID: {item_id})\n"
        f"Forecast horizon: {forecast_horizon_days} days\n"
        f"Predicted demand: {predicted_demand:.1f} units\n"
        f"Confidence: {confidence:.1%}\n"
        f"Trend: {trend}\n"
    )
    if notes:
        content += f"Notes: {notes[:300]}"

    source_id = f"forecast:{item_id}"
    metadata: dict[str, Any] = {
        "item_id": item_id,
        "item_name": item_name,
        "horizon_days": forecast_horizon_days,
        "predicted_demand": round(predicted_demand, 2),
        "confidence": round(confidence, 4),
        "trend": trend,
    }
    return _safe_upsert(content, "forecast", source_id, metadata)
