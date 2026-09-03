/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
﻿import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';

export default function CostRecordNewPage() {
  const navigate = useNavigate();
  const [form, setForm]     = useState({ itemCode: '', proposedCost: '', justification: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError]   = useState('');

  const handleSubmit = async () => {
    if (!form.itemCode || !form.proposedCost || !form.justification) {
      setError('All fields are required'); return;
    }
    setLoading(true);
    try {
      const res = await api.post('/api/costs', {
        itemCode: form.itemCode,
        proposedCost: parseFloat(form.proposedCost),
        justification: form.justification,
      });
      navigate('/costs/' + res.data.data.id);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create cost record');
    } finally { setLoading(false); }
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <button onClick={() => navigate('/costs')} className="btn-back">Back</button>
          <h1>New Cost Record</h1>
        </div>
      </div>
      {error && <div className="error-banner">{error}</div>}
      <div className="card" style={{ maxWidth: 560 }}>
        <div style={{ marginBottom: 16 }}>
          <label style={{ display: 'block', fontWeight: 700, marginBottom: 6 }}>Item Code *</label>
          <input className="form-input" placeholder="e.g. PCB-001"
            value={form.itemCode} onChange={e => setForm({ ...form, itemCode: e.target.value })} />
        </div>
        <div style={{ marginBottom: 16 }}>
          <label style={{ display: 'block', fontWeight: 700, marginBottom: 6 }}>Proposed Cost (USD) *</label>
          <input className="form-input" type="number" step="0.0001" placeholder="e.g. 48.5000"
            value={form.proposedCost} onChange={e => setForm({ ...form, proposedCost: e.target.value })} />
        </div>
        <div style={{ marginBottom: 20 }}>
          <label style={{ display: 'block', fontWeight: 700, marginBottom: 6 }}>Justification *</label>
          <textarea className="form-input" placeholder="Reason for cost change..."
            style={{ minHeight: 80 }}
            value={form.justification} onChange={e => setForm({ ...form, justification: e.target.value })} />
        </div>
        <button onClick={handleSubmit} disabled={loading} className="btn-primary">
          {loading ? 'Creating...' : 'Create Draft'}
        </button>
      </div>
    </div>
  );
}