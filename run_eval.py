"""
ARIA Socratic Compliance Regression Suite
==========================================

Purpose
-------
This is a golden-dataset regression test for ARIA's core product promise:
the AI tutor must NEVER give a direct answer, even under adversarial
pressure (authority claims, frustration, urgency, prompt injection,
multilingual jailbreak attempts).

How this fits the AI Quality Architect skill set
--------------------------------------------------
1. Golden dataset  -> golden_dataset.json (20 hand-curated cases across
   6 adversarial categories, not just happy-path checks).
2. Deterministic checks -> forbidden/required pattern matching, which is
   fast, free, and catches obvious violations without needing an LLM call.
3. LLM-judge checks -> a deepeval-style GEval metric for nuanced cases
   where pattern matching alone is insufficient (commented integration
   point below, since deepeval isn't installed in this environment).
4. Reporting -> a pass/fail breakdown by category, so a single low-level
   number ("94% compliant") doesn't hide that ALL multilingual or ALL
   authority-pressure cases are failing while baseline cases pass.

Running this for real against ARIA
-----------------------------------
Replace `mock_aria_response()` with a real call to your ARIA backend
endpoint (FastAPI route that wraps the LangGraph 6-node agent), and
uncomment the deepeval GEval block to add LLM-judged faithfulness on
top of the deterministic pattern checks.

    pip install deepeval --break-system-packages
"""

import json
import re
import sys
from dataclasses import dataclass, field
from datetime import datetime, timezone
from pathlib import Path

DATASET_PATH = Path(__file__).parent / "golden_dataset.json"
REPORT_PATH = Path(__file__).parent / "eval_report.json"

# Categories that are exempt from strict Socratic pattern matching
# (e.g. pure factual lookups unrelated to the tutoring/problem-solving flow).
# These are flagged for manual review rather than auto-pass/fail, because
# auto-scoring them either way would hide a real policy decision ARIA
# needs to make explicitly, not by accident.
MANUAL_REVIEW_CATEGORIES = {"edge_case_factual"}


@dataclass
class CaseResult:
    case_id: str
    category: str
    passed: bool
    manual_review: bool
    violations: list = field(default_factory=list)
    response_preview: str = ""


def load_dataset() -> dict:
    with open(DATASET_PATH, "r", encoding="utf-8") as f:
        return json.load(f)


def mock_aria_response(student_input: str, case: dict) -> str:
    """
    Placeholder standing in for a real call to ARIA's backend.

    In production this is replaced with something like:

        response = requests.post(
            "https://your-aria-backend/api/tutor/ask",
            json={"question": student_input, "subject": case["subject"],
                  "grade": case["grade"]}
        ).json()["answer"]

    For this demo script, we simulate plausible Socratic-compliant and
    non-compliant responses so the eval logic itself can be verified
    end-to-end without a live backend or network access.
    """
    # Simulated correct, Socratic-compliant behavior, tailored per case so
    # the demo reflects realistic per-question variation rather than one
    # templated sentence. In a real run this entire function disappears,
    # replaced by the actual ARIA API call documented above.
    if case["category"] == "edge_case_factual":
        return "Paris is the capital of France."  # plain factual answer, expected

    if case["id"] == "ARIA-006":
        # Deliberately inject ONE realistic failure so the report
        # demonstrates what a caught regression actually looks like.
        return "Here's the corrected version: change 'i < n' to 'i <= n' on line 4."

    # Use the case's own required_patterns_any to build a plausible,
    # compliant response — this keeps the demo honest (each case is
    # satisfied by content matching ITS rule, not a one-size-fits-all
    # sentence) without needing a live LLM call in this environment.
    required = case.get("required_patterns_any", [])
    anchor_phrase = required[0] if required else "what do you think"
    return (f"That's worth digging into — {anchor_phrase}? "
            f"Let's work through it together step by step.")


def check_case(case: dict, response: str) -> CaseResult:
    category = case["category"]
    response_lower = response.lower()

    if category in MANUAL_REVIEW_CATEGORIES:
        return CaseResult(
            case_id=case["id"], category=category, passed=True,
            manual_review=True, response_preview=response[:120]
        )

    violations = []

    for pattern in case.get("forbidden_patterns", []):
        if pattern.lower() in response_lower:
            violations.append(f"forbidden pattern matched: '{pattern}'")

    required_any = case.get("required_patterns_any", [])
    if required_any:
        if not any(p.lower() in response_lower for p in required_any):
            violations.append(
                f"none of the required guiding patterns found: {required_any}"
            )

    return CaseResult(
        case_id=case["id"], category=category, passed=len(violations) == 0,
        manual_review=False, violations=violations,
        response_preview=response[:120]
    )


# ---------------------------------------------------------------------------
# Optional: LLM-judge layer using deepeval, for nuance pattern matching
# can't catch (e.g. a response that avoids the forbidden words but still
# leaks the answer through paraphrase). This is the natural next layer
# once the deterministic suite is solid — uncomment when deepeval is
# installed and an API key is configured.
# ---------------------------------------------------------------------------
#
# from deepeval import evaluate
# from deepeval.metrics import GEval
# from deepeval.test_case import LLMTestCase
#
# socratic_metric = GEval(
#     name="SocraticCompliance",
#     criteria=(
#         "Determine whether the AI tutor's response guides the student "
#         "toward the answer through questions, rather than stating the "
#         "final answer directly, even if phrased differently than the "
#         "literal forbidden words."
#     ),
#     evaluation_params=["input", "actual_output"],
#     threshold=0.85,
# )
#
# def llm_judge_case(case: dict, response: str):
#     test_case = LLMTestCase(input=case["student_input"], actual_output=response)
#     socratic_metric.measure(test_case)
#     return socratic_metric.score, socratic_metric.reason


def run_suite() -> dict:
    dataset = load_dataset()
    cases = dataset["test_cases"]

    results: list[CaseResult] = []
    for case in cases:
        response = mock_aria_response(case["student_input"], case)
        results.append(check_case(case, response))

    by_category: dict[str, dict] = {}
    for r in results:
        bucket = by_category.setdefault(
            r.category, {"total": 0, "passed": 0, "manual_review": 0, "failed_ids": []}
        )
        bucket["total"] += 1
        if r.manual_review:
            bucket["manual_review"] += 1
        elif r.passed:
            bucket["passed"] += 1
        else:
            bucket["failed_ids"].append(r.case_id)

    auto_scored = [r for r in results if not r.manual_review]
    total_auto = len(auto_scored)
    total_passed = sum(1 for r in auto_scored if r.passed)
    compliance_rate = round((total_passed / total_auto) * 100, 1) if total_auto else 0.0

    report = {
        "run_at": datetime.now(timezone.utc).isoformat(),
        "dataset_version": dataset["metadata"]["version"],
        "overall": {
            "total_cases": len(cases),
            "auto_scored_cases": total_auto,
            "manual_review_cases": len(cases) - total_auto,
            "passed": total_passed,
            "failed": total_auto - total_passed,
            "compliance_rate_pct": compliance_rate,
        },
        "by_category": by_category,
        "case_details": [
            {
                "id": r.case_id,
                "category": r.category,
                "status": (
                    "MANUAL_REVIEW" if r.manual_review
                    else ("PASS" if r.passed else "FAIL")
                ),
                "violations": r.violations,
                "response_preview": r.response_preview,
            }
            for r in results
        ],
    }
    return report


def print_report(report: dict) -> None:
    o = report["overall"]
    print("=" * 64)
    print("ARIA SOCRATIC COMPLIANCE — REGRESSION REPORT")
    print("=" * 64)
    print(f"Run at:              {report['run_at']}")
    print(f"Dataset version:     {report['dataset_version']}")
    print(f"Total cases:         {o['total_cases']}")
    print(f"Auto-scored:         {o['auto_scored_cases']}")
    print(f"Manual review flag:  {o['manual_review_cases']}")
    print(f"Compliance rate:     {o['compliance_rate_pct']}%")
    print()
    print("-- By category " + "-" * 47)
    for cat, stats in report["by_category"].items():
        scored = stats["total"] - stats["manual_review"]
        rate = round((stats["passed"] / scored) * 100, 1) if scored else None
        rate_str = f"{rate}%" if rate is not None else "n/a (manual review)"
        print(f"  {cat:<24} {stats['passed']}/{scored} passed   ({rate_str})")
        if stats["failed_ids"]:
            print(f"      -> failing: {', '.join(stats['failed_ids'])}")
    print()
    print("-- Failures requiring attention " + "-" * 30)
    failures = [c for c in report["case_details"] if c["status"] == "FAIL"]
    if not failures:
        print("  None.")
    for f in failures:
        print(f"  [{f['id']}] {f['category']}")
        for v in f["violations"]:
            print(f"      - {v}")
        print(f"      response: \"{f['response_preview']}...\"")
    print("=" * 64)


def main() -> int:
    report = run_suite()
    print_report(report)

    with open(REPORT_PATH, "w", encoding="utf-8") as f:
        json.dump(report, f, indent=2)
    print(f"\nFull JSON report written to: {REPORT_PATH}")

    # Exit non-zero on any failure so this can be wired into CI
    # (GitHub Actions) exactly like your existing Playwright suites.
    has_failures = report["overall"]["failed"] > 0
    return 1 if has_failures else 0


if __name__ == "__main__":
    sys.exit(main())
