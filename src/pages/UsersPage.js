import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';

const api = axios.create({ baseURL: '/supchain', timeout: 15000 });
api.interceptors.request.use(c => {
  const t = localStorage.getItem('jwt_token');
  if (t) c.headers.Authorization = 'Bearer ' + t;
  return c;
});

const ROLES = ['ADMIN', 'BUS_ADMIN', 'GUEST'];

export default function UsersPage() {
  const [users,    setUsers]    = useState([]);
  const [loading,  setLoading]  = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [msg,      setMsg]      = useState({ text:'', type:'' });
  const [form,     setForm]     = useState({ userId:'', userName:'', role:'GUEST', password:'' });

  const flash = (text, type='success') => { setMsg({ text, type }); setTimeout(() => setMsg({ text:'', type:'' }), 3000); };

  const load = useCallback(async () => {
    try {
      setLoading(true);
      const r = await api.get('/api/users');
      setUsers(r.data || []);
    } catch(e) {
      // Fallback mock data if endpoint not available
      setUsers([
        { userId:'ADMIN',  userName:'Administrator', role:'ADMIN',     enabled:true,  lastLogin:'2024-11-15' },
        { userId:'kumar',  userName:'Kumar Swamy',   role:'ADMIN',     enabled:true,  lastLogin:'2024-11-15' },
      ]);
    } finally { setLoading(false); }
  }, []);

  useEffect(() => { load(); }, [load]);

  const handleCreate = async () => {
    if (!form.userId || !form.userName) { flash('User ID and Name are required', 'error'); return; }
    try {
      await api.post('/api/users', form);
      flash('User created successfully');
      setShowForm(false);
      load();
    } catch(e) {
      flash('Failed to create user: ' + (e.response?.data?.message || e.message), 'error');
    }
  };

  const toggleEnabled = async (userId, current) => {
    try {
      await api.put('/api/users/' + userId + '/toggle');
      flash(`User ${current ? 'disabled' : 'enabled'}`);
      load();
    } catch(e) { flash('Failed to update user', 'error'); }
  };

  const ROLE_COLORS = { ADMIN: { bg:'#fff1f2', color:'#dc2626' }, BUS_ADMIN: { bg:'#eff6ff', color:'#1d4ed8' }, GUEST: { bg:'#f0fdf4', color:'#15803d' } };

  return (
    <div style={{ padding:'24px', maxWidth:'1000px', margin:'0 auto' }}>
      {msg.text && (
        <div style={{ padding:'12px 16px', borderRadius:'8px', marginBottom:'16px',
          background:msg.type==='error'?'#fff1f2':'#f0fdf4', color:msg.type==='error'?'#dc2626':'#15803d',
          border:`1px solid ${msg.type==='error'?'#fecaca':'#bbf7d0'}` }}>{msg.text}</div>
      )}

      <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:'24px' }}>
        <div>
          <h1 style={{ fontSize:'24px', fontWeight:'800', margin:0 }}>User Management</h1>
          <p style={{ color:'#6b7280', margin:'4px 0 0' }}>Manage platform users and role assignments</p>
        </div>
        <button onClick={()=>{ setForm({userId:'',userName:'',role:'GUEST',password:''}); setShowForm(true); }}
          style={{ background:'#1d4ed8', color:'#fff', border:'none', borderRadius:'8px', padding:'10px 20px', fontWeight:'700', cursor:'pointer', fontSize:'14px' }}>
          + Add User
        </button>
      </div>

      {/* Stats */}
      <div style={{ display:'grid', gridTemplateColumns:'repeat(3,1fr)', gap:'12px', marginBottom:'24px' }}>
        {[
          { label:'Total Users', val:users.length, color:'#1d4ed8' },
          { label:'Active', val:users.filter(u=>u.enabled!==false).length, color:'#15803d' },
          { label:'Admins', val:users.filter(u=>u.role==='ADMIN').length, color:'#dc2626' },
        ].map(s => (
          <div key={s.label} style={{ background:'#fff', border:'1px solid #e5e7eb', borderRadius:'10px', padding:'16px', borderTop:`3px solid ${s.color}` }}>
            <div style={{ fontSize:'28px', fontWeight:'800', color:s.color }}>{s.val}</div>
            <div style={{ fontSize:'12px', color:'#6b7280', textTransform:'uppercase', letterSpacing:'0.5px', marginTop:'4px' }}>{s.label}</div>
          </div>
        ))}
      </div>

      {/* Users table */}
      {loading ? (
        <div style={{ textAlign:'center', padding:'60px', color:'#6b7280' }}>Loading users...</div>
      ) : (
        <div style={{ background:'#fff', borderRadius:'12px', border:'1px solid #e5e7eb', overflow:'hidden' }}>
          <table style={{ width:'100%', borderCollapse:'collapse', fontSize:'14px' }}>
            <thead>
              <tr style={{ background:'#f8fafc' }}>
                {['User ID','Name','Role','Status','Last Login','Actions'].map(h => (
                  <th key={h} style={{ padding:'12px 16px', textAlign:'left', fontWeight:'700', fontSize:'11px', textTransform:'uppercase', letterSpacing:'1px', color:'#6b7280', borderBottom:'1px solid #e5e7eb' }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {users.map((u, i) => {
                const rc = ROLE_COLORS[u.role] || ROLE_COLORS.GUEST;
                const isEnabled = u.enabled !== false;
                return (
                  <tr key={i} style={{ borderBottom:'1px solid #f1f5f9' }}
                    onMouseEnter={e=>e.currentTarget.style.background='#f8fafc'}
                    onMouseLeave={e=>e.currentTarget.style.background='#fff'}>
                    <td style={{ padding:'12px 16px', fontWeight:'700', fontFamily:'monospace' }}>{u.userId}</td>
                    <td style={{ padding:'12px 16px' }}>{u.userName}</td>
                    <td style={{ padding:'12px 16px' }}>
                      <span style={{ background:rc.bg, color:rc.color, padding:'3px 10px', borderRadius:'20px', fontSize:'11px', fontWeight:'700' }}>{u.role}</span>
                    </td>
                    <td style={{ padding:'12px 16px' }}>
                      <span style={{ background:isEnabled?'#f0fdf4':'#f1f5f9', color:isEnabled?'#15803d':'#6b7280', padding:'3px 10px', borderRadius:'20px', fontSize:'11px', fontWeight:'700' }}>
                        {isEnabled ? '* Active' : '- Disabled'}
                      </span>
                    </td>
                    <td style={{ padding:'12px 16px', color:'#6b7280' }}>{u.lastLogin || '-'}</td>
                    <td style={{ padding:'12px 16px' }}>
                      <button onClick={()=>toggleEnabled(u.userId, isEnabled)} style={{
                        background: isEnabled ? '#fff1f2' : '#f0fdf4',
                        color: isEnabled ? '#dc2626' : '#15803d',
                        border: `1px solid ${isEnabled ? '#fca5a5' : '#86efac'}`,
                        borderRadius:'6px', padding:'4px 12px', fontSize:'12px', fontWeight:'700', cursor:'pointer' }}>
                        {isEnabled ? 'Disable' : 'Enable'}
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}

      {/* Create user modal */}
      {showForm && (
        <div style={{ position:'fixed', inset:0, background:'rgba(0,0,0,0.5)', display:'flex', alignItems:'center', justifyContent:'center', zIndex:1000 }}>
          <div style={{ background:'#fff', borderRadius:'14px', padding:'32px', width:'440px', maxWidth:'95vw' }}>
            <h2 style={{ fontSize:'18px', fontWeight:'800', marginBottom:'20px' }}>Add New User</h2>
            {[
              { label:'User ID *', key:'userId', ph:'e.g. john.smith' },
              { label:'Full Name *', key:'userName', ph:'e.g. John Smith' },
              { label:'Password', key:'password', ph:'Leave blank for no password', type:'password' },
            ].map(f => (
              <div key={f.key} style={{ marginBottom:'14px' }}>
                <label style={{ fontSize:'13px', fontWeight:'600', display:'block', marginBottom:'6px' }}>{f.label}</label>
                <input type={f.type||'text'} value={form[f.key]} placeholder={f.ph}
                  onChange={e=>setForm(p=>({...p,[f.key]:e.target.value}))}
                  style={{ width:'100%', padding:'10px 12px', border:'1px solid #d1d5db', borderRadius:'8px', fontSize:'14px', outline:'none' }} />
              </div>
            ))}
            <div style={{ marginBottom:'20px' }}>
              <label style={{ fontSize:'13px', fontWeight:'600', display:'block', marginBottom:'6px' }}>Role</label>
              <select value={form.role} onChange={e=>setForm(p=>({...p,role:e.target.value}))}
                style={{ width:'100%', padding:'10px 12px', border:'1px solid #d1d5db', borderRadius:'8px', fontSize:'14px', outline:'none', background:'#fff' }}>
                {ROLES.map(r => <option key={r} value={r}>{r}</option>)}
              </select>
            </div>
            <div style={{ display:'flex', gap:'10px', justifyContent:'flex-end' }}>
              <button onClick={()=>setShowForm(false)} style={{ padding:'10px 20px', border:'1px solid #d1d5db', borderRadius:'8px', background:'#fff', cursor:'pointer', fontWeight:'600' }}>Cancel</button>
              <button onClick={handleCreate} style={{ padding:'10px 20px', background:'#1d4ed8', color:'#fff', border:'none', borderRadius:'8px', cursor:'pointer', fontWeight:'700' }}>Create User</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
