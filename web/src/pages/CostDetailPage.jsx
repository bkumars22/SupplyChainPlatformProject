/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
﻿import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api';

const STATUS_COLORS = {
  DRAFT:            { bg: '#e3f2fd', color: '#1565c0' },
  PENDING_APPROVAL: { bg: '#fff8e1', color: '#f57f17' },
  APPROVED:         { bg: '#e8f5e9', color: '#2e7d32' },
  REJECTED:         { bg: '#ffebee', color: '#c62828' },
};

export default function CostDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [record, setRecord]         = useState(null);
  const [loading, setLoading]       = useState(true);
  const [actionLoading, setAction]  = useState(false);
  const [rejectReason, setReject]   = useState('');
  const [showRejectBox, setShowBox] = useState(false);
  const [error, setError]           = useState('');

  useEffect(() => { fetchRecord(); }, [id]);

  const fetchRecord = async () => {
    try {
      const res = await api.get('/api/costs/' + id);
      setRecord(res.data.data);
    } catch (err) { setError('Failed to load cost record'); }
    finally { setLoading(false); }
  };

  const handleSubmit = async () => {
    setAction(true);
    try { await api.put('/api/costs/' + id + '/submit'); await fetchRecord(); }
    catch (err) { setError(err.response?.data?.message || 'Submit failed'); }
    finally { setAction(false); }
  };

  const handleApprove = async () => {
    setAction(true);
    try { await api.put('/api/costs/' + id + '/approve'); await fetchRecord(); }
    catch (err) { setError(err.response?.data?.message || 'Approve failed'); }
    finally { setAction(false); }
  };

  const handleReject = async () => {
    if (!rejectReason.trim()) { setError('Rejection reason is required'); return; }
    setAction(true);
    try {
      await api.put('/api/costs/' + id + '/reject', { reason: rejectReason });
      await fetchRecord(); setShowBox(false);
    } catch (err) { setError(err.response?.data?.message || 'Reject failed'); }
    finally { setAction(false); }
  };

  if (loading) return <div className="spinner" />;
  if (!record) return <div>Record not found</div>;
  const sc = STATUS_COLORS[record.status] || {};

  return (
    <div className="page-container">
      <div className="page-header">
        <button onClick={() => navigate('/costs')} className="btn-back">Back</button>
        <h1>Cost Record #{record.id}</h1>
        <span style={{ ...sc, padding: '6px 16px', borderRadius: 20, fontWeight: 700, fontSize: 13 }}>
          {record.status?.replace('_', ' ')}
        </span>
      </div>

      {error && <div className="error-banner">{error}</div>}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit,minmax(280px,1fr))', gap: 16, marginBottom: 24 }}>
        <div className="card">
          <h3>Item</h3>
          <p><strong>Code:</strong> <code>{record.item?.itemCode}</code></p>
          <p><strong>Description:</strong> {record.item?.description}</p>
          <p><strong>Version:</strong> v{record.versionNumber}</p>
        </div>
        <div className="card">
          <h3>Cost Change</h3>
          <p><strong>Previous:</strong> {record.previousCost ? '$' + record.previousCost.toFixed(4) : '--'}</p>
          <p><strong>Proposed:</strong> <strong style={{ color: '#1a237e' }}>{record.proposedCost ? '$' + record.proposedCost.toFixed(4) : '--'}</strong></p>
          <p><strong>Change:</strong>
            <span style={{ color: record.changePercent > 0 ? '#c62828' : '#2e7d32', fontWeight: 700, marginLeft: 6 }}>
              {record.changePercent > 0 ? '+' : ''}{record.changePercent?.toFixed(2)}%
            </span>
          </p>
        </div>
        <div className="card">
          <h3>Workflow</h3>
          <p><strong>Created By:</strong> {record.createdBy}</p>
          <p><strong>Created:</strong> {record.createdDate ? new Date(record.createdDate).toLocaleString() : '--'}</p>
          <p><strong>Submitted:</strong> {record.submittedDate ? new Date(record.submittedDate).toLocaleString() : '--'}</p>
          <p><strong>Approved By:</strong> {record.approvedBy || '--'}</p>
        </div>
      </div>

      <div className="card" style={{ marginBottom: 24 }}>
        <h3>Justification</h3>
        <p style={{ marginTop: 8, color: '#555', lineHeight: 1.7 }}>{record.justification}</p>
        {record.rejectionReason && (
          <div style={{ marginTop: 12, background: '#ffebee', borderRadius: 8, padding: '10px 14px', color: '#c62828' }}>
            <strong>Rejection Reason:</strong> {record.rejectionReason}
          </div>
        )}
      </div>

      <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
        {record.status === 'DRAFT' && (
          <button onClick={handleSubmit} disabled={actionLoading} className="btn-primary">
            {actionLoading ? 'Submitting...' : 'Submit for Approval'}
          </button>
        )}
        {record.status === 'PENDING_APPROVAL' && (
          <>
            <button onClick={handleApprove} disabled={actionLoading} className="btn-success">
              {actionLoading ? 'Approving...' : 'Approve'}
            </button>
            <button onClick={() => setShowBox(true)} disabled={actionLoading} className="btn-danger">Reject</button>
          </>
        )}
      </div>

      {showRejectBox && (
        <div className="card" style={{ marginTop: 16 }}>
          <h3>Rejection Reason *</h3>
          <textarea value={rejectReason} onChange={e => setReject(e.target.value)}
            placeholder="Enter reason for rejection..."
            style={{ width: '100%', minHeight: 80, marginTop: 8, padding: 10, borderRadius: 8, border: '1px solid #ddd', fontSize: 14 }} />
          <div style={{ display: 'flex', gap: 10, marginTop: 10 }}>
            <button onClick={handleReject} disabled={actionLoading} className="btn-danger">Confirm Reject</button>
            <button onClick={() => setShowBox(false)} className="btn-secondary">Cancel</button>
          </div>
        </div>
      )}
    </div>
  );
}