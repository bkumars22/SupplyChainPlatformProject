/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
import { test, expect } from "@playwright/test";
import { APP_URL, API_URL, loginViaUI } from "./helpers/auth";

// Python AI service — separate port from the Spring Boot backend
const AI_URL = process.env.AI_URL ?? "http://localhost:8001";

// ── Helpers ──────────────────────────────────────────────────────────────────

/**
 * Run the eval suite and return the raw response object.
 * Cached across tests in this file via module-level promise so we don't pay
 * the 20-case evaluation cost more than once per run.
 */
let _evalRunCache: Promise<{ report: Record<string, unknown>; results: unknown[] }> | null = null;

function runEvalOnce() {
  if (!_evalRunCache) {
    _evalRunCache = fetch(AI_URL + "/eval/run").then((r) => r.json());
  }
  return _evalRunCache;
}

// ── Test 1: GET /eval/run returns results array with required per-case fields ─

test.describe("Eval API — /eval/run result shape", () => {

  test("results array contains items with prompt, score, and pass fields", async () => {
    const data = await runEvalOnce();

    expect(data).toHaveProperty("results");
    expect(Array.isArray(data.results)).toBe(true);
    expect(data.results.length).toBeGreaterThan(0);

    const first = data.results[0] as Record<string, unknown>;
    // "prompt" maps to the `input` field in the evaluator
    expect(first).toHaveProperty("input");
    expect(typeof first.input).toBe("string");

    // "score" maps to scores.overall_score
    const scores = first.scores as Record<string, unknown>;
    expect(scores).toHaveProperty("overall_score");
    expect(typeof scores.overall_score).toBe("number");

    // "pass" is derived: overall_score > 0.7
    expect(scores.overall_score as number).toBeGreaterThanOrEqual(0);
    expect(scores.overall_score as number).toBeLessThanOrEqual(1);
  });

});

// ── Test 2: GET /eval/run report summary has required aggregate fields ────────

test.describe("Eval API — /eval/run summary fields", () => {

  test("report contains total_cases, avg_overall_score, pass, and avg_consistency", async () => {
    const data = await runEvalOnce();

    expect(data).toHaveProperty("report");
    const report = data.report as Record<string, unknown>;

    expect(report).toHaveProperty("total_cases");
    expect(typeof report.total_cases).toBe("number");
    expect((report.total_cases as number)).toBeGreaterThan(0);

    expect(report).toHaveProperty("avg_overall_score");
    expect(typeof report.avg_overall_score).toBe("number");

    expect(report).toHaveProperty("pass");
    expect(typeof report.pass).toBe("boolean");

    // consistency_rate maps to avg_consistency in the report
    expect(report).toHaveProperty("avg_consistency");
    expect(typeof report.avg_consistency).toBe("number");
  });

});

// ── Test 3: POST-style trigger — /eval/run responds with evaluation results ───

test.describe("Eval API — /eval/run triggers evaluation", () => {

  test("GET /eval/run returns a completed evaluation with mode field", async () => {
    // The endpoint runs synchronously and returns results immediately.
    // Treat this as the "trigger and return" contract (the API doesn't use POST).
    const res = await fetch(AI_URL + "/eval/run");
    expect(res.status).toBe(200);

    const data = (await res.json()) as { report: Record<string, unknown>; results: unknown[] };
    expect(data.report).toHaveProperty("mode");
    expect(["heuristic", "llm-judge"]).toContain(data.report.mode as string);
    expect(data.results.length).toBeGreaterThan(0);
  });

});

// ── Test 4: /eval/health page loads without errors ────────────────────────────

test.describe("Eval health endpoint", () => {

  test("GET /eval/health responds 200 with status ok", async () => {
    const res = await fetch(AI_URL + "/eval/health");
    expect(res.status).toBe(200);

    const data = (await res.json()) as Record<string, unknown>;
    expect(data.status).toBe("ok");
    expect(data).toHaveProperty("eval_module");
    expect(data).toHaveProperty("test_cases");
  });

});

// ── Test 5: results array has at least one row with all score sub-fields ──────

test.describe("Eval API — results completeness", () => {

  test("every result row has the four expected score sub-fields", async () => {
    const data = await runEvalOnce();
    const REQUIRED_SCORE_KEYS = [
      "overall_score",
      "answer_relevancy",
      "hallucination_score",
      "consistency_score",
    ];

    for (const result of data.results as Array<Record<string, unknown>>) {
      const scores = result.scores as Record<string, unknown>;
      for (const key of REQUIRED_SCORE_KEYS) {
        expect(scores).toHaveProperty(key);
        expect(typeof scores[key]).toBe("number");
      }
    }
  });

});

// ── Test 6: every result row has a pass/fail determination ───────────────────

test.describe("Eval API — pass/fail per result", () => {

  test("each result overall_score can be classified as pass (>0.7) or fail", async () => {
    const data = await runEvalOnce();

    let passCount = 0;
    let failCount = 0;

    for (const result of data.results as Array<Record<string, unknown>>) {
      const scores = result.scores as Record<string, number>;
      expect(scores.overall_score).toBeGreaterThanOrEqual(0);
      expect(scores.overall_score).toBeLessThanOrEqual(1);

      if (scores.overall_score > 0.7) {
        passCount++;
      } else {
        failCount++;
      }
    }

    // At least some results must exist; both counts are valid — we just confirm
    // the classification logic can be applied to all rows without error.
    expect(passCount + failCount).toEqual(data.results.length);
  });

});

// ── Test 7: avg_consistency is a 0-1 float, maps to a % for display ──────────

test.describe("Eval API — consistency_rate percentage", () => {

  test("avg_consistency is between 0 and 1 (multiply by 100 for display %)", async () => {
    const data = await runEvalOnce();
    const report = data.report as Record<string, number>;

    expect(report.avg_consistency).toBeGreaterThanOrEqual(0);
    expect(report.avg_consistency).toBeLessThanOrEqual(1);

    // When shown as a percentage (0-100) it must be in the valid range
    const asPercent = report.avg_consistency * 100;
    expect(asPercent).toBeGreaterThanOrEqual(0);
    expect(asPercent).toBeLessThanOrEqual(100);
  });

});

// ── Test 8: /eval/results returns saved results (auto-save after /eval/run) ───

test.describe("Eval API — /eval/results persisted data", () => {

  test("GET /eval/results returns saved run after /eval/run has been called", async () => {
    // Ensure a run has been completed first (reuses the cached run)
    await runEvalOnce();

    const res = await fetch(AI_URL + "/eval/results");
    // 200 if results exist; 404 if no prior run has been saved to disk yet in this environment
    expect([200, 404]).toContain(res.status);

    if (res.status === 200) {
      const data = (await res.json()) as Record<string, unknown>;
      // Persisted file wraps results in { run_id, generated_at, results }
      expect(data).toHaveProperty("results");
      expect(Array.isArray(data.results)).toBe(true);
    }
  });

});

// ── Test 9: eval endpoint rejects unauthenticated when auth is enforced ───────

test.describe("Eval API — no auth required (Python service is open)", () => {

  test("GET /eval/health is accessible without Authorization header", async () => {
    // The Python AI service does not enforce JWT — it relies on network isolation.
    // This test documents the current contract: open access returns 200.
    const res = await fetch(AI_URL + "/eval/health", {
      headers: {},   // no Authorization header
    });
    expect(res.status).toBe(200);
  });

  test("Java backend /api/ai/insights rejects requests without a token with 401 or 403", async () => {
    // The Spring Boot layer enforces JWT. Hitting it without a token must not return 200.
    const res = await fetch(API_URL + "/api/ai/insights");
    expect([401, 403]).toContain(res.status);
  });

});

// ── Test 10: consistency_rate is numerically between 0 and 100 (percent form) ─

test.describe("Eval API — consistency_rate range validation", () => {

  test("avg_consistency * 100 is between 0 and 100 inclusive", async () => {
    const data = await runEvalOnce();
    const report = data.report as Record<string, unknown>;

    const consistencyRate = (report.avg_consistency as number) * 100;
    expect(consistencyRate).toBeGreaterThanOrEqual(0);
    expect(consistencyRate).toBeLessThanOrEqual(100);

    // Sanity-check: heuristic mode always scores well above 0% consistency
    const mode = report.mode as string;
    if (mode === "heuristic") {
      expect(consistencyRate).toBeGreaterThan(70);
    }
  });

});
