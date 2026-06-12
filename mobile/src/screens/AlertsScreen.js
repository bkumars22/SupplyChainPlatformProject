/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
﻿import React, { useEffect, useState } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet, RefreshControl, ActivityIndicator } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

const API = 'http://172.30.208.1:8089/supchain';
const SEV = { HIGH:{bg:'#fee2e2',color:'#dc2626'}, MEDIUM:{bg:'#fff7ed',color:'#c2410c'}, LOW:{bg:'#f0fdf4',color:'#15803d'} };

export default function AlertsScreen() {
  const [alerts,     setAlerts]     = useState([]);
  const [loading,    setLoading]    = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const fetchAlerts = async () => {
    try {
      const token = await AsyncStorage.getItem('jwt_token');
      const res   = await fetch(API + '/api/alerts/active', { headers: { Authorization: 'Bearer ' + token } });
      if (res.ok) { const d = await res.json(); setAlerts(Array.isArray(d) ? d : d.alerts || []); }
    } catch (e) { console.log('Alerts error:', e); }
    finally { setLoading(false); setRefreshing(false); }
  };

  useEffect(() => { fetchAlerts(); }, []);

  if (loading) return <View style={s.center}><ActivityIndicator size="large" color="#2563eb" /></View>;

  return (
    <View style={s.container}>
      <Text style={s.header}>Active Alerts</Text>
      <Text style={s.sub}>{alerts.length} alerts</Text>
      <FlatList
        data={alerts}
        keyExtractor={(item, i) => (item.id || i).toString()}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => { setRefreshing(true); fetchAlerts(); }} />}
        renderItem={({ item }) => {
          const sev = SEV[item.severity] || SEV.LOW;
          return (
            <View style={s.card}>
              <View style={s.row}>
                <Text style={s.title}>{item.alertType || item.type || 'Alert'}</Text>
                <View style={[s.badge,{backgroundColor:sev.bg}]}>
                  <Text style={[s.badgeText,{color:sev.color}]}>{item.severity || 'INFO'}</Text>
                </View>
              </View>
              <Text style={s.desc}>{item.shortSummary || item.summary || item.description || 'No description'}</Text>
              <Text style={s.meta}>{item.item || ''}{item.supplier ? ' Â· ' + item.supplier : ''}</Text>
            </View>
          );
        }}
        ListEmptyComponent={<Text style={s.empty}>No active alerts</Text>}
      />
    </View>
  );
}

const s = StyleSheet.create({
  container: { flex:1, backgroundColor:'#f8fafc', padding:16 },
  center:    { flex:1, alignItems:'center', justifyContent:'center' },
  header:    { fontSize:22, fontWeight:'700', color:'#0f172a', marginBottom:4 },
  sub:       { fontSize:13, color:'#64748b', marginBottom:16 },
  card:      { backgroundColor:'#fff', borderRadius:12, padding:14, marginBottom:10, borderWidth:0.5, borderColor:'#e2e8f0' },
  row:       { flexDirection:'row', justifyContent:'space-between', alignItems:'center', marginBottom:6 },
  title:     { fontSize:14, fontWeight:'600', color:'#0f172a', flex:1 },
  badge:     { paddingHorizontal:8, paddingVertical:3, borderRadius:6 },
  badgeText: { fontSize:11, fontWeight:'600' },
  desc:      { fontSize:13, color:'#475569', lineHeight:18, marginBottom:4 },
  meta:      { fontSize:11, color:'#94a3b8' },
  empty:     { textAlign:'center', color:'#94a3b8', marginTop:40, fontSize:14 },
});
