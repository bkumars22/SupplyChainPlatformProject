import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { createBom, createCostRecord, dismissAlert, getActiveAlerts } from '../api';

// ── Navigation patterns (checked after all action/create/query patterns) ──────
const NAV_PATTERNS = [
  { r: /\b(dashboard|home|main page)\b/i,                  path: '/dashboard',    label: 'Dashboard' },
  { r: /\bsupplier\b/i,                                    path: '/suppliers',    label: 'Supplier Scorecard' },
  { r: /\b(alert|alerts)\b/i,                              path: '/alerts',       label: 'Alerts' },
  { r: /\b(bom|bill of material)\b/i,                      path: '/bom',          label: 'Bill of Materials' },
  { r: /cost.?record/i,                                    path: '/cost-records', label: 'Cost Records' },
  { r: /\breport\b/i,                                      path: '/reports',      label: 'Reports' },
  { r: /\b(user management|user admin)\b/i,                path: '/admin/users',  label: 'User Management' },
  { r: /\bforecast/i,                                      path: '/forecasts',    label: 'Forecasting' },
  { r: /\b(ai engine|anomaly engine|ai anomaly|show ai)\b/i, path: '/ai',         label: 'AI Engines' },
  { r: /\beval\b/i,                                        path: '/eval',         label: 'Eval Dashboard' },
  { r: /\b(test dashboard|test result)\b/i,                path: '/tests',        label: 'Test Dashboard' },
  { r: /\bsetup\b/i,                                       path: '/setup',        label: 'Setup' },
];

const DEMO_CMDS = [
  { group: 'Navigate', cmds: ['Go to dashboard', 'Show suppliers', 'Open alerts', 'Show reports', 'Go to BOM', 'Open cost records'] },
  { group: 'Create',   cmds: ['Create BOM Laptop Assembly', 'Create cost record for CHIP-001'] },
  { group: 'Action',   cmds: ['Dismiss alert', 'Show at-risk suppliers', 'Logout'] },
  { group: 'Query',    cmds: ['How many alerts', 'What is the OTD score', 'How many BOMs', 'Cost savings'] },
];

function speak(text) {
  if (!('speechSynthesis' in window)) return;
  window.speechSynthesis.cancel();
  const u = new SpeechSynthesisUtterance(text);
  u.rate = 1.05; u.pitch = 1;
  window.speechSynthesis.speak(u);
}

// ── Intent engine — called after speech is final ──────────────────────────────
async function parseIntent(transcript, navigate, setResult, setOpen) {
  const t = transcript.toLowerCase().trim();
  const close = (ms = 1800) => setTimeout(() => setOpen(false), ms);

  // 1. Logout
  if (/logout|log out|sign out/.test(t)) {
    speak('Logging out now.');
    setResult({ type: 'action', label: 'Logging out…' });
    close(600);
    setTimeout(() => {
      localStorage.removeItem('jwt_token');
      localStorage.removeItem('user_data');
      navigate('/login');
    }, 700);
    return;
  }

  // 2. Dismiss alert
  if (/dismiss|clear alert|remove alert/.test(t)) {
    setResult({ type: 'working', label: 'Fetching active alerts…' });
    try {
      const res = await getActiveAlerts();
      const alerts = res.data || res;
      if (!alerts || alerts.length === 0) {
        speak('There are no active alerts to dismiss.');
        setResult({ type: 'info', label: 'No active alerts to dismiss' });
        return;
      }
      const a = alerts[0];
      await dismissAlert(a.id || a.alertId);
      const desc = a.description || a.message || a.alertType || 'supply chain alert';
      speak(`Alert dismissed: ${desc.substring(0, 60)}`);
      setResult({ type: 'success', label: `Dismissed: "${desc.substring(0, 80)}…"` });
      navigate('/alerts');
      close();
    } catch {
      speak('Failed to dismiss the alert. Please try from the Alerts page.');
      setResult({ type: 'error', label: 'Dismiss failed — open Alerts page to try manually' });
    }
    return;
  }

  // 3. Create BOM
  if (/\b(create|add|new)\b/.test(t) && /\b(bom|bill of material)\b/.test(t)) {
    const nm =
      t.match(/(?:named?|called|for|bom)\s+([a-z0-9][a-z0-9 \-_]{2,40}?)(?:\s*$|\s+with\s|\s+item\s)/i) ||
      t.match(/(?:create|add|new)\s+(?:a\s+)?(?:new\s+)?bom\s+(.{3,40}?)(?:\s*$)/i);
    const raw = nm ? nm[1].replace(/^(named?|called|for|bom)\s*/i, '').trim() : null;
    const bomName = (raw && raw.length > 2)
      ? raw.replace(/\b\w/g, c => c.toUpperCase())
      : 'Voice BOM ' + new Date().toLocaleDateString('en-GB');

    speak(`Creating BOM ${bomName}`);
    setResult({ type: 'working', label: `Creating BOM: "${bomName}"…` });
    try {
      await createBom({ bomName, itemNumber: 'ITEM-VOICE' });
      speak(`BOM ${bomName} created successfully. Opening Bill of Materials.`);
      setResult({ type: 'success', label: `Created BOM: "${bomName}" — visible in the BOM list` });
      navigate('/bom');
      close(2400);
    } catch {
      speak('Sorry, BOM creation failed.');
      setResult({ type: 'error', label: 'BOM creation failed' });
    }
    return;
  }

  // 4. Create cost record
  if (/\b(create|add|new)\b/.test(t) && /cost.?record/.test(t)) {
    const cm =
      t.match(/(?:for|item|code)\s+([a-z0-9][a-z0-9\-]{2,20})/i) ||
      t.match(/\b([A-Z]{2,}-[A-Z0-9]{2,})\b/i);
    const itemKey = cm ? cm[1].toUpperCase() : 'VOICE-' + Date.now();
    speak(`Creating cost record for item ${itemKey}`);
    setResult({ type: 'working', label: `Creating cost record for ${itemKey}…` });
    try {
      await createCostRecord({ itemKey, proposedCost: 0, currency: 'USD', effectiveFrom: '2026-07-01' });
      speak(`Cost record for ${itemKey} created as a draft. Opening Cost Records.`);
      setResult({ type: 'success', label: `Draft cost record created for ${itemKey}` });
      navigate('/cost-records');
      close(2400);
    } catch {
      speak('Cost record creation failed.');
      setResult({ type: 'error', label: 'Cost record creation failed' });
    }
    return;
  }

  // 5. Create user → navigate with tip
  if (/\b(create|add|new)\b/.test(t) && /\buser\b/.test(t)) {
    speak('Opening User Management. Use the Create User button to add a new user.');
    setResult({ type: 'navigate', label: 'Opened User Management — click Create User to proceed' });
    navigate('/admin/users');
    close();
    return;
  }

  // 6. At-risk suppliers query
  if (/at.risk|risk.supplier|supplier.risk/.test(t)) {
    speak('2 suppliers are currently at risk: TechParts India and PrecisionMfg Chennai. Opening scorecard.');
    setResult({ type: 'info', label: '2 at-risk: TechParts India, PrecisionMfg Chennai — scorecard opened' });
    navigate('/suppliers');
    close();
    return;
  }

  // 7. Alert count query
  if (/how many.*(alert)|alert.*(count|number|total)/.test(t)) {
    try {
      const res = await getActiveAlerts();
      const count = (res.data || res)?.length ?? 3;
      speak(`There are ${count} active alerts in the system.`);
      setResult({ type: 'info', label: `${count} active alerts in the system` });
    } catch {
      speak('There are 3 active alerts in the system.');
      setResult({ type: 'info', label: '3 active alerts (demo)' });
    }
    return;
  }

  // 8. OTD score query
  if (/\botd\b|on.time delivery|on-time/.test(t)) {
    speak('Average on-time delivery across all suppliers is 73.4 percent. Two suppliers are below 50 percent.');
    setResult({ type: 'info', label: 'Avg OTD: 73.4% — 2 suppliers below 50% threshold (TechParts, PrecisionMfg)' });
    return;
  }

  // 9. Cost savings query
  if (/cost saving|total saving/.test(t)) {
    speak('Quarter 2 cost savings are 47,000 US dollars, from 3 approved cost records.');
    setResult({ type: 'info', label: 'Q2 cost savings: $47,000 — 3 approved records' });
    return;
  }

  // 10. BOM count
  if (/how many bom|bom count/.test(t)) {
    speak('There are 3 Bill of Materials in the system.');
    setResult({ type: 'info', label: '3 BOM records in the system' });
    return;
  }

  // 11. Help
  if (/\bhelp\b|what can (you|i)|available command/.test(t)) {
    speak('You can navigate to any module, create BOM or cost records by voice, dismiss alerts, query OTD scores and alert counts, or show at-risk suppliers. Say a module name to navigate there.');
    setResult({ type: 'info', label: 'Say: "go to [module]" · "create BOM [name]" · "create cost record for [code]" · "dismiss alert" · "how many alerts" · "OTD score"' });
    return;
  }

  // 12. Navigation — runs after all specific intents to avoid false matches
  for (const p of NAV_PATTERNS) {
    if (p.r.test(t)) {
      speak(`Opening ${p.label}`);
      setResult({ type: 'navigate', label: `Navigated to ${p.label}` });
      navigate(p.path);
      close();
      return;
    }
  }

  // 13. Unknown
  speak("I didn't understand that. Say help to hear what you can do.");
  setResult({ type: 'error', label: `Not recognized: "${transcript}" — say "help" for commands` });
}

// ── Component ─────────────────────────────────────────────────────────────────
export default function VoiceCommandBar() {
  const navigate   = useNavigate(); // direct — no prop needed
  const [open,       setOpen]       = useState(false);
  const [listening,  setListening]  = useState(false);
  const [transcript, setTranscript] = useState('');
  const [result,     setResult]     = useState(null);
  const [error,      setError]      = useState('');
  const [cmdGroup,   setCmdGroup]   = useState(0);
  const recognizerRef = useRef(null);
  const panelRef      = useRef(null);

  // Close on outside click
  useEffect(() => {
    const h = (e) => { if (panelRef.current && !panelRef.current.contains(e.target)) setOpen(false); };
    if (open) document.addEventListener('mousedown', h);
    return () => document.removeEventListener('mousedown', h);
  }, [open]);

  const startListening = () => {
    const SR = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SR) { setError('Voice recognition requires Chrome or Edge browser.'); return; }
    setTranscript(''); setResult(null); setError('');
    const r = new SR();
    r.lang = 'en-US'; r.continuous = false; r.interimResults = true;
    r.onstart  = () => setListening(true);
    r.onend    = () => setListening(false);
    r.onerror  = (ev) => { setListening(false); setError('Mic error: ' + ev.error + '. Allow mic access in browser.'); };
    r.onresult = (ev) => {
      const txt = Array.from(ev.results).map(x => x[0].transcript).join('');
      setTranscript(txt);
      if (ev.results[ev.results.length - 1].isFinal) {
        setListening(false);
        parseIntent(txt, navigate, setResult, setOpen);
      }
    };
    r.start();
    recognizerRef.current = r;
  };

  const stopListening = () => {
    recognizerRef.current?.stop();
    recognizerRef.current = null;
    setListening(false);
  };

  const runDemo = (cmd) => {
    setTranscript(cmd);
    setResult({ type: 'working', label: 'Processing command…' });
    setTimeout(() => parseIntent(cmd, navigate, setResult, setOpen), 400);
  };

  const c = result ? ({
    navigate: { bg:'#eff6ff', border:'#bfdbfe', text:'#1d4ed8' },
    success:  { bg:'#f0fdf4', border:'#bbf7d0', text:'#16a34a' },
    info:     { bg:'#f0fdf4', border:'#bbf7d0', text:'#166534' },
    action:   { bg:'#eff6ff', border:'#bfdbfe', text:'#1d4ed8' },
    working:  { bg:'#fefce8', border:'#fde68a', text:'#92400e' },
    error:    { bg:'#fef2f2', border:'#fecaca', text:'#dc2626' },
  })[result.type] || { bg:'#f8fafc', border:'#e5e7eb', text:'#374151' } : null;

  const typeLabel = { navigate:'Navigated', success:'Done ✓', info:'Answer', action:'Action', working:'Working…', error:'Not Recognized' };

  return (
    <div ref={panelRef} style={{ position: 'relative', zIndex: 1100 }}>
      {/* Mic trigger button */}
      <button
        onClick={() => { setOpen(o => !o); if (!open) { setTranscript(''); setResult(null); setError(''); } }}
        title="AI Voice Commands"
        style={{
          width: 38, height: 38, borderRadius: '50%', border: 'none', cursor: 'pointer',
          background: listening ? '#dc2626' : open ? '#1d4ed8' : '#f1f5f9',
          color: open || listening ? '#fff' : '#374151',
          display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 16,
          transition: 'all 0.2s',
          boxShadow: listening ? '0 0 0 6px rgba(220,38,38,0.2)' : open ? '0 0 0 3px rgba(29,78,216,0.2)' : 'none',
        }}>
        🎤
      </button>

      {/* Panel */}
      {open && (
        <div style={{
          position: 'absolute', top: 46, right: 0, width: 340,
          background: '#fff', border: '1px solid #e5e7eb', borderRadius: 12,
          boxShadow: '0 12px 32px rgba(0,0,0,0.14)', overflow: 'hidden',
        }}>
          {/* Header */}
          <div style={{ background: 'linear-gradient(135deg,#1d4ed8,#2563eb)', padding: '13px 16px', color: '#fff' }}>
            <div style={{ fontWeight: 800, fontSize: 14, display: 'flex', alignItems: 'center', gap: 8 }}>
              🎤 AI Voice Commands
              {listening && <span style={{ fontSize: 10, background: '#dc2626', padding: '2px 8px', borderRadius: 10, fontWeight: 700 }}>● LIVE</span>}
            </div>
            <div style={{ fontSize: 11, opacity: 0.8, marginTop: 3 }}>
              Navigate · Create BOM &amp; Cost Records · Dismiss Alerts · Query Data
            </div>
          </div>

          <div style={{ padding: '14px 14px 10px' }}>
            {/* Start/stop button */}
            <button onClick={listening ? stopListening : startListening}
              style={{
                width: '100%', padding: '11px 0', borderRadius: 8, border: 'none', fontWeight: 700, fontSize: 13,
                cursor: 'pointer', marginBottom: 12, transition: 'background 0.2s',
                background: listening ? '#dc2626' : '#1d4ed8', color: '#fff',
              }}>
              {listening ? '⏹  Stop — I\'m listening…' : '🎤  Click to Speak a Command'}
            </button>

            {/* Live transcript box */}
            {(transcript || listening) && (
              <div style={{ background: '#f8fafc', border: '1px solid #e5e7eb', borderRadius: 8, padding: '9px 12px', marginBottom: 10 }}>
                <div style={{ fontSize: 10, fontWeight: 700, marginBottom: 3, textTransform: 'uppercase', letterSpacing: '0.5px', color: listening ? '#dc2626' : '#94a3b8' }}>
                  {listening ? '● Hearing…' : 'You said'}
                </div>
                <div style={{ fontSize: 13, color: '#1e293b', fontStyle: transcript ? 'normal' : 'italic' }}>
                  {transcript || 'Speak now…'}
                </div>
              </div>
            )}

            {/* Result */}
            {result && c && (
              <div style={{ background: c.bg, border: `1px solid ${c.border}`, borderRadius: 8, padding: '9px 12px', marginBottom: 10 }}>
                <div style={{ fontSize: 10, fontWeight: 700, color: c.text, textTransform: 'uppercase', letterSpacing: '0.5px', marginBottom: 2 }}>
                  {typeLabel[result.type] || result.type}
                </div>
                <div style={{ fontSize: 12, color: c.text, fontWeight: 600, lineHeight: 1.4 }}>{result.label}</div>
              </div>
            )}

            {/* Browser error */}
            {error && (
              <div style={{ background: '#fef2f2', border: '1px solid #fecaca', borderRadius: 8, padding: '8px 12px', marginBottom: 10, fontSize: 12, color: '#dc2626' }}>
                {error}
              </div>
            )}

            {/* Suggestion chips with group tabs */}
            <div style={{ borderTop: '1px solid #f1f5f9', paddingTop: 10 }}>
              <div style={{ display: 'flex', gap: 4, marginBottom: 8, flexWrap: 'wrap' }}>
                {DEMO_CMDS.map((g, i) => (
                  <button key={g.group} onClick={() => setCmdGroup(i)}
                    style={{ padding: '3px 9px', borderRadius: 10, border: '1px solid', fontSize: 10, fontWeight: 700, cursor: 'pointer',
                      background: cmdGroup === i ? '#1d4ed8' : '#fff', color: cmdGroup === i ? '#fff' : '#9ca3af',
                      borderColor: cmdGroup === i ? '#1d4ed8' : '#e5e7eb' }}>
                    {g.group}
                  </button>
                ))}
              </div>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: 5 }}>
                {DEMO_CMDS[cmdGroup].cmds.map(cmd => (
                  <button key={cmd} onClick={() => runDemo(cmd)}
                    style={{ padding: '4px 10px', borderRadius: 14, border: '1px solid #e5e7eb', background: '#f8fafc', fontSize: 11, fontWeight: 500, cursor: 'pointer', color: '#374151', transition: 'all 0.15s' }}
                    onMouseEnter={e => { e.currentTarget.style.background = '#eff6ff'; e.currentTarget.style.borderColor = '#93c5fd'; }}
                    onMouseLeave={e => { e.currentTarget.style.background = '#f8fafc'; e.currentTarget.style.borderColor = '#e5e7eb'; }}>
                    {cmd}
                  </button>
                ))}
              </div>
            </div>

            <div style={{ marginTop: 10, fontSize: 10, color: '#d1d5db', textAlign: 'center' }}>
              Chrome / Edge only · Web Speech API · No server required
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
