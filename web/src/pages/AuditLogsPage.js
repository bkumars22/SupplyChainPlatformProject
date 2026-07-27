import React, { useState, useEffect, useCallback } from 'react';
import { getAuditLogs } from '../api';

const ACTION_STYLE = {
  CREATE:  { bg:'#f0fdf4', color:'#15803d' },
  UPDATE:  { bg:'#eff6ff', color:'#1d4ed8' },
  DELETE:  { bg:'#fef2f2', color:'#dc2626' },
  APPROVE: { bg:'#f0fdf4', color:'#15803d' },
  SUBMIT:  { bg:'#fffbeb', color:'#d97706' },
  CONFIRM: { bg:'#fffbeb', color:'#d97706' },
  RECEIVE: { bg:'#f0fdf4', color:'#15803d' },
  CANCEL:  { bg:'#fef2f2', color:'#dc2626' },
  DISMISS: { bg:'#f5f3ff', color:'#7c3aed' },
  ADJUST:  { bg:'#ecfeff', color:'#0891b2' },
};

const ENTITY_TYPES = ['ALL','PurchaseOrder','CostRecord','SupplierRating','Inventory','User','Alert'];

export default function AuditLogsPage() {
  const [logs,      setLogs]      = useState([]);
  const [loading,   setLoading]   = useState(true);
  const [entityFil, setEntityFil] = useState('ALL');
  const [search,    setSearch]    = useState('');
  const [expanded,  setExpanded]  = useState(null);

  const load = useCallback(() => {
    setLoading(true);
    const params = entityFil !== 'ALL' ? { entityType: entityFil } : {};
    getAuditLogs(params)
      .then(r => { const d = r.data.data; setLogs(Array.isArray(d) ? d : d?.logs || []); })
      .catch(() => setLogs([]))
      .finally(() => setLoading(false));
  }, [entityFil]);

  useEffect(() => { load(); }, [load]);

  const filtered = logs.filter(l =>
    !search ||
    (l.entityId||'').toLowerCase().includes(search.toLowerCase()) ||
    (l.performedBy||'').toLowerCase().includes(search.toLowerCase()) ||
    (l.details||'').toLowerCase().includes(search.toLowerCase())
  );

  const kpis = [
    { label:'Total Events',   val: logs.length,                                              color:'#1d4ed8' },
    { label:'Creates',        val: logs.filter(l=>l.action==='CREATE').length,                color:'#15803d' },
    { label:'Updates/Actions',val: logs.filter(l=>['UPDATE','SUBMIT','CONFIRM','APPROVE','RECEIVE','ADJUST'].includes(l.action)).length, color:'#d97706' },
    { label:'Users Active',   val: [...new Set(logs.map(l=>l.performedBy))].length,           color:'#7c3aed' },
  ];

  return (
    <div data-testid="audit-logs-page" style={{ padding:24, maxWidth:1100, margin:'0 auto' }}>
      {/* KPI strip */}
      <div style={{ display:'grid', gridTemplateColumns:'repeat(4,1fr)', gap:12, marginBottom:24 }}>
        {kpis.map(k => (
          <div key={k.label} style={{ background:'#fff', border:'1px solid #e5e7eb', borderRadius:10, padding:'16px 18px', borderTop:`3px solid ${k.color}` }}>
            <div style={{ fontSize:28, fontWeight:800, color:k.color }}>{k.val}</div>
            <div style={{ fontSize:12, color:'#6b7280', marginTop:4 }}>{k.label}</div>
          </div>
        ))}
      </div>

      {/* Controls */}
      <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:16, flexWrap:'wrap', gap:12 }}>
        <div>
          <h1 style={{ fontSize:22, fontWeight:800, margin:0 }}>Audit Logs</h1>
          <p style={{ color:'#6b7280', margin:'4px 0 0', fontSize:13 }}>Complete trail of all platform actions — admin visibility only</p>
        </div>
        <input data-testid="search-audit" value={search} onChange={e=>setSearch(e.target.value)} placeholder="Search entity / user / details…"
          style={{ padding:'8px 12px', border:'1px solid #e5e7eb', borderRadius:8, fontSize:13, outline:'none', minWidth:240 }} />
      </div>

      {/* Entity type filter */}
      <div style={{ display:'flex', gap:6, marginBottom:16, flexWrap:'wrap' }}>
        {ENTITY_TYPES.map(et => (
          <button key={et} data-testid={`filter-entity-${et}`} onClick={()=>setEntityFil(et)}
            style={{ padding:'5px 14px', borderRadius:20, fontSize:12, fontWeight:600, cursor:'pointer',
              border:`1px solid ${entityFil===et?'#1d4ed8':'#e5e7eb'}`,
              background: entityFil===et ? '#1d4ed8' : '#fff',
              color: entityFil===et ? '#fff' : '#374151' }}>
            {et}
          </button>
        ))}
      </div>

      {loading ? (
        <div style={{ textAlign:'center', padding:60, color:'#6b7280' }}>Loading audit logs…</div>
      ) : filtered.length === 0 ? (
        <div style={{ textAlign:'center', padding:60, background:'#fff', borderRadius:12, border:'1px solid #e5e7eb', color:'#6b7280' }}>
          No audit log entries found.
        </div>
      ) : (
        <div style={{ background:'#fff', borderRadius:12, border:'1px solid #e5e7eb', overflow:'hidden' }}>
          <table data-testid="audit-table" style={{ width:'100%', borderCollapse:'collapse', fontSize:13 }}>
            <thead>
              <tr style={{ background:'#f8fafc' }}>
                {['Timestamp','Entity Type','Entity ID','Action','Performed By','Details'].map(h => (
                  <th key={h} style={{ padding:'10px 14px', textAlign:'left', fontWeight:700, fontSize:11, textTransform:'uppercase', color:'#6b7280', borderBottom:'1px solid #e5e7eb' }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {filtered.map((log, i) => {
                const ac = ACTION_STYLE[log.action] || { bg:'#f8fafc', color:'#374151' };
                return (
                  <tr key={log.id || i} data-testid={`audit-row-${i}`}
                    onClick={() => setExpanded(expanded === i ? null : i)}
                    style={{ borderBottom:'1px solid #f1f5f9', cursor:'pointer' }}
                    onMouseEnter={e=>e.currentTarget.style.background='#f9fafb'}
                    onMouseLeave={e=>e.currentTarget.style.background=expanded===i?'#f0f9ff':'#fff'}>
                    <td style={{ padding:'10px 14px', color:'#6b7280', whiteSpace:'nowrap', fontSize:11 }}>
                      {(log.loggedAt||log.logged_at||'').replace('T',' ').slice(0,16)}
                    </td>
                    <td style={{ padding:'10px 14px' }}>
                      <span style={{ background:'#f1f5f9', color:'#374151', padding:'2px 8px', borderRadius:8, fontSize:11, fontWeight:600 }}>{log.entityType}</span>
                    </td>
                    <td style={{ padding:'10px 14px', fontFamily:'monospace', fontSize:11, color:'#374151' }}>{log.entityId||'—'}</td>
                    <td style={{ padding:'10px 14px' }}>
                      <span style={{ background:ac.bg, color:ac.color, padding:'2px 10px', borderRadius:20, fontSize:11, fontWeight:700 }}>{log.action}</span>
                    </td>
                    <td style={{ padding:'10px 14px', fontWeight:600 }}>{log.performedBy}</td>
                    <td style={{ padding:'10px 14px', color:'#6b7280', maxWidth:300 }}>
                      <span style={{ overflow:'hidden', textOverflow:'ellipsis', whiteSpace: expanded===i?'normal':'nowrap', display:'block' }}>
                        {log.details}
                      </span>
                      {expanded === i && log.ipAddress && (
                        <span style={{ fontSize:10, color:'#9ca3af' }}>IP: {log.ipAddress}</span>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
          <div style={{ padding:'10px 14px', background:'#f8fafc', borderTop:'1px solid #e5e7eb', fontSize:12, color:'#9ca3af', textAlign:'right' }}>
            {filtered.length} events {search || entityFil !== 'ALL' ? '(filtered)' : ''}
          </div>
        </div>
      )}
    </div>
  );
}
