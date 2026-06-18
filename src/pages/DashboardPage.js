import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getDashboardSummary, getActiveAlerts, dismissAlert } from "../api";

export default function DashboardPage() {
  const [kpis,    setKpis]    = useState({ activeAlerts:"-", totalBoms:"-", pendingApprovals:"-", atRiskSuppliers:"-" });
  const [alerts,  setAlerts]  = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const user = (() => { try { return JSON.parse(localStorage.getItem("user_data")||"{}"); } catch(e){ return {}; } })();
  const today = new Date().toLocaleDateString("en-GB", { weekday:"long", day:"numeric", month:"long", year:"numeric" });

  useEffect(() => {
    Promise.all([getDashboardSummary(), getActiveAlerts()])
      .then(([kpiRes, alertRes]) => {
        const k = kpiRes.data || {};
        setKpis({ activeAlerts: (k.activeAlerts ?? k.unreadAlerts ?? 0) ?? 0, totalBoms: (k.totalBoms ?? k.activeBoms ?? 0) ?? 0, pendingApprovals: (k.pendingApprovals ?? k.pendingBoms ?? 0) ?? 0, atRiskSuppliers: (k.atRiskSuppliers ?? 0) ?? 0 });
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

      <div style={{ display:"flex", gap:16, marginBottom:32, flexWrap:"wrap" }}>
        <KpiCard label="Active Alerts"     value={kpis.activeAlerts}     color="#ef4444" path="/alerts" />
        <KpiCard label="Total BOMs"        value={kpis.totalBoms}        color="#3b82f6" path="/bom" />
        <KpiCard label="Pending Approvals" value={kpis.pendingApprovals} color="#f59e0b" path="/cost-records" />
        <KpiCard label="At Risk Suppliers" value={kpis.atRiskSuppliers}  color="#8b5cf6" path="/suppliers" />
      </div>

      <div style={{ display:"grid", gridTemplateColumns:"1fr 1fr", gap:20 }}>
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
    </div>
  );
}
