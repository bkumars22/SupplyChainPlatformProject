"""SCIP RAG retriever.

retrieve_for_query(question) — main entry point for NL supplier queries.
  Used by the explain_risk agent node to pull supplier context before
  generating the AI analysis.

format_supplier_context(retrieved) — format results for LLM prompt injection.
"""

from __future__ import annotations

import logging
from typing import Any

from .embedder import embed
from .vector_store import search

logger = logging.getLogger("rag.retriever")


def retrieve_for_query(
    question: str,
    top_k: int = 5,
    source_type: str | None = None,
) -> list[dict[str, Any]]:
    """Retrieve the most relevant SCIP documents for a natural-language query."""
    try:
        q_embed = embed(question)
        return search(
            query_embedding=q_embed,
            project_id="SCIP",
            top_k=top_k,
            source_type=source_type,
            min_similarity=0.2,
        )
    except Exception as exc:
        logger.warning("retrieve_for_query failed: %s", exc)
        return []


def retrieve_supplier(supplier_name: str) -> list[dict[str, Any]]:
    """Retrieve all stored analyses for a specific supplier."""
    query_text = f"supplier risk analysis: {supplier_name}"
    try:
        q_embed = embed(query_text)
        return search(
            query_embedding=q_embed,
            project_id="SCIP",
            top_k=3,
            source_type="supplier_analysis",
            min_similarity=0.3,
        )
    except Exception as exc:
        logger.warning("retrieve_supplier(%s) failed: %s", supplier_name, exc)
        return []


def format_supplier_context(retrieved: list[dict[str, Any]]) -> str:
    """Format retrieved docs as a context block for LLM prompts."""
    if not retrieved:
        return ""
    lines = ["--- HISTORICAL SUPPLIER DATA ---"]
    for i, doc in enumerate(retrieved, 1):
        sim = doc.get("similarity", 0)
        lines.append(f"\n[{i}] (similarity: {sim:.2f})\n{doc['content']}")
    lines.append("--- END DATA ---")
    return "\n".join(lines)
