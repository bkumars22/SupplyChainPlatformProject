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

  if (window.__SCIP_DEMO_MODE__ !== "true") return;

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
    { supplierId: "SUP-1001", supplierName: "Shenzhen Electronics Co.", country: "China", tier: "PROBATION", otdScore: 58.0, qualityScore: 61.0, responsivenessScore: 55.0, compositeScore: 59.5, totalDeliveries: 142, onTimeDeliveries: 82, atRisk: true },
    { supplierId: "SUP-1002", supplierName: "Pinnacle Assembly Group", country: "Taiwan", tier: "CONDITIONAL", otdScore: 66.0, qualityScore: 72.0, responsivenessScore: 64.0, compositeScore: 69.0, totalDeliveries: 310, onTimeDeliveries: 205, atRisk: true },
    { supplierId: "SUP-1003", supplierName: "Strait Semiconductor Ltd", country: "Taiwan", tier: "APPROVED", otdScore: 82.0, qualityScore: 85.0, responsivenessScore: 80.0, compositeScore: 83.5, totalDeliveries: 201, onTimeDeliveries: 165, atRisk: false },
    { supplierId: "SUP-1004", supplierName: "Sakura Components Co.", country: "Japan", tier: "PREFERRED", otdScore: 96.0, qualityScore: 94.0, responsivenessScore: 92.0, compositeScore: 95.2, totalDeliveries: 418, onTimeDeliveries: 401, atRisk: false },
    { supplierId: "SUP-1005", supplierName: "Ganges Electronics India", country: "India", tier: "CONDITIONAL", otdScore: 64.0, qualityScore: 70.0, responsivenessScore: 62.0, compositeScore: 67.0, totalDeliveries: 96, onTimeDeliveries: 61, atRisk: true },
    { supplierId: "SUP-1006", supplierName: "Hanbit Energy Korea", country: "South Korea", tier: "PREFERRED", otdScore: 91.0, qualityScore: 89.0, responsivenessScore: 87.0, compositeScore: 90.1, totalDeliveries: 275, onTimeDeliveries: 250, atRisk: false },
    { supplierId: "SUP-1007", supplierName: "Cascade Semiconductor Inc.", country: "USA", tier: "APPROVED", otdScore: 79.0, qualityScore: 81.0, responsivenessScore: 76.0, compositeScore: 80.0, totalDeliveries: 188, onTimeDeliveries: 149, atRisk: false },
    { supplierId: "SUP-1008", supplierName: "Alpine Connectors AG", country: "Switzerland", tier: "PROBATION", otdScore: 52.0, qualityScore: 60.0, responsivenessScore: 50.0, compositeScore: 55.8, totalDeliveries: 74, onTimeDeliveries: 38, atRisk: true },
    { supplierId: "SUP-1009", supplierName: "Rhineland Semiconductor GmbH", country: "Germany", tier: "APPROVED", otdScore: 77.0, qualityScore: 79.0, responsivenessScore: 74.0, compositeScore: 78.0, totalDeliveries: 163, onTimeDeliveries: 126, atRisk: false },
    { supplierId: "SUP-1010", supplierName: "Milano Silicon Systems", country: "France", tier: "CONDITIONAL", otdScore: 68.0, qualityScore: 71.0, responsivenessScore: 63.0, compositeScore: 69.4, totalDeliveries: 129, onTimeDeliveries: 88, atRisk: true },
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

  // Matches the real SupplierDelivery entity's fields (poNumber, itemCode,
  // promisedDate/actualDate, status enum ON_TIME/LATE, delayDays) — the page
  // reads these names directly, not the otdPct/onTime shape used elsewhere.
  function supplierDeliveries(supplierId) {
    var items = ["ITEM-2048", "ITEM-3312", "ITEM-4471", "ITEM-5590"];
    var base = [88, 92, 76, 95, 81, 90, 85];
    return base.map(function (pct, i) {
      var promised = new Date();
      promised.setDate(promised.getDate() - (base.length - i) * 7);
      var late = pct < 80;
      var delay = late ? Math.round((100 - pct) / 10) : 0;
      var actual = new Date(promised);
      actual.setDate(actual.getDate() + delay);
      var ordered = 500 + i * 25;
      return {
        id: i + 1,
        poNumber: "PO-" + (9000 + i),
        itemCode: items[i % items.length],
        promisedDate: promised.toISOString().slice(0, 10),
        actualDate: actual.toISOString().slice(0, 10),
        qtyOrdered: ordered,
        qtyReceived: late ? ordered - 15 : ordered,
        status: late ? "LATE" : "ON_TIME",
        delayDays: delay,
        notes: null,
      };
    });
  }

  // ── Supplier dependency graph ────────────────────────────────────────────
  // Mirrors the real backend's Phase 1 demo scenario (same 3 edges, same
  // decay-factor/max-hops cascade math as SupplierRiskCascadeService) so the
  // Dependency Risk tab isn't a dead end on the static demo site.
  var DEPENDENCIES = [
    { dependentSupplierId: "SUP-1003", upstreamSupplierId: "SUP-1005", componentOrMaterial: "Connector Housings", dependencyCriticality: 0.9, isSoleSource: true },
    { dependentSupplierId: "SUP-1009", upstreamSupplierId: "SUP-1003", componentOrMaterial: "Silicon Wafers", dependencyCriticality: 0.7, isSoleSource: false },
    { dependentSupplierId: "SUP-1002", upstreamSupplierId: "SUP-1001", componentOrMaterial: "PCB Assemblies", dependencyCriticality: 0.4, isSoleSource: false },
  ];
  var CASCADE_DECAY = 0.6;
  var CASCADE_MAX_HOPS = 3;

  function directRiskOf(supplierId) {
    var s = SUPPLIERS.find(function (x) { return x.supplierId === supplierId; });
    return s ? (100 - s.compositeScore) / 100 : 0;
  }

  function computeCascadedRisk(supplierId) {
    var upstreamOf = {};
    DEPENDENCIES.forEach(function (e) {
      (upstreamOf[e.dependentSupplierId] = upstreamOf[e.dependentSupplierId] || []).push(e);
    });
    var visited = {};
    visited[supplierId] = true;
    var current = [supplierId];
    var cascadedContribution = 0;
    var flags = [];
    for (var hop = 1; hop <= CASCADE_MAX_HOPS && current.length; hop++) {
      var next = [];
      current.forEach(function (id) {
        (upstreamOf[id] || []).forEach(function (edge) {
          if (visited[edge.upstreamSupplierId]) return;
          visited[edge.upstreamSupplierId] = true;
          var upstreamRisk = directRiskOf(edge.upstreamSupplierId);
          var criticality = edge.dependencyCriticality != null ? edge.dependencyCriticality : 0.5;
          cascadedContribution += upstreamRisk * Math.pow(CASCADE_DECAY, hop) * criticality;
          if (edge.isSoleSource && upstreamRisk > 0.5) {
            var up = SUPPLIERS.find(function (x) { return x.supplierId === edge.upstreamSupplierId; });
            flags.push({
              upstreamSupplierId: edge.upstreamSupplierId,
              upstreamSupplierName: up ? up.supplierName : edge.upstreamSupplierId,
              componentOrMaterial: edge.componentOrMaterial,
              upstreamRisk: Math.round(upstreamRisk * 1000) / 1000,
              hopDistance: hop,
            });
          }
          next.push(edge.upstreamSupplierId);
        });
      });
      current = next;
    }
    var directRisk = directRiskOf(supplierId);
    var effectiveRisk = Math.min(1, directRisk + cascadedContribution);
    var summary = cascadedContribution > 0.05
      ? "Elevated effective risk from cascading dependency exposure"
      : "No material cascading risk from upstream suppliers";
    return {
      directRisk: Math.round(directRisk * 1000) / 1000,
      effectiveRisk: Math.round(effectiveRisk * 1000) / 1000,
      cascadedContribution: Math.round(cascadedContribution * 1000) / 1000,
      summary: summary,
      soleSourceRiskFlags: flags,
    };
  }

  function computeStructuralRisk(supplierId) {
    var deps = DEPENDENCIES.filter(function (e) { return e.dependentSupplierId === supplierId && e.isSoleSource; });
    return {
      soleSourceDependencies: deps.map(function (e) {
        var up = SUPPLIERS.find(function (x) { return x.supplierId === e.upstreamSupplierId; });
        return {
          componentOrMaterial: e.componentOrMaterial,
          upstreamSupplierId: e.upstreamSupplierId,
          upstreamSupplierName: up ? up.supplierName : e.upstreamSupplierId,
        };
      }),
    };
  }

  // ── Dashboard / Alerts ───────────────────────────────────────────────────
  var DASHBOARD_SUMMARY = {
    activeAlerts: 3, totalBoms: 12, pendingApprovals: 2, atRiskSuppliers: 4,
    lowStockCount: 2, openPOCount: 6, avgSupplierRating: 4.1, totalInventorySkus: 50,
  };

  var ALERTS = [
    { id: "ALT-1", alertType: "SUPPLIER_RISK", alertLabel: "Supplier Risk", shortSummary: "Alpine Connectors OTD dropped below 55%" },
    { id: "ALT-2", alertType: "LOW_STOCK", alertLabel: "Low Stock", shortSummary: "SKU-2048 (Connector Housing) below reorder threshold" },
    { id: "ALT-3", alertType: "COST_VARIANCE", alertLabel: "Cost Variance", shortSummary: "Cost record CR-118 exceeds budget by 18%" },
  ];

  // ── Cost records ─────────────────────────────────────────────────────────
  // Matches CostRecordRestController's real CostDto fields (itemCode,
  // proposedCost/previousCost, changePercent, justification, createdDate) —
  // "item" is a cosmetic nested alias for CostRecordsPage/CostDetailPage,
  // which read record.item?.itemCode/description (a pre-existing frontend
  // quirk; the real DTO itself is flat).
  var COST_RECORDS = [
    { id: 101, itemCode: "ITEM-2048", item: { itemCode: "ITEM-2048", description: "Connector Housing" },
      versionNumber: 3, proposedCost: 62.0, previousCost: 58.0, changePercent: 6.90, status: "APPROVED",
      justification: "Q3 component reorder — Sakura Components price increase", rejectionReason: null,
      createdBy: "kumar", createdDate: "2026-06-02T10:00:00", submittedDate: "2026-06-02T10:05:00",
      approvedBy: "kumar", approvedDate: "2026-06-03T09:00:00" },
    { id: 102, itemCode: "ITEM-4471", item: { itemCode: "ITEM-4471", description: "Capacitor 100uF" },
      versionNumber: 2, proposedCost: 8.3, previousCost: 9.0, changePercent: -7.78, status: "PENDING_APPROVAL",
      justification: "Emergency freight — Shenzhen Electronics", rejectionReason: null,
      createdBy: "ops", createdDate: "2026-06-18T14:20:00", submittedDate: "2026-06-18T14:25:00",
      approvedBy: null, approvedDate: null },
    { id: 103, itemCode: "ITEM-5590", item: { itemCode: "ITEM-5590", description: "Sensor Array Module" },
      versionNumber: 1, proposedCost: 214.5, previousCost: 210.0, changePercent: 2.14, status: "DRAFT",
      justification: "Tooling cost — Rhineland Semiconductor", rejectionReason: null,
      createdBy: "kumar", createdDate: "2026-06-21T08:00:00", submittedDate: null,
      approvedBy: null, approvedDate: null },
    { id: 104, itemCode: "ITEM-3312", item: { itemCode: "ITEM-3312", description: "PCB Substrate 4-layer" },
      versionNumber: 4, proposedCost: 155.0, previousCost: 148.0, changePercent: 4.73, status: "REJECTED",
      justification: "Quarterly audit fee reallocation", rejectionReason: "Insufficient supporting documentation",
      createdBy: "ops", createdDate: "2026-06-25T16:10:00", submittedDate: "2026-06-25T16:15:00",
      approvedBy: "kumar", approvedDate: "2026-06-26T09:30:00" },
  ];
  var nextCostId = 105;

  // ── Purchase orders ──────────────────────────────────────────────────────
  var PURCHASE_ORDERS = [
    { id: 5001, poNumber: "PO-5001", supplierId: "SUP-1004", supplierName: "Sakura Components Co.", status: "SUBMITTED",
      orderDate: "2026-06-10", expectedDate: "2026-06-24", receivedDate: null, totalAmount: 62000.0, currency: "USD", createdBy: "kumar",
      lineItems: [
        { itemKey: "ITEM-2048", description: "Connector Housing", quantity: 500, uom: "EACH", unitPrice: 62.0, lineTotal: 31000.0 },
        { itemKey: "ITEM-3312", description: "PCB Substrate 4-layer", quantity: 200, uom: "EACH", unitPrice: 155.0, lineTotal: 31000.0 },
      ] },
    { id: 5002, poNumber: "PO-5002", supplierId: "SUP-1006", supplierName: "Hanbit Energy Korea", status: "CONFIRMED",
      orderDate: "2026-06-14", expectedDate: "2026-06-28", receivedDate: null, totalAmount: 41500.0, currency: "USD", createdBy: "kumar",
      lineItems: [
        { itemKey: "ITEM-4471", description: "Capacitor 100uF", quantity: 5000, uom: "EACH", unitPrice: 8.3, lineTotal: 41500.0 },
      ] },
    { id: 5003, poNumber: "PO-5003", supplierId: "SUP-1003", supplierName: "Strait Semiconductor Ltd", status: "RECEIVED",
      orderDate: "2026-06-01", expectedDate: "2026-06-08", receivedDate: "2026-06-07", totalAmount: 28750.0, currency: "USD", createdBy: "kumar",
      lineItems: [
        { itemKey: "ITEM-3312", description: "PCB Substrate 4-layer", quantity: 250, uom: "EACH", unitPrice: 115.0, lineTotal: 28750.0 },
      ] },
  ];

  // ── Bill of materials ────────────────────────────────────────────────────
  // Matches BomRestController's real (raw, non-enveloped) BomSummary/BomDetail
  // records. bomDescription/itemQuantity are cosmetic aliases for
  // BomDetailPage.js, which reads those exact (differently-named) keys — a
  // pre-existing frontend quirk, not part of the real DTO.
  var BOMS = [
    { bomKey: "BOM-A100", bomName: "Control Module A100", description: "Primary control module assembly",
      bomDescription: "Primary control module assembly", status: "Approved", itemNumber: "ITEM-5590",
      version: "v3", revision: "A", isTopLevel: true, leadTime: 12, supplier: "Sakura Components Co.",
      lineCount: 2, effectiveFrom: "2026-01-01", effectiveTo: null,
      lines: [
        { bomLineKey: 1, itemNumber: "ITEM-2048", description: "Connector Housing", quantity: "4", itemQuantity: "4",
          leadTime: 5, managedFlag: "Y", rollupFlag: "Y", notes: null, effectiveFrom: "2026-01-01", effectiveTo: null },
        { bomLineKey: 2, itemNumber: "ITEM-4471", description: "Capacitor 100uF", quantity: "12", itemQuantity: "12",
          leadTime: 3, managedFlag: "Y", rollupFlag: "N", notes: null, effectiveFrom: "2026-01-01", effectiveTo: null },
      ] },
    { bomKey: "BOM-B220", bomName: "Sensor Array B220", description: "Secondary sensor array board",
      bomDescription: "Secondary sensor array board", status: "Pending", itemNumber: "ITEM-3312",
      version: "v1", revision: "-", isTopLevel: false, leadTime: 8, supplier: "Strait Semiconductor Ltd",
      lineCount: 1, effectiveFrom: "2026-03-01", effectiveTo: null,
      lines: [
        { bomLineKey: 3, itemNumber: "ITEM-4471", description: "Capacitor 100uF", quantity: "2", itemQuantity: "2",
          leadTime: 3, managedFlag: "N", rollupFlag: "N", notes: null, effectiveFrom: "2026-03-01", effectiveTo: null },
      ] },
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
  // Matches AiController.getAnomalies()'s real (raw, non-enveloped) shape:
  // { anomalies: [...], total: N }, each anomaly keyed by itemCode/severity/etc.
  var ANOMALIES = [
    { id: 1, itemCode: "SUP-1001", category: "Supply", severity: "CRITICAL",
      description: "Shenzhen Electronics Co. is classified as HIGH RISK with a quality score of 61% and composite score of 59.5.",
      detectedAt: "2026-07-25T09:12:00", confidenceScore: 92,
      recommendation: "Initiate dual-sourcing and issue a supplier performance notice.",
      affectedItems: [{ itemCode: "ITEM-2048", itemName: "Connector Housing" }] },
    { id: 2, itemCode: "SUP-1008", category: "Supply", severity: "CRITICAL",
      description: "Alpine Connectors AG is classified as HIGH RISK with a quality score of 60% and composite score of 55.8.",
      detectedAt: "2026-07-24T15:40:00", confidenceScore: 88,
      recommendation: "Historical data shows repeated risk flags for this supplier — review contract terms.",
      affectedItems: [{ itemCode: "ITEM-4471", itemName: "Capacitor 100uF" }] },
    { id: 3, itemCode: "SUP-1005", category: "Operational", severity: "WARNING",
      description: "Ganges Electronics India is classified as MEDIUM RISK with a quality score of 70% and composite score of 67.0.",
      detectedAt: "2026-07-23T11:05:00", confidenceScore: 74,
      recommendation: "Schedule a performance review and increase monitoring frequency.",
      affectedItems: [] },
  ];

  // ── Users / Roles ────────────────────────────────────────────────────────
  // Matches UsersApiController -> UserManagementController's real (raw,
  // non-enveloped) shapes: /api/users -> {users, total}, /api/roles -> {roles}.
  var USERS = [
    { userId: "kumar",   userName: "Kumar Swamy",     emailId: "kumar@scip.io",   isEnabled: true,  hasPassword: true,  roleName: "ADMIN" },
    { userId: "ops",     userName: "Ops Manager",     emailId: "ops@scip.io",     isEnabled: true,  hasPassword: true,  roleName: "BUS_ADMIN" },
    { userId: "buyer",   userName: "Priya Sharma",    emailId: "buyer@scip.io",   isEnabled: true,  hasPassword: true,  roleName: "BUS_ADMIN" },
    { userId: "analyst", userName: "Alex Analyst",    emailId: "analyst@scip.io", isEnabled: true,  hasPassword: true,  roleName: "GUEST" },
    { userId: "viewer",  userName: "Sam Viewer",      emailId: "viewer@scip.io",  isEnabled: false, hasPassword: true,  roleName: "GUEST" },
  ];

  var ROLES = [
    { roleName: "ADMIN",     roleKey: 1 },
    { roleName: "BUS_ADMIN", roleKey: 2 },
    { roleName: "GUEST",     roleKey: 3 },
  ];

  // ── Forecasts ────────────────────────────────────────────────────────────
  // List matches ForecastRestController's real raw ForecastSummary array.
  // The detail "values" time series has no real backend endpoint yet — it's
  // a demo-only synthesis so the "90-day projections" story on the dashboard
  // actually shows something, same pattern as the Reports trend lines.
  var FORECASTS = [
    { forecastKey: 1, externalId: "FC-2026-Q3-01", forecastType: "DEMAND", calendarName: "2026-Q3", forecastModel: "PROPHET",
      status: "APPROVED", description: "Q3 demand forecast — Connector Housing", itemNumber: "ITEM-2048",
      effectiveFrom: "2026-07-01", effectiveTo: "2026-09-30", isActive: true },
    { forecastKey: 2, externalId: "FC-2026-Q3-02", forecastType: "DEMAND", calendarName: "2026-Q3", forecastModel: "PROPHET",
      status: "SUBMITTED", description: "Q3 demand forecast — PCB Substrate", itemNumber: "ITEM-3312",
      effectiveFrom: "2026-07-01", effectiveTo: "2026-09-30", isActive: true },
    { forecastKey: 3, externalId: "FC-2026-Q4-01", forecastType: "SUPPLY", calendarName: "2026-Q4", forecastModel: "ARIMA",
      status: "DRAFT", description: "Q4 supply forecast — Capacitor 100uF", itemNumber: "ITEM-4471",
      effectiveFrom: "2026-10-01", effectiveTo: "2026-12-31", isActive: true },
  ];

  // ── Audit logs ───────────────────────────────────────────────────────────
  // Matches AuditLog entity fields (see AuditAspect.java): entityType,
  // entityId, action, performedBy, details, loggedAt.
  var AUDIT_LOGS = [
    { id: 1, entityType: "PurchaseOrder",  entityId: "5003", action: "RECEIVE", performedBy: "kumar", details: "SUCCESS", loggedAt: "2026-07-27T07:09:55" },
    { id: 2, entityType: "SupplierRating", entityId: "SUP-1004", action: "CREATE",  performedBy: "kumar", details: "SUCCESS", loggedAt: "2026-07-27T04:09:55" },
    { id: 3, entityType: "Inventory",      entityId: "ITEM-4471", action: "ADJUST",  performedBy: "ops",   details: "SUCCESS", loggedAt: "2026-07-27T01:09:55" },
    { id: 4, entityType: "CostRecord",     entityId: "103", action: "CREATE",  performedBy: "kumar", details: "SUCCESS", loggedAt: "2026-07-26T22:09:55" },
    { id: 5, entityType: "PurchaseOrder",  entityId: "5001", action: "SUBMIT",   performedBy: "ops",   details: "SUCCESS", loggedAt: "2026-07-26T19:09:55" },
    { id: 6, entityType: "PurchaseOrder",  entityId: "5002", action: "CONFIRM",  performedBy: "kumar", details: "SUCCESS", loggedAt: "2026-07-25T13:40:00" },
    { id: 7, entityType: "CostRecord",     entityId: "104", action: "APPROVE",  performedBy: "kumar", details: "SUCCESS", loggedAt: "2026-07-26T09:30:00" },
  ];

  function forecastValues(forecastKey) {
    var base = { 1: 1200, 2: 800, 3: 3000 }[forecastKey] || 1000;
    var months = ["Jan","Feb","Mar","Apr","May","Jun"];
    return months.map(function (m, i) {
      var forecastQty = Math.round(base * (1 + i * 0.04));
      var actualQty = i < 4 ? Math.round(forecastQty * (0.92 + ((i * 7) % 10) / 100)) : null;
      return { periodLabel: m, period: m, forecastQty: forecastQty, actualQty: actualQty };
    });
  }

  // ── Route table ──────────────────────────────────────────────────────────
  // Each entry: [regex matching the request path, handler(match, params, method, body)]
  var routes = [
    [/\/api\/suppliers\/stats$/, function () { return { data: supplierStats() }; }],
    [/\/api\/suppliers\/([^/]+)\/deliveries$/, function (m) { return { data: supplierDeliveries(m[1]) }; }],
    [/\/api\/suppliers\/dependencies$/, function () { return { data: DEPENDENCIES }; }],
    [/\/api\/suppliers\/([^/]+)\/cascaded-risk$/, function (m) { return { data: computeCascadedRisk(m[1]) }; }],
    [/\/api\/suppliers\/([^/]+)\/structural-risk$/, function (m) { return { data: computeStructuralRisk(m[1]) }; }],
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

    [/\/api\/ai\/anomalies$/, function () { return { anomalies: ANOMALIES, total: ANOMALIES.length }; }],
    [/\/api\/eval\/results$/, function () { return { data: [] }; }],

    [/\/api\/costs\/stats$/, function () {
      var byStatus = {};
      COST_RECORDS.forEach(function (r) { byStatus[r.status] = (byStatus[r.status] || 0) + 1; });
      return { data: { totalRecords: COST_RECORDS.length, byStatus: byStatus,
        pendingCount: COST_RECORDS.filter(function (r) { return r.status === "PENDING_APPROVAL"; }).length } };
    }],
    [/\/api\/costs\/([^/]+)\/submit$/, function (m) {
      var r = COST_RECORDS.find(function (x) { return String(x.id) === m[1]; });
      if (r) { r.status = "PENDING_APPROVAL"; r.submittedDate = new Date().toISOString(); }
      return { data: r || COST_RECORDS[0] };
    }],
    [/\/api\/costs\/([^/]+)\/approve$/, function (m) {
      var r = COST_RECORDS.find(function (x) { return String(x.id) === m[1]; });
      if (r) { r.status = "APPROVED"; r.approvedBy = "kumar"; r.approvedDate = new Date().toISOString(); }
      return { data: r || COST_RECORDS[0] };
    }],
    [/\/api\/costs\/([^/]+)\/reject$/, function (m, params, method, body) {
      var r = COST_RECORDS.find(function (x) { return String(x.id) === m[1]; });
      var reason = "No reason given";
      try { reason = JSON.parse(body || "{}").reason || reason; } catch (e) {}
      if (r) { r.status = "REJECTED"; r.rejectionReason = reason; }
      return { data: r || COST_RECORDS[0] };
    }],
    [/\/api\/costs\/([^/]+)$/, function (m) {
      var r = COST_RECORDS.find(function (x) { return String(x.id) === m[1]; });
      return { data: r || COST_RECORDS[0] };
    }],
    [/\/api\/costs$/, function (_m, params, method, body) {
      if (method === "POST") {
        var req = {};
        try { req = JSON.parse(body || "{}"); } catch (e) {}
        var itemCode = req.itemKey || req.itemCode || "ITEM-2048";
        var created = {
          id: nextCostId++, itemCode: itemCode, item: { itemCode: itemCode, description: itemCode },
          versionNumber: 1, proposedCost: parseFloat(req.proposedCost) || 0, previousCost: null, changePercent: null,
          status: "DRAFT", justification: req.justification || "", rejectionReason: null,
          createdBy: "kumar", createdDate: new Date().toISOString(), submittedDate: null,
          approvedBy: null, approvedDate: null,
        };
        COST_RECORDS.unshift(created);
        return { data: created };
      }
      var q = (params && params.search || "").toLowerCase();
      var list = q ? COST_RECORDS.filter(function (r) {
        return (r.itemCode || "").toLowerCase().indexOf(q) !== -1 ||
               (r.justification || "").toLowerCase().indexOf(q) !== -1;
      }) : COST_RECORDS;
      return { data: { content: list, totalPages: 1 } };
    }],
    [/\/api\/cost-records/, function () { return { data: COST_RECORDS }; }],

    [/\/api\/purchase-orders\/(\d+)$/, function (m) {
      var po = PURCHASE_ORDERS.find(function (x) { return String(x.id) === m[1]; });
      return { data: po || PURCHASE_ORDERS[0] };
    }],
    [/\/api\/purchase-orders/, function () { return { data: PURCHASE_ORDERS }; }],
    [/\/api\/bom\/([^/]+)$/, function (m) {
      return BOMS.find(function (x) { return x.bomKey === m[1]; }) || null;
    }],
    [/\/api\/bom/, function () { return BOMS; }],
    [/\/api\/inventory\/[^/]+\/transactions$/, function () { return { data: [] }; }],
    [/\/api\/inventory/, function () { return { data: INVENTORY }; }],

    [/\/api\/forecasts\/(\d+)$/, function (m) {
      var f = FORECASTS.find(function (x) { return String(x.forecastKey) === m[1]; });
      if (!f) return null;
      var d = {};
      for (var k in f) d[k] = f[k];
      d.values = forecastValues(f.forecastKey);
      return d;
    }],
    [/\/api\/forecasts/, function () { return FORECASTS; }],
    [/\/api\/users$/, function () { return { users: USERS, total: USERS.length }; }],
    [/\/api\/roles$/, function () { return { roles: ROLES }; }],
    [/\/api\/audit-logs\/user\/([^/]+)$/, function (m) {
      return { data: AUDIT_LOGS.filter(function (l) { return l.performedBy === m[1]; }) };
    }],
    [/\/api\/audit-logs/, function (_m, params) {
      var list = AUDIT_LOGS;
      if (params && params.entityType) {
        list = list.filter(function (l) { return l.entityType === params.entityType; });
      }
      return { data: list };
    }],

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
