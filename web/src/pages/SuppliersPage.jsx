/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
﻿import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';

const TIER_COLORS = {
  PREFERRED:   { bg: '#e8f5e9', color: '#2e7d32' },
  APPROVED:    { bg: '#e3f2fd', color: '#1565c0' },
  CONDITIONAL: { bg: '#fff8e1', color: '#f57f17' },
  PROBATION:   { bg: '#ffebee', color: '#c62828' },
};

function ScoreBar({ score }) {
  const pct = Math.min(100, score);
  const color = pct >= 80 ? '#2e7d32' : pct >= 60 ? '#f57f17' : '#c62828';
  return (
    <div style={{ background: '#e0e0e0', borderRadius: 10, height: 8, overflow: 'hidden' }}>
      <div style={{ width: pct + '%', height: '100%', background: color, borderRadius: 10 }} />
    </div>
  );
}

export default function SuppliersPage() {
  const [suppliers, setSuppliers] = useState([]);
  const [search, setSearch]       = useState('');
  const [stats, setStats]         = useState(null);
  const [loading, setLoading]     = useState(false);
  const navigate = useNavigate();

  useEffect(() => { fetchSuppliers(); }, [search]);
  useEffect(() => { fetchStats(); }, []);

  const fetchSuppliers = async () => {
    setLoading(true);
    try {
      const res = await api.get('/api/suppliers', { params: { search } });
      setSuppliers(res.data.data);
    } catch (err) { console.error(err); }
    finally { setLoading(false); }
  };

  const fetchStats = async () => {
    try {
      const res = await api.get('/api/suppliers/stats');
      setStats(res.data.data);
    } catch (err) { console.error(err); }
  };

  return (
    <div className="page-container">
      <div className="page-header"><h1>Supplier Scorecard</h1></div>

      {stats && (
        <div style={{ display: 'flex', gap: 12, marginBottom: 20, flexWrap: 'wrap' }}>
          <div style={{ background: '#e8eaf6', color: '#1a237e', borderRadius: 10, padding: '10px 18px', fontWeight: 700, fontSize: 13 }}>Total: {stats.totalSuppliers}</div>
          <div style={{ background: '#ffebee', color: '#c62828', borderRadius: 10, padding: '10px 18px', fontWeight: 700, fontSize: 13 }}>At Risk: {stats.atRiskCount}</div>
          <div style={{ background: '#e8eaf6', color: '#1a237e', borderRadius: 10, padding: '10px 18px', fontWeight: 700, fontSize: 13 }}>Avg OTD: {stats.avgOtdScore?.toFixed(1)}%</div>
          <div style={{ background: '#e8eaf6', color: '#1a237e', borderRadius: 10, padding: '10px 18px', fontWeight: 700, fontSize: 13 }}>Avg Score: {stats.avgCompositeScore?.toFixed(1)}</div>
        </div>
      )}

      <input type="text" placeholder="Search suppliers..." value={search}
        onChange={e => setSearch(e.target.value)} className="search-input" />

      {loading ? <div className="spinner" /> : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill,minmax(320px,1fr))', gap: 16 }}>
          {suppliers.map(s => (
            <div key={s.supplierId} onClick={() => navigate('/suppliers/' + s.supplierId)}
              style={{ background: '#fff', borderRadius: 14, padding: 20, cursor: 'pointer',
                       boxShadow: '0 2px 8px rgba(0,0,0,0.07)',
                       borderTop: '4px solid ' + (s.atRisk ? '#c62828' : '#1a237e') }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 10 }}>
                <div>
                  <div style={{ fontWeight: 700, fontSize: 15 }}>{s.supplierName}</div>
                  <div style={{ fontSize: 12, color: '#888' }}>{s.supplierId} · {s.country}</div>
                </div>
                <span style={{ ...TIER_COLORS[s.tier], fontSize: 11, fontWeight: 700, padding: '3px 10px', borderRadius: 20 }}>{s.tier}</span>
              </div>
              <div style={{ marginBottom: 8 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 12, marginBottom: 3 }}>
                  <span>OTD Score</span><span style={{ fontWeight: 700 }}>{s.otdScore?.toFixed(1)}%</span>
                </div>
                <ScoreBar score={s.otdScore} />
              </div>
              <div style={{ marginBottom: 8 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 12, marginBottom: 3 }}>
                  <span>Quality Score</span><span style={{ fontWeight: 700 }}>{s.qualityScore?.toFixed(1)}</span>
                </div>
                <ScoreBar score={s.qualityScore} />
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 10 }}>
                <span style={{ fontSize: 12, color: '#888' }}>{s.totalDeliveries} deliveries</span>
                <span style={{ fontWeight: 800, fontSize: 20, color: s.compositeScore >= 80 ? '#2e7d32' : s.compositeScore >= 60 ? '#f57f17' : '#c62828' }}>
                  {s.compositeScore?.toFixed(1)}
                </span>
              </div>
              {s.atRisk && (
                <div style={{ background: '#ffebee', color: '#c62828', borderRadius: 6, padding: '4px 10px', fontSize: 11, fontWeight: 700, marginTop: 8 }}>
                  AT RISK â€” OTD below 70%
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}