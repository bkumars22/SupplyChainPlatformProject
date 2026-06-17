#!/usr/bin/env python3
# Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
# Supply Chain Intelligence Platform — LLM Eval Runner
#
# Usage:
#   python eval/eval_runner.py
#
# Exit codes:
#   0 — average overall score > 0.7  (CI pass)
#   1 — average overall score <= 0.7 (CI fail)

import io
import json
import sys
import time
import uuid
from datetime import datetime, timezone
from pathlib import Path

# Force UTF-8 output on Windows so block/box characters render correctly.
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8", errors="replace")

# Allow running as `python eval/eval_runner.py` from ai-service/
sys.path.insert(0, str(Path(__file__).resolve().parent.parent))

from eval.prompt_evaluator import LLMEvaluator
from eval.test_cases import SUPPLY_CHAIN_TEST_CASES

RESULTS_DIR = Path(__file__).parent / "results"
PASS_THRESHOLD = 0.70

# ── ANSI colour helpers ───────────────────────────────────────────────────────
def _green(s):  return f"\033[92m{s}\033[0m"
def _red(s):    return f"\033[91m{s}\033[0m"
def _yellow(s): return f"\033[93m{s}\033[0m"
def _bold(s):   return f"\033[1m{s}\033[0m"
def _cyan(s):   return f"\033[96m{s}\033[0m"


def _score_colour(v: float) -> str:
    if v >= 0.80:  return _green(f"{v:.3f}")
    if v >= 0.70:  return _yellow(f"{v:.3f}")
    return _red(f"{v:.3f}")


def run_evaluation() -> int:
    """Run all 20 test cases and return exit code (0=pass, 1=fail)."""
    RESULTS_DIR.mkdir(parents=True, exist_ok=True)

    evaluator = LLMEvaluator()

    print()
    print(_bold("=" * 72))
    print(_bold("  SCIP LLM Evaluation Harness — Module 1"))
    print(_bold("=" * 72))
    print(f"  Date     : {datetime.now(timezone.utc).strftime('%Y-%m-%d %H:%M:%S')} UTC")
    print(f"  Mode     : {_cyan(evaluator._mode)}")
    print(f"  Cases    : {len(SUPPLY_CHAIN_TEST_CASES)}")
    print(f"  Threshold: {PASS_THRESHOLD}")
    print(_bold("=" * 72))
    print()

    # Header row
    col = "{:<8}  {:<20}  {:>10}  {:>10}  {:>12}  {:>12}  {:>10}"
    print(_bold(col.format("ID", "Category", "Relevancy", "Hallucin.", "Consistency", "Overall", "Latency ms")))
    print("-" * 92)

    results = []
    t_total = time.perf_counter()

    for tc in SUPPLY_CHAIN_TEST_CASES:
        scores = evaluator.evaluate_single(
            prompt=tc["input"],
            response=tc["expected_response"],
            expected_keywords=tc.get("expected_keywords", []),
        )
        results.append({
            "test_case_id": tc["id"],
            "category":     tc["category"],
            "input":        tc["input"],
            "scores":       scores,
        })
        print(col.format(
            tc["id"],
            tc["category"][:20],
            _score_colour(scores["answer_relevancy"]),
            _score_colour(1 - scores["hallucination_score"]),   # invert: higher=better
            _score_colour(scores["consistency_score"]),
            _score_colour(scores["overall_score"]),
            f"{scores['response_latency_ms']:.1f}",
        ))

    elapsed = time.perf_counter() - t_total

    # Generate report
    report = evaluator.generate_report(results)

    print("-" * 92)
    print(_bold(col.format(
        "AVG",
        "",
        _score_colour(report["avg_answer_relevancy"]),
        _score_colour(1 - report["avg_hallucination"]),
        _score_colour(report["avg_consistency"]),
        _score_colour(report["avg_overall_score"]),
        f"{report['avg_latency_ms']:.1f}",
    )))
    print()

    # Category breakdown
    print(_bold("  Category Breakdown"))
    print("  " + "-" * 48)
    for cat, avg in sorted(report["by_category"].items()):
        bar_len = int(avg * 30)
        bar = "█" * bar_len + "░" * (30 - bar_len)
        print(f"  {cat:<22}  {_score_colour(avg)}  {bar}")
    print()

    # Persist timestamped copy alongside the canonical eval_results.json
    run_id = str(uuid.uuid4())[:8]
    ts = datetime.now(timezone.utc).strftime("%Y%m%d_%H%M%S")
    timestamped_file = RESULTS_DIR / f"eval_{ts}_{run_id}.json"
    payload = {
        "run_id":       run_id,
        "generated_at": datetime.now(timezone.utc).isoformat() + "Z",
        "mode":         evaluator._mode,
        "report":       report,
        "results":      results,
    }
    with open(timestamped_file, "w", encoding="utf-8") as f:
        json.dump(payload, f, indent=2)

    # Also overwrite the canonical "latest" file
    canonical = RESULTS_DIR / "eval_results.json"
    with open(canonical, "w", encoding="utf-8") as f:
        json.dump(payload, f, indent=2)

    # Final verdict
    passed = report["avg_overall_score"] > PASS_THRESHOLD
    verdict = _green("✓  PASS") if passed else _red("✗  FAIL")
    print(_bold("=" * 72))
    print(f"  Verdict      : {_bold(verdict)}")
    print(f"  Avg Score    : {_score_colour(report['avg_overall_score'])}  (threshold: {PASS_THRESHOLD})")
    print(f"  Wall time    : {elapsed:.2f}s")
    print(f"  Results file : {timestamped_file.name}")
    print(_bold("=" * 72))
    print()

    return 0 if passed else 1


if __name__ == "__main__":
    sys.exit(run_evaluation())
