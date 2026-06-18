import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { getBomList } from '../api';

export default function BomListPage() {
  const [boms,    setBoms]    = useState([]);
  const [loading, setLoading] = useState(true);
  const [search,  setSearch]  = useState('');
  const [total,   setTotal]   = useState(0);
  const navigate = useNavigate();

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

  const filtered = search ? boms.filter(b =>
    (b.bomName||b.bom_name||'').toLowerCase().includes(search.toLowerCase()) ||
    (b.bomExternalId||b.bom_external_id||'').toLowerCase().includes(search.toLowerCase())
  ) : boms;

  const statusColor = (s) => s==='APPROVED' ? { bg:'#f0fdf4', color:'#15803d' } : { bg:'#fff7ed', color:'#c2410c' };

  return (
    <div style={{ padding:'24px', maxWidth:'1100px', margin:'0 auto' }}>
      <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:'24px' }}>
        <div>
          <h1 style={{ fontSize:'24px', fontWeight:'800', margin:0 }}>Bill of Materials</h1>
          <p style={{ color:'#6b7280', margin:'4px 0 0' }}>Browse and manage product BOMs</p>
        </div>
        <span style={{ background:'#eff6ff', border:'1px solid #93c5fd', borderRadius:'8px', padding:'8px 14px', fontSize:'13px', fontWeight:'700', color:'#1d4ed8' }}>
          {total || boms.length} BOMs
        </span>
      </div>

      <input value={search} onChange={e => setSearch(e.target.value)}
        placeholder="Search BOM name or external ID..."
        style={{ width:'100%', padding:'10px 14px', border:'1px solid #d1d5db', borderRadius:'8px', fontSize:'14px', marginBottom:'16px', outline:'none' }} />

      {loading ? (
        <div style={{ textAlign:'center', padding:'60px', color:'#6b7280' }}>Loading BOMs...</div>
      ) : filtered.length === 0 ? (
        <div style={{ textAlign:'center', padding:'60px', color:'#6b7280' }}>No BOMs found</div>
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
                const key  = bom.bomKey || bom.bom_key || bom.id;
                const sc   = statusColor(bom.status);
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
    </div>
  );
}
