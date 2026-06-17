# Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
# Supply Chain Intelligence Platform — Module 2: Agent Test Runner
import sys, io
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8', errors='replace')
sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8', errors='replace')
"""
Runs the LangGraph supplier risk agent across 10 test scenarios and
measures reliability, consistency, hallucination rate, and latency.

Usage:
    python agents/agent_test_runner.py
    python agents/agent_test_runner.py --backend http://localhost:8089/supchain
"""
import argparse
import json
import statistics
import time
import uuid
from datetime import datetime, timezone
from pathlib import Path

from supplier_agent import run_agent, _synthetic_suppliers

RESULTS_DIR = Path(__file__).parent / "results"
RESULTS_FILE = RESULTS_DIR / "agent_test_results.json"

# ── 10 test scenarios (5 HIGH, 3 MEDIUM, 2 LOW) ──────────────────────────────

TEST_SCENARIOS = [
    # HIGH RISK — OTD < 70
    {"scenario_id": "S-01", "risk_band": "HIGH",   "supplier_name": "Delta Electronics India",   "expected_otd": 20.0},
    {"scenario_id": "S-02", "risk_band": "HIGH",   "supplier_name": "Foxconn Technology Group",  "expected_otd": 30.0},
    {"scenario_id": "S-03", "risk_band": "HIGH",   "supplier_name": "TE Connectivity",           "expected_otd": 35.0},
    {"scenario_id": "S-04", "risk_band": "HIGH",   "supplier_name": "STMicroelectronics",        "expected_otd": 40.0},
    {"scenario_id": "S-05", "risk_band": "HIGH",   "supplier_name": "Shenzhen Electronics Co.",  "expected_otd": 45.0},
    # MEDIUM RISK — OTD 70-85
    {"scenario_id": "S-06", "risk_band": "MEDIUM", "supplier_name": "Vishay Intertechnology",    "expected_otd": 65.0},
    {"scenario_id": "S-07", "risk_band": "MEDIUM", "supplier_name": "Samsung SDI Korea",         "expected_otd": 78.0},
    {"scenario_id": "S-08", "risk_band": "MEDIUM", "supplier_name": "Taiwan Semiconductors Ltd", "expected_otd": 55.0},
    # LOW RISK — OTD > 85
    {"scenario_id": "S-09", "risk_band": "LOW",    "supplier_name": "Murata Manufacturing",      "expected_otd": 88.0},
    {"scenario_id": "S-10", "risk_band": "LOW",    "supplier_name": "Infineon Technologies",     "expected_otd": 91.0},
]

NODES = ["fetch_supplier_data", "score_risk", "generate_explanation", "validate_response"]


def run_single(backend_url: str) -> dict:
    """Run the full agent once and return a structured result."""
    t0 = time.perf_counter()
    state = run_agent(backend_url)
    total_ms = round((time.perf_counter() - t0) * 1000, 1)

    # Determine which nodes succeeded
    node_success = {
        "fetch_supplier_data": len(state.get("suppliers", [])) > 0,
        "score_risk":          len(state.get("risk_scores", [])) > 0,
        "generate_explanation":len(state.get("explanations", [])) > 0,
        "validate_response":   len(state.get("validations", [])) > 0,
    }
    all_nodes_passed = all(node_success.values())

    # Validation pass rate
    validations = state.get("validations", [])
    val_pass_rate = (
        sum(1 for v in validations if v.get("passed")) / len(validations)
        if validations else 0.0
    )

    # Hallucination rate: explanations where no_hallucinated_names check failed
    hall_failures = sum(
        1 for v in validations
        if not v.get("checks", {}).get("no_hallucinated_names", True)
    )
    hallucination_rate = hall_failures / len(validations) if validations else 0.0

    return {
        "run_id": str(uuid.uuid4())[:8],
        "all_nodes_passed": all_nodes_passed,
        "node_success": node_success,
        "supplier_count": len(state.get("suppliers", [])),
        "explanations_generated": len(state.get("explanations", [])),
        "validations_passed": sum(1 for v in validations if v.get("passed")),
        "validations_total": len(validations),
        "val_pass_rate": round(val_pass_rate, 4),
        "hallucination_rate": round(hallucination_rate, 4),
        "latency_ms": total_ms,
        "node_timings": state.get("node_timings", {}),
        "errors": state.get("errors", []),
        "raw_validations": validations,
    }


def measure_consistency(backend_url: str, runs: int = 3) -> float:
    """Run the agent `runs` times on the same input and measure explanation variance."""
    explanation_sets = []
    for _ in range(runs):
        state = run_agent(backend_url)
        exps = {e["supplierName"]: e["explanation"] for e in state.get("explanations", [])}
        explanation_sets.append(exps)

    if len(explanation_sets) < 2:
        return 1.0

    # Jaccard similarity between explanation token sets across runs
    all_names = set()
    for es in explanation_sets:
        all_names.update(es.keys())

    jaccard_scores = []
    for name in all_names:
        texts = [es.get(name, "") for es in explanation_sets]
        for i in range(len(texts)):
            for j in range(i + 1, len(texts)):
                a_tokens = set(texts[i].lower().split())
                b_tokens = set(texts[j].lower().split())
                if a_tokens or b_tokens:
                    jac = len(a_tokens & b_tokens) / len(a_tokens | b_tokens)
                    jaccard_scores.append(jac)

    return round(statistics.mean(jaccard_scores), 4) if jaccard_scores else 1.0


def check_scenario_coverage(state_from_run) -> list:
    """Cross-check which test scenarios were found and correctly risk-classified."""
    if isinstance(state_from_run, dict):
        risk_scores = state_from_run.get("risk_scores", [])
    else:
        risk_scores = []
    scored_map = {s["supplierName"]: s for s in risk_scores}

    scenario_results = []
    for sc in TEST_SCENARIOS:
        name = sc["supplier_name"]
        found = name in scored_map
        if found:
            actual_risk = scored_map[name]["riskLevel"]
            expected_risk = sc["risk_band"]
            correct = actual_risk == expected_risk
        else:
            actual_risk = None
            correct = False

        scenario_results.append({
            "scenario_id": sc["scenario_id"],
            "supplier_name": name,
            "risk_band_expected": sc["risk_band"],
            "risk_band_actual": actual_risk,
            "found_in_data": found,
            "correctly_classified": correct,
        })
    return scenario_results


def run_suite(backend_url: str) -> dict:
    print(f"\n{'='*60}")
    print("  Supply Chain Intelligence Platform")
    print("  Module 2 — LangGraph Agent Test Suite")
    print(f"{'='*60}")
    print(f"  Backend : {backend_url}")
    print(f"  Scenarios: {len(TEST_SCENARIOS)} (5 HIGH / 3 MEDIUM / 2 LOW)")
    print(f"{'='*60}\n")

    # ── Single full run for scenario coverage ─────────────────────────────────
    print("[1/3] Running agent (full pass for scenario analysis)...")
    t_full = time.perf_counter()
    full_state = run_agent(backend_url)
    full_ms = round((time.perf_counter() - t_full) * 1000, 1)
    print(f"      Suppliers fetched  : {len(full_state.get('suppliers', []))}")
    print(f"      Risk scores        : {len(full_state.get('risk_scores', []))}")
    print(f"      Explanations       : {len(full_state.get('explanations', []))}")
    print(f"      Validations        : {len(full_state.get('validations', []))}")
    if full_state.get("errors"):
        for e in full_state["errors"]:
            print(f"      [WARN] {e}")

    scenario_coverage = check_scenario_coverage(full_state)

    # ── 3-run reliability measurement ─────────────────────────────────────────
    print("\n[2/3] Measuring reliability across 3 runs...")
    run_results = []
    for i in range(3):
        r = run_single(backend_url)
        run_results.append(r)
        status = "✓" if r["all_nodes_passed"] else "✗"
        print(f"      Run {i+1}: {status}  {r['latency_ms']}ms  "
              f"validations {r['validations_passed']}/{r['validations_total']}")

    agent_reliability_score = round(
        sum(1 for r in run_results if r["all_nodes_passed"]) / len(run_results), 4
    )

    # ── Consistency across 3 runs ─────────────────────────────────────────────
    print("\n[3/3] Measuring explanation consistency (3 runs)...")
    response_consistency_score = measure_consistency(backend_url, runs=3)
    print(f"      Consistency score  : {response_consistency_score:.4f}")

    # ── Aggregate metrics ─────────────────────────────────────────────────────
    avg_latency    = round(statistics.mean(r["latency_ms"]          for r in run_results), 1)
    hall_rate      = round(statistics.mean(r["hallucination_rate"]   for r in run_results), 4)
    val_pass_rate  = round(statistics.mean(r["val_pass_rate"]        for r in run_results), 4)

    node_pass_counts = {node: 0 for node in NODES}
    for r in run_results:
        for node in NODES:
            if r["node_success"].get(node):
                node_pass_counts[node] += 1
    node_success_rate = {node: round(count / len(run_results), 4)
                         for node, count in node_pass_counts.items()}

    correctly_classified = sum(1 for s in scenario_coverage if s["correctly_classified"])
    scenario_accuracy = round(correctly_classified / len(TEST_SCENARIOS), 4)

    report = {
        "run_id": str(uuid.uuid4()),
        "generated_at": datetime.now(timezone.utc).isoformat() + "Z",
        "backend_url": backend_url,
        "module": "Module 2 — LangGraph Agent Test Framework",
        "summary": {
            "agent_reliability_score":    agent_reliability_score,
            "response_consistency_score": response_consistency_score,
            "hallucination_rate":         hall_rate,
            "validation_pass_rate":       val_pass_rate,
            "avg_latency_ms":             avg_latency,
            "scenario_accuracy":          scenario_accuracy,
            "correctly_classified":       correctly_classified,
            "total_scenarios":            len(TEST_SCENARIOS),
        },
        "node_success_rate": node_success_rate,
        "scenario_coverage": scenario_coverage,
        "run_details": run_results,
        "full_run_latency_ms": full_ms,
    }

    # ── Persist ───────────────────────────────────────────────────────────────
    RESULTS_DIR.mkdir(parents=True, exist_ok=True)
    with open(RESULTS_FILE, "w", encoding="utf-8") as f:
        json.dump(report, f, indent=2)

    # ── Print summary ─────────────────────────────────────────────────────────
    print(f"\n{'='*60}")
    print("  RESULTS SUMMARY")
    print(f"{'='*60}")
    s = report["summary"]
    print(f"  Agent Reliability Score    : {s['agent_reliability_score']:.0%}")
    print(f"  Response Consistency Score : {s['response_consistency_score']:.4f}")
    print(f"  Hallucination Rate         : {s['hallucination_rate']:.0%}")
    print(f"  Validation Pass Rate       : {s['validation_pass_rate']:.0%}")
    print(f"  Avg Latency                : {s['avg_latency_ms']} ms")
    print(f"  Scenario Accuracy          : {s['scenario_accuracy']:.0%} ({s['correctly_classified']}/{s['total_scenarios']})")
    print(f"\n  Node Success Rates:")
    for node, rate in node_success_rate.items():
        print(f"    {node:<30} {rate:.0%}")
    print(f"\n  Scenario Coverage:")
    for sc in scenario_coverage:
        mark = "✓" if sc["correctly_classified"] else "✗"
        print(f"    {mark} {sc['scenario_id']}  {sc['supplier_name']:<35} "
              f"expected={sc['risk_band_expected']}  actual={sc['risk_band_actual'] or 'NOT FOUND'}")
    print(f"\n  Results saved → {RESULTS_FILE}")
    print(f"{'='*60}\n")

    return report


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Run Module 2 agent test suite")
    parser.add_argument("--backend", default="http://localhost:8089/supchain",
                        help="Spring Boot backend URL")
    args = parser.parse_args()
    run_suite(args.backend)
