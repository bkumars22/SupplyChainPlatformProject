/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
﻿import React, { useEffect, useState } from 'react';
import { View, Text, ScrollView, StyleSheet, ActivityIndicator } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

const API = 'http://172.30.208.1:8089/supchain';

export default function SupplierDetailScreen({ route }) {
  const { supplierId } = route.params || {};
  const [supplier, setSupplier] = useState(null);
  const [risk,     setRisk]     = useState(null);
  const [loading,  setLoading]  = useState(true);

  useEffect(() => {
    const fetch_ = async () => {
      try {
        const token = await AsyncStorage.getItem('jwt_token');
        const h     = { Authorization: 'Bearer ' + token };
        const [sRes, rRes] = await Promise.all([
          fetch(API + '/api/suppliers/' + supplierId, { headers: h }),
          fetch(API + '/api/ai/supplier/' + supplierId + '/risk', { headers: h }),
        ]);
        if (sRes.ok) setSupplier(await sRes.json());
        if (rRes.ok) setRisk(await rRes.json());
      } catch (e) { console.log('Supplier detail error:', e); }
      finally { setLoading(false); }
    };
    if (supplierId) fetch_();
  }, [supplierId]);

  const riskColor = (l) => l === 'High' ? '#dc2626' : l === 'Medium' ? '#d97706' : '#16a34a';

  if (loading) return <View style={s.center}><ActivityIndicator size="large" color="#2563eb" /></View>;

  return (
    <ScrollView style={s.container}>
      <View style={s.card}>
        <Text style={s.name}>{supplier?.supplierName || supplier?.name || 'Supplier ' + supplierId}</Text>
        <Text style={s.meta}>ID: {supplierId}</Text>
        {supplier?.tier && <Text style={s.meta}>Tier: {supplier.tier}</Text>}
      </View>
      {risk && (
        <View style={s.card}>
          <Text style={s.sectionTitle}>AI Risk Analysis</Text>
          <View style={s.row}>
            <Text style={s.label}>Risk Level</Text>
            <Text style={[s.value,{color:riskColor(risk.riskLevel),fontWeight:'700'}]}>{risk.riskLevel}</Text>
          </View>
          <View style={s.row}>
            <Text style={s.label}>Risk Score</Text>
            <Text style={s.value}>{risk.riskScore}/100</Text>
          </View>
          <View style={s.row}>
            <Text style={s.label}>On-Time Rate</Text>
            <Text style={s.value}>{risk.onTimeRate}%</Text>
          </View>
          <Text style={s.explanation}>{risk.explanation}</Text>
        </View>
      )}
    </ScrollView>
  );
}

const s = StyleSheet.create({
  container:    { flex:1, backgroundColor:'#f8fafc', padding:16 },
  center:       { flex:1, alignItems:'center', justifyContent:'center' },
  card:         { backgroundColor:'#fff', borderRadius:12, padding:16, marginBottom:12, borderWidth:0.5, borderColor:'#e2e8f0' },
  name:         { fontSize:18, fontWeight:'700', color:'#0f172a', marginBottom:4 },
  meta:         { fontSize:13, color:'#64748b', marginBottom:2 },
  sectionTitle: { fontSize:14, fontWeight:'600', color:'#0f172a', marginBottom:12 },
  row:          { flexDirection:'row', justifyContent:'space-between', paddingVertical:6, borderBottomWidth:0.5, borderBottomColor:'#f1f5f9' },
  label:        { fontSize:13, color:'#64748b' },
  value:        { fontSize:13, color:'#0f172a' },
  explanation:  { fontSize:12, color:'#475569', marginTop:10, lineHeight:18 },
});
