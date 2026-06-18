import React, { useState } from 'react';
import { createSupplierRating } from '../api';

export default function SupplierRatingModal({ supplier, onClose, onSaved }) {
  const user = JSON.parse(localStorage.getItem('user_data') || '{}');
  const [form, setForm] = useState({ qualityScore: '', deliveryScore: '', responsiveness: '', comments: '', ratedBy: user.userId || 'kumar' });
  const [saving, setSaving] = useState(false);
  const [err, setErr] = useState('');

  const set = (k, v) => setForm(p => ({ ...p, [k]: v }));

  const validate = () => {
    const { qualityScore, deliveryScore, responsiveness } = form;
    for (const [label, val] of [['Quality', qualityScore], ['Delivery', deliveryScore], ['Responsiveness', responsiveness]]) {
      const n = parseFloat(val);
      if (isNaN(n) || n < 0 || n > 100) return `${label} score must be 0–100`;
    }
    return null;
  };

  const handleSubmit = async () => {
    const e = validate();
    if (e) { setErr(e); return; }
    setSaving(true); setErr('');
    try {
      await createSupplierRating(supplier.supplierId, {
        qualityScore: parseFloat(form.qualityScore),
        deliveryScore: parseFloat(form.deliveryScore),
        responsiveness: parseFloat(form.responsiveness),
        comments: form.comments,
        ratedBy: form.ratedBy,
      });
      onSaved();
      onClose();
    } catch (ex) {
      setErr(ex.response?.data?.error || ex.message || 'Failed to save rating');
    } finally {
      setSaving(false);
    }
  };

  const overall = (!isNaN(+form.qualityScore) && !isNaN(+form.deliveryScore) && !isNaN(+form.responsiveness))
    ? (((+form.qualityScore) + (+form.deliveryScore) + (+form.responsiveness)) / 3).toFixed(1)
    : null;

  const scoreColor = (v) => !v ? '#9ca3af' : +v >= 80 ? '#15803d' : +v >= 60 ? '#1d4ed8' : +v >= 40 ? '#d97706' : '#dc2626';

  return (
    <div data-testid="supplier-rating-modal"
      style={{ position:'fixed', inset:0, background:'rgba(0,0,0,0.5)', display:'flex', alignItems:'center', justifyContent:'center', zIndex:1000 }}>
      <div style={{ background:'#fff', borderRadius:14, padding:32, width:460, maxWidth:'95vw', maxHeight:'90vh', overflowY:'auto' }}>
        <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:20 }}>
          <div>
            <h2 style={{ fontSize:18, fontWeight:800, margin:0 }}>Submit Quality Rating</h2>
            <p style={{ color:'#6b7280', fontSize:13, margin:'4px 0 0' }}>{supplier.supplierName}</p>
          </div>
          <button data-testid="close-rating-modal" onClick={onClose}
            style={{ background:'none', border:'none', fontSize:22, cursor:'pointer', color:'#6b7280', lineHeight:1 }}>×</button>
        </div>

        {err && <div style={{ background:'#fef2f2', color:'#dc2626', border:'1px solid #fecaca', borderRadius:8, padding:'10px 14px', marginBottom:16, fontSize:13 }}>{err}</div>}

        {/* Score inputs */}
        {[
          { key:'qualityScore',   label:'Quality Score',      hint:'Product quality, defect rate (0–100)' },
          { key:'deliveryScore',  label:'Delivery Score',     hint:'On-time delivery performance (0–100)' },
          { key:'responsiveness', label:'Responsiveness',     hint:'Communication & issue resolution (0–100)' },
        ].map(f => (
          <div key={f.key} style={{ marginBottom:16 }}>
            <label style={{ display:'block', fontWeight:700, fontSize:13, marginBottom:4 }}>
              {f.label} <span style={{ fontWeight:400, color:'#9ca3af', fontSize:11 }}>— {f.hint}</span>
            </label>
            <div style={{ display:'flex', alignItems:'center', gap:10 }}>
              <input
                data-testid={`input-${f.key}`}
                type="number" min="0" max="100" step="0.5"
                value={form[f.key]}
                onChange={e => set(f.key, e.target.value)}
                placeholder="0–100"
                style={{ flex:1, padding:'9px 12px', border:'1px solid #d1d5db', borderRadius:8, fontSize:14, outline:'none' }}
              />
              {form[f.key] !== '' && (
                <span style={{ fontWeight:800, color: scoreColor(form[f.key]), minWidth:40 }}>
                  {parseFloat(form[f.key]).toFixed(1)}
                </span>
              )}
            </div>
          </div>
        ))}

        {/* Overall preview */}
        {overall && (
          <div style={{ background:'#f8fafc', border:'1px solid #e5e7eb', borderRadius:8, padding:'10px 14px', marginBottom:16, display:'flex', alignItems:'center', gap:10 }}>
            <span style={{ fontSize:13, color:'#6b7280' }}>Overall score (auto-calculated):</span>
            <span style={{ fontWeight:800, fontSize:18, color: scoreColor(overall) }}>{overall}</span>
          </div>
        )}

        {/* Comments */}
        <div style={{ marginBottom:20 }}>
          <label style={{ display:'block', fontWeight:700, fontSize:13, marginBottom:4 }}>Comments (optional)</label>
          <textarea
            data-testid="input-comments"
            value={form.comments}
            onChange={e => set('comments', e.target.value)}
            rows={3}
            placeholder="Add notes about supplier performance..."
            style={{ width:'100%', padding:'9px 12px', border:'1px solid #d1d5db', borderRadius:8, fontSize:13, outline:'none', resize:'vertical', boxSizing:'border-box' }}
          />
        </div>

        <div style={{ display:'flex', gap:10, justifyContent:'flex-end' }}>
          <button onClick={onClose} data-testid="cancel-rating"
            style={{ padding:'10px 20px', border:'1px solid #d1d5db', borderRadius:8, background:'#fff', cursor:'pointer', fontWeight:600 }}>Cancel</button>
          <button onClick={handleSubmit} disabled={saving} data-testid="submit-rating"
            style={{ padding:'10px 22px', background: saving ? '#9ca3af' : '#1d4ed8', color:'#fff', border:'none', borderRadius:8, cursor: saving ? 'not-allowed' : 'pointer', fontWeight:700 }}>
            {saving ? 'Saving…' : 'Submit Rating'}
          </button>
        </div>
      </div>
    </div>
  );
}
