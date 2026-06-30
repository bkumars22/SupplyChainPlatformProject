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
