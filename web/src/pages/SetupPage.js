import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const DEMO = process.env.REACT_APP_DEMO_MODE === 'true';

const STEPS = [
  { id: 1, key: 'company',   label: 'Company Profile',  icon: '🏢' },
  { id: 2, key: 'suppliers', label: 'Supplier Setup',   icon: '🏭' },
  { id: 3, key: 'items',     label: 'Item Categories',  icon: '📦' },
  { id: 4, key: 'users',     label: 'User Accounts',    icon: '👥' },
  { id: 5, key: 'done',      label: 'Launch',           icon: '🚀' },
];

const INDUSTRIES = ['Manufacturing','Automotive','Electronics','Aerospace','FMCG','Pharmaceuticals','Logistics'];
const CURRENCIES  = ['USD','EUR','GBP','INR','SGD','AED'];
const TIMEZONES   = ['Asia/Kolkata','UTC','America/New_York','Europe/London','Asia/Singapore','Australia/Sydney'];
const ALL_CATS    = ['Electronics','Mechanical','Packaging','Raw Materials','Software','Chemicals','Textiles','Spare Parts'];

const inp = (extra) => ({
  width: '100%', padding: '9px 12px', border: '1px solid #d1d5db', borderRadius: 7,
  fontSize: 13, outline: 'none', boxSizing: 'border-box', ...extra,
});

function Field({ label, hint, children, required }) {
  return (
    <div>
      <label style={{ display: 'block', fontSize: 12, fontWeight: 600, color: '#374151', marginBottom: 5 }}>
        {label}{required && <span style={{ color: '#dc2626' }}> *</span>}
        {hint && <span style={{ fontWeight: 400, color: '#9ca3af', marginLeft: 6 }}>{hint}</span>}
      </label>
      {children}
    </div>
  );
}

function Step1({ data, set }) {
  return (
    <div style={{ display: 'grid', gap: 18 }}>
      <Field label="Organisation Name" required>
        <input value={data.orgName} onChange={e => set({ orgName: e.target.value })}
          placeholder="e.g. Acme Supply Chain Ltd" style={inp()} />
      </Field>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 14 }}>
        <Field label="Industry">
          <select value={data.industry} onChange={e => set({ industry: e.target.value })} style={inp()}>
            {INDUSTRIES.map(i => <option key={i}>{i}</option>)}
          </select>
        </Field>
        <Field label="Base Currency">
          <select value={data.currency} onChange={e => set({ currency: e.target.value })} style={inp()}>
            {CURRENCIES.map(c => <option key={c}>{c}</option>)}
          </select>
        </Field>
      </div>
      <Field label="Timezone">
        <select value={data.timezone} onChange={e => set({ timezone: e.target.value })} style={inp()}>
          {TIMEZONES.map(t => <option key={t}>{t}</option>)}
        </select>
      </Field>
      <Field label="Platform Domain" hint="optional — used in notifications">
        <input value={data.domain} onChange={e => set({ domain: e.target.value })}
          placeholder="scip.yourcompany.com" style={inp()} />
      </Field>
      {DEMO && (
        <div style={{ background: '#eff6ff', border: '1px solid #bfdbfe', borderRadius: 8, padding: 12, fontSize: 12, color: '#1d4ed8' }}>
          Demo mode — company profile is simulated. In production this saves to the backend configuration table.
        </div>
      )}
    </div>
  );
}

function Step2({ data, set }) {
  const [newName, setNewName] = useState('');
  const [newCountry, setNewCountry] = useState('');
  const seeds = data.supplierSeeds || [];
  const add = () => {
    if (!newName.trim()) return;
    set({ supplierSeeds: [...seeds, { name: newName.trim(), country: newCountry || 'India' }] });
    setNewName(''); setNewCountry('');
  };
  const remove = (i) => set({ supplierSeeds: seeds.filter((_, idx) => idx !== i) });

  return (
    <div style={{ display: 'grid', gap: 18 }}>
      <p style={{ margin: 0, fontSize: 13, color: '#6b7280' }}>
        Add initial suppliers to seed the platform. You can add more after setup from the Supplier Scorecard module.
      </p>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 140px auto', gap: 8, alignItems: 'end' }}>
        <Field label="Supplier Name">
          <input value={newName} onChange={e => setNewName(e.target.value)} onKeyDown={e => e.key === 'Enter' && add()}
            placeholder="e.g. TechParts Asia Pvt Ltd" style={inp()} />
        </Field>
        <Field label="Country">
          <input value={newCountry} onChange={e => setNewCountry(e.target.value)}
            placeholder="India" style={inp()} />
        </Field>
        <button onClick={add} style={{ padding: '9px 16px', background: '#1d4ed8', color: '#fff', border: 'none', borderRadius: 7, fontWeight: 700, cursor: 'pointer', fontSize: 13, whiteSpace: 'nowrap' }}>
          + Add
        </button>
      </div>
      {seeds.length > 0 && (
        <div style={{ border: '1px solid #e5e7eb', borderRadius: 8, overflow: 'hidden' }}>
          {seeds.map((s, i) => (
            <div key={i} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '10px 14px', borderBottom: i < seeds.length - 1 ? '1px solid #f3f4f6' : 'none', background: '#fff' }}>
              <span style={{ fontSize: 13, fontWeight: 600 }}>{s.name}</span>
              <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                <span style={{ fontSize: 11, color: '#9ca3af' }}>{s.country}</span>
                <button onClick={() => remove(i)} style={{ background: 'none', border: 'none', color: '#ef4444', cursor: 'pointer', fontSize: 16, padding: 0 }}>×</button>
              </div>
            </div>
          ))}
        </div>
      )}
      {seeds.length === 0 && (
        <div style={{ textAlign: 'center', padding: '24px', color: '#9ca3af', fontSize: 13, border: '1px dashed #e5e7eb', borderRadius: 8 }}>
          No suppliers added yet — add at least one above, or skip and add later
        </div>
      )}
      {DEMO && (
        <div style={{ background: '#eff6ff', border: '1px solid #bfdbfe', borderRadius: 8, padding: 12, fontSize: 12, color: '#1d4ed8' }}>
          Demo mode — 5 seed suppliers (TechParts India, GlobalComp Singapore, etc.) are pre-loaded automatically.
        </div>
      )}
    </div>
  );
}

function Step3({ data, set }) {
  const selected = data.categories || [];
  const toggle = (cat) => {
    const next = selected.includes(cat) ? selected.filter(c => c !== cat) : [...selected, cat];
    set({ categories: next });
  };
  return (
    <div style={{ display: 'grid', gap: 18 }}>
      <p style={{ margin: 0, fontSize: 13, color: '#6b7280' }}>
        Select the item categories your organisation works with. These drive classification in the Item Master and BOM modules.
      </p>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
        {ALL_CATS.map(cat => (
          <label key={cat} style={{ display: 'flex', alignItems: 'center', gap: 10, padding: '12px 14px', border: `1px solid ${selected.includes(cat) ? '#1d4ed8' : '#e5e7eb'}`, borderRadius: 8, cursor: 'pointer', background: selected.includes(cat) ? '#eff6ff' : '#fff', transition: 'all 0.15s' }}>
            <input type="checkbox" checked={selected.includes(cat)} onChange={() => toggle(cat)} style={{ accentColor: '#1d4ed8', width: 15, height: 15 }} />
            <span style={{ fontSize: 13, fontWeight: selected.includes(cat) ? 600 : 400, color: selected.includes(cat) ? '#1d4ed8' : '#374151' }}>{cat}</span>
          </label>
        ))}
      </div>
      <div style={{ fontSize: 12, color: '#9ca3af' }}>{selected.length} of {ALL_CATS.length} categories selected</div>
    </div>
  );
}

function Step4({ data, set }) {
  return (
    <div style={{ display: 'grid', gap: 18 }}>
      <p style={{ margin: 0, fontSize: 13, color: '#6b7280' }}>
        Set credentials for the platform administrator account. Additional users can be created from the User Management module after setup.
      </p>
      <Field label="Admin User ID" required>
        <input value={data.adminId} onChange={e => set({ adminId: e.target.value })}
          placeholder="admin" style={inp()} />
      </Field>
      <Field label="Admin Full Name">
        <input value={data.adminName} onChange={e => set({ adminName: e.target.value })}
          placeholder="Platform Administrator" style={inp()} />
      </Field>
      <Field label="Admin Password" required hint="min 8 characters">
        <input type="password" value={data.adminPassword} onChange={e => set({ adminPassword: e.target.value })}
          placeholder="••••••••" style={inp()} />
      </Field>
      <Field label="Confirm Password">
        <input type="password" value={data.confirmPassword} onChange={e => set({ confirmPassword: e.target.value })}
          placeholder="••••••••" style={inp()} />
      </Field>
      {data.adminPassword && data.confirmPassword && data.adminPassword !== data.confirmPassword && (
        <div style={{ color: '#dc2626', fontSize: 12 }}>Passwords do not match</div>
      )}
      {DEMO && (
        <div style={{ background: '#eff6ff', border: '1px solid #bfdbfe', borderRadius: 8, padding: 12, fontSize: 12, color: '#1d4ed8' }}>
          Demo mode — accounts kumar / Admin@2026 / viewer are pre-created. Password hashing uses BCrypt on the backend.
        </div>
      )}
    </div>
  );
}

function Step5({ config }) {
  const items = [
    ['Organisation', config.orgName || '(not set)'],
    ['Industry', config.industry],
    ['Currency', config.currency],
    ['Timezone', config.timezone],
    ['Suppliers to seed', (config.supplierSeeds || []).length + (DEMO ? ' (+ 5 demo)' : '')],
    ['Item categories', (config.categories || []).length + ' selected'],
    ['Admin account', config.adminId || '(not set)'],
  ];
  return (
    <div style={{ display: 'grid', gap: 18 }}>
      <div style={{ textAlign: 'center', padding: '16px 0' }}>
        <div style={{ fontSize: 48 }}>🚀</div>
        <h3 style={{ margin: '8px 0 4px', color: '#1e293b' }}>Ready to launch</h3>
        <p style={{ margin: 0, color: '#6b7280', fontSize: 13 }}>Review your configuration and click Launch to go to the dashboard.</p>
      </div>
      <div style={{ border: '1px solid #e5e7eb', borderRadius: 8, overflow: 'hidden' }}>
        {items.map(([k, v], i) => (
          <div key={k} style={{ display: 'flex', justifyContent: 'space-between', padding: '10px 16px', background: i % 2 === 0 ? '#f8fafc' : '#fff', fontSize: 13 }}>
            <span style={{ color: '#6b7280' }}>{k}</span>
            <span style={{ fontWeight: 600, color: '#1e293b' }}>{v}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

export default function SetupPage() {
  const navigate = useNavigate();
  const [step, setStep] = useState(1);
  const [saving, setSaving] = useState(false);
  const [config, setConfig] = useState({
    orgName: '', industry: 'Manufacturing', currency: 'USD', timezone: 'Asia/Kolkata', domain: '',
    supplierSeeds: [], categories: ['Electronics', 'Mechanical', 'Raw Materials'],
    adminId: 'admin', adminName: 'Platform Administrator', adminPassword: '', confirmPassword: '',
  });

  const patch = (partial) => setConfig(c => ({ ...c, ...partial }));

  const canNext = () => {
    if (step === 1) return config.orgName.trim().length > 0;
    if (step === 4) return config.adminId.trim() && config.adminPassword.length >= 6 && config.adminPassword === config.confirmPassword;
    return true;
  };

  const launch = async () => {
    setSaving(true);
    await new Promise(r => setTimeout(r, DEMO ? 1200 : 0));
    localStorage.setItem('scip_setup_done', 'true');
    localStorage.setItem('scip_setup_config', JSON.stringify(config));
    setSaving(false);
    navigate('/dashboard');
  };

  const pct = ((step - 1) / (STEPS.length - 1)) * 100;

  return (
    <div style={{ minHeight: '100vh', background: '#f8fafc', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 24 }}>
      <div style={{ width: '100%', maxWidth: 640 }}>
        {/* Header */}
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <h1 style={{ fontSize: 26, fontWeight: 800, color: '#1e293b', margin: '0 0 6px' }}>Platform Setup</h1>
          <p style={{ color: '#6b7280', fontSize: 14, margin: 0 }}>Supply Chain Intelligence Platform — initial configuration</p>
        </div>

        {/* Step indicators */}
        <div style={{ display: 'flex', alignItems: 'center', marginBottom: 32 }}>
          {STEPS.map((s, i) => (
            <React.Fragment key={s.id}>
              <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', flex: i < STEPS.length - 1 ? 0 : 1 }}>
                <div style={{
                  width: 36, height: 36, borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center',
                  fontWeight: 700, fontSize: 14, transition: 'all 0.2s',
                  background: step > s.id ? '#16a34a' : step === s.id ? '#1d4ed8' : '#e5e7eb',
                  color: step >= s.id ? '#fff' : '#6b7280',
                }}>
                  {step > s.id ? '✓' : s.id}
                </div>
                <span style={{ fontSize: 10, marginTop: 4, color: step === s.id ? '#1d4ed8' : '#9ca3af', fontWeight: step === s.id ? 700 : 400, whiteSpace: 'nowrap' }}>
                  {s.label}
                </span>
              </div>
              {i < STEPS.length - 1 && (
                <div style={{ flex: 1, height: 2, background: step > s.id ? '#16a34a' : '#e5e7eb', margin: '0 4px', marginBottom: 16, transition: 'background 0.3s' }} />
              )}
            </React.Fragment>
          ))}
        </div>

        {/* Card */}
        <div style={{ background: '#fff', border: '1px solid #e5e7eb', borderRadius: 12, padding: 28, boxShadow: '0 2px 8px rgba(0,0,0,0.06)' }}>
          <div style={{ marginBottom: 22 }}>
            <h2 style={{ margin: '0 0 4px', fontSize: 17, fontWeight: 700, color: '#1e293b' }}>
              {STEPS[step - 1].icon} {STEPS[step - 1].label}
            </h2>
            <div style={{ height: 3, background: '#f1f5f9', borderRadius: 2 }}>
              <div style={{ width: pct + '%', height: '100%', background: '#1d4ed8', borderRadius: 2, transition: 'width 0.4s' }} />
            </div>
          </div>

          {step === 1 && <Step1 data={config} set={patch} />}
          {step === 2 && <Step2 data={config} set={patch} />}
          {step === 3 && <Step3 data={config} set={patch} />}
          {step === 4 && <Step4 data={config} set={patch} />}
          {step === 5 && <Step5 config={config} />}

          {/* Nav buttons */}
          <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 28, paddingTop: 20, borderTop: '1px solid #f1f5f9' }}>
            <button onClick={() => step > 1 ? setStep(s => s - 1) : navigate('/dashboard')}
              style={{ padding: '9px 20px', border: '1px solid #e5e7eb', borderRadius: 7, background: '#fff', fontSize: 13, fontWeight: 600, cursor: 'pointer', color: '#374151' }}>
              {step === 1 ? 'Skip Setup' : '← Back'}
            </button>
            {step < 5 ? (
              <button onClick={() => setStep(s => s + 1)} disabled={!canNext()}
                style={{ padding: '9px 24px', background: canNext() ? '#1d4ed8' : '#e5e7eb', color: canNext() ? '#fff' : '#9ca3af', border: 'none', borderRadius: 7, fontWeight: 700, fontSize: 13, cursor: canNext() ? 'pointer' : 'not-allowed' }}>
                Next →
              </button>
            ) : (
              <button onClick={launch} disabled={saving}
                style={{ padding: '9px 24px', background: '#16a34a', color: '#fff', border: 'none', borderRadius: 7, fontWeight: 700, fontSize: 13, cursor: 'pointer' }}>
                {saving ? 'Launching…' : '🚀 Launch Platform'}
              </button>
            )}
          </div>
        </div>

        <p style={{ textAlign: 'center', marginTop: 16, fontSize: 12, color: '#9ca3af' }}>
          All settings can be changed later in User Management &amp; Settings
        </p>
      </div>
    </div>
  );
}
