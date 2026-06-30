import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getDashboardSummary, getActiveAlerts, dismissAlert } from "../api";

export default function DashboardPage() {
  const [kpis,    setKpis]    = useState({ activeAlerts:"-", totalBoms:"-", pendingApprovals:"-", atRiskSuppliers:"-", lowStockCount:"-", openPOCount:"-", avgSupplierRating:"-", totalInventorySkus:"-" });
  const [alerts,  setAlerts]  = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const user = (() => { try { return JSON.parse(localStorage.getItem("user_data")||"{}"); } catch(e){ return {}; } })();
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

  const KpiCard = ({ label, value, color, path }) => (
    <div onClick={() => path && navigate(path)}
      style={{ background:"#fff", border:"1px solid #e5e7eb", borderRadius:12, padding:"20px 24px",
               cursor:path?"pointer":"default", borderTop:"3px solid "+color, flex:1, minWidth:160 }}>
      <div style={{ fontSize:32, fontWeight:800, color }}>{loading ? "..." : value}</div>
      <div style={{ fontSize:12, color:"#6b7280", marginTop:4, textTransform:"uppercase", letterSpacing:"0.5px" }}>{label}</div>
      {path && <div style={{ fontSize:12, color, marginTop:8, fontWeight:600 }}>View details &rarr;</div>}
    </div>
  );

  return (
    <div style={{ padding:24, maxWidth:1100, margin:"0 auto" }}>
      <div style={{ marginBottom:24 }}>
        <h1 style={{ fontSize:22, fontWeight:800, margin:0 }}>Welcome back, {user.userId || "kumar"}</h1>
        <p style={{ color:"#6b7280", margin:"4px 0 0" }}>Supply Chain Intelligence Platform &mdash; {today}</p>
      </div>

      <div style={{ display:"grid", gridTemplateColumns:"repeat(auto-fill,minmax(180px,1fr))", gap:16, marginBottom:32 }}>
        <KpiCard label="Active Alerts"     value={kpis.activeAlerts}     color="#ef4444" path="/alerts" />
        <KpiCard label="Total BOMs"        value={kpis.totalBoms}        color="#3b82f6" path="/bom" />
        <KpiCard label="Pending Approvals" value={kpis.pendingApprovals} color="#f59e0b" path="/cost-records" />
        <KpiCard label="At Risk Suppliers" value={kpis.atRiskSuppliers}  color="#8b5cf6" path="/suppliers" />
        <KpiCard label="Low Stock Items"   value={kpis.lowStockCount}    color={kpis.lowStockCount > 0 ? "#dc2626" : "#6b7280"} path="/inventory" />
        <KpiCard label="Open POs"          value={kpis.openPOCount}      color="#0891b2" path="/purchase-orders" />
        <KpiCard label="Avg Rating / 5"    value={kpis.avgSupplierRating !== "-" ? `${kpis.avgSupplierRating} / 5` : "-"} color="#16a34a" path="/suppliers" />
        <KpiCard label="Inventory SKUs"    value={kpis.totalInventorySkus} color="#7c3aed" path="/inventory" />
      </div>

      <div style={{ display:"grid", gridTemplateColumns:"1fr 1fr", gap:20, marginBottom:32 }}>
        <div style={{ background:"#fff", border:"1px solid #e5e7eb", borderRadius:12, padding:20 }}>
          <div style={{ display:"flex", justifyContent:"space-between", alignItems:"center", marginBottom:16 }}>
            <h3 style={{ margin:0, fontSize:15, fontWeight:700 }}>Recent Alerts</h3>
            <span onClick={() => navigate("/alerts")} style={{ fontSize:12, color:"#3b82f6", cursor:"pointer", fontWeight:600 }}>View All</span>
          </div>
          {loading ? <p style={{ color:"#6b7280" }}>Loading...</p> :
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
            { label:"Review Pending Cost Records", path:"/cost-records", color:"#f59e0b" },
            { label:"View Supplier Scorecards",    path:"/suppliers",    color:"#8b5cf6" },
            { label:"Browse Bill of Materials",    path:"/bom",          color:"#3b82f6" },
            { label:"AI Anomaly Engine",           path:"/ai",           color:"#10b981" },
            { label:"User Management",             path:"/admin/users",  color:"#6366f1" },
            { label:"Forecasting",                 path:"/forecasts",    color:"#f43f5e" },
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

      {/* ── What is SCIP ─────────────────────────────────── */}
      <div style={{ background:"linear-gradient(135deg,#0f172a 0%,#1e3a5f 60%,#1d4ed8 100%)", borderRadius:16, padding:"32px 36px", marginBottom:28, color:"#fff" }}>
        <div style={{ display:"flex", alignItems:"flex-start", gap:20, flexWrap:"wrap" }}>
          <div style={{ flex:1, minWidth:280 }}>
            <div style={{ fontSize:11, fontWeight:700, letterSpacing:"1.5px", color:"#93c5fd", textTransform:"uppercase", marginBottom:8 }}>About this platform</div>
            <h2 style={{ margin:"0 0 12px", fontSize:22, fontWeight:800, lineHeight:1.3 }}>
              SCIP — Supply Chain Intelligence Platform
            </h2>
            <p style={{ margin:"0 0 10px", fontSize:14, color:"#cbd5e1", lineHeight:1.7 }}>
              SCIP is an <strong style={{ color:"#fff" }}>AI-powered supply chain management system</strong> that combines real-time supplier risk scoring,
              full procurement workflows, and natural language explanations — so you always know which suppliers need attention and why.
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

      {/* ── Feature guide ────────────────────────────────── */}
      <div style={{ marginBottom:8 }}>
        <h3 style={{ fontSize:15, fontWeight:700, color:"#111827", margin:"0 0 16px" }}>What you can do here</h3>
        <div style={{ display:"grid", gridTemplateColumns:"repeat(auto-fill,minmax(280px,1fr))", gap:14 }}>
          {[
            {
              icon:"🧠", color:"#8b5cf6", bg:"#f5f3ff", border:"#ddd6fe",
              title:"Simple Dashboard",
              path:"/simple-dashboard",
              desc:"Plain-English overview of every supplier — red/yellow/green risk labels, no jargon. Perfect starting point.",
            },
            {
              icon:"⚠️", color:"#dc2626", bg:"#fef2f2", border:"#fecaca",
              title:"AI Anomaly Engine",
              path:"/ai",
              desc:"IsolationForest ML flags suppliers behaving abnormally. Claude AI explains the risk in one plain sentence.",
            },
            {
              icon:"📦", color:"#0891b2", bg:"#ecfeff", border:"#a5f3fc",
              title:"Purchase Orders",
              path:"/purchase-orders",
              desc:"Create, submit, confirm, and receive POs. Auto-creates cost records and inventory adjustments on receipt.",
            },
            {
              icon:"📊", color:"#1d4ed8", bg:"#eff6ff", border:"#bfdbfe",
              title:"Supplier Scorecard",
              path:"/suppliers",
              desc:"On-time delivery %, quality score, responsiveness — scored 0–100 with historical trend and delivery log.",
            },
            {
              icon:"📁", color:"#059669", bg:"#ecfdf5", border:"#6ee7b7",
              title:"CSV Upload",
              path:"/csv-upload",
              desc:"No ERP? Import supplier order history from a spreadsheet. Download the template, fill it in, upload.",
            },
            {
              icon:"📈", color:"#f59e0b", bg:"#fffbeb", border:"#fde68a",
              title:"Forecasting",
              path:"/forecasts",
              desc:"Prophet-based demand forecasting for inventory planning. See 90-day projections with confidence intervals.",
            },
            {
              icon:"🧾", color:"#7c3aed", bg:"#faf5ff", border:"#ddd6fe",
              title:"Bill of Materials",
              path:"/bom",
              desc:"Manage multi-level BOMs with cost rollup, approval workflows, and change-history tracking.",
            },
            {
              icon:"🏭", color:"#0369a1", bg:"#f0f9ff", border:"#bae6fd",
              title:"Inventory",
              path:"/inventory",
              desc:"Real-time stock levels across all SKUs. Low-stock alerts trigger automatically based on reorder thresholds.",
            },
          ].map(f => (
            <div key={f.path} onClick={() => navigate(f.path)}
              style={{ background:f.bg, border:`1px solid ${f.border}`, borderRadius:12, padding:"16px 18px", cursor:"pointer", transition:"box-shadow 0.15s" }}
              onMouseEnter={e => e.currentTarget.style.boxShadow="0 4px 16px rgba(0,0,0,0.08)"}
              onMouseLeave={e => e.currentTarget.style.boxShadow="none"}>
              <div style={{ display:"flex", alignItems:"center", gap:10, marginBottom:8 }}>
                <span style={{ fontSize:20 }}>{f.icon}</span>
                <span style={{ fontWeight:700, fontSize:14, color:f.color }}>{f.title}</span>
              </div>
              <p style={{ margin:0, fontSize:13, color:"#374151", lineHeight:1.6 }}>{f.desc}</p>
              <div style={{ marginTop:10, fontSize:12, color:f.color, fontWeight:600 }}>Open {f.title} →</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
