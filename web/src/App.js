import React from 'react';
import { BrowserRouter, Routes, Route, Navigate, Link, useNavigate } from 'react-router-dom';
import { resetDemoData } from './api';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import AlertsPage from './pages/AlertsPage';
import BomListPage from './pages/BomListPage';
import BomDetailPage from './pages/BomDetailPage';
import CostRecordsPage from './pages/CostRecordsPage';
import SupplierPage from './pages/SupplierPage';
import AiEnginesPage from './pages/AiEnginesPage';
import UserManagementPage from './pages/UserManagementPage';
import ForecastingPage from './pages/ForecastingPage';
import TestDashboardPage from './pages/TestDashboardPage';
import UsersPage from './pages/UsersPage';
import EvalDashboard from './pages/EvalDashboard';
import ReportsPage from './pages/ReportsPage';
import SetupPage from './pages/SetupPage';
import HelpPage from './pages/HelpPage';
import PurchaseOrdersPage from './pages/PurchaseOrdersPage';
import InventoryPage from './pages/InventoryPage';
import AuditLogsPage from './pages/AuditLogsPage';
import SimpleDashboardPage from './pages/SimpleDashboardPage';
import SimpleSupplierDetailPage from './pages/SimpleSupplierDetailPage';
import CsvUploadPage from './pages/CsvUploadPage';
import VoiceCommandBar from './components/VoiceCommandBar';
import './App.css';

function PrivateRoute({ children }) {
  return localStorage.getItem('jwt_token') ? children : <Navigate to="/login" />;
}

function Sidebar({ onLogout }) {
  const userData = localStorage.getItem('user_data');
  const user = userData ? JSON.parse(userData) : null;
  const [toast, setToast] = React.useState('');
  const isAdmin = user && (user.role === 'ADMIN' || user.role === 'MANAGER');

  const handleReset = async () => {
    try {
      await resetDemoData();
      setToast('Demo data restored!');
      setTimeout(() => setToast(''), 3000);
    } catch (e) {
      setToast('Reset failed — check console');
      setTimeout(() => setToast(''), 3000);
    }
  };

  return (
    <div className="sidebar">
      {toast && (
        <div style={{ position:'fixed', top:16, right:16, background:'#10b981', color:'#fff', padding:'10px 18px', borderRadius:8, fontWeight:700, fontSize:13, zIndex:9999, boxShadow:'0 4px 12px rgba(0,0,0,0.2)' }}>
          {toast}
        </div>
      )}
      <div className="sidebar-logo">
        <h2>Supply Chain</h2>
        <p>Intelligence Platform</p>
      </div>
      <nav className="sidebar-nav">
        <Link to="/dashboard">Dashboard</Link>
        <Link to="/alerts">Alerts</Link>
        <Link to="/bom">Bill of Materials</Link>
        <Link to="/cost-records">Cost Records</Link>
        <Link to="/suppliers">Supplier Scorecard</Link>
        <Link to="/ai">AI Anomaly Engine</Link>
        <Link to="/forecasts">Forecasting</Link>
        <Link to="/reports">Reports</Link>
        <Link to="/admin/users">User Management</Link>
        <Link to="/tests">Test Dashboard</Link>
        <Link to="/eval">Eval Dashboard</Link>
        <Link to="/setup">Setup</Link>
        <Link to="/purchase-orders">Purchase Orders</Link>
        <Link to="/inventory">Inventory</Link>
        <Link to="/audit-logs">Audit Logs</Link>
        <Link to="/simple-dashboard">Simple Dashboard</Link>
        <Link to="/csv-upload">Upload History</Link>
        <Link to="/help" style={{ color:'#facc15', fontWeight:700 }}>Help / Guide</Link>
      </nav>
      <div className="sidebar-user">
        <p>{user ? user.role : 'Administrator'}</p>
        {!IS_DEMO && isAdmin && (
          <button onClick={handleReset} style={{ marginBottom:8, background:'#1d4ed8', color:'#fff', border:'none', borderRadius:6, padding:'6px 12px', fontSize:12, fontWeight:700, cursor:'pointer', width:'100%' }}>
            Reset Demo Data
          </button>
        )}
        <button onClick={onLogout}>Logout</button>
      </div>
    </div>
  );
}

const IS_DEMO = process.env.REACT_APP_DEMO_MODE === 'true';

function DemoBanner() {
  const [visible, setVisible] = React.useState(true);
  if (!IS_DEMO || !visible) return null;
  return (
    <div style={{
      background: '#fbbf24', color: '#78350f', padding: '10px 20px',
      display: 'flex', alignItems: 'center', justifyContent: 'space-between',
      fontWeight: 600, fontSize: 14, position: 'sticky', top: 0, zIndex: 1000,
      gap: 12
    }}>
      <span>
        🎯 Demo Mode — Static Preview &nbsp;|&nbsp; Real platform:&nbsp;
        <a
          href="https://github.com/bkumars22/SupplyChainPlatformProject"
          target="_blank"
          rel="noreferrer"
          style={{ color: '#78350f', textDecoration: 'underline' }}
        >
          github.com/bkumars22/SupplyChainPlatformProject
        </a>
      </span>
      <button onClick={() => setVisible(false)}
        style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: 18, color: '#78350f', flexShrink: 0 }}>
        ×
      </button>
    </div>
  );
}

function DemoFooterBanner() {
  if (IS_DEMO) return null;
  return (
    <div style={{ padding:'10px 24px', borderTop:'1px solid #e5e7eb', background:'#f9fafb', textAlign:'center', fontSize:12, color:'#9ca3af', flexShrink:0 }}>
      Demo data resets daily at midnight IST &mdash; feel free to create, edit, and delete anything
    </div>
  );
}

function AppLayout({ children }) {
  const navigate = useNavigate();
  const handleLogout = () => {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user_data');
    navigate('/login');
  };
  return (
    <div className="app-layout">
      <Sidebar onLogout={handleLogout} />
      <div className="main-content" style={{ display:'flex', flexDirection:'column' }}>
        <DemoBanner />
        <div style={{ display:'flex', justifyContent:'flex-end', padding:'6px 16px 0', flexShrink:0 }}>
          <VoiceCommandBar />
        </div>
        <div style={{ flex:1 }}>{children}</div>
        <DemoFooterBanner />
      </div>
    </div>
  );
}

function App() {
  return (
    <BrowserRouter basename={process.env.REACT_APP_BASENAME || ""}>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/" element={<Navigate to="/dashboard" />} />
        <Route path="/dashboard" element={<PrivateRoute><AppLayout><DashboardPage /></AppLayout></PrivateRoute>} />
        <Route path="/alerts" element={<PrivateRoute><AppLayout><AlertsPage /></AppLayout></PrivateRoute>} />
        <Route path="/bom" element={<PrivateRoute><AppLayout><BomListPage /></AppLayout></PrivateRoute>} />
        <Route path="/bom/:bomKey" element={<PrivateRoute><AppLayout><BomDetailPage /></AppLayout></PrivateRoute>} />
        <Route path="/cost-records" element={<PrivateRoute><AppLayout><CostRecordsPage /></AppLayout></PrivateRoute>} />
        <Route path="/suppliers" element={<PrivateRoute><AppLayout><SupplierPage /></AppLayout></PrivateRoute>} />
        <Route path="/ai" element={<PrivateRoute><AppLayout><AiEnginesPage /></AppLayout></PrivateRoute>} />
        <Route path="/ai-engines" element={<PrivateRoute><AppLayout><AiEnginesPage /></AppLayout></PrivateRoute>} />
        <Route path="/admin/users" element={<PrivateRoute><AppLayout><UserManagementPage /></AppLayout></PrivateRoute>} />
        <Route path="/forecasts" element={<PrivateRoute><AppLayout><ForecastingPage /></AppLayout></PrivateRoute>} />
        <Route path="/tests" element={<PrivateRoute><AppLayout><TestDashboardPage /></AppLayout></PrivateRoute>} />
        <Route path="/users" element={<PrivateRoute><AppLayout><UsersPage /></AppLayout></PrivateRoute>} />
        <Route path="/eval" element={<PrivateRoute><AppLayout><EvalDashboard /></AppLayout></PrivateRoute>} />
        <Route path="/reports" element={<PrivateRoute><AppLayout><ReportsPage /></AppLayout></PrivateRoute>} />
        <Route path="/setup" element={<SetupPage />} />
        <Route path="/purchase-orders" element={<PrivateRoute><AppLayout><PurchaseOrdersPage /></AppLayout></PrivateRoute>} />
        <Route path="/inventory" element={<PrivateRoute><AppLayout><InventoryPage /></AppLayout></PrivateRoute>} />
        <Route path="/audit-logs" element={<PrivateRoute><AppLayout><AuditLogsPage /></AppLayout></PrivateRoute>} />
        <Route path="/help" element={<PrivateRoute><AppLayout><HelpPage /></AppLayout></PrivateRoute>} />
        <Route path="/simple-dashboard" element={<PrivateRoute><AppLayout><SimpleDashboardPage /></AppLayout></PrivateRoute>} />
        <Route path="/simple-dashboard/:supplierId" element={<PrivateRoute><AppLayout><SimpleSupplierDetailPage /></AppLayout></PrivateRoute>} />
        <Route path="/csv-upload" element={<PrivateRoute><AppLayout><CsvUploadPage /></AppLayout></PrivateRoute>} />
        <Route path="*" element={<Navigate to="/dashboard" />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;