import { useEffect, useRef, useState, useCallback } from 'react';

/**
 * useWebSocket — Real-time SCIP updates hook
 * Sprint 4 — Real-time Architecture
 *
 * Usage in DashboardPage.js:
 *   const { alerts, dashboardKpis, isConnected } = useWebSocket();
 *
 * Usage in AlertsPage.js:
 *   const { alerts } = useWebSocket();
 */

const WS_URL = process.env.REACT_APP_WS_URL || 'ws://localhost:8089/supchain/ws/websocket';

export function useWebSocket() {
  const ws = useRef(null);
  const [isConnected, setIsConnected]     = useState(false);
  const [alerts, setAlerts]               = useState([]);
  const [dashboardKpis, setDashboardKpis] = useState(null);
  const [costEvents, setCostEvents]       = useState([]);
  const [supplierEvents, setSupplierEvents] = useState([]);
  const reconnectTimer = useRef(null);

  const connect = useCallback(() => {
    try {
      ws.current = new WebSocket(WS_URL);

      ws.current.onopen = () => {
        setIsConnected(true);
        console.log('[WebSocket] Connected to SCIP real-time feed');
        // Subscribe to topics via STOMP frames
        ws.current.send(JSON.stringify({ type: 'SUBSCRIBE', topic: '/topic/alerts' }));
        ws.current.send(JSON.stringify({ type: 'SUBSCRIBE', topic: '/topic/dashboard' }));
        ws.current.send(JSON.stringify({ type: 'SUBSCRIBE', topic: '/topic/cost-approvals' }));
        ws.current.send(JSON.stringify({ type: 'SUBSCRIBE', topic: '/topic/supplier-risk' }));
      };

      ws.current.onmessage = (event) => {
        try {
          const msg = JSON.parse(event.data);
          switch (msg.event) {
            case 'NEW_ALERT':
              setAlerts(prev => [msg, ...prev].slice(0, 50));
              break;
            case 'ALERT_DISMISSED':
              setAlerts(prev => prev.filter(a => a.alertId !== msg.alertId));
              break;
            case 'KPI_UPDATE':
              setDashboardKpis(msg);
              break;
            case 'COST_RECORD_APPROVED':
            case 'COST_RECORD_REJECTED':
              setCostEvents(prev => [msg, ...prev].slice(0, 20));
              break;
            case 'TIER_CHANGED':
              setSupplierEvents(prev => [msg, ...prev].slice(0, 20));
              break;
            default:
              break;
          }
        } catch (e) {
          console.warn('[WebSocket] Parse error:', e);
        }
      };

      ws.current.onclose = () => {
        setIsConnected(false);
        console.log('[WebSocket] Disconnected — reconnecting in 5s...');
        reconnectTimer.current = setTimeout(connect, 5000);
      };

      ws.current.onerror = (err) => {
        console.warn('[WebSocket] Error — falling back to polling');
        ws.current.close();
      };

    } catch (e) {
      console.warn('[WebSocket] Could not connect:', e);
    }
  }, []);

  useEffect(() => {
    connect();
    return () => {
      clearTimeout(reconnectTimer.current);
      ws.current?.close();
    };
  }, [connect]);

  return { isConnected, alerts, dashboardKpis, costEvents, supplierEvents };
}
