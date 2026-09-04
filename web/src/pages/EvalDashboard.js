import React, { useState, useEffect, useCallback } from "react";

const AI_BASE = process.env.REACT_APP_AI_URL || "http://localhost:8001";
const IS_DEMO = process.env.REACT_APP_DEMO_MODE === "true";

// Static stand-in for a /eval/results response — the real ai-service (port
// 8001) only runs on a dev machine, so the deployed demo has no backend to
// reach for this page (unlike every other page, which goes through
// mock-api.js's fetch shim instead of calling the AI service directly).
const DEMO_EVAL_RESULTS = {
  run_id: "demo-run-2026-07-27",
  generated_at: "2026-07-27T09:12:00Z",
  results: [
    { test_case_id: "EVAL-01", category: "risk-explanation", input: "Explain why Shenzhen Electronics Co. is flagged high risk.", scores: { overall_score: 0.92, answer_relevancy: 0.95, consistency_score: 0.93, response_latency_ms: 612 } },
    { test_case_id: "EVAL-02", category: "risk-explanation", input: "Explain why Alpine Connectors AG is flagged high risk.", scores: { overall_score: 0.89, answer_relevancy: 0.90, consistency_score: 0.88, response_latency_ms: 588 } },
    { test_case_id: "EVAL-03", category: "cost-variance", input: "Summarize why cost record CR-118 exceeds budget by 18%.", scores: { overall_score: 0.85, answer_relevancy: 0.87, consistency_score: 0.82, response_latency_ms: 701 } },
    { test_case_id: "EVAL-04", category: "cost-variance", input: "Summarize the cost trend for ITEM-2048 across 4 versions.", scores: { overall_score: 0.78, answer_relevancy: 0.80, consistency_score: 0.75, response_latency_ms: 745 } },
    { test_case_id: "EVAL-05", category: "forecast-summary", input: "Summarize the 90-day demand forecast for ITEM-4471.", scores: { overall_score: 0.81, answer_relevancy: 0.83, consistency_score: 0.79, response_latency_ms: 690 } },
    { test_case_id: "EVAL-06", category: "forecast-summary", input: "Explain the confidence interval on ITEM-3312's forecast.", scores: { overall_score: 0.74, answer_relevancy: 0.76, consistency_score: 0.71, response_latency_ms: 812 } },
    { test_case_id: "EVAL-07", category: "alert-narrative", input: "Explain the low-stock alert for SKU-2048.", scores: { overall_score: 0.90, answer_relevancy: 0.92, consistency_score: 0.89, response_latency_ms: 540 } },
    { test_case_id: "EVAL-08", category: "alert-narrative", input: "Explain the supplier-risk alert for Alpine Connectors AG.", scores: { overall_score: 0.88, answer_relevancy: 0.90, consistency_score: 0.85, response_latency_ms: 561 } },
    { test_case_id: "EVAL-09", category: "consistency-check", input: "Re-run the Shenzhen Electronics risk explanation prompt 3x and compare.", scores: { overall_score: 0.65, answer_relevancy: 0.70, consistency_score: 0.60, response_latency_ms: 930 } },
    { test_case_id: "EVAL-10", category: "consistency-check", input: "Re-run the cost-variance explanation prompt 3x and compare.", scores: { overall_score: 0.72, answer_relevancy: 0.74, consistency_score: 0.68, response_latency_ms: 877 } },
  ],
};

const badge = (type) => {
  const m = {
    pass: { background: "#f0fdf4", color: "#15803d", border: "1px solid #bbf7d0" },
    fail: { background: "#fef2f2", color: "#dc2626", border: "1px solid #fecaca" },
    warn: { background: "#fffbeb", color: "#d97706", border: "1px solid #fde68a" },
    info: { background: "#eff6ff", color: "#a9790f", border: "1px solid #bfdbfe" },
    skip: { background: "#f1f5f9", color: "#475569", border: "1px solid #e2e8f0" },
  };
  return {
    display: "inline-flex", alignItems: "center", padding: "2px 10px",
    borderRadius: 20, fontSize: 11, fontWeight: 700, whiteSpace: "nowrap",
    ...(m[type] || m.info),
  };
};

function KpiCard({ label, value, sub, color, loading }) {
  return (
    <div style={{
      background: "#fff", border: "1px solid #e2e8f0", borderRadius: 10,
      padding: "16px 20px", borderTop: "3px solid " + color, flex: 1, minWidth: 160,
    }}>
      <div style={{ fontSize: 30, fontWeight: 800, color: "#1e293b" }}>
        {loading ? <span style={{ color: "#94a3b8" }}>—</span> : value}
      </div>
      <div style={{ fontSize: 11, color: "#64748b", textTransform: "uppercase", letterSpacing: "0.5px", marginTop: 4 }}>
        {label}
      </div>
      {sub && (
        <div style={{ fontSize: 12, color, fontWeight: 600, marginTop: 6 }}>{sub}</div>
      )}
    </div>
  );
}

function Spinner() {
  return (
    <span style={{
      display: "inline-block", width: 14, height: 14, border: "2px solid #fff",
      borderTopColor: "transparent", borderRadius: "50%",
      animation: "spin 0.7s linear infinite", verticalAlign: "middle",
    }} />
  );
}

export default function EvalDashboard() {
  const [summary,    setSummary]    = useState(null);
  const [scores,     setScores]     = useState([]);
  const [loading,    setLoading]    = useState(true);
  const [running,    setRunning]    = useState(false);
  const [error,      setError]      = useState(null);
  const [lastUpdate, setLastUpdate] = useState(null);

  // Fetch summary + scores from /eval/run (returns { report, results })
  const fetchEval = useCallback(async (silent = false) => {
    if (!silent) setLoading(true);
    setError(null);
    if (IS_DEMO) {
      applyResults(DEMO_EVAL_RESULTS);
      if (!silent) setLoading(false);
      return;
    }
    try {
      const res = await fetch(AI_BASE + "/eval/results");
      if (res.status === 404) {
        // No prior run — return empty state without an error
        setSummary(null);
        setScores([]);
        setLastUpdate(new Date());
        return;
      }
      if (!res.ok) throw new Error("HTTP " + res.status);
      const data = await res.json();
      // /eval/results returns { run_id, generated_at, results }
      // run it through the same shape that /eval/run returns for the report
      applyResults(data);
    } catch (e) {
      setError("Could not reach AI service at " + AI_BASE + " — " + e.message);
    } finally {
      if (!silent) setLoading(false);
    }
  }, []);

  const applyResults = (data) => {
    // data is either from /eval/results ({ run_id, generated_at, results })
    // or from /eval/run ({ report, results })
    const rows = data.results || [];
    setScores(rows);

    if (data.report) {
      // came from /eval/run
      setSummary(data.report);
    } else if (rows.length > 0) {
      // came from /eval/results — compute summary client-side
      const overalls = rows.map(r => r.scores?.overall_score ?? 0);
      const avg = overalls.reduce((a, b) => a + b, 0) / overalls.length;
      const passed = overalls.filter(s => s > 0.7).length;
      const consistencies = rows.map(r => r.scores?.consistency_score ?? 0);
      const avgConsist = consistencies.reduce((a, b) => a + b, 0) / consistencies.length;
      setSummary({
        total_cases: rows.length,
        avg_overall_score: avg,
        pass: avg > 0.7,
        avg_consistency: avgConsist,
        mode: rows[0]?.scores?.mode || "heuristic",
        generated_at: data.generated_at,
        _computed: true,
        _passed: passed,
        _failed: rows.length - passed,
      });
    }
    setLastUpdate(new Date());
  };

  const runEvaluation = async () => {
    setRunning(true);
    setError(null);
    if (IS_DEMO) {
      applyResults({ ...DEMO_EVAL_RESULTS, generated_at: new Date().toISOString() });
      setRunning(false);
      return;
    }
    try {
      const res = await fetch(AI_BASE + "/eval/run");
      if (!res.ok) throw new Error("HTTP " + res.status);
      const data = await res.json();
      applyResults(data);
    } catch (e) {
      setError("Evaluation failed: " + e.message);
    } finally {
      setRunning(false);
    }
  };

  // Initial load
  useEffect(() => {
    fetchEval();
  }, [fetchEval]);

  // Auto-refresh every 30 seconds
  useEffect(() => {
    const t = setInterval(() => fetchEval(true), 30000);
    return () => clearInterval(t);
  }, [fetchEval]);

  // ── Derived KPI values ──────────────────────────────────────────────────────
  const totalCases     = summary?.total_cases ?? 0;
  const passedCount    = summary?._passed    ?? (summary ? Math.round((summary.avg_overall_score ?? 0) > 0.7 ? totalCases : 0) : 0);
  const failedCount    = summary?._failed    ?? (totalCases - passedCount);
  const consistPct     = summary ? Math.round((summary.avg_consistency ?? 0) * 100) : null;
  const avgScore       = summary ? Math.round((summary.avg_overall_score ?? 0) * 100) : null;
  const overallPass    = summary?.pass ?? false;

  // ── Render ──────────────────────────────────────────────────────────────────
  return (
    <div style={{ padding: 24, maxWidth: 1100, margin: "0 auto" }}>

      {/* ── Header ── */}
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: 20, flexWrap: "wrap", gap: 12 }}>
        <div>
          <h1 style={{ fontSize: 22, fontWeight: 800, margin: 0 }}>LLM Eval Dashboard</h1>
          <p style={{ fontSize: 13, color: "#64748b", marginTop: 4, margin: "4px 0 0" }}>
            Module 1 — Prompt Evaluation Harness · {totalCases} test cases
            {summary?.mode && (
              <span style={{ marginLeft: 10, ...badge(summary.mode === "llm-judge" ? "info" : "skip"), fontSize: 10 }}>
                {summary.mode === "llm-judge" ? "LLM Judge" : "Heuristic"}
              </span>
            )}
            {lastUpdate && (
              <span style={{ marginLeft: 10, fontSize: 12, color: "#94a3b8" }}>
                Updated {lastUpdate.toLocaleTimeString()}
              </span>
            )}
            <span style={{ marginLeft: 8, ...badge("skip"), fontSize: 10 }}>Auto-refreshing 30s</span>
          </p>
        </div>
        <div style={{ display: "flex", gap: 8 }}>
          <button
            onClick={() => fetchEval()}
            disabled={loading || running}
            style={{ padding: "7px 14px", borderRadius: 8, fontSize: 12, fontWeight: 700, cursor: "pointer", border: "1px solid #e2e8f0", background: "#fff", color: "#374151" }}
          >
            Refresh
          </button>
          <button
            onClick={runEvaluation}
            disabled={running || loading}
            style={{ padding: "7px 16px", borderRadius: 8, fontSize: 12, fontWeight: 700, cursor: running ? "not-allowed" : "pointer", border: "none", background: "#a9790f", color: "#fff", display: "flex", alignItems: "center", gap: 7, opacity: running ? 0.85 : 1 }}
          >
            {running && <Spinner />}
            {running ? "Running…" : "Run Evaluation"}
          </button>
        </div>
      </div>

      {/* ── Error banner ── */}
      {error && (
        <div style={{ background: "#fef2f2", border: "1px solid #fecaca", borderRadius: 8, padding: "10px 16px", marginBottom: 16, color: "#dc2626", fontSize: 13, display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <span>{error}</span>
          <button onClick={() => setError(null)} style={{ background: "none", border: "none", cursor: "pointer", color: "#dc2626", fontSize: 16, lineHeight: 1 }}>×</button>
        </div>
      )}

      {/* ── KPI cards ── */}
      <div style={{ display: "grid", gridTemplateColumns: "repeat(4, 1fr)", gap: 12, marginBottom: 24 }}>
        <KpiCard
          label="Total Cases"
          value={totalCases || "—"}
          color="#a9790f"
          loading={loading}
        />
        <KpiCard
          label="Passed"
          value={passedCount || "—"}
          sub={totalCases ? `${Math.round((passedCount / totalCases) * 100)}% pass rate` : null}
          color="#15803d"
          loading={loading}
        />
        <KpiCard
          label="Failed"
          value={failedCount || "0"}
          color={failedCount > 0 ? "#dc2626" : "#15803d"}
          loading={loading}
        />
        <KpiCard
          label="Consistency Rate"
          value={consistPct !== null ? consistPct + "%" : "—"}
          sub={avgScore !== null ? `Avg score ${avgScore}%` : null}
          color={consistPct !== null ? (consistPct >= 80 ? "#15803d" : consistPct >= 60 ? "#d97706" : "#dc2626") : "#94a3b8"}
          loading={loading}
        />
      </div>

      {/* ── Scores table ── */}
      <div style={{ background: "#fff", border: "1px solid #e2e8f0", borderRadius: 10, overflow: "hidden" }}>
        <div style={{ padding: "14px 18px", borderBottom: "1px solid #e2e8f0", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <h3 style={{ margin: 0, fontSize: 15, fontWeight: 700 }}>Evaluation Scores</h3>
          {summary && (
            <span style={badge(overallPass ? "pass" : "fail")}>
              {overallPass ? "SUITE PASS" : "SUITE FAIL"}
            </span>
          )}
        </div>

        {/* Loading state */}
        {loading && (
          <div style={{ padding: 48, textAlign: "center", color: "#64748b", fontSize: 14 }}>
            <div style={{ fontSize: 28, marginBottom: 10 }}>⏳</div>
            Loading evaluation results…
          </div>
        )}

        {/* Empty state */}
        {!loading && scores.length === 0 && !error && (
          <div style={{ padding: 52, textAlign: "center" }}>
            <div style={{ fontSize: 32, marginBottom: 12 }}>🧪</div>
            <p style={{ color: "#64748b", fontSize: 14, fontWeight: 600, margin: "0 0 6px" }}>No evaluation results yet</p>
            <p style={{ color: "#94a3b8", fontSize: 13, margin: 0 }}>Click <strong>Run Evaluation</strong> to score all 20 test cases</p>
          </div>
        )}

        {/* Scores table */}
        {!loading && scores.length > 0 && (
          <div style={{ overflowX: "auto" }}>
            <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 13 }}>
              <thead>
                <tr style={{ background: "#f8fafc" }}>
                  {["ID", "Category", "Prompt", "Score", "Relevancy", "Consistency", "Latency", "Status"].map(h => (
                    <th key={h} style={{ padding: "10px 14px", textAlign: "left", fontSize: 11, fontWeight: 700, color: "#64748b", textTransform: "uppercase", letterSpacing: "0.4px", borderBottom: "1px solid #e2e8f0", whiteSpace: "nowrap" }}>
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {scores.map((row, i) => {
                  const s = row.scores || {};
                  const overall = s.overall_score ?? 0;
                  const pass = overall > 0.7;
                  return (
                    <tr
                      key={row.test_case_id || i}
                      style={{ borderBottom: "1px solid #f1f5f9", background: i % 2 === 0 ? "#fff" : "#fafafa" }}
                    >
                      <td style={{ padding: "10px 14px", fontFamily: "monospace", fontSize: 11, color: "#94a3b8", whiteSpace: "nowrap" }}>
                        {row.test_case_id || "—"}
                      </td>
                      <td style={{ padding: "10px 14px", whiteSpace: "nowrap" }}>
                        <span style={badge("info")}>{row.category || "—"}</span>
                      </td>
                      <td style={{ padding: "10px 14px", maxWidth: 320, color: "#374151", lineHeight: 1.4 }}>
                        <div style={{ overflow: "hidden", display: "-webkit-box", WebkitLineClamp: 2, WebkitBoxOrient: "vertical" }}>
                          {row.input || "—"}
                        </div>
                      </td>
                      <td style={{ padding: "10px 14px", fontWeight: 700, color: overall >= 0.8 ? "#15803d" : overall >= 0.7 ? "#d97706" : "#dc2626", whiteSpace: "nowrap" }}>
                        {(overall * 100).toFixed(1)}%
                      </td>
                      <td style={{ padding: "10px 14px", color: "#374151", whiteSpace: "nowrap" }}>
                        {((s.answer_relevancy ?? 0) * 100).toFixed(1)}%
                      </td>
                      <td style={{ padding: "10px 14px", color: "#374151", whiteSpace: "nowrap" }}>
                        {((s.consistency_score ?? 0) * 100).toFixed(1)}%
                      </td>
                      <td style={{ padding: "10px 14px", color: "#64748b", whiteSpace: "nowrap" }}>
                        {s.response_latency_ms != null ? s.response_latency_ms.toFixed(0) + " ms" : "—"}
                      </td>
                      <td style={{ padding: "10px 14px" }}>
                        <span style={badge(pass ? "pass" : "fail")}>
                          {pass ? "PASS" : "FAIL"}
                        </span>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Spinner keyframe */}
      <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
    </div>
  );
}
