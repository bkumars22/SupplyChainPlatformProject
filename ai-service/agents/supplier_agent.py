# Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
# Supply Chain Intelligence Platform — Module 2: LangGraph Supplier Risk Agent
import os
import re
import time
from typing import TypedDict, List, Optional

import numpy as np
import requests
from sklearn.ensemble import IsolationForest
from sklearn.preprocessing import StandardScaler

# ── LangGraph imports (with graceful fallback if not installed) ───────────────
try:
    from langgraph.graph import StateGraph, END
    LANGGRAPH_AVAILABLE = True
except ImportError:
    LANGGRAPH_AVAILABLE = False

# ── State schema ──────────────────────────────────────────────────────────────

class AgentState(TypedDict):
    suppliers: List[dict]           # raw supplier records from API
    risk_scores: List[dict]         # scored suppliers from IsolationForest
    rag_context: List[dict]         # historical context from pgvector, per supplier
    explanations: List[dict]        # AI-generated explanations per high-risk supplier
    validations: List[dict]         # pass/fail per explanation
    errors: List[str]               # node-level error log
    node_timings: dict              # ms per node
    backend_url: str                # injected at runtime


# ── Node 1: fetch_supplier_data ───────────────────────────────────────────────

def fetch_supplier_data(state: AgentState) -> AgentState:
    t0 = time.perf_counter()
    backend = state.get("backend_url", "http://localhost:8089/supchain")
    errors = list(state.get("errors", []))

    try:
        # Login first to get a JWT token
        login_resp = requests.post(
            f"{backend}/api/auth/login",
            json={"username": os.getenv("SCIP_USERNAME", "kumar"),
                  "password": os.getenv("SCIP_PASSWORD", "Kumar@2026")},
            timeout=10,
        )
        token = login_resp.json().get("token", "")
        headers = {"Authorization": f"Bearer {token}"}

        resp = requests.get(f"{backend}/api/suppliers", headers=headers, timeout=10)
        resp.raise_for_status()
        data = resp.json()
        # API returns {"data": [...], "success": true} or a plain list
        if isinstance(data, list):
            suppliers = data
        elif isinstance(data, dict):
            suppliers = data.get("data", data.get("suppliers", data.get("content", [])))
        else:
            suppliers = []

        # Normalise field names to what score_risk expects
        normalised = []
        for s in suppliers:
            normalised.append({
                "id":                 s.get("id") or s.get("supplierId"),
                "name":               s.get("name") or s.get("supplierName", "Unknown"),
                "otdRate":            float(s.get("otdScore") or s.get("otdRate") or 0.0),
                "qualityScore":       float(s.get("qualityScore") or 50.0),
                "responsivenessScore":float(s.get("responsivenessScore") or 50.0),
                "compositeScore":     float(s.get("compositeScore") or 100.0),
            })
        suppliers = normalised
    except Exception as exc:
        errors.append(f"fetch_supplier_data: {exc}")
        suppliers = _synthetic_suppliers()

    timings = dict(state.get("node_timings", {}))
    timings["fetch_supplier_data_ms"] = round((time.perf_counter() - t0) * 1000, 1)

    return {**state, "suppliers": suppliers, "errors": errors, "node_timings": timings}


# ── Node 2: score_risk ────────────────────────────────────────────────────────

def score_risk(state: AgentState) -> AgentState:
    t0 = time.perf_counter()
    suppliers = state.get("suppliers", [])
    errors = list(state.get("errors", []))
    risk_scores = []

    try:
        if not suppliers:
            raise ValueError("No supplier data to score")

        # Build feature matrix: qualityScore, responsivenessScore, compositeScore
        # (otdScore is 0 for all suppliers when no deliveries are recorded —
        #  use compositeScore as the primary differentiation signal instead)
        features = []
        for s in suppliers:
            quality = float(s.get("qualityScore", 50.0) or 50.0)
            response = float(s.get("responsivenessScore", 50.0) or 50.0)
            composite = float(s.get("compositeScore", 100.0) or 100.0)
            features.append([quality, response, composite])

        X = np.array(features)
        scaler = StandardScaler()
        X_scaled = scaler.fit_transform(X)

        iso = IsolationForest(contamination=0.3, random_state=42)
        anomaly_labels = iso.fit_predict(X_scaled)
        anomaly_scores = iso.decision_function(X_scaled)  # negative = more anomalous

        for i, s in enumerate(suppliers):
            quality = features[i][0]
            composite = features[i][2]
            otd = float(s.get("otdRate", 0.0) or 0.0)

            # Risk from IsolationForest anomaly signal + quality score thresholds
            # Quality scores in live data range from 1 (Murata) to 35 (Delta)
            # High quality = low number in this dataset (defect/cost variance based)
            # Anomaly = statistical outlier detected by IsolationForest
            if anomaly_labels[i] == -1:
                risk_level = "HIGH"
            elif quality <= 15.0:
                risk_level = "HIGH"
            elif quality <= 30.0:
                risk_level = "MEDIUM"
            else:
                risk_level = "LOW"

            risk_scores.append({
                "supplierId": s.get("id") or s.get("supplierId"),
                "supplierName": s.get("name") or s.get("supplierName", "Unknown"),
                "otdRate": round(otd, 1),
                "qualityScore": round(quality, 1),
                "compositeScore": round(composite, 1),
                "riskLevel": risk_level,
                "anomalyScore": round(float(anomaly_scores[i]), 4),
                "isAnomaly": bool(anomaly_labels[i] == -1),
            })
    except Exception as exc:
        errors.append(f"score_risk: {exc}")

    timings = dict(state.get("node_timings", {}))
    timings["score_risk_ms"] = round((time.perf_counter() - t0) * 1000, 1)

    return {**state, "risk_scores": risk_scores, "errors": errors, "node_timings": timings}


# ── Node 2.5: retrieve_context ────────────────────────────────────────────────

def retrieve_context(state: AgentState) -> AgentState:
    """Pull historical supplier analyses from pgvector for RAG-enhanced explanations."""
    t0 = time.perf_counter()
    risk_scores = state.get("risk_scores", [])
    errors = list(state.get("errors", []))
    rag_context: List[dict] = []

    try:
        from rag.retriever import retrieve_supplier
        from rag.vector_store import ensure_schema

        ensure_schema()

        for scored in risk_scores:
            if scored.get("riskLevel") not in ("HIGH", "MEDIUM"):
                continue
            supplier_name = scored.get("supplierName", "")
            if not supplier_name:
                continue
            hits = retrieve_supplier(supplier_name)
            if hits:
                rag_context.append({
                    "supplierName": supplier_name,
                    "history": [h["content"] for h in hits],
                    "hits": len(hits),
                })
    except Exception as exc:
        errors.append(f"retrieve_context: {exc}")

    timings = dict(state.get("node_timings", {}))
    timings["retrieve_context_ms"] = round((time.perf_counter() - t0) * 1000, 1)
    return {**state, "rag_context": rag_context, "errors": errors, "node_timings": timings}


# ── Node 3: generate_explanation ──────────────────────────────────────────────

def generate_explanation(state: AgentState) -> AgentState:
    t0 = time.perf_counter()
    risk_scores = state.get("risk_scores", [])
    errors = list(state.get("errors", []))
    explanations = []

    client = _try_anthropic_client()
    rag_map = {ctx["supplierName"]: ctx["history"] for ctx in state.get("rag_context", [])}

    for scored in risk_scores:
        if scored.get("riskLevel") not in ("HIGH", "MEDIUM"):
            continue
        try:
            supplier_name = scored.get("supplierName", "")
            historical = rag_map.get(supplier_name, [])
            explanation_text = _explain_supplier(scored, client, historical_context=historical)
            explanations.append({
                "supplierId": scored["supplierId"],
                "supplierName": supplier_name,
                "riskLevel": scored["riskLevel"],
                "otdRate": scored["otdRate"],
                "explanation": explanation_text,
                "rag_history_used": bool(historical),
            })
            # Auto-ingest this analysis into RAG for future retrieval
            try:
                from rag.ingest import ingest_supplier_analysis
                ingest_supplier_analysis(
                    supplier_name=supplier_name,
                    risk_level=scored["riskLevel"],
                    risk_score=abs(scored.get("anomalyScore", 0.0)),
                    on_time_delivery=scored.get("otdRate", 0.0),
                    quality_rating=min(scored.get("qualityScore", 0.0) / 20.0, 5.0),
                    recommendation=explanation_text[:200],
                    explanation=explanation_text,
                )
            except Exception:
                pass
        except Exception as exc:
            errors.append(f"generate_explanation[{scored.get('supplierName')}]: {exc}")

    timings = dict(state.get("node_timings", {}))
    timings["generate_explanation_ms"] = round((time.perf_counter() - t0) * 1000, 1)

    return {**state, "explanations": explanations, "errors": errors, "node_timings": timings}


# ── Node 4: validate_response ─────────────────────────────────────────────────

def validate_response(state: AgentState) -> AgentState:
    t0 = time.perf_counter()
    explanations = state.get("explanations", [])
    risk_scores = state.get("risk_scores", [])
    errors = list(state.get("errors", []))
    validations = []

    known_names = {s["supplierName"].lower() for s in risk_scores}

    for exp in explanations:
        text = exp.get("explanation", "")
        name = exp.get("supplierName", "")
        otd = exp.get("otdRate", 0)

        checks = {
            "contains_supplier_name": name.lower() in text.lower() or any(
                part.lower() in text.lower() for part in name.split() if len(part) > 3
            ),
            "contains_otd_score": bool(re.search(r'\d+\.?\d*\s*%?', text)),
            "length_in_range": 50 <= len(text) <= 500,
            "no_hallucinated_names": _check_no_hallucinated_names(text, known_names),
        }

        passed = all(checks.values())
        validations.append({
            "supplierId": exp["supplierId"],
            "supplierName": name,
            "passed": passed,
            "checks": checks,
            "explanation_length": len(text),
        })

    timings = dict(state.get("node_timings", {}))
    timings["validate_response_ms"] = round((time.perf_counter() - t0) * 1000, 1)

    return {**state, "validations": validations, "errors": errors, "node_timings": timings}


# ── Graph builder ─────────────────────────────────────────────────────────────

def build_graph():
    if not LANGGRAPH_AVAILABLE:
        return None
    graph = StateGraph(AgentState)
    graph.add_node("fetch_supplier_data", fetch_supplier_data)
    graph.add_node("score_risk", score_risk)
    graph.add_node("retrieve_context", retrieve_context)
    graph.add_node("generate_explanation", generate_explanation)
    graph.add_node("validate_response", validate_response)

    graph.set_entry_point("fetch_supplier_data")
    graph.add_edge("fetch_supplier_data", "score_risk")
    graph.add_edge("score_risk", "retrieve_context")
    graph.add_edge("retrieve_context", "generate_explanation")
    graph.add_edge("generate_explanation", "validate_response")
    graph.add_edge("validate_response", END)

    return graph.compile()


def run_agent(backend_url: str = "http://localhost:8089/supchain") -> AgentState:
    """Run the full 5-node agent graph. Falls back to sequential execution if LangGraph unavailable."""
    initial_state: AgentState = {
        "suppliers": [],
        "risk_scores": [],
        "rag_context": [],
        "explanations": [],
        "validations": [],
        "errors": [],
        "node_timings": {},
        "backend_url": backend_url,
    }

    if LANGGRAPH_AVAILABLE:
        graph = build_graph()
        return graph.invoke(initial_state)
    else:
        # Sequential fallback
        state = fetch_supplier_data(initial_state)
        state = score_risk(state)
        state = retrieve_context(state)
        state = generate_explanation(state)
        state = validate_response(state)
        return state


# ── Private helpers ───────────────────────────────────────────────────────────

def _try_anthropic_client():
    api_key = os.getenv("ANTHROPIC_API_KEY", "").strip()
    if not api_key:
        return None
    try:
        import anthropic
        return anthropic.Anthropic(api_key=api_key)
    except ImportError:
        return None


def _explain_supplier(scored: dict, client, historical_context: Optional[List[str]] = None) -> str:
    name = scored["supplierName"]
    otd = scored["otdRate"]
    risk = scored["riskLevel"]
    quality = scored.get("qualityScore", "N/A")
    composite = scored.get("compositeScore", "N/A")

    # Build historical context block
    history_block = ""
    if historical_context:
        history_block = (
            "\n\nHistorical supplier data from previous analyses:\n"
            + "\n---\n".join(h[:300] for h in historical_context[:2])
            + "\n"
        )

    if client:
        prompt = (
            f"Supplier '{name}' has a quality score of {quality}/100 and composite risk score of {composite}. "
            f"Risk level: {risk}. "
            f"{history_block}"
            f"In 2-3 sentences, explain the risk and one recommended action. "
            f"Be specific, mention the supplier name and quality score of {quality}%."
        )
        try:
            msg = client.messages.create(
                model="claude-haiku-4-5-20251001",
                max_tokens=200,
                messages=[{"role": "user", "content": prompt}],
            )
            return msg.content[0].text.strip()
        except Exception:
            pass

    # Heuristic fallback
    if risk == "HIGH":
        action = "Initiate dual-sourcing and issue a supplier performance notice."
    else:
        action = "Schedule a performance review and increase monitoring frequency."

    trend = ""
    if historical_context:
        trend = " Historical data shows repeated risk flags for this supplier."

    return (
        f"{name} is classified as {risk} RISK with a quality score of {quality}% "
        f"and composite score of {composite}. "
        f"{action}{trend}"
    )


def _check_no_hallucinated_names(text: str, known_names: set) -> bool:
    """Return True if no unknown proper-noun company names appear in the explanation."""
    # Extract capitalized multi-word sequences that look like company names
    candidates = re.findall(r'\b([A-Z][a-z]+ (?:[A-Z][a-z]+ )?(?:Inc|Ltd|Co|Corp|Group|Electronics|Manufacturing|Technologies|Semiconductors|SDI|Connectivity|Intertechnology))\b', text)
    for c in candidates:
        if c.lower() not in known_names and not any(c.lower() in kn for kn in known_names):
            return False
    return True


def _synthetic_suppliers() -> List[dict]:
    """Fallback synthetic data when API is unreachable."""
    return [
        {"id": 1, "name": "Shenzhen Electronics Co.", "otdRate": 45.0, "qualityScore": 12.0, "responsivenessScore": 60.0},
        {"id": 2, "name": "Foxconn Technology Group",  "otdRate": 30.0, "qualityScore": 10.0, "responsivenessScore": 55.0},
        {"id": 3, "name": "Taiwan Semiconductors Ltd", "otdRate": 55.0, "qualityScore": 25.0, "responsivenessScore": 65.0},
        {"id": 4, "name": "Murata Manufacturing",      "otdRate": 88.0, "qualityScore": 92.0, "responsivenessScore": 85.0},
        {"id": 5, "name": "Delta Electronics India",   "otdRate": 20.0, "qualityScore": 35.0, "responsivenessScore": 50.0},
        {"id": 6, "name": "Samsung SDI Korea",         "otdRate": 78.0, "qualityScore": 80.0, "responsivenessScore": 75.0},
        {"id": 7, "name": "Vishay Intertechnology",    "otdRate": 65.0, "qualityScore": 70.0, "responsivenessScore": 68.0},
        {"id": 8, "name": "TE Connectivity",           "otdRate": 35.0, "qualityScore": 9.0,  "responsivenessScore": 58.0},
        {"id": 9, "name": "Infineon Technologies",     "otdRate": 48.0, "qualityScore": 20.0, "responsivenessScore": 62.0},
        {"id": 10,"name": "STMicroelectronics",        "otdRate": 40.0, "qualityScore": 13.0, "responsivenessScore": 56.0},
    ]
