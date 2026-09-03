/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';

const IS_DEMO = process.env.REACT_APP_DEMO_MODE === 'true';

const DEMO_SUPPLIERS = [
  { supplierId: 'SUP-001', supplierName: 'TechParts Ltd', country: 'China',   atRisk: true,  compositeScore: 42, otdScore: 38, qualityScore: 55, responsivenessScore: 60 },
  { supplierId: 'SUP-002', supplierName: 'GlobalParts Inc', country: 'India', atRisk: true,  compositeScore: 45, otdScore: 62, qualityScore: 35, responsivenessScore: 70 },
  { supplierId: 'SUP-003', supplierName: 'FastShip Co', country: 'Vietnam',  atRisk: true,  compositeScore: 48, otdScore: 55, qualityScore: 65, responsivenessScore: 50 },
  { supplierId: 'SUP-004', supplierName: 'Prime Supplies', country: 'Germany', atRisk: false, compositeScore: 82, otdScore: 88, qualityScore: 79, responsivenessScore: 85 },
  { supplierId: 'SUP-005', supplierName: 'Apex Logistics', country: 'USA',    atRisk: false, compositeScore: 91, otdScore: 95, qualityScore: 89, responsivenessScore: 92 },
  { supplierId: 'SUP-006', supplierName: 'Horizon Goods', country: 'Mexico', atRisk: false, compositeScore: 76, otdScore: 80, qualityScore: 74, responsivenessScore: 77 },
];

// Risk tier derived from composite score (0–100 → 0–1 risk scale).
// Thresholds per product spec: healthy <0.5, check-in 0.5–0.75, attention >0.75.
const TIERS = {
  attention: { label: 'Needs attention',  dot: '#dc2626', bg: '#fef2f2', border: '#fecaca' },
  checkin:   { label: 'Needs a check-in', dot: '#d97706', bg: '#fffbeb', border: '#fde68a' },
  healthy:   { label: 'Healthy',          dot: '#16a34a', bg: '#f0fdf4', border: '#bbf7d0' },
};

// Only called for suppliers already flagged atRisk (otdScore < 70), so this
// must never resolve to 'healthy' -- that would contradict the section
// these cards are rendered in. compositeScore only picks the severity
// between the two at-risk tiers.
function riskTier(s) {
  const score = Math.max(0, 1 - (s.compositeScore / 100));
  return score > 0.75 ? 'attention' : 'checkin';
}

function plainSummary(s) {
  if (s.otdScore < 50)
    return `Deliveries have been arriving late — only ${s.otdScore.toFixed(0)}% came on time.`;
  if (s.otdScore < 70)
    return `Delivery timing needs a look — ${s.otdScore.toFixed(0)}% on-time rate this period.`;
  if (s.qualityScore < 60)
    return `Quality has been inconsistent — quality score is ${s.qualityScore.toFixed(0)}/100.`;
  return `Performance is below average across multiple areas.`;
}

function StatusDot({ tier }) {
  return (
    <span style={{
      display: 'inline-block', width: 10, height: 10, flexShrink: 0,
      borderRadius: '50%', background: TIERS[tier].dot,
    }} />
  );
}

function StatCard({ label, value, color }) {
  return (
    <div style={{
      background: '#fff', border: '1px solid #e2e8f0', borderRadius: 10,
      padding: '16px 20px', borderTop: '3px solid ' + color,
    }}>
      <div style={{ fontSize: 32, fontWeight: 800, color: '#1e293b' }}>{value}</div>
      <div style={{ fontSize: 11, color: '#64748b', textTransform: 'uppercase', letterSpacing: '0.5px', marginTop: 4 }}>
        {label}
      </div>
    </div>
  );
}

export default function SimpleDashboardPage() {
  const [suppliers, setSuppliers]             = useState([]);
  const [loading, setLoading]                 = useState(true);
  const [error, setError]                     = useState(null);
  const [healthyExpanded, setHealthyExpanded] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (IS_DEMO) {
      setSuppliers(DEMO_SUPPLIERS);
      setLoading(false);
      return;
    }
    api.get('/api/suppliers')
      .then(res => setSuppliers(res.data.data || []))
      .catch(err => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="spinner" />;

  if (error) {
    return (
      <div className="page-container">
        <div style={{ background: '#fef2f2', border: '1px solid #fecaca', borderRadius: 10, padding: 20, color: '#dc2626', fontSize: 14 }}>
          Could not load supplier data — {error}
        </div>
      </div>
    );
  }

  const atRisk  = suppliers.filter(s => s.atRisk);
  const healthy = suppliers.filter(s => !s.atRisk);

  const headingText = atRisk.length === 0
    ? 'All suppliers are on track'
    : `${atRisk.length} supplier${atRisk.length > 1 ? 's' : ''} need${atRisk.length === 1 ? 's' : ''} your attention`;

  return (
    <div className="page-container">

      {/* ── Header ───────────────────────────────────────── */}
      <div className="page-header">
        <div>
          <h1 style={{ margin: 0 }}>{headingText}</h1>
          <p style={{ margin: '4px 0 0', color: '#64748b', fontSize: 14 }}>
            Plain-English supplier overview — no technical details
          </p>
        </div>
      </div>

      {/* ── Stat cards ───────────────────────────────────── */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 12, marginBottom: 28 }}>
        <StatCard label="Suppliers tracked" value={suppliers.length} color="#a9790f" />
        <StatCard label="Need attention"    value={atRisk.length}   color={atRisk.length > 0 ? '#dc2626' : '#16a34a'} />
        <StatCard label="Healthy"           value={healthy.length}  color="#16a34a" />
      </div>

      {/* ── At-risk cards ────────────────────────────────── */}
      {atRisk.length === 0 ? (
        <div style={{
          background: '#f0fdf4', border: '1px solid #bbf7d0', borderRadius: 10,
          padding: 24, textAlign: 'center', color: '#15803d', fontWeight: 600, fontSize: 15,
          marginBottom: 20,
        }}>
          All your suppliers are performing well. Nothing to review right now.
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12, marginBottom: 24 }}>
          {atRisk.map(s => {
            const tier = riskTier(s);
            const t    = TIERS[tier];
            return (
              <div
                key={s.supplierId}
                data-testid="at-risk-card"
                onClick={() => navigate('/simple-dashboard/' + s.supplierId)}
                style={{
                  background: t.bg, border: '1px solid ' + t.border,
                  borderRadius: 12, padding: '16px 20px', cursor: 'pointer',
                  display: 'flex', alignItems: 'flex-start', gap: 14,
                }}
              >
                <div style={{ paddingTop: 4 }}>
                  <StatusDot tier={tier} />
                </div>
                <div style={{ flex: 1 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 4 }}>
                    <span style={{ fontWeight: 700, fontSize: 15, color: '#1e293b' }}>{s.supplierName}</span>
                    <span style={{ fontSize: 12, color: t.dot, fontWeight: 600 }}>{t.label}</span>
                  </div>
                  <p style={{ margin: '6px 0 0', fontSize: 13, color: '#475569', lineHeight: 1.5 }}>
                    {plainSummary(s)}
                  </p>
                  <p style={{ margin: '6px 0 0', fontSize: 11, color: '#94a3b8' }}>
                    {s.country} · {s.supplierId} · Tap for details →
                  </p>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* ── Healthy suppliers collapsed ───────────────────── */}
      {healthy.length > 0 && (
        <div>
          <button
            onClick={() => setHealthyExpanded(e => !e)}
            style={{
              display: 'flex', alignItems: 'center', gap: 8, width: '100%',
              background: '#f0fdf4', border: '1px solid #bbf7d0',
              borderRadius: healthyExpanded ? '8px 8px 0 0' : 8,
              padding: '10px 16px', cursor: 'pointer',
              fontSize: 13, color: '#15803d', fontWeight: 600,
            }}
          >
            <StatusDot tier="healthy" />
            {healthy.length} healthy supplier{healthy.length > 1 ? 's' : ''} — all on track
            <span style={{ marginLeft: 'auto', fontSize: 11, color: '#94a3b8' }}>
              {healthyExpanded ? '▲ Hide' : '▼ Show'}
            </span>
          </button>

          {healthyExpanded && (
            <div style={{
              border: '1px solid #bbf7d0', borderTop: 'none',
              borderRadius: '0 0 8px 8px', overflow: 'hidden',
            }}>
              {healthy.map((s, i) => (
                <div
                  key={s.supplierId}
                  onClick={() => navigate('/simple-dashboard/' + s.supplierId)}
                  style={{
                    display: 'flex', alignItems: 'center', gap: 10,
                    padding: '12px 16px', cursor: 'pointer',
                    background: i % 2 === 0 ? '#fff' : '#f9fafb',
                    borderTop: i > 0 ? '1px solid #f1f5f9' : 'none',
                  }}
                >
                  <StatusDot tier="healthy" />
                  <span style={{ fontWeight: 600, fontSize: 14, color: '#1e293b' }}>{s.supplierName}</span>
                  <span style={{ fontSize: 12, color: '#94a3b8' }}>{s.country}</span>
                  <span style={{ marginLeft: 'auto', fontSize: 12, fontWeight: 700, color: '#16a34a' }}>
                    {s.compositeScore?.toFixed(0)}/100
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

    </div>
  );
}
