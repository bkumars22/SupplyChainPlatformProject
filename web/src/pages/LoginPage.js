import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login as apiLogin } from '../api';

const DEMO = process.env.REACT_APP_DEMO_MODE === 'true';

export default function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading,  setLoading]  = useState(false);
  const [error,    setError]    = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    if (!username || !password) { setError('Please enter username and password'); return; }
    try {
      setLoading(true); setError('');
      const r = await apiLogin(username, password);
      localStorage.setItem('jwt_token', r.data.token);
      localStorage.setItem('user_data', JSON.stringify({ userId: r.data.userId || username, role: r.data.role }));
      navigate('/dashboard');
    } catch(e) {
      setError('Invalid username or password');
    } finally { setLoading(false); }
  };

  return (
    <div style={{ minHeight:'100vh', background:'linear-gradient(135deg, #0f172a 0%, #2f3f56 50%, #a9790f 100%)', display:'flex', alignItems:'center', justifyContent:'center', padding:'20px' }}>
      <div style={{ width:'100%', maxWidth:'420px' }}>
        <div style={{ textAlign:'center', marginBottom:'32px' }}>
          <div style={{ fontSize:'40px', marginBottom:'12px' }}>SC</div>
          <h1 style={{ color:'#fff', fontSize:'24px', fontWeight:'800', margin:'0 0 6px' }}>Supply Chain Intelligence</h1>
          <p style={{ color:'#f3e6c8', margin:0, fontSize:'14px' }}>Enterprise Platform</p>
        </div>
        <div style={{ background:'#fff', borderRadius:'20px', padding:'36px', boxShadow:'0 25px 60px rgba(0,0,0,0.3)' }}>
          <h2 style={{ fontSize:'20px', fontWeight:'800', margin:'0 0 24px', textAlign:'center', color:'#0f172a' }}>Sign In</h2>
          {error && (
            <div style={{ background:'#fff1f2', border:'1px solid #fca5a5', borderRadius:'8px', padding:'10px 14px', marginBottom:'16px', color:'#dc2626', fontSize:'13px', fontWeight:'600' }}>
              {error}
            </div>
          )}
          <form onSubmit={handleLogin}>
            <div style={{ marginBottom:'16px' }}>
              <label style={{ display:'block', fontSize:'13px', fontWeight:'700', color:'#374151', marginBottom:'6px' }}>Username</label>
              <input type="text" value={username} onChange={e => setUsername(e.target.value)}
                placeholder="Enter username" autoFocus
                style={{ width:'100%', padding:'11px 14px', border:'1px solid #d1d5db', borderRadius:'10px', fontSize:'14px', outline:'none', transition:'border 0.15s', boxSizing:'border-box' }}
                onFocus={e => e.target.style.borderColor='#a9790f'}
                onBlur={e  => e.target.style.borderColor='#d1d5db'} />
            </div>
            <div style={{ marginBottom:'24px' }}>
              <label style={{ display:'block', fontSize:'13px', fontWeight:'700', color:'#374151', marginBottom:'6px' }}>Password</label>
              <input type="password" value={password} onChange={e => setPassword(e.target.value)}
                placeholder="Enter password"
                style={{ width:'100%', padding:'11px 14px', border:'1px solid #d1d5db', borderRadius:'10px', fontSize:'14px', outline:'none', boxSizing:'border-box' }}
                onFocus={e => e.target.style.borderColor='#a9790f'}
                onBlur={e  => e.target.style.borderColor='#d1d5db'} />
            </div>
            <button type="submit" disabled={loading}
              style={{ width:'100%', padding:'13px', background: loading ? '#d8b674' : '#a9790f', color:'#fff', border:'none', borderRadius:'10px', fontSize:'15px', fontWeight:'800', cursor: loading ? 'not-allowed' : 'pointer', transition:'background 0.2s' }}>
              {loading ? 'Signing in...' : 'Sign In'}
            </button>
          </form>
          <div style={{ marginTop:'20px', padding:'12px', background:'#f8fafc', borderRadius:'8px', textAlign:'center' }}>
            {DEMO
              ? <p style={{ margin:0, fontSize:'12px', color:'#6b7280' }}>Demo mode — <strong>any username &amp; password</strong> accepted</p>
              : <p style={{ margin:0, fontSize:'12px', color:'#6b7280' }}>Default credentials: <strong>kumar / Kumar@2026</strong></p>
            }
          </div>
        </div>
      </div>
    </div>
  );
}
