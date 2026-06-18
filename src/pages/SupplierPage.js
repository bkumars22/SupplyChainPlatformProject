import React, { useState, useEffect, useCallback } from 'react';
import { getSuppliers, getSupplierDeliveries } from '../api';
import SupplierRatingModal from '../components/SupplierRatingModal';
import SupplierRatingHistory from '../components/SupplierRatingHistory';

const TIER_COLORS = {
  PREFERRED:   { bg:'#f0fdf4', color:'#15803d' },
  APPROVED:    { bg:'#eff6ff', color:'#1d4ed8' },
  CONDITIONAL: { bg:'#fff7ed', color:'#c2410c' },
  PROBATION:   { bg:'#fff1f2', color:'#dc2626' },
};

const ScoreBar = ({ val }) => {
  const color = val>=90?'#15803d':val>=70?'#1d4ed8':val>=60?'#d97706':'#dc2626';
  return (
    <div style={{ background:'#e5e7eb', borderRadius:'4px', height:'6px', width:'100%', marginTop:'4px' }}>
      <div style={{ width:`${val}%`, height:'100%', borderRadius:'4px', background:color }} />
    </div>
  );
};

export default function SupplierPage() {
  const [suppliers,  setSuppliers]  = useState([]);
  const [selected,       setSelected]       = useState(null);
  const [deliveries,     setDeliveries]     = useState([]);
  const [loading,        setLoading]        = useState(true);
  const [loadingDel,     setLoadingDel]     = useState(false);
  const [search,         setSearch]         = useState('');
  const [activeTab,      setActiveTab]      = useState('scorecard');
  const [showRatingModal, setShowRatingModal] = useState(false);
  const [ratingKey,      setRatingKey]      = useState(0);

  const load = useCallback(async () => {
    try {
      setLoading(true);
      const res = await getSuppliers();
      const raw = res.data || res;
      setSuppliers(Array.isArray(raw) ? raw : raw.suppliers || raw.content || raw.data || []);
    } catch(e) {
      console.error(e);
      setSuppliers([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const openDetail = async (sup) => {
    setSelected(sup); setActiveTab('scorecard'); setLoadingDel(true);
    try {
      const res = await getSupplierDeliveries(sup.supplierId||sup.supplier_id);
      setDeliveries(res.data||[]);
    } catch(e) {
      setDeliveries([]);
    } finally {
      setLoadingDel(false);
    }
  };

  const filtered = suppliers.filter(s =>
    (s.supplierName||s.supplier_name||'').toLowerCase().includes(search.toLowerCase()) ||
    String(s.supplierId||s.supplier_id||'').toLowerCase().includes(search.toLowerCase())
  );

  const atRisk   = suppliers.filter(s=>s.atRisk||s.at_risk).length;
  const avgScore = suppliers.length
    ? (suppliers.reduce((a,s)=>a+(s.compositeScore||s.composite_score||0),0)/suppliers.length).toFixed(1)
    : 0;

  if (selected) {
    const tc = TIER_COLORS[selected.tier]||TIER_COLORS.APPROVED;
    const score = selected.compositeScore||selected.composite_score||0;
    return (
      <div style={{ padding:'24px', maxWidth:'1000px', margin:'0 auto' }}>
        <button onClick={()=>setSelected(null)} style={{ display:'flex', alignItems:'center', gap:'6px', background:'none', border:'1px solid #e5e7eb', borderRadius:'8px', padding:'8px 16px', cursor:'pointer', fontWeight:'600', fontSize:'14px', marginBottom:'20px' }}>
          Back to Suppliers
        </button>
        <div style={{ background:'#fff', border:'1px solid #e5e7eb', borderRadius:'14px', padding:'28px', marginBottom:'20px' }}>
          <div style={{ display:'flex', justifyContent:'space-between', alignItems:'flex-start' }}>
            <div>
              <h1 style={{ fontSize:'22px', fontWeight:'800', margin:'0 0 6px' }}>{selected.supplierName||selected.supplier_name}</h1>
              <div style={{ color:'#6b7280', fontSize:'14px', marginBottom:'12px' }}>{selected.supplierId||selected.supplier_id} - {selected.country}</div>
              <div style={{ display:'flex', gap:'8px', flexWrap:'wrap' }}>
                <span style={{ background:tc.bg, color:tc.color, padding:'4px 14px', borderRadius:'20px', fontSize:'12px', fontWeight:'700' }}>{selected.tier}</span>
                {(selected.atRisk||selected.at_risk) && (
                  <span style={{ background:'#fff1f2', color:'#dc2626', padding:'4px 14px', borderRadius:'20px', fontSize:'12px', fontWeight:'700' }}>AT RISK</span>
                )}
              </div>
            </div>
            <div style={{ textAlign:'center' }}>
              <div style={{ fontSize:'48px', fontWeight:'900', color:score>=90?'#15803d':score>=70?'#1d4ed8':'#dc2626' }}>{score.toFixed(1)}</div>
              <div style={{ fontSize:'12px', color:'#6b7280', fontWeight:'600' }}>COMPOSITE SCORE</div>
            </div>
          </div>
        </div>
        <div style={{ display:'flex', gap:'4px', marginBottom:'16px', flexWrap:'wrap' }}>
          {[['scorecard','Score Breakdown'],['deliveries','Delivery History'],['ratings','Quality Ratings']].map(([k,l])=>(
            <button key={k} data-testid={`tab-${k}`} onClick={()=>setActiveTab(k)} style={{ padding:'8px 20px', borderRadius:'8px', fontWeight:'700', fontSize:'13px', cursor:'pointer', border:'none', background:activeTab===k?'#1d4ed8':'#f1f5f9', color:activeTab===k?'#fff':'#374151' }}>{l}</button>
          ))}
        </div>
        {activeTab==='scorecard' && (
          <div style={{ background:'#fff', border:'1px solid #e5e7eb', borderRadius:'14px', padding:'28px' }}>
            <div style={{ display:'grid', gridTemplateColumns:'repeat(auto-fit,minmax(200px,1fr))', gap:'20px' }}>
              {[
                {label:'On-Time Delivery', val:selected.otdScore||selected.otd_score||0},
                {label:'Quality Score',    val:selected.qualityScore||selected.quality_score||0},
                {label:'Responsiveness',   val:selected.responsivenessScore||selected.responsiveness_score||0}
              ].map(m=>(
                <div key={m.label}>
                  <div style={{ display:'flex', justifyContent:'space-between', marginBottom:'6px' }}>
                    <span style={{ fontSize:'13px', fontWeight:'600', color:'#374151' }}>{m.label}</span>
                    <span style={{ fontSize:'16px', fontWeight:'800' }}>{m.val.toFixed(1)}%</span>
                  </div>
                  <ScoreBar val={m.val} />
                </div>
              ))}
            </div>
            <div style={{ marginTop:'24px', display:'grid', gridTemplateColumns:'repeat(3,1fr)', gap:'16px' }}>
              {[
                {label:'Total Deliveries', val:selected.totalDeliveries||selected.total_deliveries||0},
                {label:'On-Time',          val:selected.onTimeDeliveries||selected.on_time_deliveries||0},
                {label:'OTD Rate',         val:((selected.onTimeDeliveries||0)/Math.max(selected.totalDeliveries||1,1)*100).toFixed(1)+'%'}
              ].map(s=>(
                <div key={s.label} style={{ background:'#f8fafc', borderRadius:'10px', padding:'16px', textAlign:'center' }}>
                  <div style={{ fontSize:'24px', fontWeight:'800', color:'#1d4ed8' }}>{s.val}</div>
                  <div style={{ fontSize:'12px', color:'#6b7280', marginTop:'4px' }}>{s.label}</div>
                </div>
              ))}
            </div>
          </div>
        )}
        {activeTab==='deliveries' && (
          <div style={{ background:'#fff', border:'1px solid #e5e7eb', borderRadius:'14px', overflow:'hidden' }}>
            {loadingDel
              ? <div style={{ padding:'40px', textAlign:'center', color:'#6b7280' }}>Loading...</div>
              : deliveries.length===0
              ? <div style={{ padding:'40px', textAlign:'center', color:'#6b7280' }}>No deliveries found</div>
              : (
                <table style={{ width:'100%', borderCollapse:'collapse', fontSize:'14px' }}>
                  <thead>
                    <tr style={{ background:'#f8fafc' }}>
                      {['PO Number','Item Code','Promised','Actual','Qty Ordered','Qty Received','Status','Delay'].map(h=>(
                        <th key={h} style={{ padding:'10px 14px', textAlign:'left', fontWeight:'700', fontSize:'11px', textTransform:'uppercase', letterSpacing:'1px', color:'#6b7280', borderBottom:'1px solid #e5e7eb' }}>{h}</th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {deliveries.map((d,i)=>(
                      <tr key={i} style={{ borderBottom:'1px solid #f1f5f9' }}>
                        <td style={{ padding:'10px 14px', fontWeight:'600' }}>{d.poNumber||d.po_number}</td>
                        <td style={{ padding:'10px 14px' }}>{d.itemCode||d.item_code}</td>
                        <td style={{ padding:'10px 14px', color:'#6b7280' }}>{d.promisedDate||d.promised_date}</td>
                        <td style={{ padding:'10px 14px', color:'#6b7280' }}>{d.actualDate||d.actual_date}</td>
                        <td style={{ padding:'10px 14px' }}>{d.qtyOrdered||d.qty_ordered}</td>
                        <td style={{ padding:'10px 14px' }}>{d.qtyReceived||d.qty_received}</td>
                        <td style={{ padding:'10px 14px' }}>
                          <span style={{ background:d.status==='ON_TIME'?'#f0fdf4':'#fff1f2', color:d.status==='ON_TIME'?'#15803d':'#dc2626', padding:'2px 10px', borderRadius:'20px', fontSize:'11px', fontWeight:'700' }}>{d.status}</span>
                        </td>
                        <td style={{ padding:'10px 14px', color:(d.delayDays||d.delay_days)>0?'#dc2626':'#15803d', fontWeight:'600' }}>
                          {(d.delayDays||d.delay_days)>0 ? `+${d.delayDays||d.delay_days}d` : '-'}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )
            }
          </div>
        )}
        {activeTab==='ratings' && (
          <div style={{ background:'#fff', border:'1px solid #e5e7eb', borderRadius:'14px', padding:20 }}>
            <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:16 }}>
              <h3 style={{ margin:0, fontSize:15, fontWeight:800 }}>Quality Rating History</h3>
              <button data-testid="open-rating-modal" onClick={()=>setShowRatingModal(true)}
                style={{ background:'#1d4ed8', color:'#fff', border:'none', borderRadius:8, padding:'8px 18px', fontWeight:700, fontSize:13, cursor:'pointer' }}>
                + Submit Rating
              </button>
            </div>
            <SupplierRatingHistory key={ratingKey} supplierId={selected.supplierId||selected.supplier_id} />
          </div>
        )}
        {showRatingModal && (
          <SupplierRatingModal
            supplier={selected}
            onClose={() => setShowRatingModal(false)}
            onSaved={() => setRatingKey(k => k + 1)}
          />
        )}
      </div>
    );
  }

  return (
    <div style={{ padding:'24px', maxWidth:'1100px', margin:'0 auto' }}>
      <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:'24px' }}>
        <div>
          <h1 style={{ fontSize:'24px', fontWeight:'800', margin:0 }}>Supplier Scorecard</h1>
          <p style={{ color:'#6b7280', margin:'4px 0 0' }}>Monitor supplier performance and delivery metrics</p>
        </div>
        <div style={{ display:'flex', gap:'10px' }}>
          <span style={{ background:'#f0fdf4', border:'1px solid #86efac', borderRadius:'8px', padding:'8px 14px', fontSize:'13px', fontWeight:'700', color:'#15803d' }}>{suppliers.length} Suppliers</span>
          {atRisk>0 && <span style={{ background:'#fff1f2', border:'1px solid #fca5a5', borderRadius:'8px', padding:'8px 14px', fontSize:'13px', fontWeight:'700', color:'#dc2626' }}>{atRisk} At Risk</span>}
          <span style={{ background:'#eff6ff', border:'1px solid #93c5fd', borderRadius:'8px', padding:'8px 14px', fontSize:'13px', fontWeight:'700', color:'#1d4ed8' }}>Avg: {avgScore}</span>
        </div>
      </div>
      <input
        value={search}
        onChange={e=>setSearch(e.target.value)}
        placeholder="Search suppliers..."
        style={{ width:'100%', padding:'10px 14px', border:'1px solid #d1d5db', borderRadius:'8px', fontSize:'14px', marginBottom:'20px', outline:'none' }}
      />
      {loading
        ? <div style={{ textAlign:'center', padding:'60px', color:'#6b7280' }}>Loading...</div>
        : filtered.length === 0
        ? <div style={{ textAlign:'center', padding:'60px', color:'#6b7280' }}>No suppliers found</div>
        : (
          <div style={{ display:'grid', gridTemplateColumns:'repeat(auto-fill,minmax(300px,1fr))', gap:'16px' }}>
            {filtered.map((sup,i) => {
              const tc = TIER_COLORS[sup.tier]||TIER_COLORS.APPROVED;
              const score = sup.compositeScore||sup.composite_score||0;
              const isAtRisk = sup.atRisk||sup.at_risk;
              return (
                <div key={i} onClick={()=>openDetail(sup)}
                  style={{ background:'#fff', border:'1px solid #e5e7eb', borderRadius:'14px', padding:'22px', cursor:'pointer', borderTop:`3px solid ${isAtRisk?'#dc2626':tc.color}`, transition:'all 0.2s' }}
                  onMouseEnter={e=>{e.currentTarget.style.boxShadow='0 4px 20px rgba(0,0,0,0.1)';e.currentTarget.style.transform='translateY(-2px)';}}
                  onMouseLeave={e=>{e.currentTarget.style.boxShadow='none';e.currentTarget.style.transform='none';}}>
                  <div style={{ display:'flex', justifyContent:'space-between', alignItems:'flex-start', marginBottom:'12px' }}>
                    <div>
                      <div style={{ fontWeight:'800', fontSize:'15px', marginBottom:'4px' }}>{sup.supplierName||sup.supplier_name}</div>
                      <div style={{ fontSize:'12px', color:'#6b7280' }}>{sup.supplierId||sup.supplier_id} - {sup.country}</div>
                    </div>
                    <div style={{ fontSize:'28px', fontWeight:'900', color:score>=90?'#15803d':score>=70?'#1d4ed8':'#dc2626' }}>{score.toFixed(1)}</div>
                  </div>
                  <div style={{ display:'flex', gap:'6px', marginBottom:'14px', flexWrap:'wrap' }}>
                    <span style={{ background:tc.bg, color:tc.color, padding:'2px 10px', borderRadius:'20px', fontSize:'11px', fontWeight:'700' }}>{sup.tier}</span>
                    {isAtRisk && <span style={{ background:'#fff1f2', color:'#dc2626', padding:'2px 10px', borderRadius:'20px', fontSize:'11px', fontWeight:'700' }}>AT RISK</span>}
                  </div>
                  {[
                    {l:'OTD',     v:sup.otdScore||sup.otd_score||0},
                    {l:'Quality', v:sup.qualityScore||sup.quality_score||0},
                    {l:'Response',v:sup.responsivenessScore||sup.responsiveness_score||0}
                  ].map(m=>(
                    <div key={m.l} style={{ marginBottom:'6px' }}>
                      <div style={{ display:'flex', justifyContent:'space-between', fontSize:'12px', marginBottom:'3px' }}>
                        <span style={{ color:'#6b7280' }}>{m.l}</span>
                        <span style={{ fontWeight:'700' }}>{m.v.toFixed(1)}%</span>
                      </div>
                      <ScoreBar val={m.v} />
                    </div>
                  ))}
                  <div style={{ marginTop:'10px', fontSize:'12px', color:'#9ca3af', textAlign:'right' }}>Click to view details</div>
                </div>
              );
            })}
          </div>
        )
      }
    </div>
  );
}