import React, { useState, useEffect } from 'react';
import api, { getAnomalies } from '../api';

/**
 * PENDING P1 (In Progress) — AiEnginesPage.js
 *
 * Full anomaly detection UI. AiEnginesPage.js existed as a stub;
 * this provides the complete implementation.
 *
 * Fetches from: GET /api/ai/anomalies
 * Displays:
 *   - Summary cards (total anomalies, critical, warnings, by category)
 *   - Filterable anomaly table (severity, category, item code search)
 *   - Detail modal with confidence score bar, affected items list
 *   - Auto-refresh every 60 seconds
 */
export default function AiEnginesPage() {
  const [anomalies, setAnomalies]           = useState([]);
  const [loading, setLoading]               = useState(true);
  const [error, setError]                   = useState(null);
  const [selected, setSelected]             = useState(null);
  const [filterSeverity, setFilterSeverity] = useState('ALL');
  const [filterCategory, setFilterCategory] = useState('ALL');
  const [searchTerm, setSearchTerm]         = useState('');
  const [lastRefresh, setLastRefresh]       = useState(null);

  const fetchAnomalies = async () => {
    try {
      setError(null);
      const res = await getAnomalies();
      setAnomalies(res.data?.anomalies ?? res.data ?? []);
      setLastRefresh(new Date());
    } catch (err) {
      setError('Failed to load anomaly data. Ensure backend is running.');
      console.error('AI anomalies fetch error:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAnomalies();
    const interval = setInterval(fetchAnomalies, 60000); // auto-refresh
    return () => clearInterval(interval);
  }, []);

  // ── Derived stats ──────────────────────────────────────────────────────────
  const critical = anomalies.filter(a => a.severity === 'CRITICAL').length;
  const warnings = anomalies.filter(a => a.severity === 'WARNING').length;
  const categories = [...new Set(anomalies.map(a => a.category).filter(Boolean))];

  // ── Filtered list ──────────────────────────────────────────────────────────
  const filtered = anomalies.filter(a => {
    const matchSev = filterSeverity === 'ALL' || a.severity === filterSeverity;
    const matchCat = filterCategory === 'ALL' || a.category === filterCategory;
    const matchSearch = !searchTerm
      || (a.itemCode || '').toLowerCase().includes(searchTerm.toLowerCase())
      || (a.description || '').toLowerCase().includes(searchTerm.toLowerCase());
    return matchSev && matchCat && matchSearch;
  });

  // ── Severity badge color ───────────────────────────────────────────────────
  const sevStyle = (sev) => {
    if (sev === 'CRITICAL') return { background: '#fee2e2', color: '#991b1b', padding: '2px 10px', borderRadius: 20, fontSize: 12, fontWeight: 700 };
    if (sev === 'WARNING')  return { background: '#fff7ed', color: '#c2410c', padding: '2px 10px', borderRadius: 20, fontSize: 12, fontWeight: 700 };
    return { background: '#f0fdf4', color: '#166534', padding: '2px 10px', borderRadius: 20, fontSize: 12, fontWeight: 700 };
  };

  // ── Confidence bar color ───────────────────────────────────────────────────
  const confColor = (pct) => {
    if (pct >= 85) return '#e63946';
    if (pct >= 65) return '#f4a261';
    return '#2a9d8f';
  };

  if (loading) return <div style={styles.loading}>⏳ Loading AI Engine data...</div>;

  return (
    <div style={styles.page}>
      {/* ── Page Header ── */}
      <div style={styles.header}>
        <div>
          <h1 style={styles.title}>🤖 AI Engine — Anomaly Detection</h1>
          <p style={styles.subtitle}>
            DistilBERT-powered anomaly detection across cost, supplier, and BOM data.
            {lastRefresh && <span style={styles.refreshNote}> Last updated: {lastRefresh.toLocaleTimeString()}</span>}
          </p>
        </div>
        <button onClick={fetchAnomalies} style={styles.refreshBtn}>↻ Refresh</button>
      </div>

      {error && <div style={styles.errorBanner}>{error}</div>}

      {/* ── Summary Cards ── */}
      <div style={styles.statsRow}>
        <StatCard label="Total Anomalies" value={anomalies.length} color="#1d3557" />
        <StatCard label="Critical"        value={critical}         color="#e63946" />
        <StatCard label="Warnings"        value={warnings}         color="#f4a261" />
        <StatCard label="Categories"      value={categories.length} color="#2a9d8f" />
      </div>

      {/* ── Filters ── */}
      <div style={styles.filtersRow}>
        <input
          style={styles.search}
          placeholder="Search by item code or description..."
          value={searchTerm}
          onChange={e => setSearchTerm(e.target.value)}
        />
        <select style={styles.select} value={filterSeverity} onChange={e => setFilterSeverity(e.target.value)}>
          <option value="ALL">All Severities</option>
          <option value="CRITICAL">Critical</option>
          <option value="WARNING">Warning</option>
          <option value="INFO">Info</option>
        </select>
        <select style={styles.select} value={filterCategory} onChange={e => setFilterCategory(e.target.value)}>
          <option value="ALL">All Categories</option>
          {categories.map(c => <option key={c} value={c}>{c}</option>)}
        </select>
        <span style={styles.resultCount}>{filtered.length} results</span>
      </div>

      {/* ── Anomaly Cards Grid ── */}
      {filtered.length === 0 ? (
        <div style={styles.empty}>
          {anomalies.length === 0
            ? '✅ No anomalies detected. All systems normal.'
            : '🔍 No anomalies match your filters.'}
        </div>
      ) : (
        <div style={styles.grid}>
          {filtered.map((anomaly, idx) => (
            <div
              key={anomaly.id ?? idx}
              style={styles.card}
              onClick={() => setSelected(anomaly)}
            >
              <div style={styles.cardTop}>
                <span style={sevStyle(anomaly.severity)}>{anomaly.severity ?? 'INFO'}</span>
                <span style={styles.category}>{anomaly.category ?? 'General'}</span>
              </div>
              <div style={styles.cardItemCode}>{anomaly.itemCode ?? '—'}</div>
              <div style={styles.cardDesc}>{anomaly.description ?? 'No description available'}</div>

              {/* Confidence score bar */}
              {anomaly.confidenceScore != null && (
                <div style={styles.confWrap}>
                  <div style={styles.confLabel}>
                    Confidence: <strong>{anomaly.confidenceScore}%</strong>
                  </div>
                  <div style={styles.confBarBg}>
                    <div style={{
                      ...styles.confBarFill,
                      width: `${anomaly.confidenceScore}%`,
                      background: confColor(anomaly.confidenceScore)
                    }} />
                  </div>
                </div>
              )}

              {anomaly.affectedItems?.length > 0 && (
                <div style={styles.affectedCount}>
                  🔗 {anomaly.affectedItems.length} affected item{anomaly.affectedItems.length > 1 ? 's' : ''}
                </div>
              )}
              <div style={styles.viewMore}>View details →</div>
            </div>
          ))}
        </div>
      )}

      {/* ── Detail Modal ── */}
      {selected && (
        <div style={styles.modalOverlay} onClick={() => setSelected(null)}>
          <div style={styles.modal} onClick={e => e.stopPropagation()}>
            <div style={styles.modalHeader}>
              <h2 style={styles.modalTitle}>Anomaly Detail</h2>
              <button style={styles.closeBtn} onClick={() => setSelected(null)}>✕</button>
            </div>

            <div style={styles.modalGrid}>
              <Detail label="Severity"  value={<span style={sevStyle(selected.severity)}>{selected.severity}</span>} />
              <Detail label="Category"  value={selected.category} />
              <Detail label="Item Code" value={selected.itemCode} />
              <Detail label="Detected"  value={selected.detectedAt ?? 'N/A'} />
            </div>

            <div style={styles.modalSection}>
              <strong>Description</strong>
              <p style={styles.modalText}>{selected.description}</p>
            </div>

            {selected.confidenceScore != null && (
              <div style={styles.modalSection}>
                <strong>Confidence Score: {selected.confidenceScore}%</strong>
                <div style={{ ...styles.confBarBg, marginTop: 8 }}>
                  <div style={{
                    ...styles.confBarFill,
                    width: `${selected.confidenceScore}%`,
                    background: confColor(selected.confidenceScore)
                  }} />
                </div>
                <p style={styles.confNote}>
                  {selected.confidenceScore >= 85
                    ? '⚠ High confidence — immediate review recommended'
                    : selected.confidenceScore >= 65
                    ? '⚡ Medium confidence — monitor closely'
                    : 'ℹ Low confidence — flagged for awareness'}
                </p>
              </div>
            )}

            {selected.affectedItems?.length > 0 && (
              <div style={styles.modalSection}>
                <strong>Affected Items ({selected.affectedItems.length})</strong>
                <ul style={styles.affectedList}>
                  {selected.affectedItems.map((item, i) => (
                    <li key={i} style={styles.affectedItem}>
                      <span style={styles.affectedCode}>{item.itemCode}</span>
                      <span style={styles.affectedName}>{item.itemName}</span>
                    </li>
                  ))}
                </ul>
              </div>
            )}

            {selected.recommendation && (
              <div style={{ ...styles.modalSection, background: '#f0fdfa', borderRadius: 8, padding: 12 }}>
                <strong>💡 Recommendation</strong>
                <p style={styles.modalText}>{selected.recommendation}</p>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}

// ── Sub-components ─────────────────────────────────────────────────────────────
function StatCard({ label, value, color }) {
  return (
    <div style={{ ...styles.statCard, borderTopColor: color }}>
      <div style={{ ...styles.statValue, color }}>{value}</div>
      <div style={styles.statLabel}>{label}</div>
    </div>
  );
}

function Detail({ label, value }) {
  return (
    <div style={styles.detailItem}>
      <div style={styles.detailLabel}>{label}</div>
      <div style={styles.detailValue}>{value ?? '—'}</div>
    </div>
  );
}

// ── Styles ─────────────────────────────────────────────────────────────────────
const styles = {
  page:        { padding: '32px', fontFamily: 'sans-serif', background: '#f8f6f1', minHeight: '100vh' },
  loading:     { padding: 60, textAlign: 'center', fontSize: 18, color: '#6b7280' },
  header:      { display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 24 },
  title:       { fontSize: 28, fontWeight: 800, color: '#16213e', margin: '0 0 4px' },
  subtitle:    { fontSize: 14, color: '#6b7280', margin: 0 },
  refreshNote: { color: '#9ca3af', marginLeft: 8 },
  refreshBtn:  { background: '#1d3557', color: '#fff', border: 'none', borderRadius: 8, padding: '10px 20px', cursor: 'pointer', fontWeight: 700 },
  errorBanner: { background: '#fee2e2', color: '#991b1b', padding: 12, borderRadius: 8, marginBottom: 20 },
  statsRow:    { display: 'flex', gap: 16, marginBottom: 24, flexWrap: 'wrap' },
  statCard:    { flex: 1, minWidth: 120, background: '#fff', border: '1px solid #e5e0d5', borderTop: '4px solid', borderRadius: 12, padding: '16px 20px' },
  statValue:   { fontSize: 36, fontWeight: 800, lineHeight: 1 },
  statLabel:   { fontSize: 11, color: '#6b7280', marginTop: 4, fontWeight: 600, textTransform: 'uppercase', letterSpacing: 0.5 },
  filtersRow:  { display: 'flex', gap: 12, marginBottom: 20, alignItems: 'center', flexWrap: 'wrap' },
  search:      { flex: 2, padding: '9px 14px', border: '1px solid #e5e0d5', borderRadius: 8, fontSize: 14, minWidth: 200 },
  select:      { padding: '9px 14px', border: '1px solid #e5e0d5', borderRadius: 8, fontSize: 14, background: '#fff' },
  resultCount: { color: '#6b7280', fontSize: 13, marginLeft: 'auto' },
  empty:       { textAlign: 'center', padding: 60, color: '#6b7280', fontSize: 16, background: '#fff', borderRadius: 12, border: '1px solid #e5e0d5' },
  grid:        { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: 16 },
  card:        { background: '#fff', border: '1px solid #e5e0d5', borderRadius: 12, padding: 20, cursor: 'pointer', transition: 'box-shadow 0.15s' },
  cardTop:     { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 10 },
  category:    { fontSize: 11, color: '#6b7280', fontWeight: 600, textTransform: 'uppercase', letterSpacing: 0.5 },
  cardItemCode:{ fontSize: 17, fontWeight: 700, color: '#16213e', marginBottom: 4 },
  cardDesc:    { fontSize: 13, color: '#374151', lineHeight: 1.5, marginBottom: 12 },
  confWrap:    { marginBottom: 10 },
  confLabel:   { fontSize: 12, color: '#6b7280', marginBottom: 4 },
  confBarBg:   { height: 6, background: '#e5e7eb', borderRadius: 3, overflow: 'hidden' },
  confBarFill: { height: '100%', borderRadius: 3, transition: 'width 0.3s' },
  affectedCount:{ fontSize: 12, color: '#6b7280', marginTop: 8 },
  viewMore:    { fontSize: 12, color: '#1d3557', fontWeight: 600, marginTop: 10, textAlign: 'right' },
  modalOverlay:{ position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 },
  modal:       { background: '#fff', borderRadius: 16, padding: 32, width: '90%', maxWidth: 600, maxHeight: '85vh', overflowY: 'auto' },
  modalHeader: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 },
  modalTitle:  { fontSize: 20, fontWeight: 800, color: '#16213e', margin: 0 },
  closeBtn:    { background: 'none', border: 'none', fontSize: 20, cursor: 'pointer', color: '#6b7280' },
  modalGrid:   { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16, marginBottom: 20 },
  detailItem:  { },
  detailLabel: { fontSize: 11, color: '#6b7280', fontWeight: 600, textTransform: 'uppercase', letterSpacing: 0.5, marginBottom: 2 },
  detailValue: { fontSize: 14, color: '#16213e', fontWeight: 600 },
  modalSection:{ marginBottom: 20 },
  modalText:   { fontSize: 14, color: '#374151', marginTop: 6, lineHeight: 1.6 },
  confNote:    { fontSize: 12, color: '#6b7280', marginTop: 6 },
  affectedList:{ listStyle: 'none', padding: 0, margin: '8px 0 0' },
  affectedItem:{ display: 'flex', gap: 12, padding: '6px 0', borderBottom: '1px solid #f3f4f6' },
  affectedCode:{ fontFamily: 'monospace', fontSize: 13, color: '#1d3557', fontWeight: 600, minWidth: 100 },
  affectedName:{ fontSize: 13, color: '#6b7280' },
};
