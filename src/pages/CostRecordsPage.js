import React, { useState, useEffect, useCallback } from "react";
import { getCostRecords, createCostRecord, updateCostRecord, submitCostRecord, approveCostRecord, rejectCostRecord } from "../api";

const STATUS_COLORS = {
  DRAFT:            { bg:"#f1f5f9", color:"#475569" },
  PENDING_APPROVAL: { bg:"#fffbeb", color:"#d97706" },
  APPROVED:         { bg:"#f0fdf4", color:"#15803d" },
  REJECTED:         { bg:"#fef2f2", color:"#dc2626" },
};

function Modal({ title, onClose, children }) {
  return (
    <div style={{ position:"fixed", inset:0, background:"rgba(0,0,0,0.5)", display:"flex", alignItems:"center", justifyContent:"center", zIndex:1000 }}>
      <div style={{ background:"#fff", borderRadius:14, padding:32, width:480, maxWidth:"95vw", maxHeight:"90vh", overflowY:"auto" }}>
        <div style={{ display:"flex", justifyContent:"space-between", alignItems:"center", marginBottom:20 }}>
          <h2 style={{ fontSize:18, fontWeight:800, margin:0 }}>{title}</h2>
          <button onClick={onClose} style={{ background:"none", border:"none", fontSize:20, cursor:"pointer", color:"#6b7280" }}>x</button>
        </div>
        {children}
      </div>
    </div>
  );
}

export default function CostRecordsPage() {
  const [records,      setRecords]      = useState([]);
  const [loading,      setLoading]      = useState(true);
  const [showForm,     setShowForm]     = useState(false);
  const [editRec,      setEditRec]      = useState(null);
  const [rejectId,     setRejectId]     = useState(null);
  const [rejectReason, setRejectReason] = useState("");
  const [filter,       setFilter]       = useState("ALL");
  const [search,       setSearch]       = useState("");
  const [msg,          setMsg]          = useState({ text:"", type:"" });
  const [form,         setForm]         = useState({ itemKey:"", proposedCost:"", justification:"", effectiveDate:"" });
  const user = (() => { try { return JSON.parse(localStorage.getItem("user_data") || "{}"); } catch(e) { return {}; } })();

  const flash = (text, type="success") => { setMsg({ text, type }); setTimeout(() => setMsg({ text:"", type:"" }), 4000); };

  const load = useCallback(() => {
    setLoading(true);
    getCostRecords(search)
      .then(r => { const d = r.data; setRecords(Array.isArray(d) ? d : d && d.content ? d.content : d && d.records ? d.records : (d?.content || d?.records || [])); })
      .catch(() => setRecords([]))
      .finally(() => setLoading(false));
  }, [search]);

  useEffect(() => { load(); }, [load]);

  const openCreate = () => { setEditRec(null); setForm({ itemKey:"", proposedCost:"", justification:"", effectiveDate:"" }); setShowForm(true); };
  const openEdit   = (r) => { setEditRec(r); setForm({ itemKey:r.itemKey||r.itemCode||"", proposedCost:r.proposedCost||"", justification:r.justification||"", effectiveDate:r.effectiveDate||"" }); setShowForm(true); };

  const handleSave = () => {
    if (!form.itemKey || !form.proposedCost || !form.justification) { flash("Item key, cost and justification are required", "error"); return; }
    if (form.justification.length < 10) { flash("Justification must be at least 10 characters", "error"); return; }
    const payload = { ...form, proposedCost: parseFloat(form.proposedCost), createdBy: user.userId || "kumar" };
    const action  = editRec ? updateCostRecord(editRec.id, payload) : createCostRecord(payload);
    action
      .then(() => { flash(editRec ? "Record updated" : "Cost record created as DRAFT"); setShowForm(false); setEditRec(null); load(); })
      .catch(e  => flash("Failed: " + (e.response?.data?.error || e.message), "error"));
  };

  const handleSubmit  = (id) => submitCostRecord(id).then(() => { flash("Submitted for approval"); load(); }).catch(e => flash(e.message, "error"));
  const handleApprove = (id) => approveCostRecord(id).then(() => { flash("Approved — ITEM_MASTER updated automatically"); load(); }).catch(e => flash(e.message, "error"));
  const handleReject  = ()   => {
    if (!rejectReason.trim()) { flash("Rejection reason required", "error"); return; }
    rejectCostRecord(rejectId, rejectReason).then(() => { flash("Record rejected"); setRejectId(null); setRejectReason(""); load(); }).catch(e => flash(e.message, "error"));
  };

  const filtered = records.filter(r => {
    const mf = filter === "ALL" || r.status === filter;
    const ms = !search || (r.itemCode || r.itemKey || "").toLowerCase().includes(search.toLowerCase());
    return mf && ms;
  });

  return (
    <div style={{ padding:24, maxWidth:1200, margin:"0 auto" }}>
      {msg.text && <div style={{ padding:"10px 16px", borderRadius:8, marginBottom:16, background:msg.type==="error"?"#fef2f2":"#f0fdf4", color:msg.type==="error"?"#dc2626":"#15803d", border:"1px solid "+(msg.type==="error"?"#fecaca":"#bbf7d0"), fontWeight:600 }}>{msg.text}</div>}

      <div style={{ display:"flex", justifyContent:"space-between", alignItems:"center", marginBottom:20, flexWrap:"wrap", gap:12 }}>
        <div><h1 style={{ fontSize:22, fontWeight:800, margin:0 }}>Cost Records</h1><p style={{ color:"#6b7280", margin:"4px 0 0" }}>DRAFT &rarr; PENDING_APPROVAL &rarr; APPROVED / REJECTED</p></div>
        <button onClick={openCreate} style={{ background:"#1d4ed8", color:"#fff", border:"none", borderRadius:8, padding:"10px 20px", fontWeight:700, cursor:"pointer", fontSize:14 }}>+ New Cost Record</button>
      </div>

      <div style={{ display:"flex", gap:8, marginBottom:16, flexWrap:"wrap", alignItems:"center" }}>
        {["ALL","DRAFT","PENDING_APPROVAL","APPROVED","REJECTED"].map(s => (
          <button key={s} onClick={() => setFilter(s)} style={{ padding:"5px 14px", borderRadius:20, border:"1px solid #e5e7eb", background:filter===s?"#111827":"#fff", color:filter===s?"#fff":"#374151", fontWeight:600, fontSize:11, cursor:"pointer" }}>{s.replace(/_/g," ")}</button>
        ))}
        <input value={search} onChange={e => setSearch(e.target.value)} placeholder="Search by item..." style={{ marginLeft:"auto", padding:"6px 12px", borderRadius:8, border:"1px solid #e5e7eb", fontSize:13, outline:"none" }} />
      </div>

      {loading ? <div style={{ textAlign:"center", padding:60, color:"#6b7280" }}>Loading...</div> :
       filtered.length === 0 ? <div style={{ textAlign:"center", padding:60, background:"#fff", borderRadius:12, border:"1px solid #e5e7eb", color:"#6b7280" }}>No records found. Click &quot;New Cost Record&quot; to create one.</div> :
       <div style={{ background:"#fff", borderRadius:12, border:"1px solid #e5e7eb", overflow:"auto" }}>
        <table style={{ width:"100%", borderCollapse:"collapse", fontSize:13 }}>
          <thead><tr style={{ background:"#f8fafc" }}>
            {["Item","Proposed Cost","Prev Cost","Change %","Status","Justification","Actions"].map(h => (
              <th key={h} style={{ padding:"10px 14px", textAlign:"left", fontWeight:700, fontSize:11, textTransform:"uppercase", color:"#6b7280", borderBottom:"1px solid #e5e7eb", whiteSpace:"nowrap" }}>{h}</th>
            ))}
          </tr></thead>
          <tbody>
            {filtered.map(r => {
              const sc  = STATUS_COLORS[r.status] || STATUS_COLORS.DRAFT;
              const pct = r.previousCost ? (((r.proposedCost - r.previousCost) / r.previousCost) * 100).toFixed(1) : null;
              return (
                <tr key={r.id} style={{ borderBottom:"1px solid #f1f5f9" }} onMouseEnter={e => e.currentTarget.style.background="#f9fafb"} onMouseLeave={e => e.currentTarget.style.background="#fff"}>
                  <td style={{ padding:"10px 14px", fontWeight:600, fontFamily:"monospace" }}>{r.itemCode || r.itemKey}</td>
                  <td style={{ padding:"10px 14px", fontWeight:700 }}>${parseFloat(r.proposedCost || 0).toFixed(4)}</td>
                  <td style={{ padding:"10px 14px", color:"#6b7280" }}>{r.previousCost ? "$"+parseFloat(r.previousCost).toFixed(4) : "-"}</td>
                  <td style={{ padding:"10px 14px" }}>{pct ? <span style={{ color:parseFloat(pct)>0?"#dc2626":"#15803d", fontWeight:700 }}>{parseFloat(pct)>0?"+":""}{pct}%</span> : "-"}</td>
                  <td style={{ padding:"10px 14px" }}><span style={{ background:sc.bg, color:sc.color, padding:"2px 10px", borderRadius:20, fontSize:11, fontWeight:700, whiteSpace:"nowrap" }}>{r.status}</span></td>
                  <td style={{ padding:"10px 14px", color:"#6b7280", maxWidth:200 }}><div style={{ overflow:"hidden", textOverflow:"ellipsis", whiteSpace:"nowrap" }} title={r.justification}>{r.justification}</div></td>
                  <td style={{ padding:"10px 14px" }}>
                    <div style={{ display:"flex", gap:5, flexWrap:"nowrap" }}>
                      {r.status === "DRAFT" && <>
                        <button onClick={() => openEdit(r)} style={{ padding:"3px 8px", fontSize:11, background:"#eff6ff", color:"#1d4ed8", border:"1px solid #bfdbfe", borderRadius:5, cursor:"pointer", fontWeight:600, whiteSpace:"nowrap" }}>Edit</button>
                        <button onClick={() => handleSubmit(r.id)} style={{ padding:"3px 8px", fontSize:11, background:"#fffbeb", color:"#d97706", border:"1px solid #fde68a", borderRadius:5, cursor:"pointer", fontWeight:600, whiteSpace:"nowrap" }}>Submit</button>
                      </>}
                      {r.status === "PENDING_APPROVAL" && <>
                        <button onClick={() => handleApprove(r.id)} style={{ padding:"3px 8px", fontSize:11, background:"#f0fdf4", color:"#15803d", border:"1px solid #86efac", borderRadius:5, cursor:"pointer", fontWeight:600, whiteSpace:"nowrap" }}>Approve</button>
                        <button onClick={() => { setRejectId(r.id); setRejectReason(""); }} style={{ padding:"3px 8px", fontSize:11, background:"#fef2f2", color:"#dc2626", border:"1px solid #fecaca", borderRadius:5, cursor:"pointer", fontWeight:600, whiteSpace:"nowrap" }}>Reject</button>
                      </>}
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
       </div>
      }

      {showForm && (
        <Modal title={editRec ? "Edit Cost Record" : "New Cost Record"} onClose={() => { setShowForm(false); setEditRec(null); }}>
          <div style={{ marginBottom:14 }}>
            <label style={{ fontSize:13, fontWeight:600, display:"block", marginBottom:6 }}>Item Key *</label>
            <input value={form.itemKey} disabled={!!editRec} onChange={e => setForm(p => ({...p, itemKey:e.target.value}))} placeholder="e.g. COMP-001"
              style={{ width:"100%", padding:"10px 12px", border:"1px solid #d1d5db", borderRadius:8, fontSize:14, outline:"none", boxSizing:"border-box", background:editRec?"#f9fafb":"#fff" }} />
          </div>
          <div style={{ marginBottom:14 }}>
            <label style={{ fontSize:13, fontWeight:600, display:"block", marginBottom:6 }}>Proposed Cost ($) *</label>
            <input type="number" step="0.0001" value={form.proposedCost} onChange={e => setForm(p => ({...p, proposedCost:e.target.value}))} placeholder="e.g. 12.5000"
              style={{ width:"100%", padding:"10px 12px", border:"1px solid #d1d5db", borderRadius:8, fontSize:14, outline:"none", boxSizing:"border-box" }} />
          </div>
          <div style={{ marginBottom:14 }}>
            <label style={{ fontSize:13, fontWeight:600, display:"block", marginBottom:6 }}>Effective Date</label>
            <input type="date" value={form.effectiveDate} onChange={e => setForm(p => ({...p, effectiveDate:e.target.value}))}
              style={{ width:"100%", padding:"10px 12px", border:"1px solid #d1d5db", borderRadius:8, fontSize:14, outline:"none", boxSizing:"border-box" }} />
          </div>
          <div style={{ marginBottom:20 }}>
            <label style={{ fontSize:13, fontWeight:600, display:"block", marginBottom:6 }}>Justification * (min 10 chars)</label>
            <textarea value={form.justification} onChange={e => setForm(p => ({...p, justification:e.target.value}))} rows={4} placeholder="Explain why this cost change is needed..."
              style={{ width:"100%", padding:"10px 12px", border:"1px solid #d1d5db", borderRadius:8, fontSize:14, outline:"none", resize:"vertical", boxSizing:"border-box" }} />
            <div style={{ fontSize:11, color:form.justification.length < 10 ? "#dc2626" : "#6b7280", marginTop:4 }}>{form.justification.length} chars {form.justification.length < 10 ? "(need 10+)" : "OK"}</div>
          </div>
          <div style={{ display:"flex", gap:10, justifyContent:"flex-end" }}>
            <button onClick={() => { setShowForm(false); setEditRec(null); }} style={{ padding:"10px 20px", border:"1px solid #d1d5db", borderRadius:8, background:"#fff", cursor:"pointer", fontWeight:600 }}>Cancel</button>
            <button onClick={handleSave} style={{ padding:"10px 20px", background:"#1d4ed8", color:"#fff", border:"none", borderRadius:8, cursor:"pointer", fontWeight:700 }}>{editRec ? "Update" : "Save Draft"}</button>
          </div>
        </Modal>
      )}

      {rejectId && (
        <Modal title="Reject Cost Record" onClose={() => setRejectId(null)}>
          <p style={{ color:"#6b7280", fontSize:13, marginBottom:12 }}>Please provide a reason for rejection.</p>
          <textarea value={rejectReason} onChange={e => setRejectReason(e.target.value)} rows={4} placeholder="Enter rejection reason..."
            style={{ width:"100%", padding:"10px 12px", border:"1px solid #d1d5db", borderRadius:8, fontSize:14, outline:"none", resize:"vertical", boxSizing:"border-box", marginBottom:16 }} />
          <div style={{ display:"flex", gap:10, justifyContent:"flex-end" }}>
            <button onClick={() => setRejectId(null)} style={{ padding:"10px 20px", border:"1px solid #d1d5db", borderRadius:8, background:"#fff", cursor:"pointer", fontWeight:600 }}>Cancel</button>
            <button onClick={handleReject} style={{ padding:"10px 20px", background:"#dc2626", color:"#fff", border:"none", borderRadius:8, cursor:"pointer", fontWeight:700 }}>Confirm Reject</button>
          </div>
        </Modal>
      )}
    </div>
  );
}
