/**
 * SCIP static demo — fetch interceptor.
 *
 * The GitHub Pages build has no live backend (the documented Railway
 * deployment no longer exists), and web/src/api.js falls back to
 * http://localhost:8089/supchain when REACT_APP_API_URL isn't set at
 * build time — which is why every screen showed empty data. This patches
 * window.fetch, before the app bundle loads, to serve realistic canned
 * JSON for every /api/* call so the "Demo Mode" banner's promise (fully
 * self-contained, no backend needed) is actually true.
 *
 * Must be loaded via a plain (non-deferred) <script> BEFORE the CRA
 * bundle's <script defer> tag in index.html.
 */
(function () {
  "use strict";

  function jsonResponse(body) {
    return Promise.resolve(
      new Response(JSON.stringify(body), {
        status: 200,
        headers: { "Content-Type": "application/json" },
      })
    );
  }

  // ── Supplier data ────────────────────────────────────────────────────────
  var SUPPLIERS = [
    { supplierId: "SUP-1001", supplierName: "Shenzhen Electronics Co.", country: "China", tier: "PROBATION", otdScore: 58.0, qualityScore: 61.0, responsivenessScore: 55.0, compositeScore: 59.5, totalDeliveries: 142, atRisk: true },
    { supplierId: "SUP-1002", supplierName: "Foxconn Technology Group", country: "Taiwan", tier: "CONDITIONAL", otdScore: 66.0, qualityScore: 72.0, responsivenessScore: 64.0, compositeScore: 69.0, totalDeliveries: 310, atRisk: true },
    { supplierId: "SUP-1003", supplierName: "Taiwan Semiconductors Ltd", country: "Taiwan", tier: "APPROVED", otdScore: 82.0, qualityScore: 85.0, responsivenessScore: 80.0, compositeScore: 83.5, totalDeliveries: 201, atRisk: false },
    { supplierId: "SUP-1004", supplierName: "Murata Manufacturing", country: "Japan", tier: "PREFERRED", otdScore: 96.0, qualityScore: 94.0, responsivenessScore: 92.0, compositeScore: 95.2, totalDeliveries: 418, atRisk: false },
    { supplierId: "SUP-1005", supplierName: "Delta Electronics India", country: "India", tier: "CONDITIONAL", otdScore: 64.0, qualityScore: 70.0, responsivenessScore: 62.0, compositeScore: 67.0, totalDeliveries: 96, atRisk: true },
    { supplierId: "SUP-1006", supplierName: "Samsung SDI Korea", country: "South Korea", tier: "PREFERRED", otdScore: 91.0, qualityScore: 89.0, responsivenessScore: 87.0, compositeScore: 90.1, totalDeliveries: 275, atRisk: false },
    { supplierId: "SUP-1007", supplierName: "Vishay Intertechnology", country: "USA", tier: "APPROVED", otdScore: 79.0, qualityScore: 81.0, responsivenessScore: 76.0, compositeScore: 80.0, totalDeliveries: 188, atRisk: false },
    { supplierId: "SUP-1008", supplierName: "TE Connectivity", country: "Switzerland", tier: "PROBATION", otdScore: 52.0, qualityScore: 60.0, responsivenessScore: 50.0, compositeScore: 55.8, totalDeliveries: 74, atRisk: true },
    { supplierId: "SUP-1009", supplierName: "Infineon Technologies", country: "Germany", tier: "APPROVED", otdScore: 77.0, qualityScore: 79.0, responsivenessScore: 74.0, compositeScore: 78.0, totalDeliveries: 163, atRisk: false },
    { supplierId: "SUP-1010", supplierName: "STMicroelectronics", country: "France", tier: "CONDITIONAL", otdScore: 68.0, qualityScore: 71.0, responsivenessScore: 63.0, compositeScore: 69.4, totalDeliveries: 129, atRisk: true },
  ];

  function supplierStats() {
    var atRisk = SUPPLIERS.filter(function (s) { return s.atRisk; }).length;
    var avg = function (key) {
      return SUPPLIERS.reduce(function (sum, s) { return sum + s[key]; }, 0) / SUPPLIERS.length;
    };
    return {
      totalSuppliers: SUPPLIERS.length,
      atRiskCount: atRisk,
      avgOtdScore: avg("otdScore"),
      avgCompositeScore: avg("compositeScore"),
    };
  }

  function supplierDeliveries(supplierId) {
    var base = [88, 92, 76, 95, 81, 90, 85];
    return base.map(function (pct, i) {
      var d = new Date();
      d.setDate(d.getDate() - (base.length - i) * 7);
      return {
        id: supplierId + "-DEL-" + (i + 1),
        supplierId: supplierId,
        deliveryDate: d.toISOString().slice(0, 10),
        onTime: pct >= 80,
        otdPct: pct,
        qtyOrdered: 500 + i * 25,
        qtyReceived: 500 + i * 25 - (pct < 80 ? 15 : 0),
      };
    });
  }

  // ── Dashboard / Alerts ───────────────────────────────────────────────────
  var DASHBOARD_SUMMARY = {
    activeAlerts: 3, totalBoms: 12, pendingApprovals: 2, atRiskSuppliers: 4,
    lowStockCount: 2, openPOCount: 6, avgSupplierRating: 4.1, totalInventorySkus: 50,
  };

  var ALERTS = [
    { id: "ALT-1", alertType: "SUPPLIER_RISK", alertLabel: "Supplier Risk", shortSummary: "TE Connectivity OTD dropped below 55%" },
    { id: "ALT-2", alertType: "LOW_STOCK", alertLabel: "Low Stock", shortSummary: "SKU-2048 (Connector Housing) below reorder threshold" },
    { id: "ALT-3", alertType: "COST_VARIANCE", alertLabel: "Cost Variance", shortSummary: "Cost record CR-118 exceeds budget by 18%" },
  ];

  // ── Cost records ─────────────────────────────────────────────────────────
  var COST_RECORDS = [
    { id: "CR-101", description: "Q3 component reorder — Murata", amount: 48250.0, status: "APPROVED", createdAt: "2026-06-02" },
    { id: "CR-102", description: "Emergency freight — Shenzhen Electronics", amount: 9120.5, status: "PENDING", createdAt: "2026-06-18" },
    { id: "CR-103", description: "Tooling cost — Infineon", amount: 15600.0, status: "SUBMITTED", createdAt: "2026-06-21" },
    { id: "CR-104", description: "Quarterly audit fee", amount: 3200.0, status: "APPROVED", createdAt: "2026-06-25" },
  ];

  // ── Purchase orders ──────────────────────────────────────────────────────
  var PURCHASE_ORDERS = [
    { id: "PO-5001", supplierName: "Murata Manufacturing", status: "OPEN", totalValue: 62000.0, createdAt: "2026-06-10" },
    { id: "PO-5002", supplierName: "Samsung SDI Korea", status: "CONFIRMED", totalValue: 41500.0, createdAt: "2026-06-14" },
    { id: "PO-5003", supplierName: "Taiwan Semiconductors Ltd", status: "RECEIVED", totalValue: 28750.0, createdAt: "2026-06-01" },
  ];

  // ── Bill of materials ────────────────────────────────────────────────────
  var BOMS = [
    { bomKey: "BOM-A100", productName: "Control Module A100", version: "v3", totalCost: 214.5, status: "APPROVED" },
    { bomKey: "BOM-B220", productName: "Sensor Array B220", version: "v1", totalCost: 88.2, status: "PENDING" },
  ];

  // ── Inventory ────────────────────────────────────────────────────────────
  var INVENTORY = [
    { sku: "SKU-2048", name: "Connector Housing", qtyOnHand: 42, reorderThreshold: 100, lowStock: true },
    { sku: "SKU-3312", name: "PCB Substrate 4-layer", qtyOnHand: 860, reorderThreshold: 200, lowStock: false },
    { sku: "SKU-4471", name: "Capacitor 100uF", qtyOnHand: 15, reorderThreshold: 500, lowStock: true },
  ];

  // ── AI anomalies ─────────────────────────────────────────────────────────
  var ANOMALIES = [
    { supplierId: "SUP-1001", supplierName: "Shenzhen Electronics Co.", riskLevel: "HIGH", anomalyScore: -0.18,
      explanation: "Shenzhen Electronics Co. is classified as HIGH RISK with a quality score of 61% and composite score of 59.5. Initiate dual-sourcing and issue a supplier performance notice." },
    { supplierId: "SUP-1008", supplierName: "TE Connectivity", riskLevel: "HIGH", anomalyScore: -0.22,
      explanation: "TE Connectivity is classified as HIGH RISK with a quality score of 60% and composite score of 55.8. Historical data shows repeated risk flags for this supplier." },
    { supplierId: "SUP-1005", supplierName: "Delta Electronics India", riskLevel: "MEDIUM", anomalyScore: -0.06,
      explanation: "Delta Electronics India is classified as MEDIUM RISK with a quality score of 70% and composite score of 67.0. Schedule a performance review and increase monitoring frequency." },
  ];

  // ── Route table ──────────────────────────────────────────────────────────
  // Each entry: [regex matching the request path, handler(match, params, method, body)]
  var routes = [
    [/\/api\/suppliers\/stats$/, function () { return { data: supplierStats() }; }],
    [/\/api\/suppliers\/([^/]+)\/deliveries$/, function (m) { return { data: supplierDeliveries(m[1]) }; }],
    [/\/api\/suppliers\/([^/]+)$/, function (m) {
      var s = SUPPLIERS.find(function (x) { return x.supplierId === m[1]; });
      return { data: s || SUPPLIERS[0] };
    }],
    [/\/api\/suppliers$/, function (_m, params) {
      var q = (params && params.search || "").toLowerCase();
      var list = q ? SUPPLIERS.filter(function (s) { return s.supplierName.toLowerCase().indexOf(q) !== -1; }) : SUPPLIERS;
      return { data: list };
    }],

    [/\/api\/dashboard\/summary$/, function () { return DASHBOARD_SUMMARY; }],
    [/\/api\/alerts\/[^/]+\/dismiss$/, function () { return { success: true }; }],
    [/\/api\/alerts$/, function () { return ALERTS; }],

    [/\/api\/ai\/anomalies$/, function () { return { data: ANOMALIES }; }],
    [/\/api\/eval\/results$/, function () { return { data: [] }; }],

    [/\/api\/costs\/stats$/, function () {
      var total = COST_RECORDS.reduce(function (s, r) { return s + r.amount; }, 0);
      return { data: { totalRecords: COST_RECORDS.length, totalAmount: total, pendingCount: COST_RECORDS.filter(function (r) { return r.status === "PENDING"; }).length } };
    }],
    [/\/api\/costs\/([^/]+)$/, function (m) {
      var r = COST_RECORDS.find(function (x) { return x.id === m[1]; });
      return { data: r || COST_RECORDS[0] };
    }],
    [/\/api\/costs$/, function () { return { data: { content: COST_RECORDS, totalPages: 1 } }; }],
    [/\/api\/cost-records/, function () { return { data: COST_RECORDS }; }],

    [/\/api\/purchase-orders/, function () { return { data: PURCHASE_ORDERS }; }],
    [/\/api\/boms\/([^/]+)$/, function (m) {
      var b = BOMS.find(function (x) { return x.bomKey === m[1]; });
      return { data: b || BOMS[0] };
    }],
    [/\/api\/boms/, function () { return { data: BOMS }; }],
    [/\/api\/inventory\/[^/]+\/transactions$/, function () { return { data: [] }; }],
    [/\/api\/inventory/, function () { return { data: INVENTORY }; }],

    [/\/api\/forecasts/, function () { return { data: [] }; }],
    [/\/api\/users$/, function () { return { data: [{ id: 1, email: "kumar@scip.io", role: "ADMIN" }, { id: 2, email: "ops@scip.io", role: "OPS" }] }; }],
    [/\/api\/roles$/, function () { return { data: ["ADMIN", "OPS", "VIEWER"] }; }],
    [/\/api\/audit-logs/, function () { return { data: [] }; }],
    [/\/api\/reports\//, function () { return { data: [] }; }],
  ];

  function parseParams(url) {
    var qIndex = url.indexOf("?");
    if (qIndex === -1) return {};
    var out = {};
    url.slice(qIndex + 1).split("&").forEach(function (pair) {
      if (!pair) return;
      var idx = pair.indexOf("=");
      var k = decodeURIComponent(idx === -1 ? pair : pair.slice(0, idx));
      var v = decodeURIComponent(idx === -1 ? "" : pair.slice(idx + 1));
      out[k] = v;
    });
    return out;
  }

  var realFetch = window.fetch.bind(window);

  window.fetch = function (input, init) {
    var url = typeof input === "string" ? input : (input && input.url) || "";
    var isSupchainApi = url.indexOf("/supchain/api/") !== -1 || url.indexOf("localhost:8089") !== -1;

    if (!isSupchainApi) {
      return realFetch(input, init);
    }

    var path = url.split("?")[0];
    var params = parseParams(url);
    var method = (init && init.method) || "GET";

    // The Test Dashboard's own fallback (computed from its embedded Playwright
    // test list) only engages when this call fails outright — an empty-envelope
    // 200 response leaves total/passed/failed undefined and breaks its stats
    // (NaN%, blank counts). Let it hit the real (absent) backend and fail
    // naturally instead of mocking it.
    if (/\/api\/test-results$/.test(path)) {
      return realFetch(input, init);
    }

    for (var i = 0; i < routes.length; i++) {
      var match = path.match(routes[i][0]);
      if (match) {
        var body = routes[i][1](match, params, method, init && init.body);
        return jsonResponse(body === undefined ? { success: true } : body);
      }
    }

    // Unmatched route — don't hard-fail the UI, just return an empty-but-valid
    // envelope so unmocked screens degrade gracefully instead of erroring.
    console.warn("[SCIP demo] unmocked API call, returning empty envelope:", method, path);
    return jsonResponse(method === "GET" ? { data: [] } : { success: true });
  };
})();
