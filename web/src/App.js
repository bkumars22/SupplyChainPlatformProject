import React from 'react';
import { BrowserRouter, Routes, Route, Navigate, Link, useNavigate } from 'react-router-dom';
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
import SimpleDashboardPage from './pages/SimpleDashboardPage';
import SimpleSupplierDetailPage from './pages/SimpleSupplierDetailPage';
import CsvUploadPage from './pages/CsvUploadPage';
import './App.css';

function PrivateRoute({ children }) {
  return localStorage.getItem('jwt_token') ? children : <Navigate to="/login" />;
}

function Sidebar({ onLogout }) {
  const userData = localStorage.getItem('user_data');
  const user = userData ? JSON.parse(userData) : null;
  return (
    <div className="sidebar">
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
        <Link to="/admin/users">User Management</Link>
        <Link to="/tests">Test Dashboard</Link>
        <Link to="/eval">Eval Dashboard</Link>
        <Link to="/simple-dashboard">Simple Dashboard</Link>
        <Link to="/csv-upload">Upload History</Link>
      </nav>
      <div className="sidebar-user">
        <p>{user ? user.role : 'Administrator'}</p>
        <button onClick={onLogout}>Logout</button>
      </div>
    </div>
  );
}

function DemoBanner() {
  const [visible, setVisible] = React.useState(true);
  const userData = localStorage.getItem('user_data');
  const user = userData ? JSON.parse(userData) : null;
  if (!user || user.role !== 'DEMO' || !visible) return null;
  return (
    <div style={{
      background: '#fbbf24', color: '#78350f', padding: '10px 20px',
      display: 'flex', alignItems: 'center', justifyContent: 'space-between',
      fontWeight: 600, fontSize: 14, position: 'sticky', top: 0, zIndex: 1000
    }}>
      <span>Demo Mode — Read Only. Sign up for full access.</span>
      <button onClick={() => setVisible(false)}
        style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: 18, color: '#78350f' }}>
        ×
      </button>
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
      <div className="main-content">
        <DemoBanner />
        {children}
      </div>
    </div>
  );
}

function App() {
  return (
    <BrowserRouter>
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
        <Route path="/simple-dashboard" element={<PrivateRoute><AppLayout><SimpleDashboardPage /></AppLayout></PrivateRoute>} />
        <Route path="/simple-dashboard/:supplierId" element={<PrivateRoute><AppLayout><SimpleSupplierDetailPage /></AppLayout></PrivateRoute>} />
        <Route path="/csv-upload" element={<PrivateRoute><AppLayout><CsvUploadPage /></AppLayout></PrivateRoute>} />
        <Route path="*" element={<Navigate to="/dashboard" />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;