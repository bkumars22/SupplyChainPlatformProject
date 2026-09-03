/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
﻿import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api';

export default function SupplierDetailPage() {
  const { supplierId } = useParams();
  const navigate = useNavigate();
  const [scorecard, setScorecard]   = useState(null);
  const [deliveries, setDeliveries] = useState([]);
  const [loading, setLoading]       = useState(true);
  const [tab, setTab]               = useState('overview');

  useEffect(() => {
    Promise.all([
      api.get('/api/suppliers/' + supplierId),
      api.get('/api/suppliers/' + supplierId + '/deliveries')
    ]).then(([sc, del]) => {
      setScorecard(sc.data.data);
      setDeliveries(del.data.data);
    }).finally(() => setLoading(false));
  }, [supplierId]);

  if (loading) return <div className="spinner" />;
  if (!scorecard) return <div>Supplier not found</div>;

  const scoreColor = (s) => s >= 80 ? '#2e7d32' : s >= 60 ? '#f57f17' : '#c62828';

  return (
    <div className="page-container">
      <div className="page-header">
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <button onClick={() => navigate('/suppliers')} className="btn-back">Back</button>
          <h1>{scorecard.supplierName}</h1>
        </div>
        {scorecard.atRisk && (
          <span style={{ background: '#ffebee', color: '#c62828', padding: '4px 12px', borderRadius: 20, fontSize: 12, fontWeight: 700 }}>
            AT RISK
          </span>
        )}
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit,minmax(150px,1fr))', gap: 12, marginBottom: 24 }}>
        {[
          { label: 'OTD Score',       value: scorecard.otdScore?.toFixed(1) + '%', color: scoreColor(scorecard.otdScore) },
          { label: 'Quality Score',   value: scorecard.qualityScore?.toFixed(1),   color: '#1e2a3b' },
          { label: 'Responsiveness',  value: scorecard.responsivenessScore?.toFixed(1), color: '#1e2a3b' },
          { label: 'Composite Score', value: scorecard.compositeScore?.toFixed(1), color: scoreColor(scorecard.compositeScore) },
          { label: 'Total Deliveries',value: scorecard.totalDeliveries,            color: '#555' },
          { label: 'On-Time',         value: scorecard.onTimeDeliveries,           color: '#2e7d32' },
        ].map(c => (
          <div key={c.label} style={{ background: '#fff', borderRadius: 10, padding: 16, textAlign: 'center', boxShadow: '0 1px 4px rgba(0,0,0,0.06)' }}>
            <div style={{ fontSize: 26, fontWeight: 800, color: c.color }}>{c.value}</div>
            <div style={{ fontSize: 12, color: '#888', marginTop: 4 }}>{c.label}</div>
          </div>
        ))}
      </div>

      <div style={{ display: 'flex', gap: 8, marginBottom: 16 }}>
        {['overview', 'deliveries'].map(t => (
          <button key={t} onClick={() => setTab(t)}
            style={{ padding: '8px 18px', borderRadius: 8, border: 'none', cursor: 'pointer', fontWeight: 700, fontSize: 13,
                     background: tab === t ? '#1e2a3b' : '#e8eaf6', color: tab === t ? '#fff' : '#1e2a3b' }}>
            {t === 'overview' ? 'Overview' : 'Delivery History'}
          </button>
        ))}
      </div>

      {tab === 'overview' && (
        <div className="card">
          <p><strong>Supplier ID:</strong> {scorecard.supplierId}</p>
          <p><strong>Country:</strong> {scorecard.country}</p>
          <p><strong>Tier:</strong> {scorecard.tier}</p>
        </div>
      )}

      {tab === 'deliveries' && (
        <table className="data-table">
          <thead>
            <tr><th>PO Number</th><th>Item</th><th>Promised</th><th>Actual</th><th>Qty</th><th>Status</th><th>Delay</th></tr>
          </thead>
          <tbody>
            {deliveries.map(d => (
              <tr key={d.id}>
                <td><code>{d.poNumber}</code></td>
                <td>{d.itemCode}</td>
                <td>{d.promisedDate}</td>
                <td>{d.actualDate || '--'}</td>
                <td>{(d.qtyReceived || 0) + '/' + d.qtyOrdered}</td>
                <td style={{ color: d.status === 'ON_TIME' ? '#2e7d32' : '#c62828', fontWeight: 700 }}>{d.status}</td>
                <td style={{ color: d.delayDays > 0 ? '#c62828' : '#2e7d32', fontWeight: 700 }}>
                  {d.delayDays > 0 ? '+' + d.delayDays + 'd' : 'On time'}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}