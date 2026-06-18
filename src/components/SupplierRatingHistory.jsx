import React, { useState, useEffect } from 'react';
import { getSupplierRatings } from '../api';

function ScoreBadge({ val }) {
  const color = +val >= 80 ? '#15803d' : +val >= 60 ? '#1d4ed8' : +val >= 40 ? '#d97706' : '#dc2626';
  const bg    = +val >= 80 ? '#f0fdf4' : +val >= 60 ? '#eff6ff' : +val >= 40 ? '#fffbeb' : '#fef2f2';
  return (
    <span style={{ background:bg, color, padding:'2px 8px', borderRadius:12, fontSize:12, fontWeight:700 }}>
      {parseFloat(val).toFixed(1)}
    </span>
  );
}

function MiniBar({ val, color }) {
  return (
    <div style={{ display:'flex', alignItems:'center', gap:6 }}>
      <div style={{ flex:1, background:'#e5e7eb', borderRadius:4, height:6 }}>
        <div style={{ width:`${Math.min(+val, 100)}%`, height:'100%', background:color, borderRadius:4, transition:'width 0.3s' }} />
      </div>
      <span style={{ fontSize:11, color:'#6b7280', minWidth:28, textAlign:'right' }}>{parseFloat(val).toFixed(0)}</span>
    </div>
  );
}

export default function SupplierRatingHistory({ supplierId }) {
  const [ratings,  setRatings]  = useState([]);
  const [loading,  setLoading]  = useState(true);
  const [expanded, setExpanded] = useState(null);

  useEffect(() => {
    if (!supplierId) return;
    setLoading(true);
    getSupplierRatings(supplierId)
      .then(r => setRatings(Array.isArray(r.data) ? r.data : (r.data?.ratings || [])))
      .catch(() => setRatings([]))
      .finally(() => setLoading(false));
  }, [supplierId]);

  if (loading) return <div data-testid="rating-history-loading" style={{ padding:24, textAlign:'center', color:'#6b7280', fontSize:13 }}>Loading ratings…</div>;

  if (ratings.length === 0) return (
    <div data-testid="rating-history-empty" style={{ padding:24, textAlign:'center', color:'#9ca3af' }}>
      <div style={{ fontSize:32, marginBottom:8 }}>📋</div>
      <p style={{ margin:0, fontSize:13 }}>No ratings submitted yet for this supplier.</p>
    </div>
  );

  const avg = (key) => (ratings.reduce((s, r) => s + +r[key], 0) / ratings.length).toFixed(1);

  return (
    <div data-testid="supplier-rating-history">
      {/* Summary stats */}
      <div style={{ display:'grid', gridTemplateColumns:'repeat(4,1fr)', gap:10, marginBottom:16 }}>
        {[
          { label:'Overall Avg', val: avg('overallScore'),  color:'#7c3aed' },
          { label:'Quality',     val: avg('qualityScore'),  color:'#1d4ed8' },
          { label:'Delivery',    val: avg('deliveryScore'), color:'#0891b2' },
          { label:'Responsive',  val: avg('responsiveness'), color:'#16a34a' },
        ].map(s => (
          <div key={s.label} style={{ background:'#f8fafc', borderRadius:8, padding:'10px 12px', borderTop:`3px solid ${s.color}` }}>
            <div style={{ fontSize:20, fontWeight:800, color:s.color }}>{s.val}</div>
            <div style={{ fontSize:11, color:'#6b7280', marginTop:2 }}>{s.label}</div>
          </div>
        ))}
      </div>

      {/* Rating list */}
      <div data-testid="rating-list" style={{ display:'flex', flexDirection:'column', gap:8 }}>
        {ratings.map((r, i) => (
          <div key={r.id || i}
            data-testid={`rating-row-${i}`}
            onClick={() => setExpanded(expanded === i ? null : i)}
            style={{ background:'#fff', border:'1px solid #e5e7eb', borderRadius:8, padding:'12px 16px', cursor:'pointer', transition:'border-color 0.15s',
              borderColor: expanded === i ? '#1d4ed8' : '#e5e7eb' }}>
            <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', gap:12 }}>
              <div style={{ display:'flex', alignItems:'center', gap:12 }}>
                <ScoreBadge val={r.overallScore} />
                <div>
                  <div style={{ fontSize:12, fontWeight:700, color:'#111827' }}>{r.ratingDate}</div>
                  <div style={{ fontSize:11, color:'#6b7280' }}>by {r.ratedBy}</div>
                </div>
              </div>
              <div style={{ display:'flex', gap:14, alignItems:'center' }}>
                <div style={{ textAlign:'center', minWidth:50 }}>
                  <div style={{ fontSize:10, color:'#9ca3af', textTransform:'uppercase' }}>Quality</div>
                  <MiniBar val={r.qualityScore} color='#1d4ed8' />
                </div>
                <div style={{ textAlign:'center', minWidth:50 }}>
                  <div style={{ fontSize:10, color:'#9ca3af', textTransform:'uppercase' }}>Delivery</div>
                  <MiniBar val={r.deliveryScore} color='#0891b2' />
                </div>
                <div style={{ textAlign:'center', minWidth:50 }}>
                  <div style={{ fontSize:10, color:'#9ca3af', textTransform:'uppercase' }}>Respond</div>
                  <MiniBar val={r.responsiveness} color='#16a34a' />
                </div>
                <span style={{ fontSize:10, color:'#9ca3af' }}>{expanded === i ? '▲' : '▼'}</span>
              </div>
            </div>
            {expanded === i && r.comments && (
              <div style={{ marginTop:10, padding:'8px 12px', background:'#f8fafc', borderRadius:6, fontSize:12, color:'#374151', lineHeight:1.6 }}>
                {r.comments}
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
