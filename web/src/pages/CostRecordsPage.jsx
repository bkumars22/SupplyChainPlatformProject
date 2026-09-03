/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
﻿import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';

const STATUS_COLORS = {
  DRAFT:            { bg: '#e3f2fd', color: '#1565c0' },
  PENDING_APPROVAL: { bg: '#fff8e1', color: '#f57f17' },
  APPROVED:         { bg: '#e8f5e9', color: '#2e7d32' },
  REJECTED:         { bg: '#ffebee', color: '#c62828' },
};

export default function CostRecordsPage() {
  const [records, setRecords]     = useState([]);
  const [search, setSearch]       = useState('');
  const [page, setPage]           = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading]     = useState(false);
  const [stats, setStats]         = useState(null);
  const [voiceMsg, setVoiceMsg]   = useState({ text: '', type: '' });
  const navigate = useNavigate();

  useEffect(() => { fetchRecords(); }, [search, page]);
  useEffect(() => { fetchStats(); }, []);

  const flashVoice = (text, type = 'success') => { setVoiceMsg({ text, type }); setTimeout(() => setVoiceMsg({ text: '', type: '' }), 4000); };

  useEffect(() => {
    const h = async (e) => {
      const { action, itemHint } = e.detail || {};
      if (action !== 'submit-first' && action !== 'approve-first') return;
      const wantStatus = action === 'submit-first' ? 'DRAFT' : 'PENDING_APPROVAL';
      const verb = action === 'submit-first' ? 'submit' : 'approve';
      const target = records.find(r => r.status === wantStatus && (!itemHint || r.item?.itemCode === itemHint));
      if (!target) {
        flashVoice(itemHint ? `No ${wantStatus.replace('_',' ').toLowerCase()} record found for ${itemHint}` : `No ${wantStatus.replace('_',' ').toLowerCase()} records to ${verb}`, 'error');
        return;
      }
      try {
        await api.put(`/api/costs/${target.id}/${verb}`);
        flashVoice(`Cost record #${target.id} (${target.item?.itemCode}) ${verb === 'submit' ? 'submitted' : 'approved'}`);
        fetchRecords(); fetchStats();
      } catch (err) {
        flashVoice(err.response?.data?.message || `Failed to ${verb} cost record`, 'error');
      }
    };
    window.addEventListener('scip-voice', h);
    return () => window.removeEventListener('scip-voice', h);
  }, [records]);

  const fetchRecords = async () => {
    setLoading(true);
    try {
      const res = await api.get('/api/costs', { params: { search, page, size: 20 } });
      setRecords(res.data.data.content);
      setTotalPages(res.data.data.totalPages);
    } catch (err) { console.error(err); }
    finally { setLoading(false); }
  };

  const fetchStats = async () => {
    try {
      const res = await api.get('/api/costs/stats');
      setStats(res.data.data);
    } catch (err) { console.error(err); }
  };

  return (
    <div className="page-container">
      {voiceMsg.text && (
        <div style={{ padding: '10px 16px', borderRadius: 8, marginBottom: 16, fontWeight: 600, fontSize: 13,
          background: voiceMsg.type === 'error' ? '#fef2f2' : '#f0fdf4',
          color: voiceMsg.type === 'error' ? '#dc2626' : '#15803d',
          border: `1px solid ${voiceMsg.type === 'error' ? '#fecaca' : '#bbf7d0'}` }}>
          🎤 {voiceMsg.text}
        </div>
      )}
      <div className="page-header">
        <h1>Cost Records</h1>
        <button onClick={() => navigate('/costs/new')}
          style={{ background: "#a9790f", color: "#fff", border: "none", borderRadius: 8, padding: "10px 20px", fontWeight: 700, cursor: "pointer", fontSize: 14 }}>
          + New Cost Record
        </button>
      </div>

      {stats && (
        <div style={{ display: 'flex', gap: 12, marginBottom: 20, flexWrap: 'wrap' }}>
          {Object.entries(stats.byStatus || {}).map(([status, count]) => (
            <div key={status} style={{ background: STATUS_COLORS[status]?.bg || '#f5f5f5', color: STATUS_COLORS[status]?.color || '#333', borderRadius: 10, padding: '10px 18px', fontWeight: 700, fontSize: 13 }}>
              {status.replace('_', ' ')}: {count}
            </div>
          ))}
          <div style={{ background: '#e8eaf6', color: '#1e2a3b', borderRadius: 10, padding: '10px 18px', fontWeight: 700, fontSize: 13 }}>
            TOTAL: {stats.totalRecords}
          </div>
        </div>
      )}

      <input type="text" placeholder="Search by item code or justification..."
        value={search} onChange={e => { setSearch(e.target.value); setPage(0); }}
        className="search-box" />

      {loading ? <div className="spinner" /> : (
        <table className="data-table">
          <thead>
            <tr>
              <th>ID</th><th>Item Code</th><th>Version</th>
              <th>Previous Cost</th><th>Proposed Cost</th><th>Change %</th>
              <th>Status</th><th>Created By</th><th>Date</th>
            </tr>
          </thead>
          <tbody>
            {records.map(r => (
              <tr key={r.id} onClick={() => navigate('/costs/' + r.id)} style={{ cursor: 'pointer' }}>
                <td>#{r.id}</td>
                <td><code>{r.item?.itemCode}</code></td>
                <td>v{r.versionNumber}</td>
                <td>{r.previousCost ? '$' + r.previousCost.toFixed(4) : '--'}</td>
                <td><strong>{r.proposedCost ? '$' + r.proposedCost.toFixed(4) : '--'}</strong></td>
                <td style={{ color: r.changePercent > 0 ? '#c62828' : '#2e7d32', fontWeight: 700 }}>
                  {r.changePercent > 0 ? '+' : ''}{r.changePercent?.toFixed(2)}%
                </td>
                <td>
                  <span style={{ ...STATUS_COLORS[r.status], padding: '3px 10px', borderRadius: 20, fontSize: 11, fontWeight: 700 }}>
                    {r.status?.replace('_', ' ')}
                  </span>
                </td>
                <td>{r.createdBy}</td>
                <td>{r.createdDate ? new Date(r.createdDate).toLocaleDateString() : '--'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <div className="pagination">
        <button disabled={page === 0} onClick={() => setPage(p => p - 1)}>Prev</button>
        <span>Page {page + 1} of {totalPages}</span>
        <button disabled={page >= totalPages - 1} onClick={() => setPage(p => p + 1)}>Next</button>
      </div>
    </div>
  );
}