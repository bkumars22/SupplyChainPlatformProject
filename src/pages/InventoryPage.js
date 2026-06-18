import React, { useState, useEffect, useCallback } from 'react';
import { getInventory, getInventoryTransactions, adjustStock } from '../api';

function StockLevel({ current, reorder, max }) {
  const pct = Math.min((current / Math.max(max, 1)) * 100, 100);
  const isLow = current <= reorder;
  const isCritical = current <= reorder * 0.5;
  const color = isCritical ? '#dc2626' : isLow ? '#d97706' : '#15803d';
  return (
    <div style={{ minWidth:120 }}>
      <div style={{ display:'flex', justifyContent:'space-between', marginBottom:3 }}>
        <span style={{ fontSize:12, fontWeight:700, color }}>{current.toLocaleString()}</span>
        <span style={{ fontSize:10, color:'#9ca3af' }}>/{max.toLocaleString()}</span>
      </div>
      <div style={{ background:'#e5e7eb', borderRadius:4, height:6 }}>
        <div style={{ width:`${pct}%`, height:'100%', borderRadius:4, background:color, transition:'width 0.3s' }} />
      </div>
      {isLow && <div style={{ fontSize:10, color, marginTop:2, fontWeight:600 }}>{isCritical?'⚠ CRITICAL':'↓ LOW STOCK'}</div>}
    </div>
  );
}

function Modal({ title, onClose, children }) {
  return (
    <div style={{ position:'fixed', inset:0, background:'rgba(0,0,0,0.5)', display:'flex', alignItems:'center', justifyContent:'center', zIndex:1000 }}>
      <div style={{ background:'#fff', borderRadius:14, padding:32, width:460, maxWidth:'95vw' }}>
        <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:20 }}>
          <h2 style={{ fontSize:18, fontWeight:800, margin:0 }}>{title}</h2>
          <button onClick={onClose} style={{ background:'none', border:'none', fontSize:22, cursor:'pointer', color:'#6b7280' }}>×</button>
        </div>
        {children}
      </div>
    </div>
  );
}

export default function InventoryPage() {
  const [items,       setItems]       = useState([]);
  const [loading,     setLoading]     = useState(true);
  const [lowStockOnly, setLowStockOnly] = useState(false);
  const [search,      setSearch]      = useState('');
  const [txItem,      setTxItem]      = useState(null);
  const [txns,        setTxns]        = useState([]);
  const [txLoading,   setTxLoading]   = useState(false);
  const [adjustItem,  setAdjustItem]  = useState(null);
  const [adjustForm,  setAdjustForm]  = useState({ transactionType:'IN', quantity:'', reference:'', notes:'' });
  const [msg,         setMsg]         = useState({ text:'', type:'' });

  const flash = (text, type='success') => { setMsg({ text, type }); setTimeout(() => setMsg({ text:'', type:'' }), 4000); };
  const user = JSON.parse(localStorage.getItem('user_data') || '{}');

  const load = useCallback(() => {
    setLoading(true);
    getInventory({ lowStock: lowStockOnly || undefined })
      .then(r => { const d = r.data; setItems(Array.isArray(d) ? d : d?.items || []); })
      .catch(() => setItems([]))
      .finally(() => setLoading(false));
  }, [lowStockOnly]);

  useEffect(() => { load(); }, [load]);

  const openTxns = (item) => {
    setTxItem(item); setTxLoading(true); setTxns([]);
    getInventoryTransactions(item.itemKey)
      .then(r => setTxns(Array.isArray(r.data) ? r.data : []))
      .catch(() => setTxns([]))
      .finally(() => setTxLoading(false));
  };

  const handleAdjust = () => {
    if (!adjustForm.quantity || isNaN(+adjustForm.quantity) || +adjustForm.quantity <= 0) {
      flash('Quantity must be a positive number', 'error'); return;
    }
    const payload = {
      itemKey: adjustItem.itemKey,
      transactionType: adjustForm.transactionType,
      quantity: parseFloat(adjustForm.quantity),
      reference: adjustForm.reference,
      notes: adjustForm.notes,
      performedBy: user.userId || 'kumar',
    };
    adjustStock(payload)
      .then(() => { flash('Stock adjusted successfully'); setAdjustItem(null); setAdjustForm({ transactionType:'IN', quantity:'', reference:'', notes:'' }); load(); })
      .catch(e => flash(e.message || 'Adjustment failed', 'error'));
  };

  const filtered = items.filter(i =>
    !search || i.itemKey.toLowerCase().includes(search.toLowerCase()) || (i.itemName||'').toLowerCase().includes(search.toLowerCase())
  );

  const lowCount = items.filter(i => i.currentStock <= i.reorderPoint).length;

  return (
    <div data-testid="inventory-page" style={{ padding:24, maxWidth:1100, margin:'0 auto' }}>
      {msg.text && (
        <div style={{ padding:'10px 16px', borderRadius:8, marginBottom:16, fontWeight:600, fontSize:13,
          background: msg.type==='error' ? '#fef2f2' : '#f0fdf4',
          color:      msg.type==='error' ? '#dc2626'  : '#15803d',
          border:`1px solid ${msg.type==='error' ? '#fecaca' : '#bbf7d0'}` }}>
          {msg.text}
        </div>
      )}

      {/* KPI strip */}
      <div style={{ display:'grid', gridTemplateColumns:'repeat(4,1fr)', gap:12, marginBottom:24 }}>
        {[
          { label:'Total SKUs',    val:items.length,                     color:'#1d4ed8' },
          { label:'Low Stock',     val:lowCount,                          color:'#d97706' },
          { label:'Critical',      val:items.filter(i=>i.currentStock<=i.reorderPoint*0.5).length, color:'#dc2626' },
          { label:'Warehouses',    val:[...new Set(items.map(i=>i.warehouseId))].length, color:'#7c3aed' },
        ].map(k => (
          <div key={k.label} style={{ background:'#fff', border:'1px solid #e5e7eb', borderRadius:10, padding:'16px 18px', borderTop:`3px solid ${k.color}` }}>
            <div style={{ fontSize:28, fontWeight:800, color:k.color }}>{k.val}</div>
            <div style={{ fontSize:12, color:'#6b7280', marginTop:4 }}>{k.label}</div>
          </div>
        ))}
      </div>

      {/* Controls */}
      <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:16, flexWrap:'wrap', gap:12 }}>
        <h1 style={{ fontSize:22, fontWeight:800, margin:0 }}>Inventory</h1>
        <div style={{ display:'flex', gap:8, flexWrap:'wrap', alignItems:'center' }}>
          <input data-testid="search-inventory" value={search} onChange={e=>setSearch(e.target.value)} placeholder="Search item key / name…"
            style={{ padding:'8px 12px', border:'1px solid #e5e7eb', borderRadius:8, fontSize:13, outline:'none', minWidth:200 }} />
          <button data-testid="low-stock-filter" onClick={()=>setLowStockOnly(v=>!v)}
            style={{ padding:'8px 14px', borderRadius:8, border:`1px solid ${lowStockOnly?'#d97706':'#e5e7eb'}`,
              background: lowStockOnly ? '#fffbeb' : '#fff', color: lowStockOnly ? '#d97706' : '#374151', fontWeight:600, fontSize:13, cursor:'pointer' }}>
            {lowStockOnly ? '↓ Low Stock Only' : 'All Items'}
          </button>
        </div>
      </div>

      {loading ? (
        <div style={{ textAlign:'center', padding:60, color:'#6b7280' }}>Loading inventory…</div>
      ) : filtered.length === 0 ? (
        <div style={{ textAlign:'center', padding:60, background:'#fff', borderRadius:12, border:'1px solid #e5e7eb', color:'#6b7280' }}>No items found.</div>
      ) : (
        <div style={{ background:'#fff', borderRadius:12, border:'1px solid #e5e7eb', overflow:'hidden' }}>
          <table data-testid="inventory-table" style={{ width:'100%', borderCollapse:'collapse', fontSize:13 }}>
            <thead>
              <tr style={{ background:'#f8fafc' }}>
                {['Item Key','Name','Warehouse','Stock Level','Reorder Pt','Category','Actions'].map(h => (
                  <th key={h} style={{ padding:'10px 14px', textAlign:'left', fontWeight:700, fontSize:11, textTransform:'uppercase', color:'#6b7280', borderBottom:'1px solid #e5e7eb' }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {filtered.map((item) => (
                <tr key={item.id || item.itemKey} data-testid={`inv-row-${item.itemKey}`}
                  style={{ borderBottom:'1px solid #f1f5f9' }}
                  onMouseEnter={e=>e.currentTarget.style.background='#f9fafb'}
                  onMouseLeave={e=>e.currentTarget.style.background='#fff'}>
                  <td style={{ padding:'10px 14px', fontFamily:'monospace', fontWeight:700 }}>{item.itemKey}</td>
                  <td style={{ padding:'10px 14px' }}>{item.itemName || '—'}</td>
                  <td style={{ padding:'10px 14px', color:'#6b7280' }}>{item.warehouseId}</td>
                  <td style={{ padding:'10px 14px' }}>
                    <StockLevel current={item.currentStock} reorder={item.reorderPoint} max={item.maxStock} />
                  </td>
                  <td style={{ padding:'10px 14px' }}>{item.reorderPoint}</td>
                  <td style={{ padding:'10px 14px' }}>
                    <span style={{ background:'#f1f5f9', color:'#374151', padding:'2px 8px', borderRadius:10, fontSize:11, fontWeight:600 }}>{item.category||'—'}</span>
                  </td>
                  <td style={{ padding:'10px 14px' }}>
                    <div style={{ display:'flex', gap:4 }}>
                      <button data-testid={`txn-btn-${item.itemKey}`} onClick={()=>openTxns(item)}
                        style={{ padding:'3px 8px', fontSize:11, background:'#eff6ff', color:'#1d4ed8', border:'1px solid #bfdbfe', borderRadius:5, cursor:'pointer', fontWeight:600 }}>History</button>
                      <button data-testid={`adjust-btn-${item.itemKey}`} onClick={()=>{ setAdjustItem(item); setAdjustForm({ transactionType:'IN', quantity:'', reference:'', notes:'' }); }}
                        style={{ padding:'3px 8px', fontSize:11, background:'#f0fdf4', color:'#15803d', border:'1px solid #bbf7d0', borderRadius:5, cursor:'pointer', fontWeight:600 }}>Adjust</button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Transaction history modal */}
      {txItem && (
        <Modal title={`Transactions — ${txItem.itemKey}`} onClose={()=>setTxItem(null)}>
          {txLoading ? (
            <div style={{ textAlign:'center', padding:30, color:'#6b7280' }}>Loading…</div>
          ) : txns.length === 0 ? (
            <p style={{ color:'#9ca3af', textAlign:'center' }}>No transactions found.</p>
          ) : (
            <div data-testid="txn-list" style={{ maxHeight:300, overflowY:'auto' }}>
              {txns.map((t, i) => (
                <div key={i} data-testid={`txn-row-${i}`}
                  style={{ display:'flex', justifyContent:'space-between', padding:'10px 0', borderBottom:'1px solid #f1f5f9', alignItems:'flex-start' }}>
                  <div>
                    <span style={{ background: t.transactionType==='IN'?'#f0fdf4':t.transactionType==='OUT'?'#fef2f2':'#eff6ff',
                      color: t.transactionType==='IN'?'#15803d':t.transactionType==='OUT'?'#dc2626':'#1d4ed8',
                      padding:'2px 8px', borderRadius:10, fontSize:11, fontWeight:700, marginRight:8 }}>
                      {t.transactionType}
                    </span>
                    <span style={{ fontSize:12, color:'#6b7280' }}>{t.reference || '—'}</span>
                    {t.notes && <p style={{ margin:'4px 0 0', fontSize:11, color:'#9ca3af' }}>{t.notes}</p>}
                  </div>
                  <div style={{ textAlign:'right' }}>
                    <div style={{ fontWeight:800, color: t.transactionType==='IN'?'#15803d':'#dc2626' }}>
                      {t.transactionType==='IN'?'+':'-'}{t.quantity}
                    </div>
                    <div style={{ fontSize:11, color:'#9ca3af' }}>{(t.transactionDate||'').slice(0,10)}</div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </Modal>
      )}

      {/* Adjustment modal */}
      {adjustItem && (
        <Modal title={`Adjust Stock — ${adjustItem.itemKey}`} onClose={()=>setAdjustItem(null)}>
          <p style={{ color:'#6b7280', fontSize:13, marginBottom:20 }}>
            Current stock: <strong>{adjustItem.currentStock.toLocaleString()} {adjustItem.uom}</strong>
          </p>
          <div style={{ marginBottom:14 }}>
            <label style={{ display:'block', fontWeight:700, fontSize:13, marginBottom:4 }}>Transaction Type *</label>
            <select data-testid="adjust-type" value={adjustForm.transactionType} onChange={e=>setAdjustForm(p=>({...p,transactionType:e.target.value}))}
              style={{ width:'100%', padding:'9px 12px', border:'1px solid #d1d5db', borderRadius:8, fontSize:14, outline:'none', background:'#fff', boxSizing:'border-box' }}>
              <option value="IN">IN — Stock received</option>
              <option value="OUT">OUT — Stock issued / consumed</option>
              <option value="ADJUSTMENT">ADJUSTMENT — Inventory count correction</option>
            </select>
          </div>
          {[
            { label:'Quantity *',    key:'quantity',  ph:'e.g. 500',     type:'number' },
            { label:'Reference',     key:'reference', ph:'PO# or WO#',   type:'text' },
            { label:'Notes',         key:'notes',     ph:'Optional note', type:'text' },
          ].map(f => (
            <div key={f.key} style={{ marginBottom:14 }}>
              <label style={{ display:'block', fontWeight:700, fontSize:13, marginBottom:4 }}>{f.label}</label>
              <input data-testid={`adjust-${f.key}`} type={f.type} value={adjustForm[f.key]} placeholder={f.ph}
                onChange={e=>setAdjustForm(p=>({...p,[f.key]:e.target.value}))}
                style={{ width:'100%', padding:'9px 12px', border:'1px solid #d1d5db', borderRadius:8, fontSize:14, outline:'none', boxSizing:'border-box' }} />
            </div>
          ))}
          <div style={{ display:'flex', gap:10, justifyContent:'flex-end', marginTop:8 }}>
            <button onClick={()=>setAdjustItem(null)} style={{ padding:'10px 20px', border:'1px solid #d1d5db', borderRadius:8, cursor:'pointer', fontWeight:600 }}>Cancel</button>
            <button data-testid="confirm-adjust" onClick={handleAdjust}
              style={{ padding:'10px 22px', background:'#15803d', color:'#fff', border:'none', borderRadius:8, cursor:'pointer', fontWeight:700 }}>Apply Adjustment</button>
          </div>
        </Modal>
      )}
    </div>
  );
}
