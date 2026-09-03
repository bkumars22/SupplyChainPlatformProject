import React, { useState, useEffect } from "react";
import { getActiveAlerts, dismissAlert } from "../api";

const SEV = {
  CRITICAL: { bg:"#fef2f2", color:"#dc2626", border:"#fecaca" },
  WARNING:  { bg:"#fffbeb", color:"#d97706", border:"#fde68a" },
  INFO:     { bg:"#eff6ff", color:"#2563eb", border:"#bfdbfe" },
};

export default function AlertsPage() {
  const [alerts,  setAlerts]  = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter,  setFilter]  = useState("ALL");
  const [msg,     setMsg]     = useState("");

  const load = () => {
    setLoading(true);
    getActiveAlerts()
      .then(r => { const a = r.data; setAlerts(Array.isArray(a) ? a : a && a.alerts ? a.alerts : (a?.alerts || [])); })
      .catch(() => setAlerts([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  useEffect(() => {
    const h = (e) => {
      const { action } = e.detail || {};
      if (action === 'dismiss-first') {
        if (filtered.length === 0) { setMsg('No active alerts to dismiss'); setTimeout(() => setMsg(''), 3000); return; }
        handleDismiss(filtered[0].id);
      } else if (action === 'dismiss-all') {
        if (filtered.length === 0) { setMsg('No active alerts to dismiss'); setTimeout(() => setMsg(''), 3000); return; }
        handleDismissAll();
      }
    };
    window.addEventListener('scip-voice', h);
    return () => window.removeEventListener('scip-voice', h);
  }, [alerts, filter]);

  const getSeverity = (a) => {
    const t = (a.alertType || "").toUpperCase();
    if (t.includes("CRITICAL") || t.includes("RISK")) return "CRITICAL";
    if (t.includes("WARN") || t.includes("DELAY")) return "WARNING";
    return "INFO";
  };

  const handleDismiss = (id) => {
    dismissAlert(id).then(() => { setAlerts(p => p.filter(a => a.id !== id)); setMsg("Alert dismissed"); setTimeout(() => setMsg(""), 3000); }).catch(() => {});
  };

  const handleDismissAll = () => {
    Promise.all(filtered.map(a => dismissAlert(a.id))).then(() => { load(); setMsg("All alerts dismissed"); setTimeout(() => setMsg(""), 3000); }).catch(() => {});
  };

  const filtered = filter === "ALL" ? alerts : alerts.filter(a => getSeverity(a) === filter);

  return (
    <div style={{ padding:24, maxWidth:1000, margin:"0 auto" }}>
      {msg && <div style={{ padding:"10px 16px", borderRadius:8, marginBottom:16, background:"#f0fdf4", color:"#15803d", border:"1px solid #bbf7d0", fontWeight:600 }}>{msg}</div>}
      <div style={{ display:"flex", justifyContent:"space-between", alignItems:"center", marginBottom:24, flexWrap:"wrap", gap:12 }}>
        <div>
          <h1 style={{ fontSize:22, fontWeight:800, margin:0 }}>Active Alerts</h1>
          <p style={{ color:"#6b7280", margin:"4px 0 0" }}>{alerts.length} total &mdash; {alerts.filter(a => getSeverity(a) === "CRITICAL").length} critical</p>
        </div>
        <div style={{ display:"flex", gap:8, flexWrap:"wrap" }}>
          {["ALL","CRITICAL","WARNING","INFO"].map(f => (
            <button key={f} onClick={() => setFilter(f)} style={{ padding:"6px 14px", borderRadius:20, border:"1px solid #e5e7eb", background:filter===f?"#111827":"#fff", color:filter===f?"#fff":"#374151", fontWeight:600, fontSize:12, cursor:"pointer" }}>{f}</button>
          ))}
          {alerts.length > 0 && <button onClick={handleDismissAll} style={{ padding:"6px 14px", borderRadius:20, border:"1px solid #fecaca", background:"#fef2f2", color:"#dc2626", fontWeight:600, fontSize:12, cursor:"pointer" }}>Dismiss All</button>}
        </div>
      </div>

      {loading ? <div style={{ textAlign:"center", padding:60, color:"#6b7280" }}>Loading alerts...</div> :
       filtered.length === 0 ? (
        <div style={{ textAlign:"center", padding:60, background:"#fff", borderRadius:12, border:"1px solid #e5e7eb" }}>
          <div style={{ fontSize:40, marginBottom:12 }}>✅</div>
          <p style={{ color:"#6b7280", fontSize:15 }}>No active alerts &mdash; all clear!</p>
        </div>
       ) : filtered.map(a => {
        const sev = getSeverity(a);
        const c = SEV[sev];
        return (
          <div key={a.id} style={{ background:"#fff", border:"1px solid "+c.border, borderLeft:"4px solid "+c.color, borderRadius:10, padding:"16px 20px", marginBottom:12, display:"flex", justifyContent:"space-between", alignItems:"flex-start", gap:16 }}>
            <div style={{ flex:1 }}>
              <div style={{ display:"flex", alignItems:"center", gap:10, marginBottom:6, flexWrap:"wrap" }}>
                <span style={{ background:c.bg, color:c.color, padding:"2px 10px", borderRadius:20, fontSize:11, fontWeight:700 }}>{sev}</span>
                <span style={{ fontSize:14, fontWeight:700, color:"#111827" }}>{a.alertLabel || a.alertType}</span>
              </div>
              <p style={{ margin:0, fontSize:13, color:"#374151" }}>{a.shortSummary || a.longSummary}</p>
              <p style={{ margin:"6px 0 0", fontSize:11, color:"#9ca3af" }}>Created: {a.created || "Today"}</p>
            </div>
            <button onClick={() => handleDismiss(a.id)} style={{ padding:"6px 14px", background:"#fef2f2", color:"#dc2626", border:"1px solid #fecaca", borderRadius:8, cursor:"pointer", fontWeight:700, fontSize:12, whiteSpace:"nowrap" }}>Dismiss</button>
          </div>
        );
       })}
    </div>
  );
}
