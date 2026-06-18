import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { getBomList, createBom } from '../api';

function Modal({ title, onClose, children }) {
  return (
    <div style={{ position:'fixed', inset:0, background:'rgba(0,0,0,0.5)', display:'flex', alignItems:'center', justifyContent:'center', zIndex:1000 }}>
      <div style={{ background:'#fff', borderRadius:14, padding:32, width:460, maxWidth:'95vw', maxHeight:'90vh', overflowY:'auto' }}>
        <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:20 }}>
          <h2 style={{ fontSize:18, fontWeight:800, margin:0 }}>{title}</h2>
          <button onClick={onClose} style={{ background:'none', border:'none', fontSize:20, cursor:'pointer', color:'#6b7280' }}>×</button>
        </div>
        {children}
      </div>
    </div>
  );
}

const inp = { width:'100%', padding:'10px 12px', border:'1px solid #d1d5db', borderRadius:8, fontSize:14, outline:'none', boxSizing:'border-box' };

export default function BomListPage() {
  const [boms,       setBoms]       = useState([]);
  const [loading,    setLoading]    = useState(true);
  const [search,     setSearch]     = useState('');
  const [total,      setTotal]      = useState(0);
  const [showCreate, setShowCreate] = useState(false);
  const [form,       setForm]       = useState({ bomName:'', externalId:'', itemNumber:'' });
  const [saving,     setSaving]     = useState(false);
  const [msg,        setMsg]        = useState('');
  const navigate = useNavigate();

  const flash = (t) => { setMsg(t); setTimeout(() => setMsg(''), 4000); };

  const load = useCallback(async () => {
    try {
      setLoading(true);
      const r = await getBomList(0, 50);
      const data = r.data;
      if (data && data.content) { setBoms(data.content); setTotal(data.totalElements || 0); }
      else if (Array.isArray(data)) { setBoms(data); setTotal(data.length); }
      else { setBoms([]); setTotal(0); }
    } catch(e) { console.error(e); }
    finally { setLoading(false); }
  }, []);

  useEffect(() => { load(); }, [load]);

  // ── Voice command listener ─────────────────────────────────────────────────
  useEffect(() => {
    const handler = (e) => {
      const { action, prefill } = e.detail || {};
      if (action === 'open-create') {
        setForm({ bomName: prefill?.bomName || '', externalId: '', itemNumber: prefill?.itemNumber || '' });
        setShowCreate(true);
      }
    };
    window.addEventListener('scip-voice', handler);
    return () => window.removeEventListener('scip-voice', handler);
  }, []);

  const openCreate = () => { setForm({ bomName:'', externalId:'', itemNumber:'' }); setShowCreate(true); };

  const handleSave = async () => {
    if (!form.bomName.trim()) { flash('BOM Name is required'); return; }
    if (!form.itemNumber.trim()) { flash('Item Number is required'); return; }
    setSaving(true);
    try {
      await createBom({ bomName: form.bomName.trim(), externalId: form.externalId.trim(), itemNumber: form.itemNumber.trim().toUpperCase() });
      flash('BOM created successfully');
      setShowCreate(false);
      load();
    } catch { flash('Failed to create BOM'); }
    finally { setSaving(false); }
  };

  const filtered = search ? boms.filter(b =>
    (b.bomName||b.bom_name||'').toLowerCase().includes(search.toLowerCase()) ||
    (b.bomExternalId||b.bom_external_id||'').toLowerCase().includes(search.toLowerCase())
  ) : boms;

  const statusColor = (s) => s === 'APPROVED' ? { bg:'#f0fdf4', color:'#15803d' } : { bg:'#fff7ed', color:'#c2410c' };

  return (
    <div style={{ padding:'24px', maxWidth:'1100px', margin:'0 auto' }}>
      {msg && <div style={{ padding:'10px 16px', borderRadius:8, marginBottom:14, background:'#f0fdf4', color:'#15803d', border:'1px solid #bbf7d0', fontWeight:600 }}>{msg}</div>}

      <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:'24px' }}>
        <div>
          <h1 style={{ fontSize:'24px', fontWeight:'800', margin:0 }}>Bill of Materials</h1>
          <p style={{ color:'#6b7280', margin:'4px 0 0' }}>Browse and manage product BOMs</p>
        </div>
        <div style={{ display:'flex', gap:10, alignItems:'center' }}>
          <span style={{ background:'#eff6ff', border:'1px solid #93c5fd', borderRadius:'8px', padding:'8px 14px', fontSize:'13px', fontWeight:'700', color:'#1d4ed8' }}>
            {total || boms.length} BOMs
          </span>
          <button onClick={openCreate}
            style={{ background:'#1d4ed8', color:'#fff', border:'none', borderRadius:8, padding:'9px 18px', fontWeight:700, fontSize:13, cursor:'pointer' }}>
            + Create BOM
          </button>
        </div>
      </div>

      <input value={search} onChange={e => setSearch(e.target.value)}
        placeholder="Search BOM name or external ID..."
        style={{ width:'100%', padding:'10px 14px', border:'1px solid #d1d5db', borderRadius:'8px', fontSize:'14px', marginBottom:'16px', outline:'none' }} />

      {loading ? (
        <div style={{ textAlign:'center', padding:'60px', color:'#6b7280' }}>Loading BOMs…</div>
      ) : filtered.length === 0 ? (
        <div style={{ textAlign:'center', padding:'60px', color:'#6b7280', background:'#fff', borderRadius:12, border:'1px solid #e5e7eb' }}>
          No BOMs found — click <strong>+ Create BOM</strong> or say "create BOM [name]"
        </div>
      ) : (
        <div style={{ background:'#fff', borderRadius:'12px', border:'1px solid #e5e7eb', overflow:'hidden' }}>
          <table style={{ width:'100%', borderCollapse:'collapse', fontSize:'14px' }}>
            <thead>
              <tr style={{ background:'#f8fafc' }}>
                {['BOM Key','External ID','Name','Item','Status','Action'].map(h => (
                  <th key={h} style={{ padding:'12px 16px', textAlign:'left', fontWeight:'700', fontSize:'11px', textTransform:'uppercase', letterSpacing:'1px', color:'#6b7280', borderBottom:'1px solid #e5e7eb' }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {filtered.map((bom, i) => {
                const key = bom.bomKey || bom.bom_key || bom.id;
                const sc  = statusColor(bom.status);
                return (
                  <tr key={i} style={{ borderBottom:'1px solid #f1f5f9', cursor:'pointer' }}
                    onMouseEnter={e => e.currentTarget.style.background='#f8fafc'}
                    onMouseLeave={e => e.currentTarget.style.background='#fff'}
                    onClick={() => navigate('/bom/' + key)}>
                    <td style={{ padding:'12px 16px', fontFamily:'monospace', fontSize:'12px', color:'#6b7280' }}>{key}</td>
                    <td style={{ padding:'12px 16px', fontSize:'12px', color:'#6b7280' }}>{bom.bomExternalId||bom.bom_external_id||'-'}</td>
                    <td style={{ padding:'12px 16px', fontWeight:'700' }}>{bom.bomName||bom.bom_name||'-'}</td>
                    <td style={{ padding:'12px 16px' }}>
                      <span style={{ background:'#eff6ff', color:'#1d4ed8', padding:'2px 10px', borderRadius:'20px', fontSize:'11px', fontWeight:'700' }}>
                        {bom.item?.itemNumber||bom.itemNumber||'-'}
                      </span>
                    </td>
                    <td style={{ padding:'12px 16px' }}>
                      <span style={{ background:sc.bg, color:sc.color, padding:'2px 10px', borderRadius:'20px', fontSize:'11px', fontWeight:'700' }}>{bom.status||'ACTIVE'}</span>
                    </td>
                    <td style={{ padding:'12px 16px' }}>
                      <button onClick={e => { e.stopPropagation(); navigate('/bom/' + key); }}
                        style={{ background:'#1d4ed8', color:'#fff', border:'none', borderRadius:'6px', padding:'5px 14px', fontSize:'12px', fontWeight:'700', cursor:'pointer' }}>
                        View Details
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}

      {/* Create BOM modal */}
      {showCreate && (
        <Modal title="Create New BOM" onClose={() => setShowCreate(false)}>
          <div style={{ marginBottom:14 }}>
            <label style={{ fontSize:13, fontWeight:600, display:'block', marginBottom:6 }}>BOM Name *</label>
            <input value={form.bomName} onChange={e => setForm(p => ({...p, bomName: e.target.value}))}
              placeholder="e.g. Laptop X1 Main Assembly" style={inp} autoFocus />
          </div>
          <div style={{ marginBottom:14 }}>
            <label style={{ fontSize:13, fontWeight:600, display:'block', marginBottom:6 }}>Item Number *</label>
            <input value={form.itemNumber} onChange={e => setForm(p => ({...p, itemNumber: e.target.value}))}
              placeholder="e.g. LAPTOP-X1" style={inp} />
          </div>
          <div style={{ marginBottom:22 }}>
            <label style={{ fontSize:13, fontWeight:600, display:'block', marginBottom:6 }}>External ID <span style={{ fontWeight:400, color:'#9ca3af' }}>(optional)</span></label>
            <input value={form.externalId} onChange={e => setForm(p => ({...p, externalId: e.target.value}))}
              placeholder="e.g. EXT-BOM-099" style={inp} />
          </div>
          <div style={{ display:'flex', gap:10, justifyContent:'flex-end' }}>
            <button onClick={() => setShowCreate(false)}
              style={{ padding:'10px 20px', border:'1px solid #d1d5db', borderRadius:8, background:'#fff', cursor:'pointer', fontWeight:600 }}>Cancel</button>
            <button onClick={handleSave} disabled={saving}
              style={{ padding:'10px 22px', background: saving ? '#93c5fd' : '#1d4ed8', color:'#fff', border:'none', borderRadius:8, cursor: saving ? 'not-allowed' : 'pointer', fontWeight:700 }}>
              {saving ? 'Saving…' : 'Create BOM'}
            </button>
          </div>
        </Modal>
      )}
    </div>
  );
}
