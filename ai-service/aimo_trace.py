"""
AIMO SDK — @aimo_trace decorator

Drop-in wrapper for LangGraph nodes. Measures node duration, ships
telemetry to AIMO's /ingest/span endpoint as a fire-and-forget
background POST. Never blocks the pipeline — if AIMO is unreachable
the pipeline continues normally.

Usage:
    from sdk.aimo_trace import aimo_trace

    @aimo_trace("teach_socratically", pipeline="ARIA")
    def teach_socratically(state):
        ...

Environment:
    AIMO_ENDPOINT  — base URL of the AIMO AI engine (default: unset = passthrough)
                     e.g. http://localhost:8001

When AIMO_ENDPOINT is not set: zero overhead, decorator is transparent.
"""
from __future__ import annotations
import os
import time
import uuid
import logging
import functools
from typing import Callable

logger = logging.getLogger("aimo.sdk")

AIMO_ENDPOINT = os.getenv("AIMO_ENDPOINT", "")


def aimo_trace(node_name: str, pipeline: str = "unknown") -> Callable:
    """
    Decorator factory.

    Args:
        node_name: LangGraph node name (e.g. "teach_socratically")
        pipeline:  Pipeline identifier (e.g. "ARIA", "QAIP")
    """
    def decorator(fn: Callable) -> Callable:
        @functools.wraps(fn)
        def wrapper(state: dict) -> dict:
            run_id  = state.get("run_id") or str(uuid.uuid4())
            started = time.time()
            error   = None
            try:
                result = fn(state)
                return result
            except Exception as exc:
                error = str(exc)
                raise
            finally:
                latency_ms = int((time.time() - started) * 1000)
                if AIMO_ENDPOINT:
                    _ship_span_nowait(
                        pipeline_id=pipeline,
                        run_id=run_id,
                        node_name=node_name,
                        state=state,
                        latency_ms=latency_ms,
                        error=error,
                    )
        return wrapper
    return decorator


def _ship_span_nowait(
    pipeline_id: str,
    run_id: str,
    node_name: str,
    state: dict,
    latency_ms: int,
    error: str | None,
) -> None:
    """Fire-and-forget POST — uses a daemon thread so it never blocks."""
    import threading
    t = threading.Thread(
        target=_do_post,
        args=(pipeline_id, run_id, node_name, state, latency_ms, error),
        daemon=True,
    )
    t.start()


def _do_post(
    pipeline_id: str,
    run_id: str,
    node_name: str,
    state: dict,
    latency_ms: int,
    error: str | None,
) -> None:
    try:
        import httpx
        payload = {
            "pipeline_id":        pipeline_id,
            "pipeline_name":      pipeline_id,
            "run_id":             run_id,
            "node_name":          node_name,
            "model_id":           state.get("model_id"),
            "input_text":         str(state.get("student_input") or state.get("input_text", ""))[:5000],
            "output_text":        str(state.get("aria_response") or state.get("output_text", ""))[:5000],
            "context":            str(state.get("rag_context", ""))[:10000] or None,
            "prompt_tokens":      state.get("prompt_tokens", 0),
            "completion_tokens":  state.get("completion_tokens", 0),
            "cost_usd":           state.get("cost_usd", 0.0),
            "latency_ms":         latency_ms,
            "user_id":            state.get("student_id") or state.get("user_id"),
        }
        httpx.post(
            f"{AIMO_ENDPOINT}/ingest/span",
            json=payload,
            timeout=2.0,
        )
    except Exception as exc:
        logger.debug("aimo_trace: span ship failed (non-blocking) — %s", exc)
