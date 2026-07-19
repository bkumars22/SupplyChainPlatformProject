#!/usr/bin/env python3
# Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
# Supply Chain Intelligence Platform — BCT Unified Runner
#
# Runs all 5 BCT modules (behavioral contract, prompt injection, data
# leakage, bias/fairness, multi-turn escalation) against a chosen target
# and produces ONE comprehensive report, replacing the original BCT
# Complete drop's unified_runner.py (which aggregated results but did not
# run anything itself, since every judge was a NotImplementedError stub).
#
# Usage:
#   python bct/runner.py                  # reference target, no API key needed
#   python bct/runner.py --target live    # real SCIP RAG assistant (needs GROQ_API_KEY)
#
# Exit codes:
#   0 — zero critical failures
#   1 — one or more critical failures found

import argparse
import dataclasses
import io
import json
import sys
import time
import uuid
from datetime import datetime, timezone
from enum import Enum
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))

from bct.judges import get_default_engine
from bct.system_adapters import get_target
from bct.behavioral_contract_testing import run_behavioral_contract_tests
from bct.injection_testing import run_injection_tests
from bct.data_leakage_testing import run_leakage_tests
from bct.bias_fairness_testing import run_bias_tests
from bct.multi_turn_testing import run_multi_turn_tests

RESULTS_DIR = Path(__file__).parent / "results"


def _green(s):  return f"\033[92m{s}\033[0m"
def _red(s):    return f"\033[91m{s}\033[0m"
def _yellow(s): return f"\033[93m{s}\033[0m"
def _bold(s):   return f"\033[1m{s}\033[0m"
def _cyan(s):   return f"\033[96m{s}\033[0m"


@dataclasses.dataclass
class ComprehensiveReport:
    target: str
    judge_mode: str
    module_reports: list
    bias_results: dict
    multi_turn_results: dict
    generated_at: str

    def total_critical_failures(self) -> int:
        n = sum(len(r.critical_failures) for r in self.module_reports)
        n += sum(
            1 for r in self.bias_results.values()
            if r["severity_if_biased"] == "critical" and r["consistency_analysis"]["bias_detected"]
        )
        n += sum(
            1 for r in self.multi_turn_results.values()
            if r["severity_if_broken"] == "critical" and not r["held_the_line"]
        )
        return n

    def overall_summary(self) -> str:
        lines = [f"=== BCT — Comprehensive AI System Test Report (target={self.target}, judge={self.judge_mode}) ===\n"]
        for report in self.module_reports:
            lines.append(report.summary())

        if self.bias_results:
            flagged = sum(1 for r in self.bias_results.values() if r["consistency_analysis"]["bias_detected"])
            lines.append(f"bias_fairness: {flagged}/{len(self.bias_results)} scenarios flagged for bias")

        if self.multi_turn_results:
            broken = sum(1 for r in self.multi_turn_results.values() if not r["held_the_line"])
            lines.append(f"multi_turn_escalation: {broken}/{len(self.multi_turn_results)} scenarios broke the rule under escalation")

        total = self.total_critical_failures()
        lines.append(f"\nTOTAL CRITICAL FAILURES ACROSS ALL MODULES: {total}")
        lines.append(
            "\nRecommendation: "
            + ("BLOCK deployment — critical failures found." if total > 0
               else "No critical failures found in this run. Continue monitoring in production.")
        )
        return "\n".join(lines)


def run_comprehensive_suite(target: str = "reference") -> ComprehensiveReport:
    engine = get_default_engine()
    adapters = get_target(target)
    assistant_fn = adapters["assistant"]

    module_reports = [
        run_behavioral_contract_tests(adapters, judge_engine=engine),
        run_injection_tests(assistant_fn, judge_engine=engine),
        run_leakage_tests(assistant_fn, judge_engine=engine),
    ]
    bias_results = run_bias_tests(assistant_fn, judge_engine=engine)
    multi_turn_results = run_multi_turn_tests(adapters["conversation"], judge_engine=engine)

    return ComprehensiveReport(
        target=target,
        judge_mode=engine.mode,
        module_reports=module_reports,
        bias_results=bias_results,
        multi_turn_results=multi_turn_results,
        generated_at=datetime.now(timezone.utc).isoformat() + "Z",
    )


def _score_colour(passed: int, total: int) -> str:
    rate = (passed / total) if total else 1.0
    text = f"{passed}/{total}"
    if rate == 1.0:  return _green(text)
    if rate >= 0.7:  return _yellow(text)
    return _red(text)


def _print_report(report: ComprehensiveReport, elapsed: float) -> None:
    print()
    print(_bold("=" * 78))
    print(_bold("  BCT — Behavioral Contract Testing Suite"))
    print(_bold("=" * 78))
    print(f"  Date       : {datetime.now(timezone.utc).strftime('%Y-%m-%d %H:%M:%S')} UTC")
    print(f"  Target     : {_cyan(report.target)}")
    print(f"  Judge mode : {_cyan(report.judge_mode)}")
    print(_bold("=" * 78))
    print()

    col = "{:<24}  {:>10}  {:>10}  {:>18}"
    print(_bold(col.format("Module", "Pass/Total", "Rate", "Critical Fails")))
    print("-" * 68)
    for r in report.module_reports:
        print(col.format(
            r.category.value,
            _score_colour(r.passed, r.total_tests),
            f"{r.pass_rate:.1f}%",
            _red(str(len(r.critical_failures))) if r.critical_failures else "0",
        ))

    bias_flagged = sum(1 for r in report.bias_results.values() if r["consistency_analysis"]["bias_detected"])
    print(col.format("bias_fairness", _score_colour(len(report.bias_results) - bias_flagged, len(report.bias_results)),
                      "", str(bias_flagged) + " flagged"))

    mt_held = sum(1 for r in report.multi_turn_results.values() if r["held_the_line"])
    print(col.format("multi_turn_escalation", _score_colour(mt_held, len(report.multi_turn_results)),
                      "", str(len(report.multi_turn_results) - mt_held) + " broke rule"))

    print("-" * 68)
    print()

    for r in report.module_reports:
        for result in r.results:
            if not result.passed:
                print(_red(f"  [FAIL] {result.test_case.id} ({result.test_case.severity_if_failed.value}) — {result.test_case.description}"))
                print(f"         {result.notes}")

    for tid, r in report.bias_results.items():
        if r["consistency_analysis"]["bias_detected"]:
            print(_yellow(f"  [BIAS] {tid} — {r['description']}"))
            print(f"         outcomes: {r['consistency_analysis']['outcomes']}")

    for tid, r in report.multi_turn_results.items():
        if not r["held_the_line"]:
            print(_red(f"  [BROKE] {tid} — {r['description']}"))
            print(f"          {r['evidence']}")

    print()
    print(_bold("=" * 78))
    total_critical = report.total_critical_failures()
    verdict = _green("PASS — no critical failures") if total_critical == 0 else _red(f"FAIL — {total_critical} critical failure(s)")
    print(f"  Verdict    : {_bold(verdict)}")
    print(f"  Wall time  : {elapsed:.2f}s")
    print(_bold("=" * 78))
    print()


class _EnumSafeEncoder(json.JSONEncoder):
    def default(self, o):
        if isinstance(o, Enum):
            return o.value
        if dataclasses.is_dataclass(o):
            return dataclasses.asdict(o)
        return super().default(o)


def _persist(report: ComprehensiveReport) -> Path:
    RESULTS_DIR.mkdir(parents=True, exist_ok=True)
    run_id = str(uuid.uuid4())[:8]
    ts = datetime.now(timezone.utc).strftime("%Y%m%d_%H%M%S")

    payload = {
        "run_id": run_id,
        "target": report.target,
        "judge_mode": report.judge_mode,
        "generated_at": report.generated_at,
        "module_reports": [dataclasses.asdict(r) for r in report.module_reports],
        "bias_results": report.bias_results,
        "multi_turn_results": report.multi_turn_results,
        "total_critical_failures": report.total_critical_failures(),
        "summary": report.overall_summary(),
    }

    timestamped = RESULTS_DIR / f"bct_{ts}_{run_id}.json"
    canonical = RESULTS_DIR / "bct_results.json"
    for path in (timestamped, canonical):
        with open(path, "w", encoding="utf-8") as f:
            json.dump(payload, f, indent=2, cls=_EnumSafeEncoder)
    return timestamped


def main(argv=None) -> int:
    parser = argparse.ArgumentParser(description="Run the BCT AI red-teaming suite.")
    parser.add_argument("--target", choices=["reference", "live"], default="reference",
                         help="reference = offline rule-based guard (default, no API key); "
                              "live = real SCIP RAG assistant via Groq (needs GROQ_API_KEY)")
    args = parser.parse_args(argv)

    t0 = time.perf_counter()
    report = run_comprehensive_suite(args.target)
    elapsed = time.perf_counter() - t0

    _print_report(report, elapsed)
    results_file = _persist(report)
    print(f"  Results file : {results_file.name}\n")

    return 0 if report.total_critical_failures() == 0 else 1


if __name__ == "__main__":
    # UTF-8 stdout so the box-drawing/ANSI output renders correctly on
    # Windows terminals — scoped to CLI use only, never applied when
    # this module is imported (e.g. by main.py's /bct/run endpoint),
    # since reassigning the process's global stdout inside a running
    # server is unsafe.
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8", errors="replace")
    sys.exit(main())
