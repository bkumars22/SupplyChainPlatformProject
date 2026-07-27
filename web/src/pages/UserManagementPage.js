import React, { useState, useEffect, useCallback } from "react";
import { getUsers, createUser, updateUser, disableUser, getRoles, setPassword } from "../api";

const RC = { ADMIN: { bg:"#fef2f2", color:"#dc2626" }, BUS_ADMIN: { bg:"#eff6ff", color:"#1d4ed8" }, GUEST: { bg:"#f0fdf4", color:"#15803d" } };

function Modal({ title, onClose, children }) {
  return (
    <div style={{ position:"fixed", inset:0, background:"rgba(0,0,0,0.5)", display:"flex", alignItems:"center", justifyContent:"center", zIndex:1000 }}>
      <div style={{ background:"#fff", borderRadius:14, padding:32, width:460, maxWidth:"95vw", maxHeight:"90vh", overflowY:"auto" }}>
        <div style={{ display:"flex", justifyContent:"space-between", alignItems:"center", marginBottom:20 }}>
          <h2 style={{ fontSize:18, fontWeight:800, margin:0 }}>{title}</h2>
          <button onClick={onClose} style={{ background:"none", border:"none", fontSize:20, cursor:"pointer", color:"#6b7280" }}>x</button>
        </div>
        {children}
      </div>
    </div>
  );
}

export default function UserManagementPage() {
  const [users,    setUsers]    = useState([]);
  const [roles,    setRoles]    = useState([]);
  const [loading,  setLoading]  = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editUser, setEditUser] = useState(null);
  const [pwUser,   setPwUser]   = useState(null);
  const [newPw,    setNewPw]    = useState("");
  const [search,   setSearch]   = useState("");
  const [msg,      setMsg]      = useState({ text:"", type:"" });
  const [form,     setForm]     = useState({ userId:"", userName:"", emailId:"", roleName:"GUEST", password:"" });

  const flash = (text, type="success") => { setMsg({ text, type }); setTimeout(() => setMsg({ text:"", type:"" }), 4000); };

  const load = useCallback(() => {
    setLoading(true);
    Promise.all([getUsers(), getRoles()])
      .then(([uRes, rRes]) => {
        const ud = uRes.data; setUsers(ud?.users || (Array.isArray(ud) ? ud : ud && ud.users ? ud.users : []));
        const rd = rRes.data; setRoles(rd?.roles || (Array.isArray(rd) ? rd : []));
      })
      .catch(e => flash("Failed to load: " + e.message, "error"))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => { load(); }, [load]);

  const handleSave = () => {
    if (!form.userId || !form.userName) { flash("User ID and Name required", "error"); return; }
    const action = editUser
      ? updateUser(editUser.userId, { userName:form.userName, emailId:form.emailId, roleName:form.roleName })
      : createUser(form);
    action.then(() => { flash(editUser ? "User updated" : "User created"); setShowForm(false); setEditUser(null); load(); })
          .catch(e => flash("Failed: " + (e.response?.data?.error || e.message), "error"));
  };

  const handleDisable = (userId) => {
    if (!window.confirm("Disable user " + userId + "?")) return;
    disableUser(userId).then(() => { flash("User disabled"); load(); }).catch(e => flash(e.message, "error"));
  };

  const handleSetPw = () => {
    if (!newPw || newPw.length < 6) { flash("Password must be 6+ characters", "error"); return; }
    setPassword(pwUser.userId, newPw).then(() => { flash("Password set for " + pwUser.userId); setPwUser(null); setNewPw(""); }).catch(e => flash(e.message, "error"));
  };

  const filtered = users.filter(u => !search || (u.userId||"").toLowerCase().includes(search.toLowerCase()) || (u.userName||"").toLowerCase().includes(search.toLowerCase()));

  return (
    <div style={{ padding:24, maxWidth:1100, margin:"0 auto" }}>
      {msg.text && <div style={{ padding:"10px 16px", borderRadius:8, marginBottom:16, background:msg.type==="error"?"#fef2f2":"#f0fdf4", color:msg.type==="error"?"#dc2626":"#15803d", border:"1px solid "+(msg.type==="error"?"#fecaca":"#bbf7d0"), fontWeight:600 }}>{msg.text}</div>}

      <div style={{ display:"grid", gridTemplateColumns:"repeat(3,1fr)", gap:12, marginBottom:24 }}>
        {[{ l:"Total Users", v:users.length, c:"#1d4ed8" }, { l:"Active", v:users.filter(u=>u.isEnabled!==false).length, c:"#15803d" }, { l:"Admins", v:users.filter(u=>u.roleName==="ADMIN").length, c:"#dc2626" }].map(s => (
          <div key={s.l} style={{ background:"#fff", border:"1px solid #e5e7eb", borderRadius:10, padding:"16px 20px", borderTop:"3px solid "+s.c }}>
            <div style={{ fontSize:28, fontWeight:800, color:s.c }}>{s.v}</div>
            <div style={{ fontSize:12, color:"#6b7280", textTransform:"uppercase", letterSpacing:"0.5px", marginTop:4 }}>{s.l}</div>
          </div>
        ))}
      </div>

      <div style={{ display:"flex", justifyContent:"space-between", alignItems:"center", marginBottom:16, flexWrap:"wrap", gap:12 }}>
        <div><h1 style={{ fontSize:22, fontWeight:800, margin:0 }}>User Management</h1><p style={{ color:"#6b7280", margin:"4px 0 0" }}>Create, edit and manage platform users</p></div>
        <div style={{ display:"flex", gap:8 }}>
          <input value={search} onChange={e => setSearch(e.target.value)} placeholder="Search users..." style={{ padding:"8px 12px", borderRadius:8, border:"1px solid #e5e7eb", fontSize:13, outline:"none" }} />
          <button onClick={() => { setEditUser(null); setForm({ userId:"", userName:"", emailId:"", roleName:"GUEST", password:"" }); setShowForm(true); }}
            style={{ background:"#1d4ed8", color:"#fff", border:"none", borderRadius:8, padding:"8px 18px", fontWeight:700, cursor:"pointer", fontSize:13 }}>+ Add User</button>
        </div>
      </div>

      {loading ? <div style={{ textAlign:"center", padding:60, color:"#6b7280" }}>Loading users...</div> :
       filtered.length === 0 ? <div style={{ textAlign:"center", padding:60, background:"#fff", borderRadius:12, border:"1px solid #e5e7eb", color:"#6b7280" }}>No users found.</div> :
       <div style={{ background:"#fff", borderRadius:12, border:"1px solid #e5e7eb", overflow:"auto" }}>
        <table style={{ width:"100%", borderCollapse:"collapse", fontSize:13 }}>
          <thead><tr style={{ background:"#f8fafc" }}>
            {["User ID","Name","Email","Role","Status","Password","Actions"].map(h => (
              <th key={h} style={{ padding:"10px 14px", textAlign:"left", fontWeight:700, fontSize:11, textTransform:"uppercase", color:"#6b7280", borderBottom:"1px solid #e5e7eb" }}>{h}</th>
            ))}
          </tr></thead>
          <tbody>
            {filtered.map((u,i) => {
              const rc  = RC[u.roleName] || RC.GUEST;
              const ena = u.isEnabled !== false;
              return (
                <tr key={i} style={{ borderBottom:"1px solid #f1f5f9" }} onMouseEnter={e => e.currentTarget.style.background="#f9fafb"} onMouseLeave={e => e.currentTarget.style.background="#fff"}>
                  <td style={{ padding:"10px 14px", fontWeight:700, fontFamily:"monospace" }}>{u.userId}</td>
                  <td style={{ padding:"10px 14px" }}>{u.userName}</td>
                  <td style={{ padding:"10px 14px", color:"#6b7280" }}>{u.emailId || "-"}</td>
                  <td style={{ padding:"10px 14px" }}><span style={{ background:rc.bg, color:rc.color, padding:"2px 10px", borderRadius:20, fontSize:11, fontWeight:700 }}>{u.roleName}</span></td>
                  <td style={{ padding:"10px 14px" }}><span style={{ background:ena?"#f0fdf4":"#f1f5f9", color:ena?"#15803d":"#6b7280", padding:"2px 10px", borderRadius:20, fontSize:11, fontWeight:700 }}>{ena?"Active":"Disabled"}</span></td>
                  <td style={{ padding:"10px 14px", color:u.hasPassword?"#15803d":"#9ca3af", fontSize:12, fontWeight:600 }}>{u.hasPassword?"Set":"Not set"}</td>
                  <td style={{ padding:"10px 14px" }}>
                    <div style={{ display:"flex", gap:5 }}>
                      <button onClick={() => { setEditUser(u); setForm({ userId:u.userId, userName:u.userName||"", emailId:u.emailId||"", roleName:u.roleName||"GUEST", password:"" }); setShowForm(true); }}
                        style={{ padding:"3px 8px", fontSize:11, background:"#eff6ff", color:"#1d4ed8", border:"1px solid #bfdbfe", borderRadius:5, cursor:"pointer", fontWeight:600 }}>Edit</button>
                      <button onClick={() => { setPwUser(u); setNewPw(""); }}
                        style={{ padding:"3px 8px", fontSize:11, background:"#f5f3ff", color:"#7c3aed", border:"1px solid #ddd6fe", borderRadius:5, cursor:"pointer", fontWeight:600 }}>Password</button>
                      {ena && <button onClick={() => handleDisable(u.userId)}
                        style={{ padding:"3px 8px", fontSize:11, background:"#fef2f2", color:"#dc2626", border:"1px solid #fecaca", borderRadius:5, cursor:"pointer", fontWeight:600 }}>Disable</button>}
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
       </div>
      }

      {showForm && (
        <Modal title={editUser ? "Edit User" : "Add New User"} onClose={() => { setShowForm(false); setEditUser(null); }}>
          {[{ label:"User ID *", key:"userId", disabled:!!editUser, ph:"e.g. john.smith" }, { label:"Full Name *", key:"userName", ph:"e.g. John Smith" }, { label:"Email", key:"emailId", ph:"john@company.com" }].map(f => (
            <div key={f.key} style={{ marginBottom:14 }}>
              <label style={{ fontSize:13, fontWeight:600, display:"block", marginBottom:6 }}>{f.label}</label>
              <input value={form[f.key]} disabled={f.disabled} onChange={e => setForm(p => ({...p,[f.key]:e.target.value}))} placeholder={f.ph}
                style={{ width:"100%", padding:"10px 12px", border:"1px solid #d1d5db", borderRadius:8, fontSize:14, outline:"none", boxSizing:"border-box", background:f.disabled?"#f9fafb":"#fff" }} />
            </div>
          ))}
          {!editUser && (
            <div style={{ marginBottom:14 }}>
              <label style={{ fontSize:13, fontWeight:600, display:"block", marginBottom:6 }}>Initial Password</label>
              <input type="password" value={form.password} onChange={e => setForm(p => ({...p,password:e.target.value}))} placeholder="Leave blank for no password"
                style={{ width:"100%", padding:"10px 12px", border:"1px solid #d1d5db", borderRadius:8, fontSize:14, outline:"none", boxSizing:"border-box" }} />
            </div>
          )}
          <div style={{ marginBottom:20 }}>
            <label style={{ fontSize:13, fontWeight:600, display:"block", marginBottom:6 }}>Role</label>
            <select value={form.roleName} onChange={e => setForm(p => ({...p,roleName:e.target.value}))}
              style={{ width:"100%", padding:"10px 12px", border:"1px solid #d1d5db", borderRadius:8, fontSize:14, outline:"none", background:"#fff", boxSizing:"border-box" }}>
              {(roles.length > 0 ? roles : [{roleName:"ADMIN"},{roleName:"BUS_ADMIN"},{roleName:"GUEST"}]).map(r => <option key={r.roleName} value={r.roleName}>{r.roleName}</option>)}
            </select>
          </div>
          <div style={{ display:"flex", gap:10, justifyContent:"flex-end" }}>
            <button onClick={() => { setShowForm(false); setEditUser(null); }} style={{ padding:"10px 20px", border:"1px solid #d1d5db", borderRadius:8, background:"#fff", cursor:"pointer", fontWeight:600 }}>Cancel</button>
            <button onClick={handleSave} style={{ padding:"10px 20px", background:"#1d4ed8", color:"#fff", border:"none", borderRadius:8, cursor:"pointer", fontWeight:700 }}>{editUser ? "Update User" : "Create User"}</button>
          </div>
        </Modal>
      )}

      {pwUser && (
        <Modal title="Set Password" onClose={() => setPwUser(null)}>
          <p style={{ color:"#6b7280", fontSize:13, marginBottom:16 }}>Setting password for: <strong>{pwUser.userId}</strong></p>
          <input type="password" value={newPw} onChange={e => setNewPw(e.target.value)} placeholder="New password (min 6 chars)"
            style={{ width:"100%", padding:"10px 12px", border:"1px solid #d1d5db", borderRadius:8, fontSize:14, outline:"none", boxSizing:"border-box", marginBottom:16 }} />
          <div style={{ display:"flex", gap:10, justifyContent:"flex-end" }}>
            <button onClick={() => setPwUser(null)} style={{ padding:"10px 20px", border:"1px solid #d1d5db", borderRadius:8, background:"#fff", cursor:"pointer", fontWeight:600 }}>Cancel</button>
            <button onClick={handleSetPw} style={{ padding:"10px 20px", background:"#7c3aed", color:"#fff", border:"none", borderRadius:8, cursor:"pointer", fontWeight:700 }}>Set Password</button>
          </div>
        </Modal>
      )}
    </div>
  );
}
