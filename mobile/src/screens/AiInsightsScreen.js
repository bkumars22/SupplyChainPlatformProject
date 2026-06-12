/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
﻿import React, { useEffect, useState } from 'react';
import { View, Text, FlatList, TouchableOpacity, ActivityIndicator, StyleSheet, RefreshControl, Alert } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
const API = 'http://172.30.208.1:8089/supchain';
export default function AiInsightsScreen({ navigation }) {
  const [insights, setInsights] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const fetchInsights = async () => {
    try {
      const token = await AsyncStorage.getItem('jwt_token');
      const res = await fetch(API + '/api/ai/insights', { headers: { Authorization: 'Bearer ' + token } });
      if (res.status === 401) { navigation.replace('Login'); return; }
      const data = await res.json();
      setInsights(Array.isArray(data) ? data : []);
    } catch (e) { Alert.alert('Error', 'Could not load AI insights'); }
    finally { setLoading(false); setRefreshing(false); }
  };
  useEffect(() => { fetchInsights(); }, []);
  const riskColor = (l) => l === 'High' ? '#dc2626' : l === 'Medium' ? '#d97706' : '#16a34a';
  if (loading) return <View style={s.center}><ActivityIndicator size="large" color="#2563eb" /><Text style={s.loadingText}>Analyzing suppliers...</Text></View>;
  return (
    <View style={s.container}>
      <Text style={s.header}>AI Risk Insights</Text>
      <Text style={s.sub}>{insights.length} suppliers analyzed</Text>
      <FlatList data={insights} keyExtractor={(item, i) => (item.supplierId || i).toString()}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => { setRefreshing(true); fetchInsights(); }} />}
        renderItem={({ item }) => (
          <TouchableOpacity style={s.card} onPress={() => navigation.navigate('SupplierDetail', { supplierId: item.supplierId })}>
            <View style={s.row}>
              <Text style={s.name}>{item.supplierName || 'Supplier ' + item.supplierId}</Text>
              <View style={[s.badge, { backgroundColor: riskColor(item.riskLevel) + '20', borderColor: riskColor(item.riskLevel) }]}>
                <Text style={[s.badgeText, { color: riskColor(item.riskLevel) }]}>{item.riskLevel || 'N/A'}</Text>
              </View>
            </View>
            <Text style={s.exp}>{item.explanation || 'No explanation available'}</Text>
            <Text style={s.ontime}>On-time: {item.onTimeRate || 0}%  |  Score: {item.riskScore || 0}/100</Text>
          </TouchableOpacity>
        )}
        ListEmptyComponent={<Text style={s.empty}>No supplier data yet.</Text>}
      />
    </View>
  );
}
const s = StyleSheet.create({
  container: { flex:1, backgroundColor:'#f8fafc', padding:16 },
  center:    { flex:1, alignItems:'center', justifyContent:'center' },
  header:    { fontSize:22, fontWeight:'700', color:'#0f172a', marginBottom:4 },
  sub:       { fontSize:13, color:'#64748b', marginBottom:16 },
  loadingText: { marginTop:12, color:'#64748b', fontSize:14 },
  card:      { backgroundColor:'#fff', borderRadius:12, padding:14, marginBottom:10, borderWidth:0.5, borderColor:'#e2e8f0' },
  row:       { flexDirection:'row', justifyContent:'space-between', alignItems:'center', marginBottom:6 },
  name:      { fontSize:14, fontWeight:'600', color:'#0f172a', flex:1 },
  badge:     { paddingHorizontal:8, paddingVertical:3, borderRadius:6, borderWidth:1, marginLeft:8 },
  badgeText: { fontSize:11, fontWeight:'600' },
  exp:       { fontSize:12, color:'#475569', lineHeight:18, marginBottom:3 },
  ontime:    { fontSize:11, color:'#94a3b8' },
  empty:     { textAlign:'center', color:'#94a3b8', marginTop:40, fontSize:14 },
});
