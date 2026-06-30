import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getDashboardSummary, getActiveAlerts, dismissAlert } from "../api";

const IS_DEMO = process.env.REACT_APP_DEMO_MODE === 'true';

// ── Sparkline bar chart (7-day trend) ───────────────────────────────────────
function MiniBarChart({ data, color, h = 44 }) {
  const max = Math.max(...data, 1);
  const bw = 10, gap = 4;
  const w = data.length * (bw + gap) - gap;
  return (
    <svg width={w} height={h} style={{ display: "block", overflow: "visible" }}>
      {data.map((v, i) => {
        const bh = Math.max(2, Math.round((v / max) * h));
        const isLast = i === data.length - 1;
        return (
          <rect key={i} x={i * (bw + gap)} y={h - bh} width={bw} height={bh}
            fill={color} opacity={isLast ? 1 : 0.3} rx={2} />
        );
      })}
    </svg>
  );
}

// ── Mini line chart ──────────────────────────────────────────────────────────
function MiniLineChart({ data, color, h = 44 }) {
  const max = Math.max(...data, 1);
  const min = Math.min(...data, 0);
  const range = Math.max(max - min, 1);
  const w = 110;
  const pts = data.map((v, i) => {
    const x = (i / (data.length - 1)) * w;
    const y = h - Math.max(3, ((v - min) / range) * (h - 6)) - 3;
    return `${x.toFixed(1)},${y.toFixed(1)}`;
  }).join(" ");
  const last = data[data.length - 1];
  const lx = w;
  const ly = h - Math.max(3, ((last - min) / range) * (h - 6)) - 3;
  return (
    <svg width={w} height={h} style={{ display: "block", overflow: "visible" }}>
      <polyline points={pts} fill="none" stroke={color} strokeWidth={2.5}
        strokeLinecap="round" strokeLinejoin="round" opacity={0.9} />
      <circle cx={lx} cy={ly} r={4} fill={color} />
    </svg>
  );
}

// ── Donut gauge for rating ───────────────────────────────────────────────────
function DonutGauge({ value, max = 5, color }) {
  const pct = Math.min(1, Math.max(0, value / max));
  const r = 28, cx = 36, cy = 36;
  const circ = 2 * Math.PI * r;
  const dash = pct * circ;
  return (
    <svg width={72} height={72} style={{ display: "block" }}>
      <circle cx={cx} cy={cy} r={r} fill="none" stroke="#e5e7eb" strokeWidth={8} />
      <circle cx={cx} cy={cy} r={r} fill="none" stroke={color} strokeWidth={8}
        strokeDasharray={`${dash.toFixed(1)} ${circ.toFixed(1)}`}
        strokeLinecap="round" transform={`rotate(-90 ${cx} ${cy})`} />
      <text x={cx} y={cy + 5} textAnchor="middle" fontSize={13} fontWeight={800} fill={color}>
        {typeof value === "number" ? value.toFixed(1) : value}
      </text>
    </svg>
  );
}

// Demo 7-day trend data per KPI
const DEMO_TRENDS = {
  activeAlerts:      [2, 4, 3, 5, 3, 2, 3],
  totalBoms:         [8, 9, 9, 10, 11, 11, 12],
  pendingApprovals:  [3, 2, 4, 3, 5, 3, 2],
  atRiskSuppliers:   [4, 3, 3, 2, 3, 3, 3],
  lowStockCount:     [0, 1, 0, 2, 1, 2, 2],
  openPOCount:       [5, 6, 7, 6, 8, 7, 6],
  avgSupplierRating: [3.8, 3.9, 4.0, 3.9, 4.1, 4.0, 4.1],
  totalInventorySkus:[45, 46, 47, 47, 48, 49, 50],
};

export default function DashboardPage() {
  const [kpis,    setKpis]    = useState({ activeAlerts:"-", totalBoms:"-", pendingApprovals:"-", atRiskSuppliers:"-", lowStockCount:"-", openPOCount:"-", avgSupplierRating:"-", totalInventorySkus:"-" });
  const [alerts,  setAlerts]  = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const user = (() => { try { return JSON.parse(localStorage.getItem("user_data") || "{}"); } catch(e) { return {}; } })();
  const today = new Date().toLocaleDateString("en-GB", { weekday:"long", day:"numeric", month:"long", year:"numeric" });

  useEffect(() => {
    Promise.all([getDashboardSummary(), getActiveAlerts()])
      .then(([kpiRes, alertRes]) => {
        const k = kpiRes.data || {};
        setKpis({
          activeAlerts:       k.activeAlerts       ?? k.unreadAlerts ?? 0,
          totalBoms:          k.totalBoms          ?? k.activeBoms   ?? 0,
          pendingApprovals:   k.pendingApprovals   ?? k.pendingBoms  ?? 0,
          atRiskSuppliers:    k.atRiskSuppliers    ?? 0,
          lowStockCount:      k.lowStockCount      ?? 0,
          openPOCount:        k.openPOCount        ?? 0,
          avgSupplierRating:  k.avgSupplierRating  ?? 0,
          totalInventorySkus: k.totalInventorySkus ?? 0,
        });
        const a = alertRes.data;
        setAlerts(Array.isArray(a) ? a : (a?.alerts || []));
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const handleDismiss = (id) => {
    dismissAlert(id).then(() => setAlerts(p => p.filter(a => a.id !== id))).catch(() => {});
  };

  // Resolved value: in demo mode use the last point of DEMO_TRENDS; otherwise use live kpis
  const val = (key) => {
    if (loading) return "…";
    if (IS_DEMO) return DEMO_TRENDS[key][DEMO_TRENDS[key].length - 1];
    return kpis[key];
  };

  // Delta vs previous day (trend direction label)
  const delta = (key) => {
    const d = DEMO_TRENDS[key];
    const diff = d[d.length - 1] - d[d.length - 2];
    if (diff === 0) return null;
    return { dir: diff > 0 ? "↑" : "↓", pos: diff > 0, n: Math.abs(diff) };
  };

  const KPI_CARDS = [
    { key:"activeAlerts",      label:"Active Alerts",     color:"#ef4444", path:"/alerts",           chart:"bar",   goodDown:true },
    { key:"totalBoms",         label:"Total BOMs",        color:"#3b82f6", path:"/bom",              chart:"line",  goodDown:false },
    { key:"pendingApprovals",  label:"Pending Approvals", color:"#f59e0b", path:"/cost-records",     chart:"bar",   goodDown:true },
    { key:"atRiskSuppliers",   label:"At-Risk Suppliers", color:"#8b5cf6", path:"/suppliers",        chart:"bar",   goodDown:true },
    { key:"lowStockCount",     label:"Low Stock Items",   color:"#dc2626", path:"/inventory",        chart:"bar",   goodDown:true },
    { key:"openPOCount",       label:"Open POs",          color:"#0891b2", path:"/purchase-orders",  chart:"bar",   goodDown:false },
    { key:"avgSupplierRating", label:"Avg Rating / 5",    color:"#16a34a", path:"/suppliers",        chart:"donut", goodDown:false },
    { key:"totalInventorySkus",label:"Inventory SKUs",    color:"#7c3aed", path:"/inventory",        chart:"line",  goodDown:false },
  ];

  const DAY_LABELS = ["Mon","Tue","Wed","Thu","Fri","Sat","Today"];

  return (
    <div style={{ padding:24, maxWidth:1160, margin:"0 auto" }}>

      {/* ── Header ─────────────────────────────────────────── */}
      <div style={{ marginBottom:28 }}>
        <h1 style={{ fontSize:22, fontWeight:800, margin:0 }}>Welcome back, {user.userId || "kumar"}</h1>
        <p style={{ color:"#6b7280", margin:"4px 0 0" }}>Supply Chain Intelligence Platform &mdash; {today}</p>
      </div>

      {/* ══ 1. ABOUT SCIP ══════════════════════════════════════════════════ */}
      <div style={{ background:"linear-gradient(135deg,#0f172a 0%,#1e3a5f 60%,#1d4ed8 100%)", borderRadius:16, padding:"32px 36px", marginBottom:28, color:"#fff" }}>
        <div style={{ display:"flex", alignItems:"flex-start", gap:20, flexWrap:"wrap" }}>
          <div style={{ flex:1, minWidth:280 }}>
            <div style={{ fontSize:11, fontWeight:700, letterSpacing:"1.5px", color:"#93c5fd", textTransform:"uppercase", marginBottom:8 }}>About this platform</div>
            <h2 style={{ margin:"0 0 12px", fontSize:22, fontWeight:800, lineHeight:1.3 }}>
              SCIP — Supply Chain Intelligence Platform
            </h2>
            <p style={{ margin:"0 0 10px", fontSize:14, color:"#cbd5e1", lineHeight:1.7 }}>
              SCIP is an <strong style={{ color:"#fff" }}>AI-powered supply chain management system</strong> that combines
              real-time supplier risk scoring, full procurement workflows, and natural language explanations — so you
              always know which suppliers need attention and why.
            </p>
            <p style={{ margin:0, fontSize:13, color:"#94a3b8", lineHeight:1.6 }}>
              Built on <strong style={{ color:"#93c5fd" }}>IsolationForest anomaly detection</strong> + <strong style={{ color:"#93c5fd" }}>Claude AI</strong> for plain-English risk explanations.
              Import supplier data via CSV, manage purchase orders, track inventory, and get AI-generated forecasts — no ERP required.
            </p>
          </div>
          <div style={{ display:"flex", flexDirection:"column", gap:8, minWidth:200 }}>
            <div style={{ background:"rgba(255,255,255,0.08)", borderRadius:10, padding:"10px 14px", border:"1px solid rgba(255,255,255,0.12)" }}>
              <div style={{ fontSize:11, color:"#93c5fd", fontWeight:700, marginBottom:2 }}>DEMO CREDENTIALS</div>
              <div style={{ fontSize:13, color:"#e2e8f0" }}>Email: <strong>kumar@scip.io</strong></div>
              <div style={{ fontSize:13, color:"#e2e8f0" }}>Password: <strong>Kumar@2026</strong></div>
            </div>
            <div style={{ background:"rgba(255,255,255,0.08)", borderRadius:10, padding:"10px 14px", border:"1px solid rgba(255,255,255,0.12)" }}>
              <div style={{ fontSize:11, color:"#93c5fd", fontWeight:700, marginBottom:2 }}>ALSO TRY</div>
              <div style={{ fontSize:13, color:"#e2e8f0" }}>Ops: <strong>ops@scip.io</strong> / <strong>Ops@2026</strong></div>
            </div>
          </div>
        </div>
      </div>

      {/* ══ 2. FEATURE GUIDE ═══════════════════════════════════════════════ */}
      <div style={{ marginBottom:36 }}>
        <h3 style={{ fontSize:15, fontWeight:700, color:"#111827", margin:"0 0 14px" }}>What you can do here</h3>
        <div style={{ display:"grid", gridTemplateColumns:"repeat(auto-fill,minmax(260px,1fr))", gap:14 }}>
          {[
            { icon:"🧠", color:"#8b5cf6", bg:"#f5f3ff", border:"#ddd6fe", title:"Simple Dashboard",  path:"/simple-dashboard",  desc:"Plain-English overview of every supplier — red/yellow/green risk labels, no jargon." },
            { icon:"⚠️", color:"#dc2626", bg:"#fef2f2", border:"#fecaca", title:"AI Anomaly Engine", path:"/ai",               desc:"IsolationForest ML flags suppliers behaving abnormally. Claude AI explains the risk in plain English." },
            { icon:"📦", color:"#0891b2", bg:"#ecfeff", border:"#a5f3fc", title:"Purchase Orders",   path:"/purchase-orders",  desc:"Create, submit, confirm, and receive POs. Auto-creates cost records and inventory adjustments on receipt." },
            { icon:"📊", color:"#1d4ed8", bg:"#eff6ff", border:"#bfdbfe", title:"Supplier Scorecard",path:"/suppliers",        desc:"On-time delivery %, quality score, responsiveness — scored 0–100 with historical trend." },
            { icon:"📁", color:"#059669", bg:"#ecfdf5", border:"#6ee7b7", title:"CSV Upload",        path:"/csv-upload",       desc:"No ERP? Import supplier order history from a spreadsheet. Download template, fill in, upload." },
            { icon:"📈", color:"#f59e0b", bg:"#fffbeb", border:"#fde68a", title:"Forecasting",       path:"/forecasts",        desc:"Prophet-based demand forecasting. See 90-day projections with confidence intervals." },
            { icon:"🧾", color:"#7c3aed", bg:"#faf5ff", border:"#ddd6fe", title:"Bill of Materials", path:"/bom",              desc:"Manage multi-level BOMs with cost rollup, approval workflows, and change-history tracking." },
            { icon:"🏭", color:"#0369a1", bg:"#f0f9ff", border:"#bae6fd", title:"Inventory",         path:"/inventory",        desc:"Real-time stock levels across all SKUs. Low-stock alerts trigger on reorder thresholds." },
          ].map(f => (
            <div key={f.path} onClick={() => navigate(f.path)}
              style={{ background:f.bg, border:`1px solid ${f.border}`, borderRadius:12, padding:"16px 18px", cursor:"pointer", transition:"box-shadow 0.15s" }}
              onMouseEnter={e => e.currentTarget.style.boxShadow="0 4px 16px rgba(0,0,0,0.08)"}
              onMouseLeave={e => e.currentTarget.style.boxShadow="none"}>
              <div style={{ display:"flex", alignItems:"center", gap:10, marginBottom:8 }}>
                <span style={{ fontSize:20 }}>{f.icon}</span>
                <span style={{ fontWeight:700, fontSize:14, color:f.color }}>{f.title}</span>
              </div>
              <p style={{ margin:"0 0 10px", fontSize:13, color:"#374151", lineHeight:1.6 }}>{f.desc}</p>
              <div style={{ fontSize:12, color:f.color, fontWeight:600 }}>Open {f.title} →</div>
            </div>
          ))}
        </div>
      </div>

      {/* ══ 3. KPI CHART CARDS ═════════════════════════════════════════════ */}
      <div style={{ marginBottom:36 }}>
        <div style={{ display:"flex", alignItems:"center", justifyContent:"space-between", marginBottom:14 }}>
          <h3 style={{ fontSize:15, fontWeight:700, color:"#111827", margin:0 }}>Platform metrics — 7-day trend</h3>
          <span style={{ fontSize:12, color:"#9ca3af" }}>
            {IS_DEMO ? "Demo data" : (loading ? "Loading…" : "Live")}
          </span>
        </div>

        <div style={{ display:"grid", gridTemplateColumns:"repeat(auto-fill,minmax(230px,1fr))", gap:16 }}>
          {KPI_CARDS.map(({ key, label, color, path, chart, goodDown }) => {
            const trend = DEMO_TRENDS[key];
            const current = val(key);
            const d = IS_DEMO || !loading ? delta(key) : null;
            const deltaColor = d ? (d.pos === goodDown ? "#16a34a" : "#dc2626") : "#6b7280";

            return (
              <div key={key} onClick={() => navigate(path)}
                style={{ background:"#fff", border:"1px solid #e5e7eb", borderRadius:14, padding:"18px 20px",
                         cursor:"pointer", borderTop:`3px solid ${color}`, transition:"box-shadow 0.15s" }}
                onMouseEnter={e => e.currentTarget.style.boxShadow="0 4px 20px rgba(0,0,0,0.08)"}
                onMouseLeave={e => e.currentTarget.style.boxShadow="none"}>

                {/* Value + delta */}
                <div style={{ display:"flex", alignItems:"flex-start", justifyContent:"space-between", marginBottom:4 }}>
                  <div style={{ fontSize:30, fontWeight:800, color, lineHeight:1 }}>
                    {loading && !IS_DEMO ? "…" : (
                      key === "avgSupplierRating"
                        ? typeof current === "number" ? current.toFixed(1) : current
                        : current
                    )}
                  </div>
                  {d && (
                    <span style={{ fontSize:11, fontWeight:700, color: deltaColor, background: deltaColor + "18",
                                   padding:"2px 7px", borderRadius:20, marginTop:4, whiteSpace:"nowrap" }}>
                      {d.dir} {d.n}
                    </span>
                  )}
                </div>

                <div style={{ fontSize:11, fontWeight:700, color:"#6b7280", textTransform:"uppercase",
                               letterSpacing:"0.5px", marginBottom:14 }}>{label}</div>

                {/* Chart */}
                {chart === "donut" ? (
                  <div style={{ display:"flex", alignItems:"center", gap:16 }}>
                    <DonutGauge value={typeof current === "number" ? current : 4.1} color={color} />
                    <div>
                      <div style={{ fontSize:11, color:"#9ca3af", marginBottom:4 }}>7-day avg</div>
                      {trend.map((v, i) => (
                        <div key={i} style={{ display:"flex", alignItems:"center", gap:6, marginBottom:2 }}>
                          <div style={{ width:`${(v/5)*60}px`, height:5, background: color, opacity: i === trend.length-1 ? 1 : 0.3, borderRadius:3 }} />
                          <span style={{ fontSize:10, color:"#9ca3af" }}>{DAY_LABELS[i]}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                ) : chart === "line" ? (
                  <>
                    <MiniLineChart data={trend} color={color} />
                    <div style={{ display:"flex", justifyContent:"space-between", marginTop:4 }}>
                      {DAY_LABELS.map((d, i) => (
                        <span key={i} style={{ fontSize:9, color:"#d1d5db", textAlign:"center", width:16 }}>{d.slice(0,1)}</span>
                      ))}
                    </div>
                  </>
                ) : (
                  <>
                    <MiniBarChart data={trend} color={color} />
                    <div style={{ display:"flex", justifyContent:"space-between", marginTop:4 }}>
                      {DAY_LABELS.map((d, i) => (
                        <span key={i} style={{ fontSize:9, color:"#d1d5db", textAlign:"center", width:14 }}>{d.slice(0,1)}</span>
                      ))}
                    </div>
                  </>
                )}

                <div style={{ fontSize:11, color, fontWeight:600, marginTop:12 }}>View details →</div>
              </div>
            );
          })}
        </div>
      </div>

      {/* ══ 4. RECENT ALERTS + QUICK ACTIONS ══════════════════════════════ */}
      <div style={{ display:"grid", gridTemplateColumns:"1fr 1fr", gap:20 }}>
        <div style={{ background:"#fff", border:"1px solid #e5e7eb", borderRadius:12, padding:20 }}>
          <div style={{ display:"flex", justifyContent:"space-between", alignItems:"center", marginBottom:16 }}>
            <h3 style={{ margin:0, fontSize:15, fontWeight:700 }}>Recent Alerts</h3>
            <span onClick={() => navigate("/alerts")} style={{ fontSize:12, color:"#3b82f6", cursor:"pointer", fontWeight:600 }}>View All</span>
          </div>
          {loading && !IS_DEMO ? <p style={{ color:"#6b7280" }}>Loading...</p> :
           alerts.length === 0 ? <p style={{ color:"#6b7280", fontSize:13 }}>No active alerts</p> :
           alerts.slice(0,5).map(a => (
            <div key={a.id} style={{ display:"flex", justifyContent:"space-between", alignItems:"center", padding:"10px 0", borderBottom:"1px solid #f3f4f6" }}>
              <div>
                <div style={{ fontSize:13, fontWeight:600, color:"#111827" }}>{a.alertLabel || a.alertType}</div>
                <div style={{ fontSize:12, color:"#6b7280" }}>{a.shortSummary}</div>
              </div>
              <button onClick={() => handleDismiss(a.id)} style={{ fontSize:11, padding:"3px 10px", background:"#fef2f2", color:"#dc2626", border:"1px solid #fecaca", borderRadius:6, cursor:"pointer", fontWeight:600 }}>Dismiss</button>
            </div>
           ))}
        </div>

        <div style={{ background:"#fff", border:"1px solid #e5e7eb", borderRadius:12, padding:20 }}>
          <h3 style={{ margin:"0 0 16px", fontSize:15, fontWeight:700 }}>Quick Actions</h3>
          {[
            { label:"Review Pending Cost Records", path:"/cost-records",  color:"#f59e0b" },
            { label:"View Supplier Scorecards",    path:"/suppliers",     color:"#8b5cf6" },
            { label:"Browse Bill of Materials",    path:"/bom",           color:"#3b82f6" },
            { label:"AI Anomaly Engine",           path:"/ai",            color:"#10b981" },
            { label:"User Management",             path:"/admin/users",   color:"#6366f1" },
            { label:"Forecasting",                 path:"/forecasts",     color:"#f43f5e" },
          ].map(a => (
            <div key={a.path} onClick={() => navigate(a.path)}
              style={{ padding:"10px 14px", borderRadius:8, marginBottom:8, background:"#f9fafb", cursor:"pointer", display:"flex", alignItems:"center", gap:10, border:"1px solid #e5e7eb" }}
              onMouseEnter={e => e.currentTarget.style.background="#f3f4f6"}
              onMouseLeave={e => e.currentTarget.style.background="#f9fafb"}>
              <div style={{ width:8, height:8, borderRadius:"50%", background:a.color }} />
              <span style={{ fontSize:13, fontWeight:600, color:"#374151" }}>{a.label} &rarr;</span>
            </div>
          ))}
        </div>
      </div>

    </div>
  );
}
