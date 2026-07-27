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
    { id: 5001, poNumber: "PO-5001", supplierId: "SUP-1004", supplierName: "Murata Manufacturing", status: "SUBMITTED",
      orderDate: "2026-06-10", expectedDate: "2026-06-24", receivedDate: null, totalAmount: 62000.0, currency: "USD", createdBy: "kumar",
      lineItems: [
        { itemKey: "ITEM-2048", description: "Connector Housing", quantity: 500, uom: "EACH", unitPrice: 62.0, lineTotal: 31000.0 },
        { itemKey: "ITEM-3312", description: "PCB Substrate 4-layer", quantity: 200, uom: "EACH", unitPrice: 155.0, lineTotal: 31000.0 },
      ] },
    { id: 5002, poNumber: "PO-5002", supplierId: "SUP-1006", supplierName: "Samsung SDI Korea", status: "CONFIRMED",
      orderDate: "2026-06-14", expectedDate: "2026-06-28", receivedDate: null, totalAmount: 41500.0, currency: "USD", createdBy: "kumar",
      lineItems: [
        { itemKey: "ITEM-4471", description: "Capacitor 100uF", quantity: 5000, uom: "EACH", unitPrice: 8.3, lineTotal: 41500.0 },
      ] },
    { id: 5003, poNumber: "PO-5003", supplierId: "SUP-1003", supplierName: "Taiwan Semiconductors Ltd", status: "RECEIVED",
      orderDate: "2026-06-01", expectedDate: "2026-06-08", receivedDate: "2026-06-07", totalAmount: 28750.0, currency: "USD", createdBy: "kumar",
      lineItems: [
        { itemKey: "ITEM-3312", description: "PCB Substrate 4-layer", quantity: 250, uom: "EACH", unitPrice: 115.0, lineTotal: 28750.0 },
      ] },
  ];

  // ── Bill of materials ────────────────────────────────────────────────────
  var BOMS = [
    { bomKey: "BOM-A100", productName: "Control Module A100", version: "v3", totalCost: 214.5, status: "APPROVED" },
    { bomKey: "BOM-B220", productName: "Sensor Array B220", version: "v1", totalCost: 88.2, status: "PENDING" },
  ];

  // ── Inventory ────────────────────────────────────────────────────────────
  var INVENTORY = [
    { id: 1, itemKey: "ITEM-2048", itemName: "Connector Housing", warehouseId: "WH-01", currentStock: 42, reorderPoint: 100, maxStock: 1000, uom: "EACH", category: "Electromechanical", lastUpdated: "2026-07-20T09:15:00" },
    { id: 2, itemKey: "ITEM-3312", itemName: "PCB Substrate 4-layer", warehouseId: "WH-01", currentStock: 860, reorderPoint: 200, maxStock: 2000, uom: "EACH", category: "PCB", lastUpdated: "2026-07-22T14:40:00" },
    { id: 3, itemKey: "ITEM-4471", itemName: "Capacitor 100uF", warehouseId: "WH-02", currentStock: 15, reorderPoint: 500, maxStock: 5000, uom: "EACH", category: "Passive Components", lastUpdated: "2026-07-25T11:05:00" },
    { id: 4, itemKey: "ITEM-5590", itemName: "Sensor Array Module", warehouseId: "WH-02", currentStock: 320, reorderPoint: 150, maxStock: 800, uom: "EACH", category: "Sensors", lastUpdated: "2026-07-21T08:30:00" },
  ];

  // ── Reports ──────────────────────────────────────────────────────────────
  // Mirrors ReportsController's own PALETTE + trend() so the demo matches
  // the real (raw, non-enveloped) JSON shape each report tab expects.
  var REPORT_PALETTE = ["#2563eb", "#16a34a", "#d97706", "#dc2626", "#7c3aed", "#0891b2", "#db2777", "#65a30d"];

  function reportTrend(current, points) {
    var values = [];
    for (var i = points - 1; i >= 1; i--) {
      var drift = ((current * 31 + i * 17) % 11) - 5;
      values.push(Math.max(0, Math.min(100, Math.round((current - drift) * 100) / 100)));
    }
    values.push(current);
    return values;
  }

  function lastMonths(count) {
    var months = [];
    var names = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"];
    var now = new Date();
    for (var i = count - 1; i >= 0; i--) {
      var d = new Date(now.getFullYear(), now.getMonth() - i, 1);
      months.push(names[d.getMonth()]);
    }
    return months;
  }

  function supplierPerformanceReport() {
    var suppliers = SUPPLIERS.map(function (s, i) {
      return {
        name: s.supplierName, country: s.country, otd: s.otdScore, quality: s.qualityScore,
        composite: s.compositeScore, tier: s.tier, atRisk: s.atRisk, color: REPORT_PALETTE[i % REPORT_PALETTE.length],
        otdTrend: reportTrend(s.otdScore, 6), qualityTrend: reportTrend(s.qualityScore, 6), compositeTrend: reportTrend(s.compositeScore, 6),
      };
    });
    return { suppliers: suppliers, months: lastMonths(6) };
  }

  function costVarianceReport() {
    var rows = [
      { item: "ITEM-2048", category: "Electromechanical", budget: 58.0, actual: 62.0 },
      { item: "ITEM-3312", category: "PCB", budget: 148.0, actual: 155.0 },
      { item: "ITEM-4471", category: "Passive Components", budget: 9.0, actual: 8.3 },
      { item: "ITEM-5590", category: "Sensors", budget: 210.0, actual: 214.5 },
    ];
    return rows.map(function (r) {
      var variance = r.actual - r.budget;
      var pct = r.budget !== 0 ? Math.round((variance / r.budget) * 10000) / 100 : 0;
      return { item: r.item, category: r.category, budget: r.budget, actual: r.actual,
        variance: Math.round(variance * 100) / 100, pct: pct, status: variance > 0 ? "OVER" : "UNDER" };
    });
  }

  function alertSeverity(label) {
    var u = (label || "").toUpperCase();
    if (u.indexOf("RISK") !== -1 || u.indexOf("SPIKE") !== -1 || u.indexOf("QUALITY") !== -1) return "critical";
    if (u.indexOf("PENDING") !== -1 || u.indexOf("CONTRACT") !== -1 || u.indexOf("LEAD_TIME") !== -1) return "warning";
    return "info";
  }

  function alertSummaryReport() {
    var byTypeCounts = {}, byTypeOrder = [];
    ALERTS.forEach(function (a) {
      var t = a.alertType || "OTHER";
      if (!(t in byTypeCounts)) { byTypeCounts[t] = 0; byTypeOrder.push(t); }
      byTypeCounts[t]++;
    });
    var byType = byTypeOrder.map(function (t, i) { return { type: t, count: byTypeCounts[t], color: REPORT_PALETTE[i % REPORT_PALETTE.length] }; });

    var months = lastMonths(6);
    var byMonth = months.map(function (m, i) {
      var isRecent = i >= months.length - 2;
      var critical = 0, warning = 0, info = 0;
      if (isRecent) {
        ALERTS.forEach(function (a) {
          var sev = alertSeverity(a.alertLabel);
          if (sev === "critical") critical++; else if (sev === "warning") warning++; else info++;
        });
      }
      return { month: m, critical: critical, warning: warning, info: info };
    });

    return { byType: byType, total: ALERTS.length, resolved: 0, avgResolutionHrs: 0, byMonth: byMonth };
  }

  function activityReport() {
    var now = new Date();
    var rows = [
      { user: "kumar", module: "PurchaseOrder", action: "RECEIVE" },
      { user: "kumar", module: "SupplierRating", action: "CREATE" },
      { user: "ops",   module: "Inventory",      action: "ADJUST" },
      { user: "kumar", module: "CostRecord",     action: "CREATE" },
      { user: "ops",   module: "PurchaseOrder",  action: "SUBMIT" },
    ];
    return rows.map(function (r, i) {
      var t = new Date(now.getTime() - (i + 1) * 3 * 60 * 60 * 1000);
      return { time: t.toISOString(), user: r.user, module: r.module, action: r.action };
    });
  }

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

    [/\/api\/purchase-orders\/(\d+)$/, function (m) {
      var po = PURCHASE_ORDERS.find(function (x) { return String(x.id) === m[1]; });
      return { data: po || PURCHASE_ORDERS[0] };
    }],
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

    // Raw (non-enveloped) JSON, matching ReportsController's own convention —
    // must NOT be wrapped in { data: ... } like the routes above.
    [/\/api\/reports\/supplier-performance$/, function () { return supplierPerformanceReport(); }],
    [/\/api\/reports\/cost-variance$/, function () { return costVarianceReport(); }],
    [/\/api\/reports\/alert-summary$/, function () { return alertSummaryReport(); }],
    [/\/api\/reports\/activity$/, function () { return activityReport(); }],
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
