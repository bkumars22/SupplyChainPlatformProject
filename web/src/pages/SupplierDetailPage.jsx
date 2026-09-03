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
  const [scorecard, setScorecard]         = useState(null);
  const [deliveries, setDeliveries]       = useState([]);
  const [cascadedRisk, setCascadedRisk]   = useState(null);
  const [structuralRisk, setStructuralRisk] = useState(null);
  const [loading, setLoading]             = useState(true);
  const [tab, setTab]                     = useState('overview');

  useEffect(() => {
    Promise.all([
      api.get('/api/suppliers/' + supplierId),
      api.get('/api/suppliers/' + supplierId + '/deliveries'),
      api.get('/api/suppliers/' + supplierId + '/cascaded-risk').catch(() => null),
      api.get('/api/suppliers/' + supplierId + '/structural-risk').catch(() => null),
    ]).then(([sc, del, cascaded, structural]) => {
      setScorecard(sc.data.data);
      setDeliveries(del.data.data);
      setCascadedRisk(cascaded ? cascaded.data.data : null);
      setStructuralRisk(structural ? structural.data.data : null);
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
        {['overview', 'deliveries', 'dependencies'].map(t => (
          <button key={t} onClick={() => setTab(t)}
            style={{ padding: '8px 18px', borderRadius: 8, border: 'none', cursor: 'pointer', fontWeight: 700, fontSize: 13,
                     background: tab === t ? '#1e2a3b' : '#e8eaf6', color: tab === t ? '#fff' : '#1e2a3b' }}>
            {t === 'overview' ? 'Overview' : t === 'deliveries' ? 'Delivery History' : 'Dependency Risk'}
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

      {tab === 'dependencies' && (
        <div>
          {/* The demo site's mock API shim returns { data: [] } (an empty
              array) for any endpoint it doesn't specifically mock, rather
              than null -- treat that the same as "no data" so this shows the
              fallback message instead of NaN%. */}
          {!cascadedRisk || typeof cascadedRisk.effectiveRisk !== 'number' ? (
            <div className="card">Dependency risk data is unavailable right now.</div>
          ) : (
            <>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit,minmax(180px,1fr))', gap: 12, marginBottom: 16 }}>
                <div style={{ background: '#fff', borderRadius: 10, padding: 16, textAlign: 'center', boxShadow: '0 1px 4px rgba(0,0,0,0.06)' }}>
                  <div style={{ fontSize: 26, fontWeight: 800, color: '#1e2a3b' }}>{(cascadedRisk.directRisk * 100).toFixed(1)}%</div>
                  <div style={{ fontSize: 12, color: '#888', marginTop: 4 }}>Direct Risk (own metrics)</div>
                </div>
                <div style={{ background: '#fff', borderRadius: 10, padding: 16, textAlign: 'center', boxShadow: '0 1px 4px rgba(0,0,0,0.06)' }}>
                  <div style={{ fontSize: 26, fontWeight: 800, color: cascadedRisk.effectiveRisk > cascadedRisk.directRisk ? '#c62828' : '#1e2a3b' }}>
                    {(cascadedRisk.effectiveRisk * 100).toFixed(1)}%
                  </div>
                  <div style={{ fontSize: 12, color: '#888', marginTop: 4 }}>Effective Risk (with dependencies)</div>
                </div>
                <div style={{ background: '#fff', borderRadius: 10, padding: 16, textAlign: 'center', boxShadow: '0 1px 4px rgba(0,0,0,0.06)' }}>
                  <div style={{ fontSize: 26, fontWeight: 800, color: '#555' }}>+{(cascadedRisk.cascadedContribution * 100).toFixed(1)}%</div>
                  <div style={{ fontSize: 12, color: '#888', marginTop: 4 }}>From Upstream Suppliers</div>
                </div>
              </div>

              <div className="card" style={{ marginBottom: 16 }}>
                {cascadedRisk.summary}
              </div>

              {cascadedRisk.soleSourceRiskFlags?.length > 0 && (
                <div style={{ marginBottom: 16 }}>
                  <h3 style={{ marginBottom: 8 }}>Active Sole-Source Risk</h3>
                  {cascadedRisk.soleSourceRiskFlags.map((f, i) => (
                    <div key={i} style={{ background: '#ffebee', border: '1px solid #ffcdd2', borderRadius: 10, padding: 14, marginBottom: 8 }}>
                      <strong>{f.upstreamSupplierName}</strong> is the sole source of {f.componentOrMaterial || 'a critical input'}
                      {' '}and currently shows elevated risk ({(f.upstreamRisk * 100).toFixed(0)}%), {f.hopDistance} hop{f.hopDistance > 1 ? 's' : ''} upstream.
                    </div>
                  ))}
                </div>
              )}

              <div>
                <h3 style={{ marginBottom: 8 }}>Structural Single Points of Failure</h3>
                {structuralRisk?.soleSourceDependencies?.length > 0 ? (
                  structuralRisk.soleSourceDependencies.map((d, i) => (
                    <div key={i} style={{ background: '#fff8e1', border: '1px solid #ffe082', borderRadius: 10, padding: 14, marginBottom: 8 }}>
                      No alternate source for <strong>{d.componentOrMaterial || 'this input'}</strong> — sole-sourced from <strong>{d.upstreamSupplierName}</strong>.
                    </div>
                  ))
                ) : (
                  <div className="card">No sole-source dependencies mapped for this supplier.</div>
                )}
              </div>
            </>
          )}
        </div>
      )}
    </div>
  );
}