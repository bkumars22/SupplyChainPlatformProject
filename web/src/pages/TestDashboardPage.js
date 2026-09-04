import React, { useState, useEffect, useCallback } from "react";
import axios from "axios";
import { getTestResults } from "../api";

const DEMO = process.env.REACT_APP_DEMO_MODE === "true";

const api = axios.create({ baseURL: "/supchain", timeout: 15000 });
api.interceptors.request.use(c => {
  const t = localStorage.getItem("jwt_token");
  if (t) c.headers.Authorization = "Bearer " + t;
  return c;
});

const API_CHECKS = [
  { name:"Auth Login",        method:"POST", path:"/api/auth/login",          body:{username:"kumar",password:"kumar"}, auth:false },
  { name:"Dashboard Summary", method:"GET",  path:"/api/dashboard/summary",   auth:true },
  { name:"Alerts Active",     method:"GET",  path:"/api/alerts/active",        auth:true },
  { name:"BOM List",          method:"GET",  path:"/api/bom",                  auth:true },
  { name:"BOM Stats",         method:"GET",  path:"/api/bom/stats",            auth:true },
  { name:"Cost Records",      method:"GET",  path:"/api/costs",                auth:true },
  { name:"Cost Records Alt",  method:"GET",  path:"/api/cost-records",         auth:true },
  { name:"Suppliers",         method:"GET",  path:"/api/suppliers",            auth:true },
  { name:"Item Master",       method:"GET",  path:"/api/items",                auth:true },
  { name:"Users",             method:"GET",  path:"/api/admin/users",          auth:true },
  { name:"Roles",             method:"GET",  path:"/api/admin/roles",          auth:true },
  { name:"Forecasts",         method:"GET",  path:"/api/forecasts",            auth:true },
  { name:"AI Health",         method:"GET",  path:"/api/ai/health",            auth:true },
  { name:"AI Anomalies",      method:"GET",  path:"/api/ai/anomalies",         auth:true },
];

const TEST_GROUPS = [
  { id:"auth", label:"Authentication", color:"#a9790f", tests:[
    { id:"BB-AUTH-01", name:"Valid login returns JWT token", status:"pass" },
    { id:"BB-AUTH-02", name:"Wrong password returns 200", status:"bug", bug:"kumar.password is NULL — any password accepted. Fix: UPDATE PCM_USER SET PASSWORD=BCrypt('kumar123') WHERE USER_ID='kumar'" },
    { id:"BB-AUTH-03", name:"Unknown user returns 401", status:"pass" },
    { id:"BB-AUTH-04", name:"No token returns 403", status:"pass", note:"Backend uses 403 not 401" },
    { id:"BB-AUTH-05", name:"Malformed token returns 403", status:"pass" },
    { id:"NEG-AUTH-01", name:"SQL injection in login blocked", status:"pass" },
    { id:"SEC-02", name:"JWT payload has no sensitive fields", status:"pass", note:"JWT keys: sub, role, iat, exp" },
  ]},
  { id:"items", label:"Item Master  /api/items", color:"#7c3aed", tests:[
    { id:"BB-ITEM-01", name:"GET returns all 10 items with correct fields", status:"pass" },
    { id:"BB-ITEM-02", name:"Seed data has PCB-001 CHIP-001 FG-LAPTOP-X1", status:"pass" },
    { id:"BB-ITEM-03", name:"PCB-001 is COMPONENT type with id PCB-001-v1", status:"pass" },
    { id:"BB-ITEM-04", name:"POST /api/items returns 500", status:"bug", bug:"ItemManagementService crashes on businessEntity lookup. Fix: add null-check in createItem() and return 400 if entity not found" },
    { id:"NEG-ITEM-01", name:"GET without auth returns 403", status:"pass" },
    { id:"NEG-ITEM-02", name:"XSS payload rejected or sanitized", status:"pass" },
  ]},
  { id:"bom", label:"Bill of Materials  /api/bom", color:"#0f6e56", tests:[
    { id:"BB-BOM-01", name:"GET returns paginated BOM list with all fields", status:"pass" },
    { id:"BB-BOM-02", name:"Laptop X1 BOM is top level with FG-LAPTOP-X1", status:"pass" },
    { id:"BB-BOM-03", name:"GET /api/bom/201 returns detail with lines array", status:"pass" },
    { id:"BB-BOM-04", name:"GET by status filters correctly", status:"pass" },
    { id:"BB-BOM-05", name:"GET /api/bom/stats returns dashboard counts", status:"pass" },
    { id:"BB-BOM-06", name:"POST returns 405 — read-only by design", status:"pass", note:"BomRestController intentionally has no POST mapping" },
    { id:"NEG-BOM-01", name:"Non-existent BOM key returns 404", status:"pass" },
  ]},
  { id:"costs", label:"Cost Records  /api/costs", color:"#d97706", tests:[
    { id:"BB-COST-01",  name:"GET /api/cost-records returns 200", status:"pass" },
    { id:"BB-COST-01b", name:"GET /api/costs also returns 200", status:"pass" },
    { id:"BB-COST-01c", name:"Response shape logged — 0 records in DB", status:"pass", note:"Use Data Portal to create cost records" },
    { id:"BB-COST-02",  name:"POST cost record — no working endpoint", status:"bug", bug:"/api/cost-records returns 405 (no @PostMapping). /api/costs returns 500 (NullPointerException). Fix: add @PostMapping to CostRecordRestController" },
    { id:"NEG-COST-01", name:"GET without auth returns 403", status:"pass" },
    { id:"NEG-COST-02", name:"GET by status DRAFT returns 200", status:"pass" },
  ]},
  { id:"sups", label:"Suppliers  /api/suppliers", color:"#be185d", tests:[
    { id:"BB-SUP-01", name:"GET returns 200 with supplier data in data[] wrapper", status:"pass" },
    { id:"BB-SUP-02", name:"Supplier fields present: name, otdScore, tier, atRisk", status:"pass" },
    { id:"BB-SUP-03", name:"Tier/OTD mismatch in seed data", status:"bug", bug:"Pinnacle Assembly has otdScore=0 but tier=APPROVED. Fix: run /api/suppliers/recalculate-tiers after startup, or fix seed SQL" },
  ]},
  { id:"users", label:"User Management  /api/admin", color:"#0c447c", tests:[
    { id:"BB-USR-01", name:"GET /api/admin/users returns list with all fields", status:"pass" },
    { id:"BB-USR-02", name:"GET /api/admin/roles returns 3 roles", status:"pass", note:"Administrator(1), Business Administrator(2), Read Only(3)" },
    { id:"BB-USR-03", name:"POST create user returns 500 for all payloads", status:"bug", bug:"CreateUserRequest roleKey FK lookup fails. Fix: check UserManagementController CreateUserRequest record — try roleKey as int" },
    { id:"NEG-USR-01", name:"Duplicate user ID rejected", status:"pass" },
    { id:"SEC-01", name:"Password not returned in user list", status:"pass" },
    { id:"SEC-03", name:"Unauthenticated access to admin blocked", status:"pass" },
  ]},
  { id:"alerts", label:"Alerts  /api/alerts", color:"#dc2626", tests:[
    { id:"BB-ALERT-01", name:"GET /api/alerts/active returns alerts", status:"pass", note:"4 alerts in DB" },
    { id:"BB-ALERT-02", name:"Dismiss removes alert from active list", status:"pass" },
    { id:"BB-ALERT-03", name:"Dashboard activeAlerts count is a number", status:"pass" },
    { id:"NEG-ALERT-01", name:"Dismiss non-existent alert returns 404", status:"pass" },
  ]},
  { id:"dash", label:"Dashboard  /api/dashboard/summary", color:"#059669", tests:[
    { id:"BB-DASH-01", name:"GET summary returns KPI fields", status:"pass", note:"Fields: unreadAlerts, totalBoms, approvedBoms, pendingBoms, activeBoms" },
    { id:"BB-DASH-02", name:"All KPI values are numbers", status:"pass" },
  ]},
];

const badge = (type) => {
  const m = { pass:{background:"#f0fdf4",color:"#15803d"}, fail:{background:"#fef2f2",color:"#dc2626"}, bug:{background:"#fef2f2",color:"#dc2626"}, warn:{background:"#fffbeb",color:"#d97706"}, skip:{background:"#f1f5f9",color:"#475569"}, info:{background:"#eff6ff",color:"#a9790f"} };
  return { display:"inline-flex", alignItems:"center", padding:"2px 9px", borderRadius:20, fontSize:10, fontWeight:700, whiteSpace:"nowrap", ...(m[type]||m.info) };
};

export default function TestDashboardPage() {
  const [tab,        setTab]        = useState("playwright");
  const [apiRes,     setApiRes]     = useState(
    DEMO ? Object.fromEntries(API_CHECKS.map(c => [c.name, {ok:true, status:200, ms:Math.floor(Math.random()*150)+60}])) : {}
  );
  const [running,    setRunning]    = useState(false);
  const [expanded,   setExpanded]   = useState({});
  const [token,      setToken]      = useState(null);
  const [liveData,   setLiveData]   = useState(null);
  const [lastRun,    setLastRun]    = useState(null);
  const [autoTick,   setAutoTick]   = useState(0);

  const total  = TEST_GROUPS.reduce((a,g) => a + g.tests.length, 0);
  const bugs   = TEST_GROUPS.reduce((a,g) => a + g.tests.filter(t=>t.status==="bug").length, 0);

  // Live results from test-results.json written by persistent-reporter.js
  const fetchLiveResults = useCallback(async () => {
    if (DEMO) {
      try {
        const d = await getTestResults();
        setLiveData(d);
        if (d.timestamp) setLastRun(new Date(d.timestamp));
      } catch(e) {}
      return;
    }
    try {
      const r = await fetch("/test-results.json?t=" + Date.now());
      if (r.ok) {
        const d = await r.json();
        setLiveData(d);
        if (d.timestamp) setLastRun(new Date(d.timestamp));
      }
    } catch(e) {}
  }, []);

  // Auto-refresh every 10 seconds
  useEffect(() => {
    fetchLiveResults();
    const t = setInterval(() => { fetchLiveResults(); setAutoTick(n=>n+1); }, 10000);
    return () => clearInterval(t);
  }, [fetchLiveResults]);

  const liveTotal  = liveData ? liveData.total  : total;
  const livePassed = liveData ? liveData.passed : (total - bugs);
  const liveFailed = liveData ? liveData.failed : bugs;
  const apiOk      = Object.values(apiRes).filter(r=>r&&r.ok).length;

  // Get live status for a test ID
  const liveStatus = (id) => {
    if (!liveData || !liveData.tests) return null;
    const t = liveData.tests.find(t => t.name && t.name.includes(id.replace("-","").toLowerCase()) || t.id === id);
    if (!t) return null;
    return t.status;
  };

  const runApiChecks = useCallback(async () => {
    if (DEMO) {
      setRunning(true);
      setTimeout(() => {
        const r = {};
        API_CHECKS.forEach(c => { r[c.name] = {ok:true, status:200, ms:Math.floor(Math.random()*150)+60}; });
        setApiRes(r);
        setRunning(false);
      }, 800);
      return;
    }
    setRunning(true);
    let tok = token;
    if (!tok) {
      try {
        const r = await axios.post("/supchain/api/auth/login", {username:"kumar",password:"Kumar@2026"});
        tok = r.data.token; setToken(tok);
      } catch(e) { setRunning(false); return; }
    }
    const results = {};
    for (const chk of API_CHECKS) {
      const t0 = Date.now();
      try {
        const headers = chk.auth ? {Authorization:`Bearer ${tok}`} : {};
        const r = chk.method==="POST"
          ? await axios.post(`/supchain${chk.path}`, chk.body||{}, {headers})
          : await axios.get(`/supchain${chk.path}`, {headers});
        results[chk.name] = {ok:true, status:r.status, ms:Date.now()-t0};
      } catch(e) {
        results[chk.name] = {ok:false, status:e.response?.status||0, ms:Date.now()-t0};
      }
      setApiRes({...results});
    }
    setRunning(false);
  }, [token]);

  useEffect(() => { if (!DEMO) runApiChecks(); }, []);

  const toggle = (id) => setExpanded(p=>({...p,[id]:p[id]===false?true:false}));
  const isOpen = (id) => expanded[id] !== false;

  const pct = Math.round((livePassed/liveTotal)*100);

  return (
    <div style={{padding:24,maxWidth:1100,margin:"0 auto"}}>
      <div style={{display:"flex",justifyContent:"space-between",alignItems:"flex-start",marginBottom:20,flexWrap:"wrap",gap:12}}>
        <div>
          <h1 style={{fontSize:22,fontWeight:800,margin:0}}>Test Intelligence Dashboard</h1>
          <p style={{fontSize:13,color:"#64748b",marginTop:4}}>
            {liveTotal} Playwright test cases across 8 modules + live API health checks
            {lastRun && <span style={{marginLeft:12,color:"#94a3b8"}}>Last run: {lastRun.toLocaleTimeString()}</span>}
            {liveData && <span style={{marginLeft:8,fontSize:11,background:"#f0fdf4",color:"#15803d",padding:"1px 7px",borderRadius:20,fontWeight:600}}>Auto-refreshing</span>}
          </p>
        </div>
        <div style={{display:"flex",gap:8}}>
          <button onClick={fetchLiveResults} style={{padding:"7px 14px",borderRadius:8,fontSize:12,fontWeight:700,cursor:"pointer",border:"1px solid #e2e8f0",background:"#fff"}}>
            Load Results
          </button>
          <button onClick={runApiChecks} disabled={running} style={{padding:"7px 14px",borderRadius:8,fontSize:12,fontWeight:700,cursor:"pointer",border:"none",background:"#a9790f",color:"#fff"}}>
            {running?"Checking...":"Run API Checks"}
          </button>
        </div>
      </div>

      <div style={{display:"grid",gridTemplateColumns:"repeat(4,1fr)",gap:12,marginBottom:20}}>
        <div style={{background:"#fff",border:"1px solid #e2e8f0",borderRadius:10,padding:"14px 18px",borderTop:"3px solid #15803d"}}>
          <div style={{fontSize:28,fontWeight:800,color:"#1e293b"}}>{livePassed}/{liveTotal}</div>
          <div style={{fontSize:11,color:"#64748b",textTransform:"uppercase",letterSpacing:"0.5px",marginTop:2}}>Tests Passing</div>
        </div>
        <div style={{background:"#fff",border:"1px solid #e2e8f0",borderRadius:10,padding:"14px 18px",borderTop:"3px solid #dc2626"}}>
          <div style={{fontSize:28,fontWeight:800,color:"#dc2626"}}>{liveData != null ? liveFailed : bugs}</div>
          <div style={{fontSize:11,color:"#64748b",textTransform:"uppercase",letterSpacing:"0.5px",marginTop:2}}>Known Bugs</div>
        </div>
        <div style={{background:"#fff",border:"1px solid #e2e8f0",borderRadius:10,padding:"14px 18px",borderTop:"3px solid #a9790f"}}>
          <div style={{fontSize:28,fontWeight:800,color:apiOk===API_CHECKS.length?"#15803d":"#dc2626"}}>{apiOk}/{API_CHECKS.length}</div>
          <div style={{fontSize:11,color:"#64748b",textTransform:"uppercase",letterSpacing:"0.5px",marginTop:2}}>APIs Passing</div>
        </div>
        <div style={{background:"#fff",border:"1px solid #e2e8f0",borderRadius:10,padding:"14px 18px",borderTop:"3px solid #7c3aed"}}>
          <div style={{fontSize:28,fontWeight:800,color:pct>=90?"#15803d":pct>=70?"#d97706":"#dc2626"}}>{pct}%</div>
          <div style={{fontSize:11,color:"#64748b",textTransform:"uppercase",letterSpacing:"0.5px",marginTop:2}}>Overall Health</div>
        </div>
      </div>

      <div style={{display:"flex",borderBottom:"1px solid #e2e8f0",marginBottom:16}}>
        {[["playwright","Playwright Tests ("+liveTotal+")"],["api","API Health ("+API_CHECKS.length+")"],["bugs","Known Bugs ("+bugs+")"],["live","Live Run Results"]].map(([k,l])=>(
          <div key={k} onClick={()=>setTab(k)} style={{padding:"8px 18px",fontSize:13,fontWeight:600,cursor:"pointer",borderBottom:tab===k?"2px solid #a9790f":"2px solid transparent",color:tab===k?"#a9790f":"#94a3b8"}}>
            {l}
          </div>
        ))}
      </div>

      {tab==="playwright" && (
        <div>
          {TEST_GROUPS.map(g => {
            const gPassed = g.tests.filter(t=>t.status==="pass").length;
            const gBugs   = g.tests.filter(t=>t.status==="bug").length;
            const open    = isOpen(g.id);
            return (
              <div key={g.id} style={{marginBottom:16}}>
                <div onClick={()=>toggle(g.id)} style={{display:"flex",alignItems:"center",gap:8,cursor:"pointer",padding:"10px 14px",background:"#f8fafc",borderRadius:8,border:"1px solid #e2e8f0",marginBottom:open?8:0}}>
                  <div style={{width:10,height:10,borderRadius:"50%",background:g.color,flexShrink:0}}/>
                  <span style={{fontSize:13,fontWeight:700,color:"#1e293b",flex:1}}>{g.label}</span>
                  <span style={badge("pass")}>{gPassed} pass</span>
                  {gBugs>0 && <span style={badge("bug")}>{gBugs} bug</span>}
                  <span style={{fontSize:12,color:"#94a3b8",marginLeft:4}}>{open?"▲":"▼"}</span>
                </div>
                {open && g.tests.map(t => {
                  const live = liveStatus(t.id);
                  const displayStatus = live || t.status;
                  return (
                    <div key={t.id} style={{display:"flex",alignItems:"flex-start",gap:10,padding:"10px 14px",borderRadius:8,marginBottom:4,background:"#fff",border:"1px solid #e2e8f0",marginLeft:18}}>
                      <span style={{fontFamily:"monospace",fontSize:10,color:"#94a3b8",minWidth:100,paddingTop:2,flexShrink:0}}>{t.id}</span>
                      <div style={{flex:1}}>
                        <span style={{fontSize:13,color:"#1e293b",lineHeight:1.5}}>{t.name}</span>
                        {t.note && <div style={{fontSize:11,color:"#64748b",marginTop:3}}>{t.note}</div>}
                        {t.bug  && <div style={{padding:"6px 10px",borderRadius:6,fontSize:11,color:"#92400e",background:"#fffbeb",border:"1px solid #fde68a",marginTop:6}}>Fix: {t.bug}</div>}
                      </div>
                      <div style={{display:"flex",gap:5,flexShrink:0}}>
                        {live && live!==t.status && <span style={badge("info")}>live: {live}</span>}
                        <span style={badge(displayStatus==="bug"?"bug":displayStatus==="pass"?"pass":displayStatus==="failed"?"fail":"skip")}>
                          {displayStatus==="bug"?"BUG":displayStatus==="passed"?"PASS":displayStatus==="failed"?"FAIL":displayStatus.toUpperCase()}
                        </span>
                      </div>
                    </div>
                  );
                })}
              </div>
            );
          })}
        </div>
      )}

      {tab==="api" && (
        <div>
          <p style={{fontSize:12,color:"#64748b",marginBottom:14}}>{DEMO ? "Mock API health checks — all 14 endpoints shown as healthy (demo mode)" : "Live checks against localhost:8089/supchain — click Run API Checks to refresh"}</p>
          {API_CHECKS.map(chk => {
            const r = apiRes[chk.name];
            const ok = r&&r.ok;
            return (
              <div key={chk.name} style={{display:"flex",alignItems:"center",gap:12,padding:"10px 14px",borderRadius:8,marginBottom:6,background:"#f8fafc",border:"1px solid #e2e8f0",flexWrap:"wrap"}}>
                <span style={{...badge(ok?"pass":running?"warn":"fail"),minWidth:44,justifyContent:"center"}}>{chk.method}</span>
                <span style={{fontFamily:"monospace",fontSize:12,color:"#374151",flex:1,minWidth:200}}>{chk.path}</span>
                <span style={{fontSize:13,fontWeight:600,color:"#1e293b",minWidth:140}}>{chk.name}</span>
                {r
                  ? <span style={badge(ok?"pass":"fail")}>HTTP {r.status} · {r.ms}ms</span>
                  : <span style={badge("skip")}>{running?"Checking...":"—"}</span>
                }
              </div>
            );
          })}
        </div>
      )}

      {tab==="bugs" && (
        <div>
          <p style={{fontSize:12,color:"#64748b",marginBottom:14}}>{bugs} known bugs discovered through black-box and exploratory testing</p>
          {TEST_GROUPS.flatMap(g => g.tests.filter(t=>t.status==="bug").map(t => (
            <div key={t.id} style={{background:"#fff",border:"1px solid #e2e8f0",borderLeft:"4px solid #dc2626",borderRadius:8,padding:"14px 18px",marginBottom:10}}>
              <div style={{display:"flex",alignItems:"center",gap:10,marginBottom:8,flexWrap:"wrap"}}>
                <span style={badge("bug")}>BUG</span>
                <span style={{fontFamily:"monospace",fontSize:11,color:"#94a3b8"}}>{t.id}</span>
                <span style={{fontSize:12,fontWeight:700,color:"#374151"}}>{g.label}</span>
              </div>
              <p style={{fontSize:13,color:"#1e293b",marginBottom:8}}>{t.name}</p>
              <div style={{padding:"8px 12px",borderRadius:6,fontSize:12,color:"#92400e",background:"#fffbeb",border:"1px solid #fde68a"}}>
                <strong>Fix: </strong>{t.bug}
              </div>
            </div>
          )))}
        </div>
      )}

      {tab==="live" && (
        <div>
          {!liveData || !liveData.tests || liveData.tests.length===0 ? (
            <div style={{textAlign:"center",padding:60,background:"#f8fafc",borderRadius:10,border:"1px solid #e2e8f0"}}>
              <div style={{fontSize:32,marginBottom:12}}>⚡</div>
              <p style={{color:"#64748b",fontSize:14,fontWeight:600}}>No live results yet</p>
              <p style={{color:"#94a3b8",fontSize:13,marginTop:6}}>Run the tests to see live results here</p>
              <div style={{marginTop:16,padding:"12px 16px",background:"#0f172a",borderRadius:8,fontFamily:"monospace",fontSize:12,color:"#e2e8f0",textAlign:"left",maxWidth:500,margin:"16px auto 0"}}>
                cd D:\KumarFolder\mydocs\playwright-tests<br/>
                npx playwright test tests/scip.spec.ts
              </div>
              <p style={{color:"#94a3b8",fontSize:12,marginTop:10}}>Results auto-appear here within 10 seconds</p>
            </div>
          ) : (
            <div>
              <div style={{display:"flex",gap:12,marginBottom:16,flexWrap:"wrap"}}>
                <span style={{...badge("pass"),padding:"4px 12px",fontSize:12}}>{liveData.passed} passed</span>
                <span style={{...badge("fail"),padding:"4px 12px",fontSize:12}}>{liveData.failed} failed</span>
                <span style={{...badge("skip"),padding:"4px 12px",fontSize:12}}>{liveData.skipped} skipped</span>
                <span style={{...badge("info"),padding:"4px 12px",fontSize:12}}>{(liveData.duration/1000).toFixed(1)}s total</span>
                {lastRun && <span style={{...badge("info"),padding:"4px 12px",fontSize:12}}>Run at {lastRun.toLocaleTimeString()}</span>}
              </div>
              {liveData.tests.map((t,i) => (
                <div key={i} style={{display:"flex",alignItems:"flex-start",gap:10,padding:"8px 12px",borderRadius:7,marginBottom:4,background:"#fff",border:"1px solid #e2e8f0"}}>
                  <span style={badge(t.status==="passed"?"pass":t.status==="failed"?"fail":"skip")}>
                    {t.status==="passed"?"PASS":t.status==="failed"?"FAIL":"SKIP"}
                  </span>
                  <div style={{flex:1}}>
                    <div style={{fontSize:11,color:"#94a3b8",fontFamily:"monospace"}}>{t.group}</div>
                    <div style={{fontSize:12,color:"#1e293b",marginTop:2}}>{t.name}</div>
                    {t.error && <div style={{fontSize:11,color:"#dc2626",marginTop:4,padding:"4px 8px",background:"#fef2f2",borderRadius:4}}>{t.error}</div>}
                  </div>
                  <span style={{fontSize:11,color:"#94a3b8",whiteSpace:"nowrap"}}>{t.duration}ms</span>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
