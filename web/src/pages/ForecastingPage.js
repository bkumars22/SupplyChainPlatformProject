import React, { useState, useEffect, useCallback } from "react";
import { getForecasts, createForecast, getForecastDetail } from "../api";

function Modal({ title, onClose, children }) {
  return (
    <div style={{ position:"fixed", inset:0, background:"rgba(0,0,0,0.5)", display:"flex", alignItems:"center", justifyContent:"center", zIndex:1000 }}>
      <div style={{ background:"#fff", borderRadius:14, padding:32, width:460, maxWidth:"95vw" }}>
        <div style={{ display:"flex", justifyContent:"space-between", alignItems:"center", marginBottom:20 }}>
          <h2 style={{ fontSize:18, fontWeight:800, margin:0 }}>{title}</h2>
          <button onClick={onClose} style={{ background:"none", border:"none", fontSize:20, cursor:"pointer", color:"#6b7280" }}>x</button>
        </div>
        {children}
      </div>
    </div>
  );
}

export default function ForecastingPage() {
  const [forecasts, setForecasts] = useState([]);
  const [selected,  setSelected]  = useState(null);
  const [detail,    setDetail]    = useState(null);
  const [loading,   setLoading]   = useState(true);
  const [showForm,  setShowForm]  = useState(false);
  const [msg,       setMsg]       = useState({ text:"", type:"" });
  const [form,      setForm]      = useState({ itemKey:"", forecastType:"DEMAND", startPeriod:"", endPeriod:"" });

  const flash = (text, type="success") => { setMsg({ text, type }); setTimeout(() => setMsg({ text:"", type:"" }), 4000); };

  const load = useCallback(() => {
    setLoading(true);
    getForecasts()
      .then(r => { const d = r.data; setForecasts(d?.forecasts || (Array.isArray(d) ? d : d && d.forecasts ? d.forecasts : d && d.data ? d.data : [])); })
      .catch(() => setForecasts([]))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => { load(); }, [load]);

  const loadDetail = (id) => {
    setSelected(id);
    setDetail(null);
    getForecastDetail(id).then(r => setDetail(r.data)).catch(() => setDetail({ error:"Could not load detail" }));
  };

  const handleCreate = () => {
    if (!form.itemKey || !form.startPeriod || !form.endPeriod) { flash("Item key, start and end period required", "error"); return; }
    createForecast(form)
      .then(() => { flash("Forecast created"); setShowForm(false); setForm({ itemKey:"", forecastType:"DEMAND", startPeriod:"", endPeriod:"" }); load(); })
      .catch(e => flash("Failed: " + (e.response?.data?.error || e.message), "error"));
  };

  return (
    <div style={{ padding:24, maxWidth:1100, margin:"0 auto" }}>
      {msg.text && <div style={{ padding:"10px 16px", borderRadius:8, marginBottom:16, background:msg.type==="error"?"#fef2f2":"#f0fdf4", color:msg.type==="error"?"#dc2626":"#15803d", border:"1px solid "+(msg.type==="error"?"#fecaca":"#bbf7d0"), fontWeight:600 }}>{msg.text}</div>}

      <div style={{ display:"flex", justifyContent:"space-between", alignItems:"center", marginBottom:24 }}>
        <div><h1 style={{ fontSize:22, fontWeight:800, margin:0 }}>Demand Forecasting</h1><p style={{ color:"#6b7280", margin:"4px 0 0" }}>Create and manage demand forecasts with variance analysis</p></div>
        <button onClick={() => setShowForm(true)} style={{ background:"#1d4ed8", color:"#fff", border:"none", borderRadius:8, padding:"10px 20px", fontWeight:700, cursor:"pointer", fontSize:14 }}>+ New Forecast</button>
      </div>

      <div style={{ display:"grid", gridTemplateColumns:selected?"320px 1fr":"1fr", gap:20, alignItems:"start" }}>
        <div>
          {loading ? <div style={{ textAlign:"center", padding:40, color:"#6b7280" }}>Loading...</div> :
           forecasts.length === 0 ? (
            <div style={{ textAlign:"center", padding:60, background:"#fff", borderRadius:12, border:"1px solid #e5e7eb" }}>
              <div style={{ fontSize:40, marginBottom:12 }}>📈</div>
              <p style={{ color:"#6b7280" }}>No forecasts yet.</p>
              <button onClick={() => setShowForm(true)} style={{ background:"#1d4ed8", color:"#fff", border:"none", borderRadius:8, padding:"8px 18px", fontWeight:700, cursor:"pointer", marginTop:8 }}>Create First Forecast</button>
            </div>
           ) : forecasts.map(f => (
            <div key={f.forecastKey} onClick={() => loadDetail(f.forecastKey)}
              style={{ background:"#fff", border:"1px solid "+(selected===f.forecastKey?"#1d4ed8":"#e5e7eb"), borderLeft:"4px solid "+(selected===f.forecastKey?"#1d4ed8":"transparent"), borderRadius:10, padding:"14px 18px", marginBottom:10, cursor:"pointer" }}
              onMouseEnter={e => e.currentTarget.style.background="#f8fafc"}
              onMouseLeave={e => e.currentTarget.style.background=selected===f.forecastKey?"#eff6ff":"#fff"}>
              <div style={{ fontWeight:700, fontSize:14, color:"#111827" }}>{f.itemNumber}</div>
              <div style={{ fontSize:12, color:"#6b7280", marginTop:4 }}>{f.forecastType} &middot; {(f.effectiveFrom||"").substring(0,10)} to {(f.effectiveTo||"").substring(0,10)}</div>
            </div>
           ))}
        </div>

        {selected && (
          <div style={{ background:"#fff", border:"1px solid #e5e7eb", borderRadius:12, padding:20 }}>
            <div style={{ display:"flex", justifyContent:"space-between", alignItems:"center", marginBottom:20 }}>
              <h3 style={{ margin:0, fontSize:16, fontWeight:700 }}>Forecast Detail</h3>
              <button onClick={() => { setSelected(null); setDetail(null); }} style={{ background:"none", border:"none", fontSize:18, cursor:"pointer", color:"#6b7280" }}>x</button>
            </div>
            {!detail ? <div style={{ textAlign:"center", padding:40, color:"#6b7280" }}>Loading detail...</div> :
             detail.error ? <p style={{ color:"#dc2626" }}>{detail.error}</p> :
             detail.values && detail.values.length > 0 ? (
              <table style={{ width:"100%", borderCollapse:"collapse", fontSize:13 }}>
                <thead><tr style={{ background:"#f8fafc" }}>
                  {["Period","Forecast Qty","Actual Qty","Variance","Var %"].map(h => (
                    <th key={h} style={{ padding:"8px 12px", textAlign:"left", fontWeight:700, fontSize:11, textTransform:"uppercase", color:"#6b7280", borderBottom:"1px solid #e5e7eb" }}>{h}</th>
                  ))}
                </tr></thead>
                <tbody>
                  {detail.values.map((v,i) => {
                    const variance = (v.actualQty || 0) - (v.forecastQty || 0);
                    const pct = v.forecastQty ? ((variance / v.forecastQty) * 100).toFixed(1) : "0";
                    return (
                      <tr key={i} style={{ borderBottom:"1px solid #f1f5f9" }}>
                        <td style={{ padding:"8px 12px", fontWeight:600 }}>{v.periodLabel || v.period}</td>
                        <td style={{ padding:"8px 12px" }}>{v.forecastQty?.toFixed(0) || "-"}</td>
                        <td style={{ padding:"8px 12px" }}>{v.actualQty?.toFixed(0) || "-"}</td>
                        <td style={{ padding:"8px 12px", color:variance>=0?"#15803d":"#dc2626", fontWeight:600 }}>{variance>=0?"+":""}{variance.toFixed(0)}</td>
                        <td style={{ padding:"8px 12px", color:Math.abs(parseFloat(pct))>10?"#dc2626":"#15803d", fontWeight:600 }}>{parseFloat(pct)>=0?"+":""}{pct}%</td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
             ) : <p style={{ color:"#6b7280", textAlign:"center", padding:20 }}>No period data available.</p>
            }
          </div>
        )}
      </div>

      {showForm && (
        <Modal title="New Forecast" onClose={() => setShowForm(false)}>
          <div style={{ marginBottom:14 }}>
            <label style={{ fontSize:13, fontWeight:600, display:"block", marginBottom:6 }}>Item Key *</label>
            <input value={form.itemKey} onChange={e => setForm(p => ({...p,itemKey:e.target.value}))} placeholder="e.g. COMP-001"
              style={{ width:"100%", padding:"10px 12px", border:"1px solid #d1d5db", borderRadius:8, fontSize:14, outline:"none", boxSizing:"border-box" }} />
          </div>
          <div style={{ marginBottom:14 }}>
            <label style={{ fontSize:13, fontWeight:600, display:"block", marginBottom:6 }}>Forecast Type</label>
            <select value={form.forecastType} onChange={e => setForm(p => ({...p,forecastType:e.target.value}))}
              style={{ width:"100%", padding:"10px 12px", border:"1px solid #d1d5db", borderRadius:8, fontSize:14, outline:"none", background:"#fff", boxSizing:"border-box" }}>
              {["DEMAND","SUPPLY","COST","INVENTORY"].map(t => <option key={t} value={t}>{t}</option>)}
            </select>
          </div>
          <div style={{ display:"grid", gridTemplateColumns:"1fr 1fr", gap:14, marginBottom:20 }}>
            <div>
              <label style={{ fontSize:13, fontWeight:600, display:"block", marginBottom:6 }}>Start Period * (YYYY-MM)</label>
              <input value={form.startPeriod} onChange={e => setForm(p => ({...p,startPeriod:e.target.value}))} placeholder="2026-01"
                style={{ width:"100%", padding:"10px 12px", border:"1px solid #d1d5db", borderRadius:8, fontSize:14, outline:"none", boxSizing:"border-box" }} />
            </div>
            <div>
              <label style={{ fontSize:13, fontWeight:600, display:"block", marginBottom:6 }}>End Period * (YYYY-MM)</label>
              <input value={form.endPeriod} onChange={e => setForm(p => ({...p,endPeriod:e.target.value}))} placeholder="2026-12"
                style={{ width:"100%", padding:"10px 12px", border:"1px solid #d1d5db", borderRadius:8, fontSize:14, outline:"none", boxSizing:"border-box" }} />
            </div>
          </div>
          <div style={{ display:"flex", gap:10, justifyContent:"flex-end" }}>
            <button onClick={() => setShowForm(false)} style={{ padding:"10px 20px", border:"1px solid #d1d5db", borderRadius:8, background:"#fff", cursor:"pointer", fontWeight:600 }}>Cancel</button>
            <button onClick={handleCreate} style={{ padding:"10px 20px", background:"#1d4ed8", color:"#fff", border:"none", borderRadius:8, cursor:"pointer", fontWeight:700 }}>Create Forecast</button>
          </div>
        </Modal>
      )}
    </div>
  );
}
