"""
LangSmith observability layer for SCIP.

Works as a pure no-op when LANGCHAIN_API_KEY is not set.
Auto-enables when the key is present — no code changes needed.

To enable:
  LANGCHAIN_TRACING_V2=true
  LANGCHAIN_API_KEY=ls__...          (from smith.langchain.com)
  LANGCHAIN_PROJECT=SCIP-Production
"""

from __future__ import annotations

import functools
import logging
import os
import time
from typing import Any, Callable

logger = logging.getLogger("scip.langsmith")

_API_KEY = os.environ.get("LANGCHAIN_API_KEY", "")
_TRACING  = os.environ.get("LANGCHAIN_TRACING_V2", "").lower() == "true"
_PROJECT  = os.environ.get("LANGCHAIN_PROJECT", "SCIP-Production")
_ENABLED  = _TRACING and bool(_API_KEY)

_ls_client: Any = None

if _ENABLED:
    try:
        from langsmith import Client          # type: ignore
        _ls_client = Client(api_key=_API_KEY)
        logger.info("LangSmith tracing ACTIVE — project: %s", _PROJECT)
    except ImportError:
        logger.warning("langsmith not installed; run: pip install langsmith>=0.1.0")
        _ENABLED = False
else:
    logger.debug("LangSmith tracing disabled (set LANGCHAIN_TRACING_V2=true + LANGCHAIN_API_KEY)")


def trace_node(node_name: str, project: str | None = None):
    """
    Decorator that wraps a LangGraph node with a LangSmith trace span.

    When tracing is disabled (no API key), this is a zero-overhead passthrough —
    the original function is returned unchanged, so there is no performance impact.

    Usage:
        @trace_node("generate_tests")
        def generate_tests(state: AgentState) -> AgentState:
            ...
    """
    _proj = project or _PROJECT

    def decorator(func: Callable) -> Callable:
        if not _ENABLED or _ls_client is None:
            return func                                 # passthrough — zero cost

        @functools.wraps(func)
        def wrapper(state: dict) -> dict:
            metadata = {
                "node": node_name,
                "project_id": str(state.get("project_id", "")),
                "run_id": str(state.get("run_id", "")),
            }
            t0 = time.perf_counter()
            try:
                with _ls_client.trace(
                    name=node_name,
                    project_name=_proj,
                    metadata=metadata,
                ) as run:
                    result = func(state)
                    elapsed_ms = int((time.perf_counter() - t0) * 1000)
                    run.end(outputs={
                        "elapsed_ms": elapsed_ms,
                        "status": result.get("status", "ok"),
                        "error": result.get("error", ""),
                    })
                    return result
            except Exception as exc:
                logger.error("[LangSmith] trace failed for %s: %s", node_name, exc)
                return func(state)                      # fall back silently

        return wrapper
    return decorator


def trace_run(run_id: str, project_id: Any, node_count: int, cost_usd: float, deepeval_score: float | None = None):
    """
    Log one complete pipeline run as a LangSmith dataset entry.
    Called at the end of dispatch_results.
    """
    if not _ENABLED or _ls_client is None:
        return
    try:
        _ls_client.create_run(
            name=f"scip_run_{run_id}",
            run_type="chain",
            project_name=_PROJECT,
            inputs={"run_id": run_id, "project_id": str(project_id)},
            outputs={
                "node_count":     node_count,
                "cost_usd":       round(cost_usd, 6),
                "deepeval_score": deepeval_score,
            },
        )
    except Exception as exc:
        logger.debug("[LangSmith] trace_run failed: %s", exc)
