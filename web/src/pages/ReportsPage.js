import React, { useState, useEffect } from 'react';
import { getSupplierPerfReport, getCostVarianceReport, getAlertSummaryReport, getActivityReport } from '../api';

const TABS = [
  { id: 'supplier', label: 'Supplier Performance' },
  { id: 'cost',     label: 'Cost Variance' },
  { id: 'alert',    label: 'Alert Summary' },
  { id: 'activity', label: 'User Activity' },
];

// ── Simple SVG line chart ────────────────────────────────────────────────────
function SparkLine({ values, color, width = 200, height = 50 }) {
  if (!values || values.length < 2) return null;
  const max = Math.max(...values), min = Math.min(...values);
  const range = max - min || 1;
  const pts = values.map((v, i) => {
    const x = (i / (values.length - 1)) * width;
    const y = height - ((v - min) / range) * (height - 6) - 3;
    return `${x},${y}`;
  }).join(' ');
  return (
    <svg width={width} height={height} style={{ display: 'block' }}>
      <polyline points={pts} fill="none" stroke={color} strokeWidth="2" strokeLinejoin="round" />
      <circle cx={pts.split(' ').at(-1).split(',')[0]} cy={pts.split(' ').at(-1).split(',')[1]} r="3" fill={color} />
    </svg>
  );
}

// ── Donut chart ───────────────────────────────────────────────────────────────
function DonutChart({ slices, size = 140 }) {
  const total = slices.reduce((a, s) => a + s.count, 0);
  let angle = -90;
  const cx = size / 2, cy = size / 2, r = size * 0.38, inner = size * 0.24;
  const paths = slices.map(s => {
    const sweep = (s.count / total) * 360;
    const start = angle; angle += sweep;
    const toRad = d => (d * Math.PI) / 180;
    const x1 = cx + r * Math.cos(toRad(start)), y1 = cy + r * Math.sin(toRad(start));
    const x2 = cx + r * Math.cos(toRad(start + sweep)), y2 = cy + r * Math.sin(toRad(start + sweep));
    const xi1 = cx + inner * Math.cos(toRad(start)), yi1 = cy + inner * Math.sin(toRad(start));
    const xi2 = cx + inner * Math.cos(toRad(start + sweep)), yi2 = cy + inner * Math.sin(toRad(start + sweep));
    const large = sweep > 180 ? 1 : 0;
    return { color: s.color, label: s.type, count: s.count, d: `M${x1},${y1} A${r},${r} 0 ${large},1 ${x2},${y2} L${xi2},${yi2} A${inner},${inner} 0 ${large},0 ${xi1},${yi1} Z` };
  });
  return (
    <svg width={size} height={size}>
      {paths.map((p, i) => <path key={i} d={p.d} fill={p.color} />)}
      <text x={cx} y={cy - 6} textAnchor="middle" fontSize="20" fontWeight="800" fill="#1e293b">{total}</text>
      <text x={cx} y={cy + 12} textAnchor="middle" fontSize="10" fill="#6b7280">total</text>
    </svg>
  );
}

// ── Bar row ───────────────────────────────────────────────────────────────────
function BarRow({ label, value, max, color, suffix = '%' }) {
  const pct = Math.round((value / max) * 100);
  return (
    <div style={{ marginBottom: 14 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 12, marginBottom: 5 }}>
        <span style={{ fontWeight: 600, color: '#374151' }}>{label}</span>
        <span style={{ color, fontWeight: 700 }}>{value}{suffix}</span>
      </div>
      <div style={{ background: '#f1f5f9', borderRadius: 4, height: 10, overflow: 'hidden' }}>
        <div style={{ width: pct + '%', height: '100%', background: color, borderRadius: 4, transition: 'width 0.6s ease' }} />
      </div>
    </div>
  );
}

// ── CSV export ────────────────────────────────────────────────────────────────
function exportCSV(rows, filename) {
  if (!rows || !rows.length) return;
  const keys = Object.keys(rows[0]);
  const csv = [keys.join(','), ...rows.map(r => keys.map(k => JSON.stringify(r[k] ?? '')).join(','))].join('\n');
  const blob = new Blob([csv], { type: 'text/csv' });
  const a = document.createElement('a'); a.href = URL.createObjectURL(blob); a.download = filename; a.click();
}

// ── Supplier Performance tab ──────────────────────────────────────────────────
function SupplierTab({ data }) {
  const [metric, setMetric] = useState('otd');
  if (!data) return <Spinner />;
  const metricLabel = { otd: 'On-Time Delivery %', quality: 'Quality Score %', composite: 'Composite Score' };
  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20, flexWrap: 'wrap', gap: 10 }}>
        <div style={{ display: 'flex', gap: 8 }}>
          {['otd', 'quality', 'composite'].map(m => (
            <button key={m} onClick={() => setMetric(m)}
              style={{ padding: '5px 12px', borderRadius: 20, border: '1px solid', fontSize: 12, fontWeight: 600, cursor: 'pointer',
                background: metric === m ? '#1d4ed8' : '#fff', color: metric === m ? '#fff' : '#374151', borderColor: metric === m ? '#1d4ed8' : '#e5e7eb' }}>
              {metricLabel[m]}
            </button>
          ))}
        </div>
        <div style={{ display: 'flex', gap: 8 }}>
          <button onClick={() => exportCSV(data.suppliers.map(s => ({ supplier: s.name, ...s })), 'supplier_performance.csv')}
            style={{ padding: '5px 14px', borderRadius: 7, border: '1px solid #e5e7eb', fontSize: 12, fontWeight: 600, cursor: 'pointer', background: '#fff' }}>
            Export CSV
          </button>
          <button onClick={() => window.print()}
            style={{ padding: '5px 14px', borderRadius: 7, border: '1px solid #e5e7eb', fontSize: 12, fontWeight: 600, cursor: 'pointer', background: '#fff' }}>
            Print / PDF
          </button>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 20 }}>
        {/* Bar chart */}
        <div style={{ background: '#fff', border: '1px solid #e5e7eb', borderRadius: 10, padding: 20 }}>
          <h3 style={{ margin: '0 0 18px', fontSize: 14, fontWeight: 700 }}>{metricLabel[metric]} — Current</h3>
          {data.suppliers.map(s => (
            <BarRow key={s.name} label={s.name} value={Math.round(s[metric])} max={100} color={s.color} />
          ))}
        </div>

        {/* Trend lines */}
        <div style={{ background: '#fff', border: '1px solid #e5e7eb', borderRadius: 10, padding: 20 }}>
          <h3 style={{ margin: '0 0 18px', fontSize: 14, fontWeight: 700 }}>6-Month Trend</h3>
          {data.suppliers.map(s => (
            <div key={s.name} style={{ marginBottom: 14 }}>
              <div style={{ fontSize: 11, fontWeight: 600, color: '#6b7280', marginBottom: 4 }}>{s.name}</div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                <SparkLine values={s[metric === 'otd' ? 'otdTrend' : metric === 'quality' ? 'qualityTrend' : 'compositeTrend']} color={s.color} width={180} height={40} />
                <span style={{ fontSize: 12, fontWeight: 700, color: s.color }}>{Math.round(s[metric])}%</span>
              </div>
            </div>
          ))}
          <div style={{ display: 'flex', gap: 8, marginTop: 8, fontSize: 10, color: '#9ca3af' }}>
            {data.months.map(m => <span key={m} style={{ flex: 1, textAlign: 'center' }}>{m}</span>)}
          </div>
        </div>
      </div>

      {/* Summary table */}
      <div style={{ background: '#fff', border: '1px solid #e5e7eb', borderRadius: 10, marginTop: 20, overflow: 'hidden' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 12 }}>
          <thead>
            <tr style={{ background: '#f8fafc' }}>
              {['Supplier', 'Country', 'OTD %', 'Quality %', 'Composite', 'Tier', 'Status'].map(h => (
                <th key={h} style={{ padding: '10px 14px', textAlign: 'left', fontWeight: 700, color: '#374151', borderBottom: '1px solid #e5e7eb' }}>{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {data.suppliers.map((s, i) => (
              <tr key={s.name} style={{ background: i % 2 === 0 ? '#fff' : '#f8fafc' }}>
                <td style={{ padding: '10px 14px', fontWeight: 600 }}>{s.name}</td>
                <td style={{ padding: '10px 14px', color: '#6b7280' }}>{s.country}</td>
                <td style={{ padding: '10px 14px', fontWeight: 700, color: s.otd < 50 ? '#dc2626' : s.otd < 75 ? '#d97706' : '#16a34a' }}>{s.otd}%</td>
                <td style={{ padding: '10px 14px', fontWeight: 700, color: s.quality < 50 ? '#dc2626' : s.quality < 75 ? '#d97706' : '#16a34a' }}>{s.quality}%</td>
                <td style={{ padding: '10px 14px' }}>{s.composite}</td>
                <td style={{ padding: '10px 14px' }}>{s.tier}</td>
                <td style={{ padding: '10px 14px' }}>
                  <span style={{ padding: '2px 8px', borderRadius: 12, fontSize: 11, fontWeight: 700, background: s.atRisk ? '#fef2f2' : '#f0fdf4', color: s.atRisk ? '#dc2626' : '#16a34a' }}>
                    {s.atRisk ? 'AT RISK' : 'HEALTHY'}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

// ── Cost Variance tab ─────────────────────────────────────────────────────────
function CostTab({ data }) {
  if (!data) return <Spinner />;
  const fmt = (n) => '$' + Math.abs(n).toLocaleString();
  const totalBudget  = data.reduce((a, r) => a + r.budget, 0);
  const totalActual  = data.reduce((a, r) => a + r.actual, 0);
  const totalVariance = totalActual - totalBudget;
  const over  = data.filter(r => r.status === 'OVER').length;
  const under = data.filter(r => r.status === 'UNDER').length;

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 8, marginBottom: 20 }}>
        <button onClick={() => exportCSV(data, 'cost_variance.csv')}
          style={{ padding: '5px 14px', borderRadius: 7, border: '1px solid #e5e7eb', fontSize: 12, fontWeight: 600, cursor: 'pointer', background: '#fff' }}>
          Export CSV
        </button>
        <button onClick={() => window.print()}
          style={{ padding: '5px 14px', borderRadius: 7, border: '1px solid #e5e7eb', fontSize: 12, fontWeight: 600, cursor: 'pointer', background: '#fff' }}>
          Print / PDF
        </button>
      </div>

      {/* KPI cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4,1fr)', gap: 14, marginBottom: 20 }}>
        {[
          { label: 'Total Budget',   val: '$' + (totalBudget/1000).toFixed(0)+'K',    color: '#1d4ed8' },
          { label: 'Total Actual',   val: '$' + (totalActual/1000).toFixed(0)+'K',    color: '#1e293b' },
          { label: 'Total Variance', val: (totalVariance > 0 ? '+$' : '-$') + (Math.abs(totalVariance)/1000).toFixed(0)+'K', color: totalVariance > 0 ? '#dc2626' : '#16a34a' },
          { label: 'Over / Under',   val: over + ' over · ' + under + ' under',       color: '#d97706' },
        ].map(c => (
          <div key={c.label} style={{ background: '#fff', border: '1px solid #e5e7eb', borderRadius: 10, padding: '14px 16px' }}>
            <div style={{ fontSize: 20, fontWeight: 800, color: c.color }}>{c.val}</div>
            <div style={{ fontSize: 11, color: '#64748b', marginTop: 3, textTransform: 'uppercase', letterSpacing: '0.4px' }}>{c.label}</div>
          </div>
        ))}
      </div>

      <div style={{ background: '#fff', border: '1px solid #e5e7eb', borderRadius: 10, overflow: 'hidden' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 12 }}>
          <thead>
            <tr style={{ background: '#f8fafc' }}>
              {['Item', 'Category', 'Budget', 'Actual', 'Variance', '%', 'Status'].map(h => (
                <th key={h} style={{ padding: '10px 14px', textAlign: 'left', fontWeight: 700, color: '#374151', borderBottom: '1px solid #e5e7eb' }}>{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {data.map((r, i) => (
              <tr key={r.item} style={{ background: i % 2 === 0 ? '#fff' : '#f8fafc' }}>
                <td style={{ padding: '10px 14px', fontWeight: 600, fontFamily: 'monospace', fontSize: 11 }}>{r.item}</td>
                <td style={{ padding: '10px 14px', color: '#6b7280' }}>{r.category}</td>
                <td style={{ padding: '10px 14px' }}>{fmt(r.budget)}</td>
                <td style={{ padding: '10px 14px', fontWeight: 600 }}>{fmt(r.actual)}</td>
                <td style={{ padding: '10px 14px', fontWeight: 700, color: r.variance > 0 ? '#dc2626' : '#16a34a' }}>
                  {r.variance > 0 ? '+' : ''}{fmt(r.variance)}
                </td>
                <td style={{ padding: '10px 14px', fontWeight: 700, color: r.pct > 0 ? '#dc2626' : '#16a34a' }}>
                  {r.pct > 0 ? '+' : ''}{r.pct}%
                </td>
                <td style={{ padding: '10px 14px' }}>
                  <span style={{ padding: '2px 8px', borderRadius: 12, fontSize: 11, fontWeight: 700, background: r.status === 'OVER' ? '#fef2f2' : '#f0fdf4', color: r.status === 'OVER' ? '#dc2626' : '#16a34a' }}>
                    {r.status}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

// ── Alert Summary tab ─────────────────────────────────────────────────────────
function AlertTab({ data }) {
  if (!data) return <Spinner />;
  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 8, marginBottom: 20 }}>
        <button onClick={() => window.print()}
          style={{ padding: '5px 14px', borderRadius: 7, border: '1px solid #e5e7eb', fontSize: 12, fontWeight: 600, cursor: 'pointer', background: '#fff' }}>
          Print / PDF
        </button>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '240px 1fr', gap: 20, marginBottom: 20 }}>
        {/* Donut */}
        <div style={{ background: '#fff', border: '1px solid #e5e7eb', borderRadius: 10, padding: 20, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <h3 style={{ margin: '0 0 16px', fontSize: 13, fontWeight: 700 }}>By Type</h3>
          <DonutChart slices={data.byType} size={140} />
          <div style={{ width: '100%', marginTop: 16 }}>
            {data.byType.map(s => (
              <div key={s.type} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 8 }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 7 }}>
                  <div style={{ width: 10, height: 10, borderRadius: '50%', background: s.color }} />
                  <span style={{ fontSize: 12 }}>{s.type}</span>
                </div>
                <span style={{ fontSize: 12, fontWeight: 700, color: s.color }}>{s.count}</span>
              </div>
            ))}
          </div>
        </div>

        {/* KPIs + monthly breakdown */}
        <div style={{ display: 'grid', gap: 14 }}>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3,1fr)', gap: 12 }}>
            {[
              { label: 'Total Alerts',     val: data.total,                        color: '#1d4ed8' },
              { label: 'Resolved',         val: data.resolved,                     color: '#16a34a' },
              { label: 'Avg Resolution',   val: data.avgResolutionHrs + ' hrs',    color: '#d97706' },
            ].map(c => (
              <div key={c.label} style={{ background: '#fff', border: '1px solid #e5e7eb', borderRadius: 10, padding: '14px 16px' }}>
                <div style={{ fontSize: 22, fontWeight: 800, color: c.color }}>{c.val}</div>
                <div style={{ fontSize: 11, color: '#64748b', marginTop: 3, textTransform: 'uppercase', letterSpacing: '0.4px' }}>{c.label}</div>
              </div>
            ))}
          </div>

          {/* Monthly stacked bars */}
          <div style={{ background: '#fff', border: '1px solid #e5e7eb', borderRadius: 10, padding: 18 }}>
            <h3 style={{ margin: '0 0 14px', fontSize: 13, fontWeight: 700 }}>Monthly Volume</h3>
            {data.byMonth.map(m => {
              const tot = m.critical + m.warning + m.info;
              return (
                <div key={m.month} style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 8 }}>
                  <span style={{ fontSize: 11, fontWeight: 600, width: 30, color: '#6b7280', flexShrink: 0 }}>{m.month}</span>
                  <div style={{ flex: 1, display: 'flex', height: 14, borderRadius: 4, overflow: 'hidden', gap: 1 }}>
                    {m.critical > 0 && <div style={{ flex: m.critical, background: '#dc2626' }} />}
                    {m.warning  > 0 && <div style={{ flex: m.warning,  background: '#d97706' }} />}
                    {m.info     > 0 && <div style={{ flex: m.info,     background: '#2563eb' }} />}
                  </div>
                  <span style={{ fontSize: 11, fontWeight: 700, width: 20, color: '#374151' }}>{tot}</span>
                </div>
              );
            })}
            <div style={{ display: 'flex', gap: 14, marginTop: 10, fontSize: 11, color: '#6b7280' }}>
              {[['#dc2626','Critical'],['#d97706','Warning'],['#2563eb','Info']].map(([c,l]) => (
                <div key={l} style={{ display: 'flex', alignItems: 'center', gap: 5 }}>
                  <div style={{ width: 10, height: 10, background: c, borderRadius: 2 }} />
                  <span>{l}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

// ── Activity tab ──────────────────────────────────────────────────────────────
function ActivityTab({ data }) {
  if (!data) return <Spinner />;
  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 8, marginBottom: 20 }}>
        <button onClick={() => exportCSV(data, 'user_activity.csv')}
          style={{ padding: '5px 14px', borderRadius: 7, border: '1px solid #e5e7eb', fontSize: 12, fontWeight: 600, cursor: 'pointer', background: '#fff' }}>
          Export CSV
        </button>
      </div>
      <div style={{ background: '#fff', border: '1px solid #e5e7eb', borderRadius: 10, overflow: 'hidden' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 12 }}>
          <thead>
            <tr style={{ background: '#f8fafc' }}>
              {['Time', 'User', 'Module', 'Action'].map(h => (
                <th key={h} style={{ padding: '10px 14px', textAlign: 'left', fontWeight: 700, color: '#374151', borderBottom: '1px solid #e5e7eb' }}>{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {data.map((r, i) => (
              <tr key={i} style={{ background: i % 2 === 0 ? '#fff' : '#f8fafc' }}>
                <td style={{ padding: '10px 14px', fontFamily: 'monospace', fontSize: 11, color: '#9ca3af', whiteSpace: 'nowrap' }}>{r.time}</td>
                <td style={{ padding: '10px 14px', fontWeight: 700 }}>{r.user}</td>
                <td style={{ padding: '10px 14px' }}>
                  <span style={{ padding: '2px 8px', background: '#eff6ff', color: '#1d4ed8', borderRadius: 12, fontSize: 11, fontWeight: 600 }}>{r.module}</span>
                </td>
                <td style={{ padding: '10px 14px', color: '#374151' }}>{r.action}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function Spinner() {
  return <div style={{ textAlign: 'center', padding: 60, color: '#9ca3af' }}>Loading report…</div>;
}

export default function ReportsPage() {
  const [tab, setTab]   = useState('supplier');
  const [supplier, setSupplier] = useState(null);
  const [cost,     setCost]     = useState(null);
  const [alert,    setAlert]    = useState(null);
  const [activity, setActivity] = useState(null);

  useEffect(() => {
    getSupplierPerfReport().then(r  => setSupplier(r.data || r));
    getCostVarianceReport().then(r  => setCost(r.data || r));
    getAlertSummaryReport().then(r  => setAlert(r.data || r));
    getActivityReport().then(r      => setActivity(r.data || r));
  }, []);

  return (
    <div style={{ padding: 24, maxWidth: 1100, margin: '0 auto' }}>
      <div style={{ marginBottom: 22 }}>
        <h1 style={{ fontSize: 22, fontWeight: 800, margin: '0 0 4px' }}>Management Reports</h1>
        <p style={{ fontSize: 13, color: '#64748b', margin: 0 }}>Supply chain analytics and performance insights — exportable to CSV and PDF</p>
      </div>

      {/* Tabs */}
      <div style={{ display: 'flex', borderBottom: '1px solid #e2e8f0', marginBottom: 24 }}>
        {TABS.map(t => (
          <button key={t.id} onClick={() => setTab(t.id)}
            style={{ padding: '9px 18px', fontSize: 13, fontWeight: 600, cursor: 'pointer', border: 'none', background: 'none',
              borderBottom: tab === t.id ? '2px solid #1d4ed8' : '2px solid transparent',
              color: tab === t.id ? '#1d4ed8' : '#94a3b8', marginBottom: -1 }}>
            {t.label}
          </button>
        ))}
      </div>

      {tab === 'supplier' && <SupplierTab data={supplier} />}
      {tab === 'cost'     && <CostTab     data={cost}     />}
      {tab === 'alert'    && <AlertTab    data={alert}    />}
      {tab === 'activity' && <ActivityTab data={activity} />}
    </div>
  );
}
