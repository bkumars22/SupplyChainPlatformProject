const BASE = process.env.REACT_APP_API_URL || 'http://localhost:8089/supchain';

const headers = () => ({
  'Content-Type': 'application/json',
  ...(localStorage.getItem('jwt_token')
    ? { Authorization: 'Bearer ' + localStorage.getItem('jwt_token') }
    : {}),
});

const buildUrl = (path, params) => {
  let url = BASE + path;
  if (params) {
    const qs = Object.entries(params)
      .filter(([, v]) => v !== undefined && v !== null && v !== '')
      .map(([k, v]) => encodeURIComponent(k) + '=' + encodeURIComponent(v))
      .join('&');
    if (qs) url += '?' + qs;
  }
  return url;
};

const parse = async (res) => {
  if (!res.ok) throw new Error('HTTP ' + res.status);
  return { data: await res.json() };
};

const api = {
  get:    (path, opts = {})  => fetch(buildUrl(path, opts.params), { headers: headers() }).then(parse),
  post:   (path, body)       => fetch(BASE + path, { method: 'POST',   headers: headers(), body: JSON.stringify(body) }).then(parse),
  put:    (path, body)       => fetch(BASE + path, { method: 'PUT',    headers: headers(), body: JSON.stringify(body) }).then(parse),
  delete: (path)             => fetch(BASE + path, { method: 'DELETE', headers: headers() }).then(parse),
};

export default api;

// ── Auth ─────────────────────────────────────────────────────────────
export const login = (username, password) =>
  fetch(BASE + '/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  }).then(parse);

export const resetDemoData = () => api.post('/api/demo/reset', {});

// ── Dashboard / Alerts ───────────────────────────────────────────────
export const getDashboardSummary = () => api.get('/api/dashboard/summary');
export const getActiveAlerts     = () => api.get('/api/alerts');
export const dismissAlert        = (id) => api.put('/api/alerts/' + id + '/dismiss');

// ── Suppliers ────────────────────────────────────────────────────────
export const getSuppliers          = ()         => api.get('/api/suppliers');
export const getSupplierDeliveries = (id)       => api.get('/api/suppliers/' + id + '/deliveries');
export const getSupplierRatings    = (id)       => api.get('/api/suppliers/' + id + '/ratings');
export const createSupplierRating  = (id, body) => api.post('/api/suppliers/' + id + '/ratings', body);

// ── AI / Anomaly / Eval ──────────────────────────────────────────────
export const getAnomalies   = ()       => api.get('/api/ai/anomalies');
export const getEvalResults = ()       => api.get('/api/eval/results');
export const runEvaluation  = (body)   => api.post('/api/eval/run', body);

// ── Testing ──────────────────────────────────────────────────────────
export const getTestResults = () => api.get('/api/test-results');

// ── BOM ──────────────────────────────────────────────────────────────
export const getBomList   = (params) => api.get('/api/boms', { params });
export const getBomDetail = (bomKey) => api.get('/api/boms/' + bomKey);
export const createBom    = (body)   => api.post('/api/boms', body);

// ── Cost Records ─────────────────────────────────────────────────────
export const getCostRecords    = (params) => api.get('/api/cost-records', { params });
export const createCostRecord  = (body)   => api.post('/api/cost-records', body);
export const updateCostRecord  = (id, b)  => api.put('/api/cost-records/' + id, b);
export const submitCostRecord  = (id)     => api.put('/api/cost-records/' + id + '/submit');
export const approveCostRecord = (id)     => api.put('/api/cost-records/' + id + '/approve');
export const rejectCostRecord  = (id, b)  => api.put('/api/cost-records/' + id + '/reject', b);

// ── Purchase Orders ──────────────────────────────────────────────────
export const getPurchaseOrders    = (params)   => api.get('/api/purchase-orders', { params });
export const createPurchaseOrder  = (body)     => api.post('/api/purchase-orders', body);
export const submitPurchaseOrder  = (id)       => api.put('/api/purchase-orders/' + id + '/submit');
export const confirmPurchaseOrder = (id)       => api.put('/api/purchase-orders/' + id + '/confirm');
export const receivePurchaseOrder = (id, body) => api.put('/api/purchase-orders/' + id + '/receive', body);
export const cancelPurchaseOrder  = (id)       => api.put('/api/purchase-orders/' + id + '/cancel');

// ── Inventory ────────────────────────────────────────────────────────
export const getInventory             = (params) => api.get('/api/inventory', { params });
export const getInventoryTransactions = (id)     => api.get('/api/inventory/' + id + '/transactions');
export const adjustStock              = (id, b)  => api.post('/api/inventory/' + id + '/adjust', b);

// ── Forecasting ──────────────────────────────────────────────────────
export const getForecasts     = (params) => api.get('/api/forecasts', { params });
export const getForecastDetail = (id)    => api.get('/api/forecasts/' + id);
export const createForecast   = (body)   => api.post('/api/forecasts', body);

// ── Users / Roles ────────────────────────────────────────────────────
export const getUsers    = ()       => api.get('/api/users');
export const createUser  = (body)   => api.post('/api/users', body);
export const updateUser  = (id, b)  => api.put('/api/users/' + id, b);
export const disableUser = (id)     => api.put('/api/users/' + id + '/disable');
export const setPassword = (id, b)  => api.put('/api/users/' + id + '/password', b);
export const getRoles    = ()       => api.get('/api/roles');

// ── Audit Logs ───────────────────────────────────────────────────────
export const getAuditLogs = (params) => api.get('/api/audit-logs', { params });

// ── Reports ──────────────────────────────────────────────────────────
export const getSupplierPerfReport = (params) => api.get('/api/reports/supplier-performance', { params });
export const getCostVarianceReport = (params) => api.get('/api/reports/cost-variance', { params });
export const getAlertSummaryReport = (params) => api.get('/api/reports/alert-summary', { params });
export const getActivityReport     = (params) => api.get('/api/reports/activity', { params });
