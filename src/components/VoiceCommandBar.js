import React, { useState, useRef, useEffect } from 'react';

const DEMO = process.env.REACT_APP_DEMO_MODE === 'true';

const ROUTES = {
  dashboard:   '/dashboard',
  supplier:    '/suppliers',
  suppliers:   '/suppliers',
  alert:       '/alerts',
  alerts:      '/alerts',
  bom:         '/bom',
  'bill of':   '/bom',
  cost:        '/cost-records',
  report:      '/reports',
  reports:     '/reports',
  user:        '/admin/users',
  users:       '/admin/users',
  forecast:    '/forecasts',
  forecasting: '/forecasts',
  ai:          '/ai',
  eval:        '/eval',
  test:        '/tests',
  setup:       '/setup',
};

const DEMO_COMMANDS = [
  'Go to dashboard',
  'Show suppliers',
  'Open alerts',
  'Show reports',
  'Go to BOM',
  'Open cost records',
  'Show forecasting',
  'Open AI engines',
];

function speak(text) {
  if ('speechSynthesis' in window) {
    window.speechSynthesis.cancel();
    const u = new SpeechSynthesisUtterance(text);
    u.rate = 1.05;
    u.pitch = 1;
    window.speechSynthesis.speak(u);
  }
}

function parseIntent(transcript, navigate) {
  const t = transcript.toLowerCase().trim();

  // Navigation intent
  for (const [keyword, path] of Object.entries(ROUTES)) {
    if (t.includes(keyword)) {
      navigate(path);
      const label = keyword.charAt(0).toUpperCase() + keyword.slice(1);
      speak(`Opening ${label}`);
      return { type: 'navigate', path, label: `Navigated to ${path}` };
    }
  }

  // Query intents
  if (t.includes('how many alert') || t.includes('alert count')) {
    speak('There are 3 active alerts in the system.');
    return { type: 'info', label: '3 active alerts' };
  }
  if (t.includes('otd') || t.includes('on time delivery') || t.includes('on-time')) {
    speak('The average on-time delivery score is 73.4 percent.');
    return { type: 'info', label: 'Avg OTD: 73.4%' };
  }
  if (t.includes('at risk') || t.includes('risk supplier')) {
    navigate('/suppliers');
    speak('Showing suppliers. 2 suppliers are flagged as at-risk.');
    return { type: 'navigate', path: '/suppliers', label: '2 at-risk suppliers — opened scorecard' };
  }
  if (t.includes('help') || t.includes('what can') || t.includes('command')) {
    speak('You can say: go to dashboard, show suppliers, open alerts, show reports, or ask about OTD scores and alert counts.');
    return { type: 'info', label: 'Say: "go to dashboard", "show suppliers", "open alerts", "show reports"' };
  }
  if (t.includes('logout') || t.includes('sign out')) {
    speak('Logging out now.');
    return { type: 'action', label: 'Logout requested — use the Logout button in the sidebar' };
  }

  speak("Sorry, I didn't understand that. Say 'help' for a list of commands.");
  return { type: 'error', label: `Not recognized: "${transcript}"` };
}

export default function VoiceCommandBar({ navigate }) {
  const [open,      setOpen]      = useState(false);
  const [listening, setListening] = useState(false);
  const [transcript, setTranscript] = useState('');
  const [result,    setResult]    = useState(null);
  const [error,     setError]     = useState('');
  const recognizerRef = useRef(null);
  const panelRef      = useRef(null);

  // Close panel on outside click
  useEffect(() => {
    const handler = (e) => { if (panelRef.current && !panelRef.current.contains(e.target)) setOpen(false); };
    if (open) document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, [open]);

  const startListening = () => {
    const SR = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SR) {
      setError('Voice recognition is not supported in this browser. Please use Chrome or Edge.');
      return;
    }
    setTranscript(''); setResult(null); setError('');
    const r = new SR();
    r.lang = 'en-US';
    r.continuous = false;
    r.interimResults = true;

    r.onstart  = () => setListening(true);
    r.onend    = () => setListening(false);
    r.onerror  = (e) => { setListening(false); setError('Microphone error: ' + e.error); };
    r.onresult = (e) => {
      const t = Array.from(e.results).map(r => r[0].transcript).join('');
      setTranscript(t);
      if (e.results[e.results.length - 1].isFinal) {
        const res = parseIntent(t, navigate);
        setResult(res);
        setListening(false);
      }
    };

    r.start();
    recognizerRef.current = r;
  };

  const stopListening = () => {
    if (recognizerRef.current) { recognizerRef.current.stop(); recognizerRef.current = null; }
    setListening(false);
  };

  const runDemo = (cmd) => {
    setTranscript(cmd);
    setResult(null);
    setTimeout(() => {
      const res = parseIntent(cmd, navigate);
      setResult(res);
    }, 600);
  };

  const resultColor = result
    ? result.type === 'error' ? '#dc2626' : result.type === 'navigate' ? '#1d4ed8' : '#16a34a'
    : '#374151';

  return (
    <div ref={panelRef} style={{ position: 'relative' }}>
      {/* Mic button */}
      <button
        onClick={() => { setOpen(o => !o); if (!open) { setTranscript(''); setResult(null); setError(''); } }}
        title="Voice Commands"
        style={{
          width: 38, height: 38, borderRadius: '50%', border: 'none', cursor: 'pointer',
          background: listening ? '#dc2626' : open ? '#1d4ed8' : '#f1f5f9',
          color: open || listening ? '#fff' : '#374151',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          fontSize: 16, transition: 'all 0.2s',
          boxShadow: listening ? '0 0 0 4px rgba(220,38,38,0.25)' : 'none',
          animation: listening ? 'pulse 1s infinite' : 'none',
        }}>
        🎤
      </button>

      {/* Panel */}
      {open && (
        <div style={{
          position: 'absolute', top: 46, right: 0, width: 320,
          background: '#fff', border: '1px solid #e5e7eb', borderRadius: 12,
          boxShadow: '0 8px 24px rgba(0,0,0,0.12)', zIndex: 2000, overflow: 'hidden',
        }}>
          {/* Header */}
          <div style={{ background: '#1d4ed8', padding: '12px 16px', color: '#fff' }}>
            <div style={{ fontWeight: 700, fontSize: 13 }}>Voice Commands</div>
            <div style={{ fontSize: 11, opacity: 0.8, marginTop: 2 }}>
              Say a command or click a suggestion below
            </div>
          </div>

          <div style={{ padding: 14 }}>
            {/* Mic control */}
            <div style={{ display: 'flex', gap: 10, marginBottom: 14 }}>
              <button
                onClick={listening ? stopListening : startListening}
                style={{
                  flex: 1, padding: '10px 0', borderRadius: 8, border: 'none', fontWeight: 700, fontSize: 13,
                  cursor: 'pointer', transition: 'all 0.2s',
                  background: listening ? '#dc2626' : '#1d4ed8', color: '#fff',
                }}>
                {listening ? '⏹ Stop Listening' : '🎤 Start Listening'}
              </button>
            </div>

            {/* Transcript */}
            {(transcript || listening) && (
              <div style={{ background: '#f8fafc', border: '1px solid #e5e7eb', borderRadius: 8, padding: '10px 12px', marginBottom: 12, minHeight: 36 }}>
                <div style={{ fontSize: 10, fontWeight: 700, color: '#94a3b8', marginBottom: 3, textTransform: 'uppercase', letterSpacing: '0.5px' }}>
                  {listening ? '● Listening…' : 'Heard'}
                </div>
                <div style={{ fontSize: 13, color: '#1e293b', fontStyle: transcript ? 'normal' : 'italic' }}>
                  {transcript || 'Speak now…'}
                </div>
              </div>
            )}

            {/* Result */}
            {result && (
              <div style={{ background: result.type === 'error' ? '#fef2f2' : result.type === 'navigate' ? '#eff6ff' : '#f0fdf4', border: `1px solid ${result.type === 'error' ? '#fecaca' : result.type === 'navigate' ? '#bfdbfe' : '#bbf7d0'}`, borderRadius: 8, padding: '10px 12px', marginBottom: 12 }}>
                <div style={{ fontSize: 10, fontWeight: 700, color: resultColor, textTransform: 'uppercase', letterSpacing: '0.5px', marginBottom: 2 }}>
                  {result.type === 'navigate' ? 'Navigated' : result.type === 'info' ? 'Answer' : result.type === 'error' ? 'Not Recognized' : 'Action'}
                </div>
                <div style={{ fontSize: 12, color: resultColor, fontWeight: 600 }}>{result.label}</div>
              </div>
            )}

            {/* Error */}
            {error && (
              <div style={{ background: '#fef2f2', border: '1px solid #fecaca', borderRadius: 8, padding: '8px 12px', marginBottom: 12, fontSize: 12, color: '#dc2626' }}>
                {error}
              </div>
            )}

            {/* Suggestions */}
            <div style={{ borderTop: '1px solid #f1f5f9', paddingTop: 12 }}>
              <div style={{ fontSize: 10, fontWeight: 700, color: '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.5px', marginBottom: 8 }}>
                Try saying
              </div>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6 }}>
                {DEMO_COMMANDS.map(cmd => (
                  <button key={cmd} onClick={() => runDemo(cmd)}
                    style={{ padding: '4px 10px', borderRadius: 14, border: '1px solid #e5e7eb', background: '#f8fafc', fontSize: 11, fontWeight: 500, cursor: 'pointer', color: '#374151' }}>
                    {cmd}
                  </button>
                ))}
              </div>
            </div>

            <div style={{ marginTop: 12, fontSize: 10, color: '#d1d5db', textAlign: 'center' }}>
              Voice recognition uses your browser's built-in Web Speech API
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
