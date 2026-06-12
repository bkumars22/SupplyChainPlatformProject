/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
﻿import React, { useEffect, useState } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet, RefreshControl, ActivityIndicator } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

const API = 'http://172.30.208.1:8089/supchain';

export default function BomScreen({ navigation }) {
  const [boms,       setBoms]       = useState([]);
  const [loading,    setLoading]    = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const fetchBoms = async () => {
    try {
      const token = await AsyncStorage.getItem('jwt_token');
      const res   = await fetch(API + '/api/bom?page=0&size=20', { headers: { Authorization: 'Bearer ' + token } });
      if (res.ok) { const d = await res.json(); setBoms(Array.isArray(d) ? d : d.content || d.boms || []); }
    } catch (e) { console.log('BOM error:', e); }
    finally { setLoading(false); setRefreshing(false); }
  };

  useEffect(() => { fetchBoms(); }, []);

  if (loading) return <View style={s.center}><ActivityIndicator size="large" color="#2563eb" /></View>;

  return (
    <View style={s.container}>
      <Text style={s.header}>Bill of Materials</Text>
      <Text style={s.sub}>{boms.length} BOMs</Text>
      <FlatList
        data={boms}
        keyExtractor={(item, i) => (item.bomKey || item.id || i).toString()}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => { setRefreshing(true); fetchBoms(); }} />}
        renderItem={({ item }) => (
          <TouchableOpacity style={s.card} onPress={() => navigation.navigate('BomDetail', { bomKey: item.bomKey || item.id })}>
            <View style={s.row}>
              <Text style={s.bomKey}>{item.bomKey || item.id || 'BOM'}</Text>
              <View style={[s.badge,{backgroundColor: item.status==='Active'?'#dcfce7':'#f1f5f9'}]}>
                <Text style={[s.badgeText,{color: item.status==='Active'?'#16a34a':'#64748b'}]}>{item.status || 'Draft'}</Text>
              </View>
            </View>
            <Text style={s.desc}>{item.description || item.name || 'No description'}</Text>
            <Text style={s.meta}>{item.partNumber ? 'Part: ' + item.partNumber : ''}</Text>
          </TouchableOpacity>
        )}
        ListEmptyComponent={<Text style={s.empty}>No BOMs found</Text>}
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
  bomKey:    { fontSize:14, fontWeight:'600', color:'#0f172a', flex:1 },
  badge:     { paddingHorizontal:8, paddingVertical:3, borderRadius:6 },
  badgeText: { fontSize:11, fontWeight:'600' },
  desc:      { fontSize:13, color:'#475569', marginBottom:4 },
  meta:      { fontSize:11, color:'#94a3b8' },
  empty:     { textAlign:'center', color:'#94a3b8', marginTop:40, fontSize:14 },
});
