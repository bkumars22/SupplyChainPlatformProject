/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import api from '../api';

const IS_DEMO = process.env.REACT_APP_DEMO_MODE === 'true';

const DEMO_DATA = {
  'SUP-001': {
    scorecard:  { supplierId:'SUP-001', supplierName:'TechParts Ltd', country:'China',   compositeScore:42, otdScore:38, qualityScore:55, responsivenessScore:60, tier:'PROBATION',   totalDeliveries:28, onTimeDeliveries:11 },
    deliveries: [
      { actualDate:'2026-06-01', promisedDate:'2026-05-28', status:'LATE', delayDays:4 },
      { actualDate:'2026-05-15', promisedDate:'2026-05-12', status:'LATE', delayDays:3 },
      { actualDate:'2026-05-02', promisedDate:'2026-04-28', status:'LATE', delayDays:4 },
      { actualDate:'2026-04-18', promisedDate:'2026-04-17', status:'ON_TIME', delayDays:0 },
      { actualDate:'2026-04-05', promisedDate:'2026-04-01', status:'LATE', delayDays:4 },
    ],
  },
  'SUP-002': {
    scorecard:  { supplierId:'SUP-002', supplierName:'GlobalParts Inc', country:'India', compositeScore:51, otdScore:62, qualityScore:35, responsivenessScore:70, tier:'CONDITIONAL', totalDeliveries:34, onTimeDeliveries:21 },
    deliveries: [
      { actualDate:'2026-06-02', promisedDate:'2026-05-30', status:'LATE', delayDays:3 },
      { actualDate:'2026-05-20', promisedDate:'2026-05-19', status:'ON_TIME', delayDays:0 },
      { actualDate:'2026-05-08', promisedDate:'2026-05-05', status:'LATE', delayDays:3 },
      { actualDate:'2026-04-22', promisedDate:'2026-04-20', status:'LATE', delayDays:2 },
      { actualDate:'2026-04-10', promisedDate:'2026-04-10', status:'ON_TIME', delayDays:0 },
    ],
  },
  'SUP-003': {
    scorecard:  { supplierId:'SUP-003', supplierName:'FastShip Co', country:'Vietnam',  compositeScore:58, otdScore:55, qualityScore:65, responsivenessScore:50, tier:'CONDITIONAL', totalDeliveries:22, onTimeDeliveries:12 },
    deliveries: [
      { actualDate:'2026-06-03', promisedDate:'2026-06-01', status:'LATE', delayDays:2 },
      { actualDate:'2026-05-22', promisedDate:'2026-05-20', status:'LATE', delayDays:2 },
      { actualDate:'2026-05-10', promisedDate:'2026-05-10', status:'ON_TIME', delayDays:0 },
      { actualDate:'2026-04-28', promisedDate:'2026-04-25', status:'LATE', delayDays:3 },
      { actualDate:'2026-04-15', promisedDate:'2026-04-14', status:'ON_TIME', delayDays:0 },
    ],
  },
  'SUP-004': {
    scorecard:  { supplierId:'SUP-004', supplierName:'Prime Supplies', country:'Germany', compositeScore:82, otdScore:88, qualityScore:79, responsivenessScore:85, tier:'APPROVED',   totalDeliveries:45, onTimeDeliveries:40 },
    deliveries: [
      { actualDate:'2026-06-04', promisedDate:'2026-06-04', status:'ON_TIME', delayDays:0 },
      { actualDate:'2026-05-21', promisedDate:'2026-05-21', status:'ON_TIME', delayDays:0 },
      { actualDate:'2026-05-09', promisedDate:'2026-05-08', status:'LATE', delayDays:1 },
      { actualDate:'2026-04-26', promisedDate:'2026-04-26', status:'ON_TIME', delayDays:0 },
      { actualDate:'2026-04-14', promisedDate:'2026-04-14', status:'ON_TIME', delayDays:0 },
    ],
  },
  'SUP-005': {
    scorecard:  { supplierId:'SUP-005', supplierName:'Apex Logistics', country:'USA',    compositeScore:91, otdScore:95, qualityScore:89, responsivenessScore:92, tier:'PREFERRED',  totalDeliveries:62, onTimeDeliveries:59 },
    deliveries: [],
  },
  'SUP-006': {
    scorecard:  { supplierId:'SUP-006', supplierName:'Horizon Goods', country:'Mexico', compositeScore:76, otdScore:80, qualityScore:74, responsivenessScore:77, tier:'APPROVED',   totalDeliveries:31, onTimeDeliveries:25 },
    deliveries: [],
  },
};

const TIERS = {
  attention: { label: 'Needs attention',  dot: '#dc2626', bg: '#fef2f2', border: '#fecaca', text: '#991b1b' },
  checkin:   { label: 'Needs a check-in', dot: '#d97706', bg: '#fffbeb', border: '#fde68a', text: '#92400e' },
  healthy:   { label: 'Healthy',          dot: '#16a34a', bg: '#f0fdf4', border: '#bbf7d0', text: '#14532d' },
};

function riskTier(s) {
  const score = Math.max(0, 1 - (s.compositeScore / 100));
  if (score > 0.75) return 'attention';
  if (score >= 0.5) return 'checkin';
  return 'healthy';
}

// Build a plain-English narrative from scorecard + recent deliveries.
// No AI endpoint exposes a per-supplier explanation, so we derive it here.
function buildNarrative(scorecard, deliveries) {
  const recent = [...deliveries]
    .sort((a, b) => (b.actualDate || b.promisedDate || '').localeCompare(a.actualDate || a.promisedDate || ''))
    .slice(0, 5);

  const recentLate = recent.filter(d => d.status !== 'ON_TIME' && d.delayDays > 0);
  const lateDays   = recentLate.length > 0
    ? Math.round(recentLate.reduce((sum, d) => sum + (d.delayDays || 0), 0) / recentLate.length)
    : 0;

  const lines = [];

  // Delivery timing paragraph
  if (scorecard.otdScore < 50) {
    lines.push(
      `Only ${scorecard.otdScore.toFixed(0)}% of shipments from this supplier have arrived on time — that's well below a healthy level. ` +
      (recentLate.length > 0
        ? `Looking at the ${recent.length} most recent deliveries, ${recentLate.length} were late by an average of ${lateDays} day${lateDays !== 1 ? 's' : ''}.`
        : 'No recent on-time deliveries were recorded.')
    );
  } else if (scorecard.otdScore < 70) {
    lines.push(
      `${scorecard.otdScore.toFixed(0)}% of shipments have arrived on time, which is below the 70% threshold. ` +
      (recentLate.length > 0
        ? `In the last ${recent.length} deliveries, ${recentLate.length} were late — typically by around ${lateDays} day${lateDays !== 1 ? 's' : ''}.`
        : 'Recent deliveries show some late arrivals.')
    );
  } else {
    lines.push(
      `Delivery timing is solid — ${scorecard.otdScore.toFixed(0)}% of shipments have arrived on time.`
    );
  }

  // Quality paragraph
  if (scorecard.qualityScore < 60) {
    lines.push(
      `Quality is also a concern: the quality score is ${scorecard.qualityScore.toFixed(0)}/100. ` +
      'You may want to ask about their quality control process.'
    );
  } else if (scorecard.qualityScore < 80) {
    lines.push(`Quality is moderate at ${scorecard.qualityScore.toFixed(0)}/100 — worth keeping an eye on.`);
  }

  // Responsiveness note (only if notably low)
  if (scorecard.responsivenessScore < 50) {
    lines.push('Response times from this supplier have also been slower than average.');
  }

  return lines;
}

const ACTION_ITEMS = [
  { id: 'contact',  label: 'Contact supplier about the issue' },
  { id: 'backup',   label: 'Line up a backup supplier just in case' },
  { id: 'resolved', label: 'Mark as resolved once sorted' },
];

function InfoCard({ label, value, sub }) {
  return (
    <div style={{
      background: '#fff', border: '1px solid #e2e8f0', borderRadius: 10,
      padding: '14px 18px', textAlign: 'center',
    }}>
      <div style={{ fontSize: 24, fontWeight: 800, color: '#1e293b' }}>{value}</div>
      <div style={{ fontSize: 11, color: '#64748b', textTransform: 'uppercase', letterSpacing: '0.4px', marginTop: 4 }}>{label}</div>
      {sub && <div style={{ fontSize: 12, color: '#94a3b8', marginTop: 4 }}>{sub}</div>}
    </div>
  );
}

export default function SimpleSupplierDetailPage() {
  const { supplierId } = useParams();
  const navigate       = useNavigate();

  const [scorecard,  setScorecard]  = useState(null);
  const [deliveries, setDeliveries] = useState([]);
  const [loading,    setLoading]    = useState(true);
  const [error,      setError]      = useState(null);
  const [checked,    setChecked]    = useState({});

  useEffect(() => {
    if (IS_DEMO) {
      const demo = DEMO_DATA[supplierId];
      if (demo) {
        setScorecard(demo.scorecard);
        setDeliveries(demo.deliveries);
      } else {
        setError('Supplier not found in demo data.');
      }
      setLoading(false);
      return;
    }
    Promise.all([
      api.get('/api/suppliers/' + supplierId),
      api.get('/api/suppliers/' + supplierId + '/deliveries'),
    ])
      .then(([scRes, delRes]) => {
        setScorecard(scRes.data.data);
        setDeliveries(delRes.data.data || []);
      })
      .catch(err => setError(err.message))
      .finally(() => setLoading(false));
  }, [supplierId]);

  const toggle = (id) => setChecked(prev => ({ ...prev, [id]: !prev[id] }));

  if (loading) return <div className="spinner" />;

  if (error || !scorecard) {
    return (
      <div className="page-container">
        <button onClick={() => navigate('/simple-dashboard')} className="btn-back">← Back</button>
        <div style={{ marginTop: 20, background: '#fef2f2', border: '1px solid #fecaca', borderRadius: 10, padding: 20, color: '#dc2626', fontSize: 14 }}>
          {error ? 'Could not load supplier — ' + error : 'Supplier not found.'}
        </div>
      </div>
    );
  }

  const tier      = riskTier(scorecard);
  const t         = TIERS[tier];
  const narrative = buildNarrative(scorecard, deliveries);
  const lateCount = deliveries.filter(d => d.status !== 'ON_TIME' && d.delayDays > 0).length;

  return (
    <div className="page-container">

      {/* ── Header ───────────────────────────────────────── */}
      <div className="page-header" style={{ alignItems: 'flex-start', flexWrap: 'wrap', gap: 12 }}>
        <button onClick={() => navigate('/simple-dashboard')} className="btn-back">← Simple Dashboard</button>
        <div style={{ flex: 1 }}>
          <h1 style={{ margin: 0 }}>{scorecard.supplierName}</h1>
          <p style={{ margin: '4px 0 0', fontSize: 13, color: '#64748b' }}>
            {scorecard.country} · {scorecard.supplierId}
          </p>
        </div>
        <span style={{
          display: 'inline-flex', alignItems: 'center', gap: 6,
          fontSize: 13, fontWeight: 600, color: t.dot,
          background: t.bg, border: '1px solid ' + t.border,
          padding: '6px 14px', borderRadius: 20, whiteSpace: 'nowrap',
        }}>
          <span style={{ width: 8, height: 8, borderRadius: '50%', background: t.dot, display: 'inline-block' }} />
          {t.label}
        </span>
      </div>

      {/* ── Quick stats ──────────────────────────────────── */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(130px, 1fr))', gap: 10, marginBottom: 20 }}>
        <InfoCard label="Orders tracked"  value={scorecard.totalDeliveries} />
        <InfoCard label="On time"         value={scorecard.onTimeDeliveries} sub={scorecard.otdScore.toFixed(0) + '% rate'} />
        <InfoCard label="Late shipments"  value={lateCount} />
        <InfoCard label="Supplier tier"   value={scorecard.tier} />
      </div>

      {/* ── What changed ─────────────────────────────────── */}
      <div style={{
        background: t.bg, border: '1px solid ' + t.border,
        borderRadius: 12, padding: '18px 22px', marginBottom: 20,
      }}>
        <h3 style={{ margin: '0 0 12px', fontSize: 15, fontWeight: 700, color: t.text }}>
          What's going on
        </h3>
        {narrative.map((para, i) => (
          <p key={i} style={{ margin: i > 0 ? '10px 0 0' : 0, fontSize: 14, color: '#374151', lineHeight: 1.65 }}>
            {para}
          </p>
        ))}
        {narrative.length === 0 && (
          <p style={{ margin: 0, fontSize: 14, color: '#374151' }}>
            This supplier is performing well — no issues to highlight right now.
          </p>
        )}
      </div>

      {/* ── Action checklist ─────────────────────────────── */}
      <div style={{
        background: '#fff', border: '1px solid #e2e8f0',
        borderRadius: 12, padding: '18px 22px', marginBottom: 20,
      }}>
        <h3 style={{ margin: '0 0 14px', fontSize: 15, fontWeight: 700, color: '#1e293b' }}>
          Suggested next steps
        </h3>
        <p style={{ margin: '0 0 14px', fontSize: 13, color: '#64748b' }}>
          Check these off as you work through them — they're saved here while you're on this page.
        </p>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
          {ACTION_ITEMS.map(item => (
            <label
              key={item.id}
              style={{
                display: 'flex', alignItems: 'center', gap: 12, cursor: 'pointer',
                padding: '10px 14px', borderRadius: 8,
                background: checked[item.id] ? '#f0fdf4' : '#f8fafc',
                border: '1px solid ' + (checked[item.id] ? '#bbf7d0' : '#e2e8f0'),
                transition: 'background 0.15s',
              }}
            >
              <input
                type="checkbox"
                checked={!!checked[item.id]}
                onChange={() => toggle(item.id)}
                style={{ width: 16, height: 16, cursor: 'pointer', accentColor: '#16a34a', flexShrink: 0 }}
              />
              <span style={{
                fontSize: 14, color: checked[item.id] ? '#16a34a' : '#374151',
                textDecoration: checked[item.id] ? 'line-through' : 'none',
                fontWeight: checked[item.id] ? 500 : 400,
              }}>
                {item.label}
              </span>
            </label>
          ))}
        </div>
      </div>

      {/* ── Link to technical view ───────────────────────── */}
      <div style={{
        background: '#f8fafc', border: '1px solid #e2e8f0',
        borderRadius: 10, padding: '14px 20px',
        display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: 10,
      }}>
        <div>
          <div style={{ fontWeight: 600, fontSize: 14, color: '#1e293b' }}>Want the full picture?</div>
          <div style={{ fontSize: 13, color: '#64748b', marginTop: 2 }}>
            See delivery history, score breakdowns, and quality ratings.
          </div>
        </div>
        <Link
          to={'/suppliers/' + scorecard.supplierId}
          style={{
            display: 'inline-block', padding: '8px 18px', borderRadius: 8,
            background: '#1e293b', color: '#fff', fontSize: 13, fontWeight: 600,
            textDecoration: 'none', whiteSpace: 'nowrap',
          }}
        >
          View detailed breakdown →
        </Link>
      </div>

    </div>
  );
}
