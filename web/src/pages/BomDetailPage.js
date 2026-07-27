import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getBomDetail } from '../api';

export default function BomDetailPage() {
  const { bomKey } = useParams();
  const navigate   = useNavigate();
  const [bom,     setBom]     = useState(null);
  const [loading, setLoading] = useState(true);
  const [error,   setError]   = useState('');

  useEffect(() => {
    (async () => {
      try {
        setLoading(true);
        const r = await getBomDetail(bomKey);
        setBom(r.data);
      } catch(e) { setError('Failed to load BOM details'); }
      finally { setLoading(false); }
    })();
  }, [bomKey]);

  if (loading) return <div style={{ padding:'60px', textAlign:'center', color:'#6b7280' }}>Loading BOM details...</div>;
  if (error)   return <div style={{ padding:'60px', textAlign:'center', color:'#dc2626' }}>{error}</div>;
  if (!bom)    return <div style={{ padding:'60px', textAlign:'center', color:'#6b7280' }}>BOM not found</div>;

  const lines  = bom.lines || bom.lineItems || bom.line_items || bom.components || [];
  const name   = bom.bomName || bom.bom_name || '-';
  const desc   = bom.bomDescription || bom.bom_description || '-';
  const item   = bom.item?.itemNumber || bom.itemNumber || '-';
  const status = bom.status || 'APPROVED';

  return (
    <div style={{ padding:'24px', maxWidth:'1000px', margin:'0 auto' }}>
      <button onClick={() => navigate('/bom')}
        style={{ background:'none', border:'1px solid #e5e7eb', borderRadius:'8px', padding:'8px 16px', cursor:'pointer', fontWeight:'600', fontSize:'14px', marginBottom:'20px' }}>
        Back to BOM List
      </button>

      <div style={{ background:'#fff', border:'1px solid #e5e7eb', borderRadius:'14px', padding:'28px', marginBottom:'20px' }}>
        <div style={{ display:'flex', justifyContent:'space-between', alignItems:'flex-start' }}>
          <div>
            <h1 style={{ fontSize:'22px', fontWeight:'800', margin:'0 0 8px' }}>{name}</h1>
            <p style={{ color:'#6b7280', fontSize:'14px', margin:'0 0 14px' }}>{desc}</p>
            <div style={{ display:'flex', gap:'10px', flexWrap:'wrap' }}>
              <span style={{ background:'#eff6ff', color:'#1d4ed8', padding:'4px 14px', borderRadius:'20px', fontSize:'12px', fontWeight:'700' }}>Item: {item}</span>
              <span style={{ background: status==='APPROVED'?'#f0fdf4':'#fff7ed', color: status==='APPROVED'?'#15803d':'#c2410c', padding:'4px 14px', borderRadius:'20px', fontSize:'12px', fontWeight:'700' }}>{status}</span>
              <span style={{ background:'#f1f5f9', color:'#374151', padding:'4px 14px', borderRadius:'20px', fontSize:'12px', fontWeight:'700' }}>BOM Key: {bomKey}</span>
            </div>
          </div>
        </div>
      </div>

      <div style={{ background:'#fff', border:'1px solid #e5e7eb', borderRadius:'14px', overflow:'hidden' }}>
        <div style={{ padding:'16px 20px', borderBottom:'1px solid #e5e7eb', display:'flex', justifyContent:'space-between', alignItems:'center' }}>
          <h3 style={{ fontSize:'16px', fontWeight:'800', margin:0 }}>Component Lines</h3>
          <span style={{ background:'#f1f5f9', color:'#374151', padding:'3px 10px', borderRadius:'20px', fontSize:'12px', fontWeight:'700' }}>{lines.length} components</span>
        </div>
        {lines.length === 0 ? (
          <div style={{ padding:'40px', textAlign:'center', color:'#6b7280' }}>No component lines found</div>
        ) : (
          <table style={{ width:'100%', borderCollapse:'collapse', fontSize:'14px' }}>
            <thead>
              <tr style={{ background:'#f8fafc' }}>
                {['#','Item Code','Description','Quantity','Rollup','Notes'].map(h => (
                  <th key={h} style={{ padding:'10px 16px', textAlign:'left', fontWeight:'700', fontSize:'11px', textTransform:'uppercase', letterSpacing:'1px', color:'#6b7280', borderBottom:'1px solid #e5e7eb' }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {lines.map((line, i) => {
                const icode  = line.item?.itemNumber || line.itemNumber || line.item_number || '-';
                const ldesc  = line.description || '-';
                const qty    = line.itemQuantity || line.quantity || '-';
                const rollup = line.rollupFlag || line.rollup_flag || '-';
                const notes  = line.notes || '-';
                return (
                  <tr key={i} style={{ borderBottom:'1px solid #f1f5f9' }}
                    onMouseEnter={e => e.currentTarget.style.background='#f8fafc'}
                    onMouseLeave={e => e.currentTarget.style.background='#fff'}>
                    <td style={{ padding:'10px 16px', color:'#9ca3af', fontSize:'12px' }}>{i+1}</td>
                    <td style={{ padding:'10px 16px' }}>
                      <span style={{ background:'#eff6ff', color:'#1d4ed8', padding:'2px 10px', borderRadius:'6px', fontSize:'12px', fontWeight:'700' }}>{icode}</span>
                    </td>
                    <td style={{ padding:'10px 16px', color:'#374151' }}>{ldesc}</td>
                    <td style={{ padding:'10px 16px', fontWeight:'700' }}>{qty}</td>
                    <td style={{ padding:'10px 16px' }}>
                      <span style={{ background: rollup==='Y'?'#f0fdf4':'#f1f5f9', color: rollup==='Y'?'#15803d':'#6b7280', padding:'2px 8px', borderRadius:'4px', fontSize:'11px', fontWeight:'700' }}>{rollup}</span>
                    </td>
                    <td style={{ padding:'10px 16px', color:'#6b7280', fontSize:'12px' }}>{notes}</td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
