/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
import { useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';

const BASE = process.env.REACT_APP_API_URL || 'http://localhost:8089/supchain';

function StatusBadge({ type, children }) {
  const styles = {
    success: { background: '#f0fdf4', color: '#15803d', border: '1px solid #bbf7d0' },
    error:   { background: '#fef2f2', color: '#dc2626', border: '1px solid #fecaca' },
    info:    { background: '#eff6ff', color: '#1d4ed8', border: '1px solid #bfdbfe' },
  };
  return (
    <span style={{ ...styles[type], fontSize: 12, fontWeight: 700, padding: '2px 10px', borderRadius: 20, display: 'inline-block' }}>
      {children}
    </span>
  );
}

export default function CsvUploadPage() {
  const navigate = useNavigate();
  const fileRef  = useRef(null);

  const [file,     setFile]     = useState(null);
  const [result,   setResult]   = useState(null);
  const [uploading, setUploading] = useState(false);
  const [dragOver, setDragOver] = useState(false);

  const handleFileChange = (e) => {
    const f = e.target.files[0];
    if (f) { setFile(f); setResult(null); }
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setDragOver(false);
    const f = e.dataTransfer.files[0];
    if (f && f.name.endsWith('.csv')) { setFile(f); setResult(null); }
  };

  const handleUpload = async () => {
    if (!file) return;
    setUploading(true);
    setResult(null);

    const formData = new FormData();
    formData.append('file', file);

    const token = localStorage.getItem('jwt_token');
    try {
      const res = await fetch(BASE + '/api/suppliers/import/csv', {
        method: 'POST',
        headers: token ? { Authorization: 'Bearer ' + token } : {},
        body: formData,
      });
      const json = await res.json();
      setResult({ ok: res.ok, ...json });

      if (res.ok && json.data?.errors?.length === 0) {
        setTimeout(() => navigate('/simple-dashboard'), 1800);
      }
    } catch (err) {
      setResult({ ok: false, error: 'Network error: ' + err.message });
    } finally {
      setUploading(false);
    }
  };

  const downloadTemplate = () => {
    const token = localStorage.getItem('jwt_token');
    const a = document.createElement('a');
    a.href = BASE + '/api/suppliers/import/template';
    if (token) {
      // Append token as query param for the file download (headers can't be set on anchor clicks)
      a.href += '?token=' + encodeURIComponent(token);
    }
    a.download = 'supplier_import_template.csv';
    a.click();
  };

  const imported    = result?.data?.importedDeliveries ?? 0;
  const supplierCount = result?.data?.importedSuppliers ?? 0;
  const rowErrors   = result?.data?.errors ?? [];
  const hasErrors   = rowErrors.length > 0;

  return (
    <div className="page-container">

      {/* ── Header ───────────────────────────────────────── */}
      <div className="page-header">
        <div>
          <h1 style={{ margin: 0 }}>Upload supplier order history</h1>
          <p style={{ margin: '4px 0 0', fontSize: 14, color: '#64748b' }}>
            No ERP or API needed — import a spreadsheet exported from Excel or Google Sheets.
          </p>
        </div>
      </div>

      {/* ── Step 1 — Download template ───────────────────── */}
      <div style={{ background: '#fff', border: '1px solid #e2e8f0', borderRadius: 12, padding: '20px 24px', marginBottom: 20 }}>
        <div style={{ display: 'flex', alignItems: 'flex-start', gap: 16, flexWrap: 'wrap' }}>
          <div style={{ flex: 1 }}>
            <h3 style={{ margin: '0 0 6px', fontSize: 15, fontWeight: 700 }}>
              Step 1 — Download the template
            </h3>
            <p style={{ margin: 0, fontSize: 13, color: '#475569', lineHeight: 1.6 }}>
              Open the template in Excel or Google Sheets. Fill in one row per delivery or shipment event.
              Required columns are <strong>supplier_id</strong>, <strong>supplier_name</strong>,{' '}
              <strong>promised_date</strong>, and <strong>actual_date</strong>. All other columns are optional.
            </p>
            <div style={{ marginTop: 10, display: 'flex', gap: 8, flexWrap: 'wrap' }}>
              {['supplier_id', 'supplier_name', 'promised_date', 'actual_date'].map(c => (
                <code key={c} style={{ fontSize: 11, background: '#f1f5f9', padding: '2px 6px', borderRadius: 4, color: '#0f172a' }}>{c}</code>
              ))}
              <span style={{ fontSize: 11, color: '#94a3b8' }}>+ 6 optional columns</span>
            </div>
          </div>
          <button
            onClick={downloadTemplate}
            style={{
              padding: '10px 20px', borderRadius: 8, border: '1px solid #1d4ed8',
              background: '#eff6ff', color: '#1d4ed8', fontSize: 13, fontWeight: 700,
              cursor: 'pointer', whiteSpace: 'nowrap', flexShrink: 0,
            }}
          >
            ↓ Download template
          </button>
        </div>
      </div>

      {/* ── Step 2 — Upload ──────────────────────────────── */}
      <div style={{ background: '#fff', border: '1px solid #e2e8f0', borderRadius: 12, padding: '20px 24px', marginBottom: 20 }}>
        <h3 style={{ margin: '0 0 14px', fontSize: 15, fontWeight: 700 }}>
          Step 2 — Upload your filled CSV
        </h3>

        {/* Drop zone */}
        <div
          onDragOver={(e) => { e.preventDefault(); setDragOver(true); }}
          onDragLeave={() => setDragOver(false)}
          onDrop={handleDrop}
          onClick={() => fileRef.current?.click()}
          style={{
            border: '2px dashed ' + (dragOver ? '#1d4ed8' : file ? '#16a34a' : '#cbd5e1'),
            borderRadius: 10, padding: '28px 20px', textAlign: 'center',
            cursor: 'pointer', background: dragOver ? '#eff6ff' : file ? '#f0fdf4' : '#f8fafc',
            transition: 'all 0.15s', marginBottom: 16,
          }}
        >
          <input ref={fileRef} type="file" accept=".csv" onChange={handleFileChange} style={{ display: 'none' }} />
          {file ? (
            <>
              <div style={{ fontSize: 20, marginBottom: 6 }}>✓</div>
              <div style={{ fontWeight: 700, fontSize: 14, color: '#15803d' }}>{file.name}</div>
              <div style={{ fontSize: 12, color: '#64748b', marginTop: 4 }}>
                {(file.size / 1024).toFixed(1)} KB — click to change
              </div>
            </>
          ) : (
            <>
              <div style={{ fontSize: 28, marginBottom: 8, color: '#94a3b8' }}>📄</div>
              <div style={{ fontWeight: 600, fontSize: 14, color: '#475569' }}>
                Drag and drop your CSV here, or click to browse
              </div>
              <div style={{ fontSize: 12, color: '#94a3b8', marginTop: 4 }}>.csv files only</div>
            </>
          )}
        </div>

        <button
          onClick={handleUpload}
          disabled={!file || uploading}
          style={{
            padding: '10px 24px', borderRadius: 8, border: 'none',
            background: !file || uploading ? '#e2e8f0' : '#1e293b',
            color: !file || uploading ? '#94a3b8' : '#fff',
            fontSize: 14, fontWeight: 700, cursor: !file || uploading ? 'not-allowed' : 'pointer',
          }}
        >
          {uploading ? 'Uploading…' : 'Upload and import'}
        </button>
      </div>

      {/* ── Results ──────────────────────────────────────── */}
      {result && (
        <div style={{ background: '#fff', border: '1px solid #e2e8f0', borderRadius: 12, padding: '20px 24px' }}>
          <h3 style={{ margin: '0 0 14px', fontSize: 15, fontWeight: 700 }}>Import results</h3>

          {result.ok && imported > 0 ? (
            <div style={{ display: 'flex', gap: 12, marginBottom: hasErrors ? 16 : 0, flexWrap: 'wrap' }}>
              <div style={{ background: '#f0fdf4', border: '1px solid #bbf7d0', borderRadius: 8, padding: '10px 16px', flex: 1, minWidth: 140 }}>
                <div style={{ fontSize: 26, fontWeight: 800, color: '#15803d' }}>{supplierCount}</div>
                <div style={{ fontSize: 12, color: '#64748b', marginTop: 2 }}>Supplier{supplierCount !== 1 ? 's' : ''} created or matched</div>
              </div>
              <div style={{ background: '#eff6ff', border: '1px solid #bfdbfe', borderRadius: 8, padding: '10px 16px', flex: 1, minWidth: 140 }}>
                <div style={{ fontSize: 26, fontWeight: 800, color: '#1d4ed8' }}>{imported}</div>
                <div style={{ fontSize: 12, color: '#64748b', marginTop: 2 }}>Delivery records imported</div>
              </div>
              {hasErrors && (
                <div style={{ background: '#fef2f2', border: '1px solid #fecaca', borderRadius: 8, padding: '10px 16px', flex: 1, minWidth: 140 }}>
                  <div style={{ fontSize: 26, fontWeight: 800, color: '#dc2626' }}>{rowErrors.length}</div>
                  <div style={{ fontSize: 12, color: '#64748b', marginTop: 2 }}>Row{rowErrors.length !== 1 ? 's' : ''} skipped (see below)</div>
                </div>
              )}
            </div>
          ) : !result.ok ? (
            <div style={{ background: '#fef2f2', border: '1px solid #fecaca', borderRadius: 8, padding: '12px 16px', marginBottom: hasErrors ? 16 : 0, fontSize: 14, color: '#dc2626' }}>
              {result.error || result.data?.errors?.[0]?.message || 'Upload failed — please check your file and try again.'}
            </div>
          ) : (
            <div style={{ background: '#fffbeb', border: '1px solid #fde68a', borderRadius: 8, padding: '12px 16px', marginBottom: hasErrors ? 16 : 0, fontSize: 14, color: '#92400e' }}>
              No records were imported. Check that your file has data rows below the header.
            </div>
          )}

          {/* Row-level errors table */}
          {hasErrors && (
            <div>
              <div style={{ fontWeight: 700, fontSize: 13, marginBottom: 8, color: '#374151' }}>
                Rows that were skipped:
              </div>
              <div style={{ border: '1px solid #e2e8f0', borderRadius: 8, overflow: 'hidden' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 13 }}>
                  <thead>
                    <tr style={{ background: '#f8fafc' }}>
                      <th style={{ padding: '8px 14px', textAlign: 'left', fontWeight: 700, color: '#64748b', fontSize: 11, textTransform: 'uppercase', letterSpacing: '0.4px', width: 70 }}>Row</th>
                      <th style={{ padding: '8px 14px', textAlign: 'left', fontWeight: 700, color: '#64748b', fontSize: 11, textTransform: 'uppercase', letterSpacing: '0.4px', width: 120 }}>Supplier ID</th>
                      <th style={{ padding: '8px 14px', textAlign: 'left', fontWeight: 700, color: '#64748b', fontSize: 11, textTransform: 'uppercase', letterSpacing: '0.4px' }}>What went wrong</th>
                    </tr>
                  </thead>
                  <tbody>
                    {rowErrors.map((e, i) => (
                      <tr key={i} style={{ borderTop: '1px solid #f1f5f9', background: i % 2 === 0 ? '#fff' : '#fafafa' }}>
                        <td style={{ padding: '8px 14px', color: '#94a3b8' }}>{e.row}</td>
                        <td style={{ padding: '8px 14px' }}><code style={{ fontSize: 11 }}>{e.supplierId || '—'}</code></td>
                        <td style={{ padding: '8px 14px', color: '#dc2626' }}>{e.message}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {result.ok && imported > 0 && !hasErrors && (
            <div style={{ marginTop: 14, fontSize: 13, color: '#15803d', fontWeight: 600 }}>
              All rows imported successfully — redirecting to your dashboard…
            </div>
          )}

          {result.ok && imported > 0 && (
            <button
              onClick={() => navigate('/simple-dashboard')}
              style={{ marginTop: 14, padding: '8px 18px', borderRadius: 8, border: 'none', background: '#1e293b', color: '#fff', fontSize: 13, fontWeight: 700, cursor: 'pointer' }}
            >
              Go to dashboard →
            </button>
          )}
        </div>
      )}
    </div>
  );
}
