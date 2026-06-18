import React, { useState, useEffect } from 'react';
import api from '../api';

/**
 * PENDING P1 — Cost Records Edit DRAFT
 *
 * Modal component for editing a DRAFT cost record before submission.
 * Only records with status === 'DRAFT' can be edited.
 *
 * Usage in CostRecordsPage.js:
 *   {editingRecord && (
 *     <CostRecordEditModal
 *       record={editingRecord}
 *       onClose={() => setEditingRecord(null)}
 *       onSaved={handleRecordUpdated}
 *     />
 *   )}
 *
 * API calls:
 *   PUT /api/cost-records/{id}         — save draft edits
 *   PUT /api/cost-records/{id}/submit  — submit for approval
 */
export default function CostRecordEditModal({ record, onClose, onSaved }) {
  const [form, setForm]         = useState({
    proposedCost:  record?.proposedCost  ?? '',
    justification: record?.justification ?? '',
    itemKey:       record?.itemKey       ?? record?.item?.itemKey ?? '',
    effectiveDate: record?.effectiveDate ?? '',
  });
  const [saving, setSaving]   = useState(false);
  const [error, setError]     = useState(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (record) {
      setForm({
        proposedCost:  record.proposedCost  ?? '',
        justification: record.justification ?? '',
        itemKey:       record.itemKey ?? record.item?.itemKey ?? '',
        effectiveDate: record.effectiveDate ?? '',
      });
    }
  }, [record]);

  if (!record) return null;
  if (record.status !== 'DRAFT') {
    return (
      <Overlay onClose={onClose}>
        <p style={{ color: '#991b1b' }}>
          Only DRAFT records can be edited. Current status: <strong>{record.status}</strong>
        </p>
        <button style={styles.cancelBtn} onClick={onClose}>Close</button>
      </Overlay>
    );
  }

  const handleChange = (field, value) => {
    setForm(prev => ({ ...prev, [field]: value }));
    setError(null);
  };

  const validate = () => {
    if (!form.proposedCost || isNaN(parseFloat(form.proposedCost)) || parseFloat(form.proposedCost) <= 0) {
      return 'Proposed cost must be a positive number';
    }
    if (!form.justification || form.justification.trim().length < 10) {
      return 'Justification must be at least 10 characters';
    }
    return null;
  };

  // Save draft without submitting
  const handleSave = async () => {
    const err = validate();
    if (err) { setError(err); return; }
    setSaving(true);
    try {
      await api.put(`/api/cost-records/${record.id}`, {
        proposedCost:  parseFloat(form.proposedCost),
        justification: form.justification.trim(),
        effectiveDate: form.effectiveDate || null,
      });
      onSaved({ ...record, ...form, status: 'DRAFT' });
      onClose();
    } catch (e) {
      setError(e.response?.data?.error ?? 'Save failed. Please try again.');
    } finally {
      setSaving(false);
    }
  };

  // Save then submit for approval
  const handleSubmit = async () => {
    const err = validate();
    if (err) { setError(err); return; }
    setSubmitting(true);
    try {
      // First save any edits
      await api.put(`/api/cost-records/${record.id}`, {
        proposedCost:  parseFloat(form.proposedCost),
        justification: form.justification.trim(),
        effectiveDate: form.effectiveDate || null,
      });
      // Then submit
      await api.put(`/api/cost-records/${record.id}/submit`);
      onSaved({ ...record, ...form, status: 'PENDING_APPROVAL' });
      onClose();
    } catch (e) {
      setError(e.response?.data?.error ?? 'Submit failed. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  const changePct = record.currentCost && form.proposedCost
    ? (((parseFloat(form.proposedCost) - record.currentCost) / record.currentCost) * 100).toFixed(2)
    : null;

  return (
    <Overlay onClose={onClose}>
      <div style={styles.header}>
        <h2 style={styles.title}>Edit Draft Cost Record</h2>
        <button style={styles.closeX} onClick={onClose}>✕</button>
      </div>

      {/* Record context */}
      <div style={styles.contextBox}>
        <div style={styles.contextItem}>
          <span style={styles.contextLabel}>Record ID</span>
          <span style={styles.contextValue}>#{record.id}</span>
        </div>
        <div style={styles.contextItem}>
          <span style={styles.contextLabel}>Item Code</span>
          <span style={styles.contextValue}>{form.itemKey || '—'}</span>
        </div>
        <div style={styles.contextItem}>
          <span style={styles.contextLabel}>Current Unit Cost</span>
          <span style={styles.contextValue}>
            {record.currentCost != null ? `$${parseFloat(record.currentCost).toFixed(4)}` : '—'}
          </span>
        </div>
        <div style={styles.contextItem}>
          <span style={styles.contextLabel}>Status</span>
          <span style={styles.draftBadge}>DRAFT</span>
        </div>
      </div>

      {/* Form fields */}
      <div style={styles.fieldGroup}>
        <label style={styles.label}>Proposed Unit Cost *</label>
        <div style={{ position: 'relative' }}>
          <span style={styles.currencySymbol}>$</span>
          <input
            type="number"
            step="0.0001"
            min="0"
            style={styles.input}
            value={form.proposedCost}
            onChange={e => handleChange('proposedCost', e.target.value)}
          />
        </div>
        {changePct !== null && (
          <div style={{ ...styles.changePct, color: parseFloat(changePct) > 0 ? '#dc2626' : '#16a34a' }}>
            {parseFloat(changePct) > 0 ? '▲' : '▼'} {Math.abs(changePct)}% vs current cost
          </div>
        )}
      </div>

      <div style={styles.fieldGroup}>
        <label style={styles.label}>Justification * (min 10 chars)</label>
        <textarea
          rows={4}
          style={styles.textarea}
          value={form.justification}
          onChange={e => handleChange('justification', e.target.value)}
          placeholder="Explain why the cost is changing — supplier quote, market price change, contract renewal, etc."
        />
        <div style={styles.charCount}>{form.justification.length} chars</div>
      </div>

      <div style={styles.fieldGroup}>
        <label style={styles.label}>Effective Date (optional)</label>
        <input
          type="date"
          style={styles.input}
          value={form.effectiveDate}
          onChange={e => handleChange('effectiveDate', e.target.value)}
        />
      </div>

      {error && <div style={styles.errorMsg}>⚠ {error}</div>}

      <div style={styles.actions}>
        <button style={styles.cancelBtn} onClick={onClose} disabled={saving || submitting}>
          Cancel
        </button>
        <button style={styles.saveBtn} onClick={handleSave} disabled={saving || submitting}>
          {saving ? 'Saving…' : '💾 Save Draft'}
        </button>
        <button style={styles.submitBtn} onClick={handleSubmit} disabled={saving || submitting}>
          {submitting ? 'Submitting…' : '📤 Submit for Approval'}
        </button>
      </div>
    </Overlay>
  );
}

// ── Overlay wrapper ────────────────────────────────────────────────────────────
function Overlay({ children, onClose }) {
  return (
    <div style={styles.overlay} onClick={onClose}>
      <div style={styles.modal} onClick={e => e.stopPropagation()}>
        {children}
      </div>
    </div>
  );
}

// ── Styles ─────────────────────────────────────────────────────────────────────
const styles = {
  overlay:       { position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.55)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 },
  modal:         { background: '#fff', borderRadius: 16, padding: 32, width: '90%', maxWidth: 520, maxHeight: '90vh', overflowY: 'auto' },
  header:        { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 },
  title:         { fontSize: 20, fontWeight: 800, color: '#16213e', margin: 0 },
  closeX:        { background: 'none', border: 'none', fontSize: 20, cursor: 'pointer', color: '#6b7280' },
  contextBox:    { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12, background: '#f0ede6', borderRadius: 10, padding: 16, marginBottom: 20 },
  contextItem:   { display: 'flex', flexDirection: 'column', gap: 2 },
  contextLabel:  { fontSize: 10, color: '#6b7280', fontWeight: 700, textTransform: 'uppercase', letterSpacing: 0.5 },
  contextValue:  { fontSize: 14, fontWeight: 700, color: '#16213e' },
  draftBadge:    { display: 'inline-block', background: '#dbeafe', color: '#1d4ed8', padding: '2px 10px', borderRadius: 20, fontSize: 11, fontWeight: 700 },
  fieldGroup:    { marginBottom: 18 },
  label:         { display: 'block', fontSize: 13, fontWeight: 700, color: '#374151', marginBottom: 6 },
  input:         { width: '100%', padding: '9px 12px 9px 24px', border: '1px solid #d1d5db', borderRadius: 8, fontSize: 15, boxSizing: 'border-box' },
  currencySymbol:{ position: 'absolute', left: 10, top: '50%', transform: 'translateY(-50%)', color: '#6b7280', fontSize: 14 },
  changePct:     { fontSize: 12, fontWeight: 600, marginTop: 4 },
  textarea:      { width: '100%', padding: '9px 12px', border: '1px solid #d1d5db', borderRadius: 8, fontSize: 14, resize: 'vertical', boxSizing: 'border-box', fontFamily: 'sans-serif' },
  charCount:     { fontSize: 11, color: '#9ca3af', textAlign: 'right', marginTop: 2 },
  errorMsg:      { background: '#fee2e2', color: '#991b1b', padding: '10px 14px', borderRadius: 8, fontSize: 13, marginBottom: 16 },
  actions:       { display: 'flex', gap: 10, justifyContent: 'flex-end', marginTop: 8 },
  cancelBtn:     { padding: '10px 18px', border: '1px solid #d1d5db', borderRadius: 8, background: '#fff', cursor: 'pointer', fontWeight: 600, color: '#374151' },
  saveBtn:       { padding: '10px 18px', background: '#1d3557', color: '#fff', border: 'none', borderRadius: 8, cursor: 'pointer', fontWeight: 700 },
  submitBtn:     { padding: '10px 18px', background: '#2a9d8f', color: '#fff', border: 'none', borderRadius: 8, cursor: 'pointer', fontWeight: 700 },
};
