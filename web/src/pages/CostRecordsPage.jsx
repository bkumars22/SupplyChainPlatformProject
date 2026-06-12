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
  const navigate = useNavigate();

  useEffect(() => { fetchRecords(); }, [search, page]);
  useEffect(() => { fetchStats(); }, []);

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
      <div className="page-header">
        <h1>Cost Records</h1>
        <button onClick={() => navigate('/costs/new')} className="btn-primary">+ New Cost Record</button>
      </div>

      {stats && (
        <div style={{ display: 'flex', gap: 12, marginBottom: 20, flexWrap: 'wrap' }}>
          {Object.entries(stats.byStatus || {}).map(([status, count]) => (
            <div key={status} style={{ background: STATUS_COLORS[status]?.bg || '#f5f5f5', color: STATUS_COLORS[status]?.color || '#333', borderRadius: 10, padding: '10px 18px', fontWeight: 700, fontSize: 13 }}>
              {status.replace('_', ' ')}: {count}
            </div>
          ))}
          <div style={{ background: '#e8eaf6', color: '#1a237e', borderRadius: 10, padding: '10px 18px', fontWeight: 700, fontSize: 13 }}>
            TOTAL: {stats.totalRecords}
          </div>
        </div>
      )}

      <input type="text" placeholder="Search by item code or justification..."
        value={search} onChange={e => { setSearch(e.target.value); setPage(0); }}
        className="search-input" />

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