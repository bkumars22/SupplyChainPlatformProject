// Mock data for GitHub Pages demo mode — no backend required

export const MOCK_SUPPLIERS = [
  {
    supplierId: "SUPP-001",
    supplierName: "TechParts India Pvt Ltd",
    country: "India",
    tier: "PROBATION",
    compositeScore: 38.4,
    otdScore: 42.0,
    qualityScore: 36.5,
    responsivenessScore: 44.2,
    totalDeliveries: 120,
    onTimeDeliveries: 50,
    atRisk: true,
    riskLevel: "HIGH",
    anomaly: true,
  },
  {
    supplierId: "SUPP-002",
    supplierName: "GlobalComp Singapore",
    country: "Singapore",
    tier: "PREFERRED",
    compositeScore: 91.2,
    otdScore: 91.0,
    qualityScore: 92.5,
    responsivenessScore: 90.1,
    totalDeliveries: 310,
    onTimeDeliveries: 282,
    atRisk: false,
    riskLevel: "LOW",
    anomaly: false,
  },
  {
    supplierId: "SUPP-003",
    supplierName: "SwiftLogix Dubai",
    country: "UAE",
    tier: "APPROVED",
    compositeScore: 67.8,
    otdScore: 67.0,
    qualityScore: 70.3,
    responsivenessScore: 66.1,
    totalDeliveries: 185,
    onTimeDeliveries: 124,
    atRisk: false,
    riskLevel: "MEDIUM",
    anomaly: false,
  },
  {
    supplierId: "SUPP-004",
    supplierName: "PrecisionMfg Chennai",
    country: "India",
    tier: "PROBATION",
    compositeScore: 29.6,
    otdScore: 38.0,
    qualityScore: 22.8,
    responsivenessScore: 28.0,
    totalDeliveries: 95,
    onTimeDeliveries: 36,
    atRisk: true,
    riskLevel: "CRITICAL",
    anomaly: true,
  },
  {
    supplierId: "SUPP-005",
    supplierName: "NexusParts Germany",
    country: "Germany",
    tier: "PREFERRED",
    compositeScore: 88.7,
    otdScore: 88.0,
    qualityScore: 91.4,
    responsivenessScore: 86.7,
    totalDeliveries: 260,
    onTimeDeliveries: 229,
    atRisk: false,
    riskLevel: "LOW",
    anomaly: false,
  },
];

export const MOCK_SUPPLIER_DELIVERIES = {
  "SUPP-001": [
    { poNumber: "PO-2026-1001", itemCode: "PCB-001", promisedDate: "2026-03-10", actualDate: "2026-03-18", qtyOrdered: 500, qtyReceived: 480, status: "DELAYED", delayDays: 8 },
    { poNumber: "PO-2026-1002", itemCode: "CHIP-002", promisedDate: "2026-04-01", actualDate: "2026-04-12", qtyOrdered: 1000, qtyReceived: 950, status: "DELAYED", delayDays: 11 },
    { poNumber: "PO-2026-1003", itemCode: "PCB-001", promisedDate: "2026-05-15", actualDate: "2026-05-15", qtyOrdered: 300, qtyReceived: 300, status: "ON_TIME", delayDays: 0 },
  ],
  "SUPP-002": [
    { poNumber: "PO-2026-2001", itemCode: "CHIP-001", promisedDate: "2026-02-20", actualDate: "2026-02-20", qtyOrdered: 2000, qtyReceived: 2000, status: "ON_TIME", delayDays: 0 },
    { poNumber: "PO-2026-2002", itemCode: "SENSOR-X", promisedDate: "2026-03-05", actualDate: "2026-03-05", qtyOrdered: 800, qtyReceived: 800, status: "ON_TIME", delayDays: 0 },
    { poNumber: "PO-2026-2003", itemCode: "CHIP-001", promisedDate: "2026-04-18", actualDate: "2026-04-19", qtyOrdered: 1500, qtyReceived: 1500, status: "DELAYED", delayDays: 1 },
  ],
  "SUPP-003": [
    { poNumber: "PO-2026-3001", itemCode: "LOG-BOX-A", promisedDate: "2026-03-01", actualDate: "2026-03-04", qtyOrdered: 50, qtyReceived: 50, status: "DELAYED", delayDays: 3 },
    { poNumber: "PO-2026-3002", itemCode: "LOG-BOX-B", promisedDate: "2026-04-10", actualDate: "2026-04-10", qtyOrdered: 75, qtyReceived: 75, status: "ON_TIME", delayDays: 0 },
  ],
  "SUPP-004": [
    { poNumber: "PO-2026-4001", itemCode: "HYD-VALVE", promisedDate: "2026-02-15", actualDate: "2026-03-02", qtyOrdered: 200, qtyReceived: 180, status: "DELAYED", delayDays: 15 },
    { poNumber: "PO-2026-4002", itemCode: "SENSOR-Y", promisedDate: "2026-03-20", actualDate: "2026-04-01", qtyOrdered: 400, qtyReceived: 360, status: "DELAYED", delayDays: 12 },
  ],
  "SUPP-005": [
    { poNumber: "PO-2026-5001", itemCode: "CAP-100UF", promisedDate: "2026-03-08", actualDate: "2026-03-08", qtyOrdered: 5000, qtyReceived: 5000, status: "ON_TIME", delayDays: 0 },
    { poNumber: "PO-2026-5002", itemCode: "RES-10K", promisedDate: "2026-04-22", actualDate: "2026-04-22", qtyOrdered: 10000, qtyReceived: 10000, status: "ON_TIME", delayDays: 0 },
    { poNumber: "PO-2026-5003", itemCode: "CAP-100UF", promisedDate: "2026-05-30", actualDate: "2026-05-30", qtyOrdered: 5000, qtyReceived: 5000, status: "ON_TIME", delayDays: 0 },
  ],
};

export const MOCK_DASHBOARD_KPIS = {
  activeAlerts: 3,
  totalBoms: 10,
  pendingApprovals: 1,
  atRiskSuppliers: 2,
  unreadAlerts: 3,
  activeBoms: 10,
  pendingBoms: 1,
  totalSuppliers: 47,
  avgOtd: 73.4,
  costSavings: 2400000,
  // Phase 1-5 KPIs
  lowStockCount: 4,
  openPOCount: 2,
  avgSupplierRating: 3.8,
  totalInventorySkus: 8,
};

export const MOCK_ALERTS = [
  {
    id: "ALT-001",
    alertLabel: "TechParts India OTD Below Threshold",
    alertType: "CRITICAL_RISK",
    shortSummary: "OTD score dropped to 42% — below 45% minimum threshold",
    longSummary: "High risk supplier detected. OTD score of 42% indicates systemic delivery failures. Recommend immediate supplier review and qualification of backup supplier.",
    created: "2026-06-18",
  },
  {
    id: "ALT-002",
    alertLabel: "PrecisionMfg Defect Rate Exceeded",
    alertType: "CRITICAL_RISK",
    shortSummary: "Defect rate 11.7% — 3x above acceptable 4% threshold",
    longSummary: "Critical quality issue detected. Defect rate of 11.7% is 3x above acceptable threshold. Recommend production hold and root cause analysis.",
    created: "2026-06-17",
  },
  {
    id: "ALT-003",
    alertLabel: "Lead Time Variance Increased 40%",
    alertType: "WARNING",
    shortSummary: "Supply chain disruption detected in logistics routing",
    longSummary: "Supply chain disruption detected. Lead time variance suggests logistics bottleneck. Recommend route diversification.",
    created: "2026-06-16",
  },
];

export const MOCK_COST_RECORDS = [
  {
    id: "CR-2026-001",
    itemKey: "CR-2026-001",
    itemCode: "RAW-MAT-Q2",
    proposedCost: 340000,
    previousCost: 310000,
    status: "APPROVED",
    justification: "Raw materials Q2 procurement — bulk discount negotiated with 3 vendors",
    effectiveDate: "2026-04-01",
    createdBy: "kumar",
  },
  {
    id: "CR-2026-002",
    itemKey: "CR-2026-002",
    itemCode: "LOG-OPT-2026",
    proposedCost: 89500,
    previousCost: 120000,
    status: "DRAFT",
    justification: "Logistics optimization via new routing algorithm — 25% cost reduction",
    effectiveDate: "2026-07-01",
    createdBy: "kumar",
  },
  {
    id: "CR-2026-003",
    itemKey: "CR-2026-003",
    itemCode: "AUDIT-PROG",
    proposedCost: 45200,
    previousCost: 38000,
    status: "APPROVED",
    justification: "Supplier audit program expanded to cover high-risk tier suppliers",
    effectiveDate: "2026-05-15",
    createdBy: "kumar",
  },
  {
    id: "CR-2026-004",
    itemKey: "CR-2026-004",
    itemCode: "EMRG-PROC",
    proposedCost: 127800,
    previousCost: null,
    status: "PENDING_APPROVAL",
    justification: "Emergency procurement triggered by TechParts India supply disruption",
    effectiveDate: "2026-06-20",
    createdBy: "kumar",
  },
];

export const MOCK_BOMS = [
  {
    bomKey: "BOM-2026-001",
    bomName: "PCB Assembly v2.3",
    bomExternalId: "PCB-ASM-V23",
    bomDescription: "Main PCB assembly for industrial controller series",
    item: { itemNumber: "FG-PCB-V23" },
    status: "APPROVED",
    lines: [
      { itemNumber: "CAP-100UF", description: "100µF electrolytic capacitor", itemQuantity: 12, rollupFlag: "Y", notes: "Rated 50V" },
      { itemNumber: "RES-10K", description: "10kΩ resistor 1/4W", itemQuantity: 48, rollupFlag: "Y", notes: "" },
      { itemNumber: "IC-555", description: "NE555 timer IC", itemQuantity: 4, rollupFlag: "Y", notes: "SMD package" },
      { itemNumber: "CONN-DB9", description: "DB9 connector male", itemQuantity: 2, rollupFlag: "N", notes: "Panel mount" },
      { itemNumber: "PCB-BARE", description: "Bare PCB substrate FR4", itemQuantity: 1, rollupFlag: "Y", notes: "2oz copper" },
    ],
  },
  {
    bomKey: "BOM-2026-002",
    bomName: "Hydraulic Module A",
    bomExternalId: "HYD-MOD-A",
    bomDescription: "Hydraulic control module for heavy machinery line",
    item: { itemNumber: "FG-HYD-A" },
    status: "APPROVED",
    lines: [
      { itemNumber: "HYD-VALVE", description: "Solenoid valve 24V DC", itemQuantity: 3, rollupFlag: "Y", notes: "IP67 rated" },
      { itemNumber: "SENSOR-P", description: "Pressure sensor 0-300 bar", itemQuantity: 2, rollupFlag: "Y", notes: "" },
      { itemNumber: "PUMP-GEAR", description: "Gear pump 15L/min", itemQuantity: 1, rollupFlag: "Y", notes: "Replace every 5000h" },
    ],
  },
  {
    bomKey: "BOM-2026-003",
    bomName: "Sensor Array Kit",
    bomExternalId: "SENS-ARR-KT",
    bomDescription: "Multi-sensor array kit for environmental monitoring",
    item: { itemNumber: "FG-SENS-ARR" },
    status: "CONDITIONAL",
    lines: [
      { itemNumber: "SENSOR-T", description: "Temperature sensor NTC 10kΩ", itemQuantity: 8, rollupFlag: "Y", notes: "-40 to +125°C" },
      { itemNumber: "SENSOR-H", description: "Humidity sensor SHT31", itemQuantity: 4, rollupFlag: "Y", notes: "±2% RH" },
      { itemNumber: "SENSOR-Y", description: "Vibration sensor MEMS", itemQuantity: 4, rollupFlag: "Y", notes: "3-axis" },
      { itemNumber: "CABLE-SHLD", description: "Shielded cable 4-core 1m", itemQuantity: 8, rollupFlag: "N", notes: "" },
    ],
  },
];

export const MOCK_USERS = [
  { userId: "admin", userName: "Admin User", emailId: "admin@scip.com", roleName: "ADMIN", isEnabled: true, hasPassword: true },
  { userId: "demo", userName: "Demo User", emailId: "demo@scip.com", roleName: "BUS_ADMIN", isEnabled: true, hasPassword: true },
  { userId: "qa.engineer", userName: "QA Engineer", emailId: "qa@scip.com", roleName: "GUEST", isEnabled: true, hasPassword: true },
  { userId: "scm.manager", userName: "Supply Chain Manager", emailId: "scm@scip.com", roleName: "BUS_ADMIN", isEnabled: true, hasPassword: true },
];

export const MOCK_ROLES = [
  { roleName: "ADMIN" },
  { roleName: "BUS_ADMIN" },
  { roleName: "GUEST" },
];

export const MOCK_FORECASTS = [
  {
    forecastKey: "FC-2026-001",
    itemNumber: "PCB-001",
    forecastType: "DEMAND",
    effectiveFrom: "2026-01-01",
    effectiveTo: "2026-06-30",
    values: [
      { periodLabel: "Jan 2026", forecastQty: 1200, actualQty: 1150, variance: -50, variancePct: -4.2 },
      { periodLabel: "Feb 2026", forecastQty: 1350, actualQty: 1410, variance: 60, variancePct: 4.4 },
      { periodLabel: "Mar 2026", forecastQty: 1400, actualQty: 1380, variance: -20, variancePct: -1.4 },
      { periodLabel: "Apr 2026", forecastQty: 1500, actualQty: 1620, variance: 120, variancePct: 8.0 },
      { periodLabel: "May 2026", forecastQty: 1450, actualQty: 1390, variance: -60, variancePct: -4.1 },
      { periodLabel: "Jun 2026", forecastQty: 1600, actualQty: null, variance: null, variancePct: null },
    ],
  },
  {
    forecastKey: "FC-2026-002",
    itemNumber: "CHIP-001",
    forecastType: "SUPPLY",
    effectiveFrom: "2026-01-01",
    effectiveTo: "2026-06-30",
    values: [
      { periodLabel: "Jan 2026", forecastQty: 5000, actualQty: 4800, variance: -200, variancePct: -4.0 },
      { periodLabel: "Feb 2026", forecastQty: 5500, actualQty: 5500, variance: 0, variancePct: 0.0 },
      { periodLabel: "Mar 2026", forecastQty: 5200, actualQty: 5800, variance: 600, variancePct: 11.5 },
      { periodLabel: "Apr 2026", forecastQty: 6000, actualQty: 5200, variance: -800, variancePct: -13.3 },
      { periodLabel: "May 2026", forecastQty: 5800, actualQty: 6100, variance: 300, variancePct: 5.2 },
    ],
  },
  {
    forecastKey: "FC-2026-003",
    itemNumber: "HYD-VALVE",
    forecastType: "INVENTORY",
    effectiveFrom: "2026-03-01",
    effectiveTo: "2026-08-31",
    values: [
      { periodLabel: "Mar 2026", forecastQty: 200, actualQty: 185, variance: -15, variancePct: -7.5 },
      { periodLabel: "Apr 2026", forecastQty: 220, actualQty: 240, variance: 20, variancePct: 9.1 },
      { periodLabel: "May 2026", forecastQty: 210, actualQty: 195, variance: -15, variancePct: -7.1 },
      { periodLabel: "Jun 2026", forecastQty: 230, actualQty: null, variance: null, variancePct: null },
    ],
  },
];

export const MOCK_ANOMALIES = [
  {
    id: "ANO-001",
    severity: "CRITICAL",
    category: "Supplier Risk",
    itemCode: "SUPP-001",
    description: "TechParts India OTD score dropped 18% in 30 days — systemic delivery failure pattern detected",
    confidenceScore: 94,
    detectedAt: "2026-06-18T09:14:22",
    affectedItems: [
      { itemCode: "PCB-001", itemName: "PCB Assembly v2.3" },
      { itemCode: "CHIP-002", itemName: "Microcontroller Unit" },
    ],
    recommendation: "Qualify backup supplier immediately. Issue supplier performance notice. Increase safety stock for PCB-001 by 30%.",
  },
  {
    id: "ANO-002",
    severity: "CRITICAL",
    category: "Quality",
    itemCode: "SUPP-004",
    description: "PrecisionMfg defect rate 11.7% — 3x above threshold. Batch rejection events spiking",
    confidenceScore: 91,
    detectedAt: "2026-06-17T14:30:00",
    affectedItems: [
      { itemCode: "HYD-VALVE", itemName: "Solenoid Valve 24V" },
      { itemCode: "SENSOR-Y", itemName: "Vibration Sensor MEMS" },
    ],
    recommendation: "Place hold on new orders from PrecisionMfg Chennai. Initiate root cause analysis. Source HYD-VALVE from NexusParts Germany.",
  },
  {
    id: "ANO-003",
    severity: "WARNING",
    category: "Lead Time",
    itemCode: "LOG-ROUTE-EU",
    description: "EU logistics corridor lead time variance increased 40% — likely port congestion impact",
    confidenceScore: 76,
    detectedAt: "2026-06-16T11:45:00",
    affectedItems: [
      { itemCode: "CAP-100UF", itemName: "Electrolytic Capacitor 100µF" },
      { itemCode: "RES-10K", itemName: "Resistor 10kΩ" },
    ],
    recommendation: "Activate alternate logistics route via Rotterdam. Pre-position 2-week buffer stock for EU-sourced components.",
  },
  {
    id: "ANO-004",
    severity: "WARNING",
    category: "Cost Variance",
    itemCode: "RAW-MAT-STEEL",
    description: "Steel raw material price 22% above Q1 baseline — commodity volatility detected",
    confidenceScore: 68,
    detectedAt: "2026-06-15T08:00:00",
    affectedItems: [
      { itemCode: "HYD-VALVE", itemName: "Solenoid Valve 24V" },
    ],
    recommendation: "Review open POs for steel-dependent components. Consider forward contracts to hedge Q3 exposure.",
  },
];

// ── Reports mock data ─────────────────────────────────────────────────────────
export const MOCK_REPORT_SUPPLIER_PERF = {
  months: ['Jan','Feb','Mar','Apr','May','Jun'],
  suppliers: [
    { name:'TechParts India',    country:'India',     tier:'PROBATION', atRisk:true,  color:'#dc2626', otd:42, quality:37, composite:38, otdTrend:[55,50,48,42,45,42], qualityTrend:[70,65,62,60,58,37], compositeTrend:[60,55,52,48,50,38] },
    { name:'GlobalComp SG',      country:'Singapore', tier:'PREFERRED', atRisk:false, color:'#16a34a', otd:91, quality:93, composite:91, otdTrend:[92,93,91,90,91,91], qualityTrend:[93,94,92,91,93,93], compositeTrend:[92,93,91,90,92,91] },
    { name:'SwiftLogix Dubai',   country:'UAE',       tier:'APPROVED',  atRisk:false, color:'#d97706', otd:67, quality:70, composite:68, otdTrend:[72,70,68,67,65,67], qualityTrend:[74,71,70,70,68,70], compositeTrend:[72,70,68,68,66,68] },
    { name:'PrecisionMfg Chennai',country:'India',    tier:'PROBATION', atRisk:true,  color:'#ef4444', otd:38, quality:23, composite:30, otdTrend:[50,45,40,38,36,38], qualityTrend:[35,30,28,25,22,23], compositeTrend:[40,35,32,30,28,30] },
    { name:'NexusParts Germany', country:'Germany',   tier:'PREFERRED', atRisk:false, color:'#0891b2', otd:88, quality:91, composite:89, otdTrend:[90,89,88,88,87,88], qualityTrend:[92,91,91,90,91,91], compositeTrend:[91,90,89,89,88,89] },
  ],
};

export const MOCK_REPORT_COST_VARIANCE = [
  { item:'RAW-MAT-Q2',     category:'Raw Materials', budget:310000, actual:340000, variance:30000,  pct:9.7,  status:'OVER'  },
  { item:'CHIP-001-BATCH', category:'Electronics',   budget:180000, actual:165000, variance:-15000, pct:-8.3, status:'UNDER' },
  { item:'PCB-ASSEMBLY',   category:'Electronics',   budget:420000, actual:398000, variance:-22000, pct:-5.2, status:'UNDER' },
  { item:'MECH-PARTS-Q1', category:'Mechanical',    budget:560000, actual:612000, variance:52000,  pct:9.3,  status:'OVER'  },
  { item:'PACK-MAT-2026',  category:'Packaging',     budget:95000,  actual:87500,  variance:-7500,  pct:-7.9, status:'UNDER' },
  { item:'SENSOR-BATCH3',  category:'Electronics',   budget:230000, actual:245000, variance:15000,  pct:6.5,  status:'OVER'  },
];

export const MOCK_REPORT_ALERT_SUMMARY = {
  total: 12, resolved: 8, avgResolutionHrs: 18.4,
  byType: [
    { type:'Critical Risk', count:5, color:'#dc2626' },
    { type:'Warning',       count:4, color:'#d97706' },
    { type:'Info',          count:3, color:'#2563eb' },
  ],
  byMonth: [
    { month:'Jan', critical:2, warning:1, info:0 },
    { month:'Feb', critical:1, warning:2, info:1 },
    { month:'Mar', critical:3, warning:1, info:2 },
    { month:'Apr', critical:2, warning:3, info:1 },
    { month:'May', critical:1, warning:2, info:2 },
    { month:'Jun', critical:5, warning:4, info:3 },
  ],
};

export const MOCK_REPORT_ACTIVITY = [
  { user:'kumar',  action:'Approved cost record CR-2026-001',         module:'Cost Records', time:'2026-06-18 09:15' },
  { user:'admin',  action:'Created user viewer01',                    module:'User Mgmt',    time:'2026-06-17 14:22' },
  { user:'kumar',  action:'Dismissed alert ALT-003',                  module:'Alerts',       time:'2026-06-17 11:05' },
  { user:'viewer', action:'Exported supplier performance report',     module:'Reports',      time:'2026-06-16 16:30' },
  { user:'kumar',  action:'Created BOM BOM-2026-011',                module:'BOM',          time:'2026-06-16 10:45' },
  { user:'admin',  action:'Reset demo database via admin endpoint',   module:'Admin',        time:'2026-06-15 18:30' },
  { user:'kumar',  action:'Added delivery for GlobalComp Singapore',  module:'Suppliers',    time:'2026-06-15 14:20' },
  { user:'viewer', action:'Viewed Q2 forecasting report',             module:'Forecasting',  time:'2026-06-14 09:00' },
];

export const MOCK_TEST_RESULTS = {
  total: 51,
  passed: 51,
  failed: 0,
  skipped: 0,
  duration: 18420,
  timestamp: new Date().toISOString(),
  tests: [
    { group: "Auth Module", name: "BB-AUTH-01: Valid login returns JWT token", status: "passed", duration: 312 },
    { group: "Auth Module", name: "BB-AUTH-02: Wrong password returns 401", status: "passed", duration: 218 },
    { group: "Auth Module", name: "BB-AUTH-03: Unknown user returns 401", status: "passed", duration: 199 },
    { group: "Auth Module", name: "BB-AUTH-04: No token returns 403", status: "passed", duration: 185 },
    { group: "Auth Module", name: "BB-AUTH-05: Malformed token returns 403", status: "passed", duration: 201 },
    { group: "Auth Module", name: "SEC-01: SQL injection in login blocked", status: "passed", duration: 244 },
    { group: "Item Master", name: "BB-ITEM-01: GET returns all 10 items", status: "passed", duration: 156 },
    { group: "Item Master", name: "BB-ITEM-02: Seed data has PCB-001 CHIP-001 FG-LAPTOP-X1", status: "passed", duration: 143 },
    { group: "Item Master", name: "BB-ITEM-03: PCB-001 is COMPONENT type", status: "passed", duration: 138 },
    { group: "Item Master", name: "BB-ITEM-04: POST /api/items returns 201", status: "passed", duration: 290 },
    { group: "Item Master", name: "NEG-ITEM-01: GET without auth returns 403", status: "passed", duration: 122 },
    { group: "BOM", name: "BB-BOM-01: GET returns paginated BOM list", status: "passed", duration: 178 },
    { group: "BOM", name: "BB-BOM-02: Laptop X1 BOM has FG-LAPTOP-X1", status: "passed", duration: 162 },
    { group: "BOM", name: "BB-BOM-03: GET /api/bom/201 returns detail", status: "passed", duration: 195 },
    { group: "BOM", name: "NEG-BOM-01: Non-existent BOM key returns 404", status: "passed", duration: 133 },
    { group: "Cost Records", name: "BB-COST-01: GET /api/cost-records returns 200", status: "passed", duration: 167 },
    { group: "Cost Records", name: "BB-COST-02: POST cost record creates DRAFT", status: "passed", duration: 280 },
    { group: "Cost Records", name: "BB-COST-03: Submit moves to PENDING_APPROVAL", status: "passed", duration: 245 },
    { group: "Cost Records", name: "BB-COST-04: Approve updates status to APPROVED", status: "passed", duration: 263 },
    { group: "Cost Records", name: "NEG-COST-01: GET without auth returns 403", status: "passed", duration: 119 },
    { group: "Suppliers", name: "BB-SUP-01: GET returns supplier list", status: "passed", duration: 188 },
    { group: "Suppliers", name: "BB-SUP-02: Supplier fields present: name, otdScore, tier", status: "passed", duration: 142 },
    { group: "Suppliers", name: "BB-SUP-03: At-risk suppliers correctly flagged", status: "passed", duration: 155 },
    { group: "Suppliers", name: "BB-SUP-04: Deliveries endpoint returns history", status: "passed", duration: 211 },
    { group: "Suppliers", name: "BB-SUP-05: Recalculate tiers endpoint returns 200", status: "passed", duration: 334 },
    { group: "Suppliers", name: "NEG-SUP-01: GET without auth returns 403", status: "passed", duration: 121 },
    { group: "Users", name: "BB-USR-01: GET /api/admin/users returns list", status: "passed", duration: 175 },
    { group: "Users", name: "BB-USR-02: GET /api/admin/roles returns roles", status: "passed", duration: 143 },
    { group: "Users", name: "BB-USR-03: POST create user returns 201", status: "passed", duration: 308 },
    { group: "Users", name: "NEG-USR-01: Duplicate user ID rejected with 409", status: "passed", duration: 189 },
    { group: "Users", name: "SEC-USR-01: Password not returned in user list", status: "passed", duration: 138 },
    { group: "Alerts", name: "BB-ALERT-01: GET /api/alerts/active returns alerts", status: "passed", duration: 161 },
    { group: "Alerts", name: "BB-ALERT-02: Dismiss removes alert from list", status: "passed", duration: 234 },
    { group: "Alerts", name: "BB-ALERT-03: Dashboard activeAlerts count is a number", status: "passed", duration: 148 },
    { group: "Alerts", name: "NEG-ALERT-01: Dismiss non-existent alert returns 404", status: "passed", duration: 172 },
    { group: "Alerts", name: "BB-ALERT-04: All alert filter types work", status: "passed", duration: 155 },
    { group: "Dashboard", name: "BB-DASH-01: GET summary returns KPI fields", status: "passed", duration: 143 },
    { group: "Dashboard", name: "BB-DASH-02: All KPI values are numbers", status: "passed", duration: 118 },
    { group: "Dashboard", name: "BB-DASH-03: ActiveAlerts matches alerts endpoint count", status: "passed", duration: 167 },
    { group: "Dashboard", name: "BB-DASH-04: Quick actions navigate correctly", status: "passed", duration: 299 },
    { group: "Dashboard", name: "BB-DASH-05: Dismiss alert updates dashboard count", status: "passed", duration: 278 },
    { group: "Eval Module", name: "EVAL-01: /eval/results endpoint returns 200", status: "passed", duration: 422 },
    { group: "Eval Module", name: "EVAL-02: Overall score field present on all rows", status: "passed", duration: 338 },
    { group: "Eval Module", name: "EVAL-03: Consistency score between 0 and 1", status: "passed", duration: 311 },
    { group: "Eval Module", name: "EVAL-04: Response latency under 500ms", status: "passed", duration: 289 },
    { group: "Eval Module", name: "EVAL-05: /eval/run returns report + results", status: "passed", duration: 1240 },
    { group: "Eval Module", name: "EVAL-06: Pass threshold correctly applied at 0.7", status: "passed", duration: 305 },
    { group: "Eval Module", name: "EVAL-07: Heuristic mode returns consistent results", status: "passed", duration: 418 },
    { group: "Eval Module", name: "EVAL-08: LLM judge mode uses claude-haiku", status: "passed", duration: 892 },
    { group: "Eval Module", name: "EVAL-09: AI service rate limiting returns 429", status: "passed", duration: 267 },
    { group: "Eval Module", name: "EVAL-10: Eval results persist across sessions", status: "passed", duration: 344 },
  ],
};

export const MOCK_EVAL_SCORES = [
  {
    test_case_id: "EVAL-001",
    category: "supplier_risk",
    input: "Explain the risk profile of TechParts India with OTD 42% and defect rate 8.3%",
    scores: { overall_score: 0.91, answer_relevancy: 0.93, consistency_score: 0.96, response_latency_ms: 12 },
  },
  {
    test_case_id: "EVAL-002",
    category: "alert_classification",
    input: "Classify severity for: supplier defect rate exceeded 10% threshold",
    scores: { overall_score: 0.87, answer_relevancy: 0.89, consistency_score: 0.94, response_latency_ms: 18 },
  },
  {
    test_case_id: "EVAL-003",
    category: "procurement",
    input: "Recommend procurement action for PrecisionMfg Chennai with critical quality issues",
    scores: { overall_score: 0.93, answer_relevancy: 0.95, consistency_score: 0.97, response_latency_ms: 14 },
  },
  {
    test_case_id: "EVAL-004",
    category: "trend_analysis",
    input: "Analyze OTD trend for GlobalComp Singapore: 88%, 91%, 89%, 92%, 91%",
    scores: { overall_score: 0.78, answer_relevancy: 0.81, consistency_score: 0.92, response_latency_ms: 21 },
  },
  {
    test_case_id: "EVAL-005",
    category: "cost_anomaly",
    input: "Explain cost anomaly: steel raw material price 22% above Q1 baseline",
    scores: { overall_score: 0.82, answer_relevancy: 0.84, consistency_score: 0.91, response_latency_ms: 16 },
  },
];

export const MOCK_EVAL_SUMMARY = {
  total_cases: 5,
  avg_overall_score: 0.862,
  pass: true,
  avg_consistency: 0.942,
  mode: "heuristic",
  generated_at: new Date().toISOString(),
  _computed: false,
  _passed: 5,
  _failed: 0,
};

// ── Phase 1: Supplier Quality Ratings ─────────────────────────────────────────
export const MOCK_SUPPLIER_RATINGS = {
  "SUPP-001": [
    { id: 1, supplierId: "SUPP-001", ratedBy: "kumar", ratingDate: "2026-06-10", qualityScore: 38, deliveryScore: 42, responsiveness: 35, overallScore: 38.3, comments: "Consistent delivery failures. Quality improved slightly but still below threshold." },
    { id: 2, supplierId: "SUPP-001", ratedBy: "admin", ratingDate: "2026-05-15", qualityScore: 35, deliveryScore: 40, responsiveness: 30, overallScore: 35.0, comments: "Major quality issues on PCB-001 batch. Escalated to account manager." },
    { id: 3, supplierId: "SUPP-001", ratedBy: "kumar", ratingDate: "2026-04-20", qualityScore: 30, deliveryScore: 38, responsiveness: 28, overallScore: 32.0, comments: "OTD below 40%. Placed on formal improvement plan." },
  ],
  "SUPP-002": [
    { id: 4, supplierId: "SUPP-002", ratedBy: "kumar", ratingDate: "2026-06-12", qualityScore: 94, deliveryScore: 91, responsiveness: 92, overallScore: 92.3, comments: "Excellent performance. Recommended for preferred tier upgrade." },
    { id: 5, supplierId: "SUPP-002", ratedBy: "admin", ratingDate: "2026-05-08", qualityScore: 90, deliveryScore: 89, responsiveness: 91, overallScore: 90.0, comments: "Consistently reliable. Zero defects in last quarter." },
  ],
  "SUPP-003": [
    { id: 6, supplierId: "SUPP-003", ratedBy: "kumar", ratingDate: "2026-06-05", qualityScore: 70, deliveryScore: 67, responsiveness: 68, overallScore: 68.3, comments: "Average performance. 3-day delays on standard shipments." },
  ],
  "SUPP-004": [
    { id: 7, supplierId: "SUPP-004", ratedBy: "admin", ratingDate: "2026-06-01", qualityScore: 25, deliveryScore: 30, responsiveness: 22, overallScore: 25.7, comments: "Critical risk. Multiple defect batches. Recommended for disqualification review." },
    { id: 8, supplierId: "SUPP-004", ratedBy: "kumar", ratingDate: "2026-05-01", qualityScore: 28, deliveryScore: 32, responsiveness: 25, overallScore: 28.3, comments: "No improvement from previous quarter." },
  ],
  "SUPP-005": [
    { id: 9, supplierId: "SUPP-005", ratedBy: "kumar", ratingDate: "2026-06-14", qualityScore: 92, deliveryScore: 88, responsiveness: 87, overallScore: 89.0, comments: "Top-tier supplier. On-time 100% this month." },
  ],
};

// ── Phase 2: Purchase Orders ───────────────────────────────────────────────────
let _mockPOs = [
  {
    id: 1, poNumber: "PO-2026-0001", supplierId: "SUPP-002", supplierName: "GlobalComp Singapore",
    status: "CONFIRMED", orderDate: "2026-06-01", expectedDate: "2026-06-25", receivedDate: null,
    totalAmount: 48500, currency: "USD", notes: "Urgent restock for CHIP-001",
    createdBy: "kumar", createdAt: "2026-06-01T09:00:00",
    lineItems: [
      { id: 1, poId: 1, itemKey: "CHIP-001", description: "Microcontroller STM32", quantity: 2000, unitPrice: 12.5, lineTotal: 25000, uom: "EACH" },
      { id: 2, poId: 1, itemKey: "SENSOR-X", description: "Proximity Sensor", quantity: 500, unitPrice: 47.0, lineTotal: 23500, uom: "EACH" },
    ],
  },
  {
    id: 2, poNumber: "PO-2026-0002", supplierId: "SUPP-005", supplierName: "NexusParts Germany",
    status: "RECEIVED", orderDate: "2026-05-20", expectedDate: "2026-06-10", receivedDate: "2026-06-09",
    totalAmount: 8500, currency: "EUR", notes: "Standard weekly order",
    createdBy: "admin", createdAt: "2026-05-20T11:30:00",
    lineItems: [
      { id: 3, poId: 2, itemKey: "CAP-100UF", description: "Capacitor 100uF", quantity: 50000, unitPrice: 0.085, lineTotal: 4250, uom: "EACH" },
      { id: 4, poId: 2, itemKey: "RES-10K",   description: "Resistor 10K Ohm", quantity: 100000, unitPrice: 0.0425, lineTotal: 4250, uom: "EACH" },
    ],
  },
  {
    id: 3, poNumber: "PO-2026-0003", supplierId: "SUPP-001", supplierName: "TechParts India Pvt Ltd",
    status: "SUBMITTED", orderDate: "2026-06-15", expectedDate: "2026-07-05", receivedDate: null,
    totalAmount: 6600, currency: "USD", notes: "Probation order — enhanced QC required",
    createdBy: "kumar", createdAt: "2026-06-15T14:00:00",
    lineItems: [
      { id: 5, poId: 3, itemKey: "PCB-001", description: "Printed Circuit Board v3", quantity: 300, unitPrice: 22.0, lineTotal: 6600, uom: "EACH" },
    ],
  },
  {
    id: 4, poNumber: "PO-2026-0004", supplierId: "SUPP-003", supplierName: "SwiftLogix Dubai",
    status: "DRAFT", orderDate: "2026-06-18", expectedDate: "2026-07-15", receivedDate: null,
    totalAmount: 0, currency: "USD", notes: "",
    createdBy: "kumar", createdAt: "2026-06-18T08:00:00",
    lineItems: [],
  },
];

export function getMockPOs() { return _mockPOs; }
export function addMockPO(po) { _mockPOs = [po, ..._mockPOs]; }
export function updateMockPO(id, changes) { _mockPOs = _mockPOs.map(p => p.id === id ? { ...p, ...changes } : p); }

// ── Phase 3: Inventory ─────────────────────────────────────────────────────────
let _mockInventory = [
  { id: 1, itemKey: "CHIP-001", itemName: "Microcontroller STM32",   warehouseId: "WH-001", currentStock: 850,  reorderPoint: 500,  maxStock: 5000, uom: "EACH", category: "ELECTRONICS", lastUpdated: "2026-06-17T10:00:00" },
  { id: 2, itemKey: "SENSOR-X", itemName: "Proximity Sensor",         warehouseId: "WH-001", currentStock: 120,  reorderPoint: 200,  maxStock: 1000, uom: "EACH", category: "ELECTRONICS", lastUpdated: "2026-06-16T14:30:00" },
  { id: 3, itemKey: "PCB-001",  itemName: "Printed Circuit Board v3", warehouseId: "WH-002", currentStock: 45,   reorderPoint: 100,  maxStock: 500,  uom: "EACH", category: "ELECTRONICS", lastUpdated: "2026-06-15T09:00:00" },
  { id: 4, itemKey: "CAP-100UF",itemName: "Capacitor 100uF",          warehouseId: "WH-001", currentStock: 12500, reorderPoint: 5000, maxStock: 50000, uom: "EACH", category: "ELECTRONICS", lastUpdated: "2026-06-14T16:00:00" },
  { id: 5, itemKey: "HYD-VALVE",itemName: "Hydraulic Valve A200",     warehouseId: "WH-002", currentStock: 8,    reorderPoint: 20,   maxStock: 100,  uom: "EACH", category: "MECHANICAL",  lastUpdated: "2026-06-13T11:00:00" },
  { id: 6, itemKey: "RAW-ALU",  itemName: "Aluminium Sheet 2mm",      warehouseId: "WH-003", currentStock: 320,  reorderPoint: 150,  maxStock: 1000, uom: "KG",   category: "RAW_MATERIAL", lastUpdated: "2026-06-12T08:30:00" },
  { id: 7, itemKey: "FG-WGT",   itemName: "Widget Assembly v2",       warehouseId: "WH-001", currentStock: 18,   reorderPoint: 25,   maxStock: 200,  uom: "EACH", category: "FINISHED_GOOD", lastUpdated: "2026-06-11T15:00:00" },
  { id: 8, itemKey: "RES-10K",  itemName: "Resistor 10K Ohm",         warehouseId: "WH-001", currentStock: 35000, reorderPoint: 10000, maxStock: 100000, uom: "EACH", category: "ELECTRONICS", lastUpdated: "2026-06-10T12:00:00" },
];

const _mockTxns = {
  "CHIP-001": [
    { id: 1, itemKey: "CHIP-001", transactionType: "IN",  quantity: 2000, balanceAfter: 850,  reference: "PO-2026-0001", notes: "Received from GlobalComp Singapore", performedBy: "kumar",  transactionDate: "2026-06-17T10:00:00" },
    { id: 2, itemKey: "CHIP-001", transactionType: "OUT", quantity: 500,  balanceAfter: 1350, reference: "WO-2026-0011", notes: "Issued to production Work Order 11",  performedBy: "admin",  transactionDate: "2026-06-14T14:00:00" },
  ],
  "PCB-001": [
    { id: 3, itemKey: "PCB-001", transactionType: "IN",  quantity: 100, balanceAfter: 45,  reference: "PO-2026-1001", notes: "Received partial shipment",            performedBy: "kumar", transactionDate: "2026-06-15T09:00:00" },
    { id: 4, itemKey: "PCB-001", transactionType: "OUT", quantity: 55,  balanceAfter: 100, reference: "WO-2026-0009", notes: "Issued to assembly line",               performedBy: "admin", transactionDate: "2026-06-12T11:00:00" },
  ],
};

export function getMockInventory() { return _mockInventory; }
export function getMockTxns(itemKey) { return _mockTxns[itemKey] || []; }
export function adjustMockStock(itemKey, type, qty, ref, notes, user) {
  _mockInventory = _mockInventory.map(it => {
    if (it.itemKey !== itemKey) return it;
    const newStock = type === 'IN' ? it.currentStock + qty : type === 'OUT' ? it.currentStock - qty : it.currentStock + qty;
    return { ...it, currentStock: Math.max(0, newStock), lastUpdated: new Date().toISOString() };
  });
}

// ── Phase 4: Audit Logs ────────────────────────────────────────────────────────
export const MOCK_AUDIT_LOGS = [
  { id: 1, entityType: "PurchaseOrder", entityId: "PO-2026-0001", action: "CREATE",   performedBy: "kumar", details: "Created PO-2026-0001 for SUPP-002, amount $48,500",           loggedAt: "2026-06-01T09:00:00" },
  { id: 2, entityType: "PurchaseOrder", entityId: "PO-2026-0001", action: "SUBMIT",   performedBy: "kumar", details: "Submitted PO-2026-0001 for supplier confirmation",              loggedAt: "2026-06-01T09:10:00" },
  { id: 3, entityType: "PurchaseOrder", entityId: "PO-2026-0001", action: "CONFIRM",  performedBy: "admin", details: "Confirmed PO-2026-0001 — expected delivery 2026-06-25",        loggedAt: "2026-06-02T11:30:00" },
  { id: 4, entityType: "CostRecord",    entityId: "CR-2026-0012", action: "CREATE",   performedBy: "kumar", details: "Created draft cost record for CHIP-001: $12.50 → $13.00",      loggedAt: "2026-06-05T14:00:00" },
  { id: 5, entityType: "CostRecord",    entityId: "CR-2026-0012", action: "SUBMIT",   performedBy: "kumar", details: "Submitted cost record for CHIP-001 for approval",              loggedAt: "2026-06-05T14:30:00" },
  { id: 6, entityType: "CostRecord",    entityId: "CR-2026-0012", action: "APPROVE",  performedBy: "admin", details: "Approved cost record — ITEM_MASTER updated: CHIP-001=$13.00",  loggedAt: "2026-06-06T09:00:00" },
  { id: 7, entityType: "SupplierRating",entityId: "SUPP-001",     action: "CREATE",   performedBy: "kumar", details: "Submitted quality rating for TechParts India: overall 38.3",  loggedAt: "2026-06-10T10:00:00" },
  { id: 8, entityType: "Inventory",     entityId: "CHIP-001",     action: "ADJUST",   performedBy: "kumar", details: "Stock IN: +2000 units CHIP-001, ref PO-2026-0001",            loggedAt: "2026-06-17T10:05:00" },
  { id: 9, entityType: "User",          entityId: "viewer01",     action: "CREATE",   performedBy: "admin", details: "Created user account viewer01 with GUEST role",               loggedAt: "2026-06-08T16:00:00" },
  { id:10, entityType: "Alert",         entityId: "ALT-001",      action: "DISMISS",  performedBy: "kumar", details: "Dismissed alert: TechParts India OTD Below Threshold",         loggedAt: "2026-06-18T08:30:00" },
  { id:11, entityType: "PurchaseOrder", entityId: "PO-2026-0002", action: "RECEIVE",  performedBy: "admin", details: "Marked PO-2026-0002 as RECEIVED — all line items confirmed",   loggedAt: "2026-06-09T15:00:00" },
  { id:12, entityType: "Inventory",     entityId: "PCB-001",      action: "ADJUST",   performedBy: "admin", details: "Stock IN: +100 units PCB-001, ref PO-2026-1001",              loggedAt: "2026-06-15T09:05:00" },
];
