# Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
# Supply Chain Intelligence Platform — proof that RAG context helps
"""
Statistical proof that RAG historical context measurably improves validated
explanation quality in SCIP's supplier-risk agent, using the exact
explanation + validation logic the live agent runs (supplier_agent.py's
_explain_supplier and validate_response).

Compares validate_response pass/fail (0/1) per HIGH/MEDIUM synthetic
supplier, generated once with rag_context forced empty ("before") and once
with real retrieved rag_context ("after").

Honest limitations:
- Sample size is at most the ~8 non-LOW synthetic suppliers — enough to
  demonstrate the mechanism, not enough to be a rigorous production claim.
- Without a DATABASE_URL pointing at a Postgres instance with real ingested
  supplier history, retrieve_supplier() returns nothing and "after" will
  equal "before" (p-value ~1, no effect) — that's the correct, honest
  result when there's no real historical context to retrieve.
- Uses _explain_supplier() directly rather than the generate_explanation
  graph node, so this run does NOT auto-ingest its own output back into
  the RAG store (the node does that as a side effect; a proof script
  shouldn't pollute the real historical data it's trying to measure).

Run: python significance/prove_rag_improvement.py
"""

from __future__ import annotations

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))

import numpy as np

from agents.supplier_agent import (
    _explain_supplier,
    _synthetic_suppliers,
    _try_anthropic_client,
    score_risk,
    validate_response,
)
from significance.statistical_proof import prove_improvement


def _run_condition(risk_scores: list[dict], reviewed: list[dict], rag_map: dict[str, list[str]]) -> dict[str, bool]:
    client = _try_anthropic_client()
    explanations = []
    for scored in reviewed:
        historical = rag_map.get(scored["supplierName"], [])
        text = _explain_supplier(scored, client, historical_context=historical)
        explanations.append({
            "supplierId": scored["supplierId"],
            "supplierName": scored["supplierName"],
            "riskLevel": scored["riskLevel"],
            "otdRate": scored["otdRate"],
            "explanation": text,
        })
    state = validate_response({
        "explanations": explanations,
        "risk_scores": risk_scores,
        "errors": [],
        "node_timings": {},
    })
    return {v["supplierName"]: bool(v["passed"]) for v in state["validations"]}


def main() -> None:
    scored_state = score_risk({"suppliers": _synthetic_suppliers(), "errors": [], "node_timings": {}})
    risk_scores = scored_state["risk_scores"]
    reviewed = [s for s in risk_scores if s["riskLevel"] in ("HIGH", "MEDIUM")]

    if len(reviewed) < 2:
        print("Fewer than 2 HIGH/MEDIUM suppliers in the synthetic set — nothing to compare.")
        return

    rag_map: dict[str, list[str]] = {}
    try:
        from rag.retriever import retrieve_supplier
        for s in reviewed:
            hits = retrieve_supplier(s["supplierName"])
            if hits:
                rag_map[s["supplierName"]] = [h["content"] for h in hits]
    except Exception as exc:
        print(f"RAG retrieval unavailable ({exc}) — 'after' will match 'before'.\n")

    before = _run_condition(risk_scores, reviewed, rag_map={})
    after = _run_condition(risk_scores, reviewed, rag_map=rag_map)

    before_scores = np.array([1.0 if before.get(s["supplierName"]) else 0.0 for s in reviewed])
    after_scores = np.array([1.0 if after.get(s["supplierName"]) else 0.0 for s in reviewed])

    print(f"Sample size: {len(reviewed)} suppliers (HIGH/MEDIUM only)")
    print(f"Suppliers with retrieved history: {len(rag_map)}/{len(reviewed)}")
    print(f"Before (no RAG context) pass rate : {before_scores.mean():.0%}")
    print(f"After  (with RAG context) pass rate: {after_scores.mean():.0%}\n")

    if np.array_equal(before_scores, after_scores):
        print("Before and after are identical — no retrieved history changed any outcome, "
              "so there's no effect to test yet. Ingest real supplier analyses via "
              "/scip/rag/ingest-disruption or a live agent run, then re-run this script.")
        return

    proof = prove_improvement(before_scores, after_scores)
    print(proof.summary())


if __name__ == "__main__":
    main()
