import React, { useState, useEffect, useCallback } from 'react';
import {
  getPurchaseOrders, createPurchaseOrder, submitPurchaseOrder,
  confirmPurchaseOrder, receivePurchaseOrder, cancelPurchaseOrder,
} from '../api';

const STATUS_STYLE = {
  DRAFT:     { bg:'#f8fafc', color:'#6b7280', border:'#e5e7eb' },
  SUBMITTED: { bg:'#eff6ff', color:'#a9790f', border:'#bfdbfe' },
  CONFIRMED: { bg:'#fffbeb', color:'#d97706', border:'#fde68a' },
  RECEIVED:  { bg:'#f0fdf4', color:'#15803d', border:'#bbf7d0' },
  CANCELLED: { bg:'#fef2f2', color:'#dc2626', border:'#fecaca' },
};

function Modal({ title, onClose, children }) {
  return (
    <div style={{ position:'fixed', inset:0, background:'rgba(0,0,0,0.5)', display:'flex', alignItems:'center', justifyContent:'center', zIndex:1000 }}>
      <div style={{ background:'#fff', borderRadius:14, padding:32, width:520, maxWidth:'95vw', maxHeight:'90vh', overflowY:'auto' }}>
        <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:20 }}>
          <h2 style={{ fontSize:18, fontWeight:800, margin:0 }}>{title}</h2>
          <button onClick={onClose} style={{ background:'none', border:'none', fontSize:22, cursor:'pointer', color:'#6b7280' }}>×</button>
        </div>
        {children}
      </div>
    </div>
  );
}

function LineItemsTable({ items }) {
  if (!items || items.length === 0) return <p style={{ color:'#9ca3af', fontSize:13 }}>No line items</p>;
  const total = items.reduce((s, i) => s + (i.lineTotal || i.quantity * i.unitPrice || 0), 0);
  return (
    <div style={{ overflowX:'auto' }}>
      <table data-testid="line-items-table" style={{ width:'100%', borderCollapse:'collapse', fontSize:13 }}>
        <thead>
          <tr style={{ background:'#f8fafc' }}>
            {['Item Key','Description','Qty','UOM','Unit Price','Line Total'].map(h => (
              <th key={h} style={{ padding:'8px 12px', textAlign:'left', fontWeight:700, fontSize:11, textTransform:'uppercase', color:'#6b7280', borderBottom:'1px solid #e5e7eb' }}>{h}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {items.map((li, i) => (
            <tr key={i} style={{ borderBottom:'1px solid #f1f5f9' }}>
              <td style={{ padding:'8px 12px', fontFamily:'monospace', fontWeight:600 }}>{li.itemKey}</td>
              <td style={{ padding:'8px 12px' }}>{li.description}</td>
              <td style={{ padding:'8px 12px' }}>{li.quantity}</td>
              <td style={{ padding:'8px 12px', color:'#6b7280' }}>{li.uom || 'EACH'}</td>
              <td style={{ padding:'8px 12px' }}>${parseFloat(li.unitPrice).toFixed(2)}</td>
              <td style={{ padding:'8px 12px', fontWeight:700 }}>${parseFloat(li.lineTotal || li.quantity * li.unitPrice).toLocaleString()}</td>
            </tr>
          ))}
          <tr style={{ background:'#f8fafc', borderTop:'2px solid #e5e7eb' }}>
            <td colSpan={5} style={{ padding:'8px 12px', fontWeight:700, textAlign:'right' }}>Total:</td>
            <td style={{ padding:'8px 12px', fontWeight:800, fontSize:15, color:'#a9790f' }}>${total.toLocaleString()}</td>
          </tr>
        </tbody>
      </table>
    </div>
  );
}

export default function PurchaseOrdersPage() {
  const [orders,     setOrders]     = useState([]);
  const [loading,    setLoading]    = useState(true);
  const [search,     setSearch]     = useState('');
  const [statusFil,  setStatusFil]  = useState('ALL');
  const [selected,   setSelected]   = useState(null);
  const [showCreate, setShowCreate] = useState(false);
  const [msg,        setMsg]        = useState({ text:'', type:'' });
  const [form,       setForm]       = useState({ supplierId:'', supplierName:'', expectedDate:'', currency:'USD', notes:'' });

  const flash = (text, type='success') => { setMsg({ text, type }); setTimeout(() => setMsg({ text:'', type:'' }), 4000); };

  const load = useCallback(() => {
    setLoading(true);
    getPurchaseOrders()
      .then(r => { const d = r.data.data; setOrders(Array.isArray(d) ? d : d?.orders || []); })
      .catch(() => setOrders([]))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => { load(); }, [load]);

  useEffect(() => {
    const h = (e) => {
      const voiceAction = e.detail?.action;
      if (voiceAction === 'open-create') {
        setForm({ supplierId:'', supplierName:'', expectedDate:'', currency:'USD', notes:'' });
        setShowCreate(true);
      } else if (voiceAction === 'submit-first') {
        const draft = orders.find(o => o.status === 'DRAFT');
        if (!draft) { flash('No draft purchase order to submit', 'error'); return; }
        action(submitPurchaseOrder, draft.id, 'Submit');
      }
    };
    window.addEventListener('scip-voice', h);
    return () => window.removeEventListener('scip-voice', h);
  }, [orders]);

  const handleCreate = () => {
    if (!form.supplierId) { flash('Supplier ID required', 'error'); return; }
    createPurchaseOrder(form)
      .then(() => { flash('Purchase order created'); setShowCreate(false); setForm({ supplierId:'', supplierName:'', expectedDate:'', currency:'USD', notes:'' }); load(); })
      .catch(e => flash(e.response?.data?.error || e.message || 'Failed', 'error'));
  };

  const action = (fn, id, label) => {
    fn(id)
      .then(() => { flash(`${label} successful`); load(); setSelected(null); })
      .catch(e => flash(e.message || 'Failed', 'error'));
  };

  const filtered = orders.filter(o => {
    const matchStatus = statusFil === 'ALL' || o.status === statusFil;
    const matchSearch = !search || o.poNumber.toLowerCase().includes(search.toLowerCase()) || (o.supplierName||'').toLowerCase().includes(search.toLowerCase());
    return matchStatus && matchSearch;
  });

  const kpis = [
    { label:'Total Orders',  val: orders.length,                              color:'#a9790f' },
    { label:'Draft',         val: orders.filter(o=>o.status==='DRAFT').length, color:'#6b7280' },
    { label:'In Progress',   val: orders.filter(o=>['SUBMITTED','CONFIRMED'].includes(o.status)).length, color:'#d97706' },
    { label:'Received',      val: orders.filter(o=>o.status==='RECEIVED').length, color:'#15803d' },
  ];

  return (
    <div data-testid="purchase-orders-page" style={{ padding:24, maxWidth:1100, margin:'0 auto' }}>
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
        {kpis.map(k => (
          <div key={k.label} style={{ background:'#fff', border:'1px solid #e5e7eb', borderRadius:10, padding:'16px 18px', borderTop:`3px solid ${k.color}` }}>
            <div style={{ fontSize:28, fontWeight:800, color:k.color }}>{k.val}</div>
            <div style={{ fontSize:12, color:'#6b7280', marginTop:4 }}>{k.label}</div>
          </div>
        ))}
      </div>

      {/* Header bar */}
      <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:16, flexWrap:'wrap', gap:12 }}>
        <h1 style={{ fontSize:22, fontWeight:800, margin:0 }}>Purchase Orders</h1>
        <div style={{ display:'flex', gap:8, flexWrap:'wrap' }}>
          <input data-testid="search-po" value={search} onChange={e=>setSearch(e.target.value)} placeholder="Search PO / supplier…"
            style={{ padding:'8px 12px', border:'1px solid #e5e7eb', borderRadius:8, fontSize:13, outline:'none', minWidth:200 }} />
          <button data-testid="create-po-btn" onClick={()=>setShowCreate(true)}
            style={{ background:'#a9790f', color:'#fff', border:'none', borderRadius:8, padding:'8px 18px', fontWeight:700, fontSize:13, cursor:'pointer' }}>
            + Create PO
          </button>
        </div>
      </div>

      {/* Status filter */}
      <div style={{ display:'flex', gap:6, marginBottom:16, flexWrap:'wrap' }}>
        {['ALL','DRAFT','SUBMITTED','CONFIRMED','RECEIVED','CANCELLED'].map(s => (
          <button key={s} data-testid={`filter-${s}`} onClick={()=>setStatusFil(s)}
            style={{ padding:'5px 14px', borderRadius:20, fontSize:12, fontWeight:600, cursor:'pointer',
              border:`1px solid ${statusFil===s?'#a9790f':'#e5e7eb'}`,
              background: statusFil===s ? '#a9790f' : '#fff',
              color: statusFil===s ? '#fff' : '#374151' }}>
            {s}
          </button>
        ))}
      </div>

      {/* Table */}
      {loading ? (
        <div style={{ textAlign:'center', padding:60, color:'#6b7280' }}>Loading orders…</div>
      ) : filtered.length === 0 ? (
        <div style={{ textAlign:'center', padding:60, background:'#fff', borderRadius:12, border:'1px solid #e5e7eb', color:'#6b7280' }}>
          No purchase orders found.
        </div>
      ) : (
        <div style={{ background:'#fff', borderRadius:12, border:'1px solid #e5e7eb', overflow:'hidden' }}>
          <table data-testid="po-table" style={{ width:'100%', borderCollapse:'collapse', fontSize:13 }}>
            <thead>
              <tr style={{ background:'#f8fafc' }}>
                {['PO Number','Supplier','Status','Order Date','Expected','Amount','Actions'].map(h => (
                  <th key={h} style={{ padding:'10px 14px', textAlign:'left', fontWeight:700, fontSize:11, textTransform:'uppercase', color:'#6b7280', borderBottom:'1px solid #e5e7eb' }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {filtered.map((po) => {
                const sc = STATUS_STYLE[po.status] || STATUS_STYLE.DRAFT;
                return (
                  <tr key={po.id} data-testid={`po-row-${po.id}`}
                    style={{ borderBottom:'1px solid #f1f5f9' }}
                    onMouseEnter={e=>e.currentTarget.style.background='#f9fafb'}
                    onMouseLeave={e=>e.currentTarget.style.background='#fff'}>
                    <td style={{ padding:'10px 14px', fontWeight:700, fontFamily:'monospace' }}>{po.poNumber}</td>
                    <td style={{ padding:'10px 14px' }}>{po.supplierName || po.supplierId || '—'}</td>
                    <td style={{ padding:'10px 14px' }}>
                      <span style={{ background:sc.bg, color:sc.color, border:`1px solid ${sc.border}`, padding:'2px 10px', borderRadius:20, fontSize:11, fontWeight:700 }}>{po.status}</span>
                    </td>
                    <td style={{ padding:'10px 14px', color:'#6b7280' }}>{po.orderDate}</td>
                    <td style={{ padding:'10px 14px', color:'#6b7280' }}>{po.expectedDate || '—'}</td>
                    <td style={{ padding:'10px 14px', fontWeight:700 }}>${(po.totalAmount||0).toLocaleString()} {po.currency||'USD'}</td>
                    <td style={{ padding:'10px 14px' }}>
                      <div style={{ display:'flex', gap:4, flexWrap:'wrap' }}>
                        <button data-testid={`view-po-${po.id}`} onClick={()=>setSelected(po)}
                          style={{ padding:'3px 8px', fontSize:11, background:'#eff6ff', color:'#a9790f', border:'1px solid #bfdbfe', borderRadius:5, cursor:'pointer', fontWeight:600 }}>View</button>
                        {po.status === 'DRAFT' && (
                          <button data-testid={`submit-po-${po.id}`} onClick={()=>action(submitPurchaseOrder, po.id, 'Submit')}
                            style={{ padding:'3px 8px', fontSize:11, background:'#eff6ff', color:'#a9790f', border:'1px solid #bfdbfe', borderRadius:5, cursor:'pointer', fontWeight:600 }}>Submit</button>
                        )}
                        {po.status === 'SUBMITTED' && (
                          <button data-testid={`confirm-po-${po.id}`} onClick={()=>action(confirmPurchaseOrder, po.id, 'Confirm')}
                            style={{ padding:'3px 8px', fontSize:11, background:'#fffbeb', color:'#d97706', border:'1px solid #fde68a', borderRadius:5, cursor:'pointer', fontWeight:600 }}>Confirm</button>
                        )}
                        {po.status === 'CONFIRMED' && (
                          <button data-testid={`receive-po-${po.id}`} onClick={()=>action(receivePurchaseOrder, po.id, 'Receive')}
                            style={{ padding:'3px 8px', fontSize:11, background:'#f0fdf4', color:'#15803d', border:'1px solid #bbf7d0', borderRadius:5, cursor:'pointer', fontWeight:600 }}>Receive</button>
                        )}
                        {['DRAFT','SUBMITTED'].includes(po.status) && (
                          <button data-testid={`cancel-po-${po.id}`} onClick={()=>action(cancelPurchaseOrder, po.id, 'Cancel')}
                            style={{ padding:'3px 8px', fontSize:11, background:'#fef2f2', color:'#dc2626', border:'1px solid #fecaca', borderRadius:5, cursor:'pointer', fontWeight:600 }}>Cancel</button>
                        )}
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}

      {/* Detail modal */}
      {selected && (
        <Modal title={`Purchase Order — ${selected.poNumber}`} onClose={()=>setSelected(null)}>
          <div style={{ display:'grid', gridTemplateColumns:'1fr 1fr', gap:12, marginBottom:20 }}>
            {[
              ['Supplier',  selected.supplierName || selected.supplierId || '—'],
              ['Status',    selected.status],
              ['Order Date',selected.orderDate],
              ['Expected',  selected.expectedDate||'—'],
              ['Currency',  selected.currency||'USD'],
              ['Created By',selected.createdBy||'—'],
            ].map(([l,v]) => (
              <div key={l}>
                <div style={{ fontSize:11, color:'#9ca3af', textTransform:'uppercase', fontWeight:600, marginBottom:2 }}>{l}</div>
                <div style={{ fontSize:13, fontWeight:600 }}>{v}</div>
              </div>
            ))}
          </div>
          {selected.notes && <p style={{ fontSize:13, color:'#6b7280', marginBottom:16 }}>{selected.notes}</p>}
          <h4 style={{ margin:'0 0 10px', fontWeight:800 }}>Line Items</h4>
          <LineItemsTable items={selected.lineItems} />
          <div style={{ display:'flex', gap:8, justifyContent:'flex-end', marginTop:20 }}>
            <button onClick={()=>setSelected(null)} style={{ padding:'9px 18px', border:'1px solid #d1d5db', borderRadius:8, cursor:'pointer', fontWeight:600 }}>Close</button>
            {selected.status==='DRAFT' && <button onClick={()=>action(submitPurchaseOrder,selected.id,'Submit')} data-testid="detail-submit-btn"
              style={{ padding:'9px 18px', background:'#a9790f', color:'#fff', border:'none', borderRadius:8, cursor:'pointer', fontWeight:700 }}>Submit PO</button>}
            {selected.status==='SUBMITTED' && <button onClick={()=>action(confirmPurchaseOrder,selected.id,'Confirm')} data-testid="detail-confirm-btn"
              style={{ padding:'9px 18px', background:'#d97706', color:'#fff', border:'none', borderRadius:8, cursor:'pointer', fontWeight:700 }}>Confirm PO</button>}
            {selected.status==='CONFIRMED' && <button onClick={()=>action(receivePurchaseOrder,selected.id,'Receive')} data-testid="detail-receive-btn"
              style={{ padding:'9px 18px', background:'#15803d', color:'#fff', border:'none', borderRadius:8, cursor:'pointer', fontWeight:700 }}>Mark Received</button>}
          </div>
        </Modal>
      )}

      {/* Create modal */}
      {showCreate && (
        <Modal title="Create Purchase Order" onClose={()=>setShowCreate(false)}>
          {[
            { label:'Supplier ID *', key:'supplierId',   ph:'e.g. SUPP-001' },
            { label:'Supplier Name', key:'supplierName', ph:'e.g. TechParts India' },
            { label:'Expected Delivery Date', key:'expectedDate', ph:'YYYY-MM-DD', type:'date' },
            { label:'Notes',        key:'notes',         ph:'Optional order notes' },
          ].map(f => (
            <div key={f.key} style={{ marginBottom:14 }}>
              <label style={{ display:'block', fontWeight:700, fontSize:13, marginBottom:4 }}>{f.label}</label>
              <input data-testid={`create-po-${f.key}`} type={f.type||'text'} value={form[f.key]} placeholder={f.ph}
                onChange={e=>setForm(p=>({...p,[f.key]:e.target.value}))}
                style={{ width:'100%', padding:'9px 12px', border:'1px solid #d1d5db', borderRadius:8, fontSize:14, outline:'none', boxSizing:'border-box' }} />
            </div>
          ))}
          <div style={{ marginBottom:20 }}>
            <label style={{ display:'block', fontWeight:700, fontSize:13, marginBottom:4 }}>Currency</label>
            <select data-testid="create-po-currency" value={form.currency} onChange={e=>setForm(p=>({...p,currency:e.target.value}))}
              style={{ width:'100%', padding:'9px 12px', border:'1px solid #d1d5db', borderRadius:8, fontSize:14, outline:'none', background:'#fff', boxSizing:'border-box' }}>
              {['USD','EUR','GBP','INR','SGD'].map(c=><option key={c}>{c}</option>)}
            </select>
          </div>
          <div style={{ display:'flex', gap:10, justifyContent:'flex-end' }}>
            <button onClick={()=>setShowCreate(false)} style={{ padding:'10px 20px', border:'1px solid #d1d5db', borderRadius:8, cursor:'pointer', fontWeight:600 }}>Cancel</button>
            <button data-testid="confirm-create-po" onClick={handleCreate}
              style={{ padding:'10px 22px', background:'#a9790f', color:'#fff', border:'none', borderRadius:8, cursor:'pointer', fontWeight:700 }}>Create PO</button>
          </div>
        </Modal>
      )}
    </div>
  );
}
