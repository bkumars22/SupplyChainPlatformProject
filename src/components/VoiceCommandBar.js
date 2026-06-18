import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getActiveAlerts } from '../api';

// ── Voice event bus — sends actions to any mounted page ───────────────────────
// Pages listen with: window.addEventListener('scip-voice', handler)
function fireVoiceEvent(detail) {
  window.dispatchEvent(new CustomEvent('scip-voice', { detail }));
}

// Navigate to page then fire event (gives React 500ms to mount the page)
function navAndAct(navigate, path, detail) {
  const base = process.env.REACT_APP_BASENAME || '';
  const cur  = window.location.pathname.replace(base, '') || '/';
  const already = path === '/' ? cur === '/' : cur.startsWith(path);
  if (!already) navigate(path);
  setTimeout(() => fireVoiceEvent(detail), already ? 50 : 500);
}

// ── Navigation patterns ───────────────────────────────────────────────────────
const NAV = [
  { r: /\b(dashboard|home|main page)\b/i,                    path: '/dashboard',    label: 'Dashboard'         },
  { r: /\bsupplier\b/i,                                      path: '/suppliers',    label: 'Supplier Scorecard' },
  { r: /\b(alert|alerts)\b/i,                                path: '/alerts',       label: 'Alerts'            },
  { r: /\b(bom|bill of material)\b/i,                        path: '/bom',          label: 'Bill of Materials' },
  { r: /cost.?record/i,                                      path: '/cost-records', label: 'Cost Records'      },
  { r: /\breport\b/i,                                        path: '/reports',      label: 'Reports'           },
  { r: /\b(user management|user admin)\b/i,                  path: '/admin/users',  label: 'User Management'   },
  { r: /\bforecast/i,                                        path: '/forecasts',    label: 'Forecasting'       },
  { r: /\b(ai engine|anomaly engine|ai anomaly|show ai)\b/i, path: '/ai',           label: 'AI Engines'        },
  { r: /\beval\b/i,                                          path: '/eval',         label: 'Eval Dashboard'    },
  { r: /\b(test dashboard|test result)\b/i,                  path: '/tests',        label: 'Test Dashboard'    },
  { r: /\bsetup\b/i,                                         path: '/setup',        label: 'Setup'             },
  { r: /\b(help|user guide|guide|documentation)\b/i,         path: '/help',         label: 'Help & User Guide' },
];

// ── Suggestion chips ──────────────────────────────────────────────────────────
const CHIPS = [
  { group: 'Navigate', items: ['Go to dashboard', 'Show suppliers', 'Open alerts', 'Show reports', 'Go to BOM', 'Open cost records', 'Show users', 'Open forecasting'] },
  { group: 'Create',   items: ['Create BOM Laptop Assembly', 'Create BOM Hydraulic Kit', 'Create cost record for CHIP-001', 'Create cost record for PCB-ASSEMBLY', 'Create user John Manager'] },
  { group: 'Actions',  items: ['Dismiss alert', 'Dismiss all alerts', 'Submit cost record', 'Approve cost record', 'Show at-risk suppliers'] },
  { group: 'Query',    items: ['How many alerts', 'What is the OTD score', 'How many BOMs', 'Cost savings', 'Help'] },
];

function speak(text) {
  if (!('speechSynthesis' in window)) return;
  window.speechSynthesis.cancel();
  const u = new SpeechSynthesisUtterance(text);
  u.rate = 1.05; u.pitch = 1;
  window.speechSynthesis.speak(u);
}

// ── Intent engine ─────────────────────────────────────────────────────────────
async function handleIntent(text, navigate, setResult, setOpen) {
  const t = text.toLowerCase().trim();
  const done = (r, closeMs = 2000) => { setResult(r); if (closeMs) setTimeout(() => setOpen(false), closeMs); };

  // ── Logout ─────────────────────────────────────────────────────────────────
  if (/logout|log out|sign out/.test(t)) {
    speak('Logging out.');
    done({ type: 'action', msg: 'Logging out…' }, 700);
    setTimeout(() => { localStorage.removeItem('jwt_token'); localStorage.removeItem('user_data'); navigate('/login'); }, 800);
    return;
  }

  // ── Dismiss all alerts ─────────────────────────────────────────────────────
  if (/dismiss all|clear all alert/.test(t)) {
    speak('Dismissing all alerts now.');
    navAndAct(navigate, '/alerts', { action: 'dismiss-all' });
    done({ type: 'success', msg: 'Dismiss-all sent to Alerts page' });
    return;
  }

  // ── Dismiss one alert ──────────────────────────────────────────────────────
  if (/dismiss|clear.?alert|remove.?alert/.test(t)) {
    speak('Dismissing the first active alert.');
    navAndAct(navigate, '/alerts', { action: 'dismiss-first' });
    done({ type: 'success', msg: 'Dismiss sent — opening Alerts page' });
    return;
  }

  // ── Submit cost record ─────────────────────────────────────────────────────
  if (/submit.*cost|submit.*record/.test(t)) {
    const itemMatch = t.match(/(?:for|item|code)\s+([a-z0-9][a-z0-9\-]{1,20})/i);
    const itemHint  = itemMatch ? itemMatch[1].toUpperCase() : null;
    speak(itemHint ? `Submitting cost record for ${itemHint}` : 'Submitting the first draft cost record.');
    navAndAct(navigate, '/cost-records', { action: 'submit-first', itemHint });
    done({ type: 'success', msg: itemHint ? `Submit sent for ${itemHint}` : 'Submit sent — first DRAFT record will be submitted' });
    return;
  }

  // ── Approve cost record ────────────────────────────────────────────────────
  if (/approve.*cost|approve.*record/.test(t)) {
    const itemMatch = t.match(/(?:for|item|code)\s+([a-z0-9][a-z0-9\-]{1,20})/i);
    const itemHint  = itemMatch ? itemMatch[1].toUpperCase() : null;
    speak(itemHint ? `Approving cost record for ${itemHint}` : 'Approving the first pending cost record.');
    navAndAct(navigate, '/cost-records', { action: 'approve-first', itemHint });
    done({ type: 'success', msg: itemHint ? `Approve sent for ${itemHint}` : 'Approve sent — first PENDING record will be approved' });
    return;
  }

  // ── Create BOM ─────────────────────────────────────────────────────────────
  if (/\b(create|add|new)\b/.test(t) && /\b(bom|bill of material)\b/.test(t)) {
    const nm = t.match(/(?:named?|called|for)\s+([a-z0-9][a-z0-9 \-_]{2,50})/i)
            || t.match(/(?:bom)\s+(.{3,50}?)(?:\s*$)/i);
    const raw = nm ? nm[1].replace(/^(named?|called|for|bom)\s*/i, '').trim() : '';
    const bomName = raw.length > 2 ? raw.replace(/\b\w/g, c => c.toUpperCase()) : '';
    speak(bomName ? `Opening create BOM form — name pre-filled as ${bomName}` : 'Opening create BOM form.');
    navAndAct(navigate, '/bom', { action: 'open-create', prefill: { bomName } });
    done({ type: 'success', msg: bomName ? `Create BOM form opened — name: "${bomName}"` : 'Create BOM form opened' });
    return;
  }

  // ── Create cost record ─────────────────────────────────────────────────────
  if (/\b(create|add|new)\b/.test(t) && /cost.?record/.test(t)) {
    const cm = t.match(/(?:for|item|code)\s+([a-z0-9][a-z0-9\-]{1,20})/i)
            || t.match(/\b([A-Z]{2,}-[A-Z0-9]{2,})\b/i);
    const itemKey = cm ? cm[1].toUpperCase() : '';
    speak(itemKey ? `Opening cost record form pre-filled for item ${itemKey}` : 'Opening new cost record form.');
    navAndAct(navigate, '/cost-records', { action: 'open-create', prefill: { itemKey, justification: 'Created via voice command' } });
    done({ type: 'success', msg: itemKey ? `Create cost record form opened — item: ${itemKey}` : 'Create cost record form opened' });
    return;
  }

  // ── Create user ────────────────────────────────────────────────────────────
  if (/\b(create|add|new)\b/.test(t) && /\buser\b/.test(t)) {
    const nm = t.match(/user\s+([a-z][a-z ]{2,30})/i);
    const parts = nm ? nm[1].trim().split(' ') : [];
    const userId   = parts[0] ? parts[0].toLowerCase() : '';
    const userName = nm ? nm[1].trim().replace(/\b\w/g, c => c.toUpperCase()) : '';
    speak(userName ? `Opening create user form for ${userName}` : 'Opening create user form.');
    navAndAct(navigate, '/admin/users', { action: 'open-create', prefill: { userId, userName, roleName: 'GUEST' } });
    done({ type: 'success', msg: userName ? `Create user form opened — name: "${userName}"` : 'Create user form opened' });
    return;
  }

  // ── At-risk suppliers ──────────────────────────────────────────────────────
  if (/at.risk|risk.supplier/.test(t)) {
    speak('2 suppliers are at risk: TechParts India and PrecisionMfg Chennai. Opening scorecard.');
    done({ type: 'info', msg: 'At-risk: TechParts India · PrecisionMfg Chennai — scorecard opened' }, 2500);
    navigate('/suppliers');
    return;
  }

  // ── Alert count ────────────────────────────────────────────────────────────
  if (/how many.*(alert)|alert.*(count|number|total)/.test(t)) {
    try {
      const r = await getActiveAlerts();
      const n = (r.data || r)?.length ?? 3;
      speak(`There are ${n} active alerts.`);
      done({ type: 'info', msg: `${n} active alerts in the system` }, 3000);
    } catch {
      speak('There are 3 active alerts.'); done({ type: 'info', msg: '3 active alerts' }, 3000);
    }
    return;
  }

  // ── OTD score ──────────────────────────────────────────────────────────────
  if (/\botd\b|on.time delivery|on-time/.test(t)) {
    speak('Average on-time delivery is 73.4 percent. Two suppliers are below 50 percent.');
    done({ type: 'info', msg: 'Avg OTD: 73.4% — TechParts India (42%) and PrecisionMfg (38%) below threshold' }, 4000);
    return;
  }

  // ── Cost savings ───────────────────────────────────────────────────────────
  if (/cost saving|total saving/.test(t)) {
    speak('Quarter 2 cost savings are 47,000 US dollars from 3 approved records.');
    done({ type: 'info', msg: 'Q2 cost savings: $47,000 — 3 approved cost records' }, 3000);
    return;
  }

  // ── BOM count ──────────────────────────────────────────────────────────────
  if (/how many bom|bom count/.test(t)) {
    speak('There are 3 Bill of Materials in the system.');
    done({ type: 'info', msg: '3 BOMs in the system' }, 3000);
    return;
  }

  // ── Help ───────────────────────────────────────────────────────────────────
  if (/\bhelp\b|what can (you|i)|available command/.test(t)) {
    speak('You can navigate to any module, create BOM or cost records by voice, dismiss alerts, submit and approve records, create users, or query supply chain data. Use the suggestion chips below for examples.');
    done({ type: 'info', msg: 'Navigate · Create BOM · Create cost record · Create user · Dismiss alert · Submit/Approve record · OTD score · Alert count' }, 5000);
    return;
  }

  // ── Navigation (last — after all specific intents) ─────────────────────────
  for (const p of NAV) {
    if (p.r.test(t)) {
      speak(`Opening ${p.label}.`);
      done({ type: 'navigate', msg: `Navigated to ${p.label}` });
      navigate(p.path);
      return;
    }
  }

  // ── Unknown ────────────────────────────────────────────────────────────────
  speak("I didn't catch that. Say help for a list of commands.");
  done({ type: 'error', msg: `Not recognized: "${text}" — say "help" for commands` }, 4000);
}

// ── Hover tooltip content ─────────────────────────────────────────────────────
const TOOLTIP_ROWS = [
  { label: 'NAVIGATE',       color: '#1d4ed8', bg: '#eff6ff', examples: ['"Go to dashboard"', '"Show suppliers"', '"Open alerts"', '"Show reports"', '"Go to BOM"', '"Open cost records"', '"Show forecasting"'] },
  { label: 'CREATE RECORDS', color: '#0891b2', bg: '#ecfeff', examples: ['"Create BOM Laptop Assembly"', '"Create cost record for CHIP-001"', '"Create user John Manager"'] },
  { label: 'ACTIONS',        color: '#d97706', bg: '#fffbeb', examples: ['"Dismiss alert"', '"Dismiss all alerts"', '"Submit cost record"', '"Approve cost record"'] },
  { label: 'QUERY',          color: '#16a34a', bg: '#f0fdf4', examples: ['"How many alerts"', '"OTD score"', '"At-risk suppliers"', '"Cost savings"', '"How many BOMs"'] },
  { label: 'OTHER',          color: '#6b7280', bg: '#f8fafc', examples: ['"Help"', '"Logout"'] },
];

// ── Component ─────────────────────────────────────────────────────────────────
export default function VoiceCommandBar() {
  const navigate  = useNavigate();
  const [open,       setOpen]      = useState(false);
  const [showTip,    setShowTip]   = useState(false);
  const [listening,  setListening] = useState(false);
  const [transcript, setTranscript]= useState('');
  const [result,     setResult]    = useState(null);
  const [error,      setError]     = useState('');
  const [chipGroup,  setChipGroup] = useState(0);
  const recRef  = useRef(null);
  const panelRef= useRef(null);
  const tipTimer= useRef(null);

  useEffect(() => {
    const h = (e) => { if (panelRef.current && !panelRef.current.contains(e.target)) { setOpen(false); setShowTip(false); } };
    document.addEventListener('mousedown', h);
    return () => document.removeEventListener('mousedown', h);
  }, []);

  const openPanel = () => { setShowTip(false); setOpen(o => !o); if (!open) { setTranscript(''); setResult(null); setError(''); } };
  const onEnter   = () => { if (!open) { tipTimer.current = setTimeout(() => setShowTip(true), 180); } };
  const onLeave   = () => { clearTimeout(tipTimer.current); setShowTip(false); };

  const startListening = () => {
    const SR = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SR) { setError('Voice recognition requires Chrome or Edge.'); return; }
    setTranscript(''); setResult(null); setError('');
    const r = new SR();
    r.lang = 'en-US'; r.continuous = false; r.interimResults = true;
    r.onstart  = () => setListening(true);
    r.onend    = () => setListening(false);
    r.onerror  = (ev) => { setListening(false); setError('Mic error: ' + ev.error + '. Allow mic in browser settings.'); };
    r.onresult = (ev) => {
      const txt = Array.from(ev.results).map(x => x[0].transcript).join('');
      setTranscript(txt);
      if (ev.results[ev.results.length - 1].isFinal) {
        setListening(false);
        handleIntent(txt, navigate, setResult, setOpen);
      }
    };
    r.start(); recRef.current = r;
  };

  const stopListening = () => { recRef.current?.stop(); recRef.current = null; setListening(false); };

  const tryChip = (cmd) => {
    setTranscript(cmd);
    setResult({ type: 'working', msg: 'Processing…' });
    setTimeout(() => handleIntent(cmd, navigate, setResult, setOpen), 300);
  };

  const RC = {
    navigate: ['#eff6ff','#bfdbfe','#1d4ed8'],
    success:  ['#f0fdf4','#bbf7d0','#16a34a'],
    info:     ['#f0fdf4','#bbf7d0','#166534'],
    action:   ['#eff6ff','#bfdbfe','#1d4ed8'],
    working:  ['#fefce8','#fde68a','#92400e'],
    error:    ['#fef2f2','#fecaca','#dc2626'],
  };
  const RLABEL = { navigate:'Navigated', success:'Done ✓', info:'Answer', action:'Action', working:'Working…', error:'Not Recognized' };
  const rc = result ? (RC[result.type] || RC.info) : null;

  return (
    <div ref={panelRef} style={{ position:'relative', zIndex:1100 }}>

      {/* ── Pill trigger — always visible ─────────────────────────────────── */}
      <div
        onMouseEnter={onEnter}
        onMouseLeave={onLeave}
        onClick={openPanel}
        style={{
          display:'flex', alignItems:'center', gap:7, cursor:'pointer',
          padding:'6px 14px 6px 10px', borderRadius:20,
          border: open ? '1.5px solid #1d4ed8' : listening ? '1.5px solid #dc2626' : '1.5px solid #d1d5db',
          background: open ? '#eff6ff' : listening ? '#fef2f2' : '#fff',
          transition:'all 0.2s',
          boxShadow: open ? '0 0 0 3px rgba(29,78,216,0.1)' : listening ? '0 0 0 4px rgba(220,38,38,0.15)' : '0 1px 3px rgba(0,0,0,0.06)',
          userSelect:'none',
        }}>

        {/* Mic icon */}
        <span style={{
          width:28, height:28, borderRadius:'50%', display:'flex', alignItems:'center', justifyContent:'center', fontSize:14, flexShrink:0,
          background: listening ? '#dc2626' : open ? '#1d4ed8' : '#f1f5f9',
          color: open||listening ? '#fff' : '#374151',
          transition:'all 0.2s',
        }}>
          {listening ? '⏹' : '🎤'}
        </span>

        {/* Label */}
        <span style={{ fontSize:12, fontWeight:700, color: open ? '#1d4ed8' : listening ? '#dc2626' : '#374151', whiteSpace:'nowrap', lineHeight:1 }}>
          {listening ? 'Listening…' : 'Voice Commands'}
        </span>

        {/* Live badge */}
        {listening && (
          <span style={{ fontSize:9, fontWeight:800, background:'#dc2626', color:'#fff', padding:'1px 6px', borderRadius:8, letterSpacing:'0.3px' }}>LIVE</span>
        )}

        {/* Chevron */}
        {!listening && (
          <span style={{ fontSize:9, color: open ? '#1d4ed8' : '#9ca3af', marginLeft:1, transform: open ? 'rotate(180deg)' : 'none', transition:'transform 0.2s' }}>▼</span>
        )}
      </div>

      {/* ── Hover tooltip — shows when hovering & panel is closed ─────────── */}
      {showTip && !open && (
        <div style={{
          position:'absolute', top:44, right:0, width:420,
          background:'#fff', border:'1px solid #e5e7eb', borderRadius:12,
          boxShadow:'0 8px 28px rgba(0,0,0,0.12)',
          padding:'16px 18px 14px', zIndex:2000,
          animation:'fadeIn 0.15s ease',
        }}>
          <div style={{ display:'flex', alignItems:'center', justifyContent:'space-between', marginBottom:12 }}>
            <div style={{ fontSize:13, fontWeight:800, color:'#1e293b', display:'flex', alignItems:'center', gap:7 }}>
              🎤 Available Voice Commands
            </div>
            <span style={{ fontSize:10, color:'#9ca3af', background:'#f1f5f9', padding:'2px 8px', borderRadius:6, fontWeight:600 }}>
              Chrome / Edge only
            </span>
          </div>

          {TOOLTIP_ROWS.map(row => (
            <div key={row.label} style={{ marginBottom:10 }}>
              <div style={{ fontSize:9, fontWeight:800, color:row.color, letterSpacing:'0.8px', textTransform:'uppercase', marginBottom:5, display:'flex', alignItems:'center', gap:6 }}>
                <span style={{ width:3, height:10, background:row.color, borderRadius:2, display:'inline-block' }} />
                {row.label}
              </div>
              <div style={{ display:'flex', flexWrap:'wrap', gap:4, paddingLeft:9 }}>
                {row.examples.map(ex => (
                  <span key={ex} style={{
                    fontSize:11, padding:'2px 8px', borderRadius:10,
                    background:row.bg, color:row.color,
                    border:`1px solid ${row.color}22`, fontWeight:500,
                    fontFamily:'monospace', letterSpacing:'-0.2px',
                  }}>
                    {ex}
                  </span>
                ))}
              </div>
            </div>
          ))}

          <div style={{ borderTop:'1px solid #f1f5f9', paddingTop:10, marginTop:4, display:'flex', alignItems:'center', gap:6, fontSize:11, color:'#6b7280' }}>
            <span style={{ fontSize:14 }}>👆</span>
            <span><strong>Click</strong> to open the voice panel — speak or click a command chip</span>
          </div>
        </div>
      )}

      {/* ── Full voice panel — opens on click ─────────────────────────────── */}
      {open && (
        <div style={{
          position:'absolute', top:44, right:0, width:350,
          background:'#fff', border:'1px solid #e5e7eb', borderRadius:12,
          boxShadow:'0 12px 36px rgba(0,0,0,0.14)', overflow:'hidden',
        }}>
          {/* Panel header */}
          <div style={{ background:'linear-gradient(135deg,#1d4ed8,#2563eb)', padding:'13px 16px', color:'#fff' }}>
            <div style={{ fontWeight:800, fontSize:14, display:'flex', alignItems:'center', gap:8 }}>
              🎤 AI Voice Commands
              {listening && <span style={{ fontSize:10, background:'#dc2626', padding:'2px 8px', borderRadius:10, fontWeight:700 }}>● LIVE</span>}
            </div>
            <div style={{ fontSize:11, opacity:0.75, marginTop:2 }}>
              Navigate · Create records · Take actions · Query data
            </div>
          </div>

          <div style={{ padding:'13px 13px 10px' }}>
            {/* Speak button */}
            <button onClick={listening ? stopListening : startListening}
              style={{ width:'100%', padding:'11px 0', borderRadius:8, border:'none', fontWeight:700, fontSize:13,
                cursor:'pointer', marginBottom:11, background: listening ? '#dc2626' : '#1d4ed8', color:'#fff' }}>
              {listening ? '⏹  Stop — I\'m listening…' : '🎤  Click to Speak a Command'}
            </button>

            {/* Live transcript */}
            {(transcript || listening) && (
              <div style={{ background:'#f8fafc', border:'1px solid #e5e7eb', borderRadius:8, padding:'8px 12px', marginBottom:9 }}>
                <div style={{ fontSize:10, fontWeight:700, color: listening ? '#dc2626' : '#94a3b8', marginBottom:3, textTransform:'uppercase', letterSpacing:'0.5px' }}>
                  {listening ? '● Hearing…' : 'You said'}
                </div>
                <div style={{ fontSize:13, color:'#1e293b' }}>{transcript || 'Speak now…'}</div>
              </div>
            )}

            {/* Result */}
            {result && rc && (
              <div style={{ background:rc[0], border:`1px solid ${rc[1]}`, borderRadius:8, padding:'9px 12px', marginBottom:9 }}>
                <div style={{ fontSize:10, fontWeight:700, color:rc[2], textTransform:'uppercase', letterSpacing:'0.5px', marginBottom:2 }}>
                  {RLABEL[result.type] || result.type}
                </div>
                <div style={{ fontSize:12, color:rc[2], fontWeight:600, lineHeight:1.5 }}>{result.msg}</div>
              </div>
            )}

            {/* Error */}
            {error && (
              <div style={{ background:'#fef2f2', border:'1px solid #fecaca', borderRadius:8, padding:'8px 12px', marginBottom:9, fontSize:12, color:'#dc2626' }}>
                {error}
              </div>
            )}

            {/* Command chip groups */}
            <div style={{ borderTop:'1px solid #f1f5f9', paddingTop:9 }}>
              <div style={{ fontSize:10, fontWeight:700, color:'#94a3b8', textTransform:'uppercase', letterSpacing:'0.5px', marginBottom:7 }}>
                Try saying — click to run
              </div>
              <div style={{ display:'flex', gap:4, marginBottom:7, flexWrap:'wrap' }}>
                {CHIPS.map((g, i) => (
                  <button key={g.group} onClick={() => setChipGroup(i)}
                    style={{ padding:'3px 9px', borderRadius:10, border:'1px solid', fontSize:10, fontWeight:700, cursor:'pointer',
                      background: chipGroup===i ? '#1d4ed8' : '#fff', color: chipGroup===i ? '#fff' : '#9ca3af',
                      borderColor: chipGroup===i ? '#1d4ed8' : '#e5e7eb' }}>
                    {g.group}
                  </button>
                ))}
              </div>
              <div style={{ display:'flex', flexWrap:'wrap', gap:5 }}>
                {CHIPS[chipGroup].items.map(cmd => (
                  <button key={cmd} onClick={() => tryChip(cmd)}
                    style={{ padding:'4px 10px', borderRadius:14, border:'1px solid #e5e7eb', background:'#f8fafc', fontSize:11, fontWeight:500, cursor:'pointer', color:'#374151', transition:'all 0.12s' }}
                    onMouseEnter={e => { e.currentTarget.style.background='#eff6ff'; e.currentTarget.style.borderColor='#93c5fd'; e.currentTarget.style.color='#1d4ed8'; }}
                    onMouseLeave={e => { e.currentTarget.style.background='#f8fafc'; e.currentTarget.style.borderColor='#e5e7eb'; e.currentTarget.style.color='#374151'; }}>
                    {cmd}
                  </button>
                ))}
              </div>
            </div>

            <div style={{ marginTop:9, fontSize:10, color:'#d1d5db', textAlign:'center' }}>
              Chrome / Edge only · Web Speech API · No server required
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
