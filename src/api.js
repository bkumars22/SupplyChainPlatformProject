import axios from "axios";
import {
  MOCK_SUPPLIERS,
  MOCK_SUPPLIER_DELIVERIES,
  MOCK_DASHBOARD_KPIS,
  MOCK_ALERTS,
  MOCK_COST_RECORDS,
  MOCK_BOMS,
  MOCK_USERS,
  MOCK_ROLES,
  MOCK_FORECASTS,
  MOCK_ANOMALIES,
  MOCK_TEST_RESULTS,
  MOCK_EVAL_SCORES,
  MOCK_EVAL_SUMMARY,
  MOCK_REPORT_SUPPLIER_PERF,
  MOCK_REPORT_COST_VARIANCE,
  MOCK_REPORT_ALERT_SUMMARY,
  MOCK_REPORT_ACTIVITY,
} from "./services/mockData";

const DEMO = process.env.REACT_APP_DEMO_MODE === "true";

// Wrap mock data in axios-like response shape with artificial delay
function mock(data, ms = 600) {
  return new Promise((resolve) =>
    setTimeout(() => resolve({ data, status: 200 }), ms)
  );
}

// ── Axios client (real mode) ───────────────────────────────────────────────────
const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL
    ? process.env.REACT_APP_API_URL + ""
    : "/supchain",
  timeout: 30000,
});

api.interceptors.request.use(function (config) {
  var token = localStorage.getItem("jwt_token");
  if (token) {
    config.headers.Authorization = "Bearer " + token;
  }
  return config;
});

api.interceptors.response.use(
  function (response) {
    return response;
  },
  function (error) {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem("jwt_token");
      localStorage.removeItem("user_data");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export default api;

// ── Auth ──────────────────────────────────────────────────────────────────────
export function login(u, p) {
  if (DEMO) {
    return mock({
      token: "demo-jwt-token-scip-2026",
      role: "DEMO",
      userId: u || "demo",
      userName: u || "Demo User",
    }, 800);
  }
  return axios.post("/supchain/api/auth/login", { username: u, password: p });
}

export function changePassword(d) {
  if (DEMO) return mock({ success: true });
  return api.post("/api/auth/change-password", d);
}

// ── Dashboard ─────────────────────────────────────────────────────────────────
export function getDashboardSummary() {
  if (DEMO) return mock(MOCK_DASHBOARD_KPIS);
  return api.get("/api/dashboard/summary");
}
export const getDashboard = getDashboardSummary;

// ── Alerts ────────────────────────────────────────────────────────────────────
let _mockAlerts = [...MOCK_ALERTS];

export function getActiveAlerts() {
  if (DEMO) return mock([..._mockAlerts]);
  return api.get("/api/alerts/active");
}

export function getAlertCount() {
  if (DEMO) return mock(_mockAlerts.length);
  return api.get("/api/alerts/count");
}

export function dismissAlert(id) {
  if (DEMO) {
    _mockAlerts = _mockAlerts.filter((a) => a.id !== id);
    return mock({ success: true }, 300);
  }
  return api.put("/api/alerts/" + id + "/dismiss");
}

// ── BOM ───────────────────────────────────────────────────────────────────────
export function getBoms(page, size) {
  if (DEMO) {
    return mock({ content: MOCK_BOMS, totalElements: MOCK_BOMS.length });
  }
  return api.get("/api/bom", { params: { page: page || 0, size: size || 20 } });
}

export function getBomList(p, s) {
  if (DEMO) {
    return mock({ content: MOCK_BOMS, totalElements: MOCK_BOMS.length });
  }
  return api.get("/api/bom", { params: { page: p || 0, size: s || 50 } });
}

export function getBomDetail(bomKey) {
  if (DEMO) {
    const bom = MOCK_BOMS.find((b) => b.bomKey === bomKey) || MOCK_BOMS[0];
    return mock(bom);
  }
  return api.get("/api/bom/" + bomKey);
}

// ── Cost Records ──────────────────────────────────────────────────────────────
let _mockCosts = [...MOCK_COST_RECORDS];

export function getCostRecords(s) {
  if (DEMO) {
    const filtered = s
      ? _mockCosts.filter(
          (r) =>
            (r.itemCode || "").toLowerCase().includes(s.toLowerCase()) ||
            (r.itemKey || "").toLowerCase().includes(s.toLowerCase())
        )
      : [..._mockCosts];
    return mock(filtered);
  }
  return api.get("/api/cost-records", { params: { search: s || "" } });
}

export function getCostStats() {
  if (DEMO) return mock({ total: 4, approved: 2, pending: 1, draft: 1 });
  return api.get("/api/cost-records/stats");
}

export function createCostRecord(d) {
  if (DEMO) {
    const newRec = {
      ...d,
      id: "CR-DEMO-" + Date.now(),
      itemCode: d.itemKey,
      status: "DRAFT",
      createdBy: "demo",
    };
    _mockCosts = [newRec, ..._mockCosts];
    return mock(newRec, 500);
  }
  return api.post("/api/cost-records", d);
}

export function updateCostRecord(id, d) {
  if (DEMO) {
    _mockCosts = _mockCosts.map((r) => (r.id === id ? { ...r, ...d } : r));
    return mock({ success: true }, 400);
  }
  return api.put("/api/cost-records/" + id, d);
}

export function submitCostRecord(id) {
  if (DEMO) {
    _mockCosts = _mockCosts.map((r) =>
      r.id === id ? { ...r, status: "PENDING_APPROVAL" } : r
    );
    return mock({ success: true }, 400);
  }
  return api.put("/api/cost-records/" + id + "/submit");
}

export function approveCostRecord(id) {
  if (DEMO) {
    _mockCosts = _mockCosts.map((r) =>
      r.id === id ? { ...r, status: "APPROVED" } : r
    );
    return mock({ success: true }, 400);
  }
  return api.put("/api/cost-records/" + id + "/approve");
}

export function rejectCostRecord(id, r) {
  if (DEMO) {
    _mockCosts = _mockCosts.map((rec) =>
      rec.id === id ? { ...rec, status: "REJECTED" } : rec
    );
    return mock({ success: true }, 400);
  }
  return api.put("/api/cost-records/" + id + "/reject", { reason: r });
}

// ── Suppliers ─────────────────────────────────────────────────────────────────
export function getSuppliers() {
  if (DEMO) return mock(MOCK_SUPPLIERS);
  return api.get("/api/suppliers");
}

export function getSupplierDetail(id) {
  if (DEMO) {
    const s = MOCK_SUPPLIERS.find((s) => s.supplierId === id) || MOCK_SUPPLIERS[0];
    return mock(s);
  }
  return api.get("/api/suppliers/" + id);
}

export function getSupplierDeliveries(id) {
  if (DEMO) {
    return mock(MOCK_SUPPLIER_DELIVERIES[id] || [], 500);
  }
  return api.get("/api/suppliers/" + id + "/deliveries");
}

export function addSupplierDelivery(id, d) {
  if (DEMO) return mock({ success: true });
  return api.post("/api/suppliers/" + id + "/deliveries", d);
}

export function recalculateTiers() {
  if (DEMO) return mock({ success: true });
  return api.post("/api/suppliers/recalculate-tiers");
}

// ── AI / Anomalies ────────────────────────────────────────────────────────────
export function getAnomalies() {
  if (DEMO) return mock({ anomalies: MOCK_ANOMALIES });
  return api.get("/api/ai/anomalies");
}

export function getAiHealth() {
  if (DEMO) return mock({ status: "UP", model: "DistilBERT", version: "1.0" });
  return api.get("/api/ai/health");
}

export function getSupplierRisk(id) {
  if (DEMO) {
    const s = MOCK_SUPPLIERS.find((s) => s.supplierId === id);
    return mock({ riskScore: s ? (s.atRisk ? -0.8 : 0.7) : 0, isAnomaly: s?.atRisk || false });
  }
  return api.get("/api/ai/supplier/" + id + "/risk");
}

export function getAiInsights() {
  if (DEMO) return mock(MOCK_ANOMALIES);
  return api.get("/api/ai/insights");
}

// ── Users ─────────────────────────────────────────────────────────────────────
let _mockUsers = [...MOCK_USERS];

export function getUsers() {
  if (DEMO) return mock({ users: [..._mockUsers] });
  return api.get("/api/admin/users");
}

export function getUser(id) {
  if (DEMO) {
    return mock(_mockUsers.find((u) => u.userId === id) || _mockUsers[0]);
  }
  return api.get("/api/admin/users/" + id);
}

export function createUser(d) {
  if (DEMO) {
    const newUser = { ...d, isEnabled: true, hasPassword: !!d.password };
    _mockUsers = [..._mockUsers, newUser];
    return mock(newUser, 500);
  }
  return api.post("/api/admin/users", d);
}

export function updateUser(id, d) {
  if (DEMO) {
    _mockUsers = _mockUsers.map((u) => (u.userId === id ? { ...u, ...d } : u));
    return mock({ success: true }, 400);
  }
  return api.put("/api/admin/users/" + id, d);
}

export function disableUser(id) {
  if (DEMO) {
    _mockUsers = _mockUsers.map((u) =>
      u.userId === id ? { ...u, isEnabled: false } : u
    );
    return mock({ success: true }, 400);
  }
  return api.delete("/api/admin/users/" + id);
}

export function getRoles() {
  if (DEMO) return mock(MOCK_ROLES);
  return api.get("/api/admin/roles");
}

export function setPassword(id, p) {
  if (DEMO) {
    _mockUsers = _mockUsers.map((u) =>
      u.userId === id ? { ...u, hasPassword: true } : u
    );
    return mock({ success: true }, 300);
  }
  return api.post("/api/admin/users/" + id + "/set-password", { newPassword: p });
}

export function resetPassword(id) {
  if (DEMO) return mock({ success: true });
  return api.post("/api/admin/users/" + id + "/reset-password");
}

// ── Forecasts ─────────────────────────────────────────────────────────────────
export function getForecasts() {
  if (DEMO) return mock({ forecasts: MOCK_FORECASTS });
  return api.get("/api/forecasts");
}

export function getForecastDetail(id) {
  if (DEMO) {
    const f = MOCK_FORECASTS.find((f) => f.forecastKey === id) || MOCK_FORECASTS[0];
    return mock(f);
  }
  return api.get("/api/forecasts/" + id);
}

export function getForecastsByItem(itemKey) {
  if (DEMO) {
    return mock(MOCK_FORECASTS.filter((f) => f.itemNumber === itemKey));
  }
  return api.get("/api/forecasts/item/" + itemKey);
}

export function createForecast(d) {
  if (DEMO) {
    const newF = {
      ...d,
      forecastKey: "FC-DEMO-" + Date.now(),
      itemNumber: d.itemKey,
      effectiveFrom: d.startPeriod + "-01",
      effectiveTo: d.endPeriod + "-01",
      values: [],
    };
    return mock(newF, 500);
  }
  return api.post("/api/forecasts", d);
}

export function getVarianceReport() {
  if (DEMO) return mock({ items: MOCK_FORECASTS });
  return api.get("/api/forecasts/variance-report");
}

// ── Test results & Eval (used by TestDashboardPage + EvalDashboard) ──────────
export function getTestResults() {
  if (DEMO) return new Promise(resolve => setTimeout(() => resolve(MOCK_TEST_RESULTS), 600));
  return fetch("/test-results.json").then((r) => r.json());
}

export function getEvalResults() {
  if (DEMO) {
    return Promise.resolve({
      ok: true,
      json: () => Promise.resolve({ results: MOCK_EVAL_SCORES, generated_at: new Date().toISOString() }),
    });
  }
  const base = process.env.REACT_APP_AI_URL || "http://localhost:8001";
  return fetch(base + "/eval/results");
}

// ── Reports ───────────────────────────────────────────────────────────────────
export function getSupplierPerfReport() {
  if (DEMO) return mock(MOCK_REPORT_SUPPLIER_PERF, 700);
  return api.get("/api/reports/supplier-performance");
}

export function getCostVarianceReport() {
  if (DEMO) return mock(MOCK_REPORT_COST_VARIANCE, 700);
  return api.get("/api/reports/cost-variance");
}

export function getAlertSummaryReport() {
  if (DEMO) return mock(MOCK_REPORT_ALERT_SUMMARY, 700);
  return api.get("/api/reports/alert-summary");
}

export function getActivityReport() {
  if (DEMO) return mock(MOCK_REPORT_ACTIVITY, 700);
  return api.get("/api/reports/activity");
}

// ── Setup ─────────────────────────────────────────────────────────────────────
export function getSetupStatus() {
  if (DEMO) return mock({ configured: !!localStorage.getItem('scip_setup_done') });
  return api.get("/api/setup/status");
}

export function saveSetupConfig(config) {
  if (DEMO) { localStorage.setItem('scip_setup_config', JSON.stringify(config)); return mock({ ok: true }); }
  return api.post("/api/setup/configure", config);
}

export function resetDemoData() {
  if (DEMO) return mock({ message: "Demo data restored!", records_seeded: { suppliers: 5, users: 3, cost_records: 4, alerts: 3, bom_items: 3 } }, 1200);
  return api.post("/api/admin/reset-demo");
}

export function runEvaluation() {
  if (DEMO) {
    return new Promise((resolve) =>
      setTimeout(
        () =>
          resolve({
            ok: true,
            json: () =>
              Promise.resolve({
                report: MOCK_EVAL_SUMMARY,
                results: MOCK_EVAL_SCORES,
              }),
          }),
        1200
      )
    );
  }
  const base = process.env.REACT_APP_AI_URL || "http://localhost:8001";
  return fetch(base + "/eval/run");
}
