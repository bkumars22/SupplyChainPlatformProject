/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
﻿import React, { useEffect, useState } from 'react';
import { View, Text, ScrollView, TouchableOpacity, StyleSheet, RefreshControl, Alert } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

const API = 'http://172.30.208.1:8089/supchain';

export default function DashboardScreen({ navigation }) {
  const [stats,      setStats]      = useState(null);
  const [userName,   setUserName]   = useState('');
  const [refreshing, setRefreshing] = useState(false);

  const fetchData = async () => {
    try {
      const token = await AsyncStorage.getItem('jwt_token');
      const name  = await AsyncStorage.getItem('user_name');
      setUserName(name || 'User');
      const res = await fetch(API + '/api/dashboard/summary', { headers: { Authorization: 'Bearer ' + token } });
      if (res.ok) { const d = await res.json(); setStats(d); }
    } catch (e) { console.log('Dashboard error:', e); }
    finally { setRefreshing(false); }
  };

  useEffect(() => { fetchData(); }, []);

  const logout = async () => {
    Alert.alert('Logout', 'Are you sure?', [
      { text: 'Cancel', style: 'cancel' },
      { text: 'Logout', style: 'destructive', onPress: async () => {
        await AsyncStorage.multiRemove(['jwt_token','user_name','user_role']);
        navigation.replace('Login');
      }}
    ]);
  };

  const tiles = [
    { label:'Alerts',      value: stats?.unreadAlerts || 0, color:'#dc2626', screen:'Alerts' },
    { label:'BOMs',        value: stats?.totalBoms    || 0, color:'#2563eb', screen:'Bom' },
    { label:'Cost Records',value: '--',                     color:'#16a34a', screen:'CostRecords' },
    { label:'AI Insights', value: '--',                     color:'#d97706', screen:'AiInsights' },
  ];

  return (
    <ScrollView style={s.container} refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => { setRefreshing(true); fetchData(); }} />}>
      <View style={s.header}>
        <View>
          <Text style={s.welcome}>Welcome back,</Text>
          <Text style={s.name}>{userName}</Text>
        </View>
        <TouchableOpacity onPress={logout} style={s.logoutBtn}>
          <Text style={s.logoutText}>Logout</Text>
        </TouchableOpacity>
      </View>
      <Text style={s.sectionTitle}>Overview</Text>
      <View style={s.grid}>
        {tiles.map(t => (
          <TouchableOpacity key={t.label} style={[s.tile,{borderLeftColor:t.color}]} onPress={() => navigation.navigate(t.screen)}>
            <Text style={[s.tileVal,{color:t.color}]}>{t.value}</Text>
            <Text style={s.tileLabel}>{t.label}</Text>
          </TouchableOpacity>
        ))}
      </View>
      <Text style={s.sectionTitle}>Quick Actions</Text>
      {[
        {label:'AI Risk Insights',    screen:'AiInsights',  desc:'ML-powered supplier analysis'},
        {label:'Supplier Scorecard',  screen:'Suppliers',   desc:'Performance and tier data'},
        {label:'Active Alerts',       screen:'Alerts',      desc:'Real-time supply chain alerts'},
        {label:'Cost Records',        screen:'CostRecords', desc:'Pricing and cost management'},
        {label:'Bill of Materials',   screen:'Bom',         desc:'BOM management'},
        {label:'Users',               screen:'Users',       desc:'User management'},
      ].map(a => (
        <TouchableOpacity key={a.label} style={s.actionCard} onPress={() => navigation.navigate(a.screen)}>
          <View>
            <Text style={s.actionLabel}>{a.label}</Text>
            <Text style={s.actionDesc}>{a.desc}</Text>
          </View>
          <Text style={s.arrow}>â€º</Text>
        </TouchableOpacity>
      ))}
    </ScrollView>
  );
}

const s = StyleSheet.create({
  container:    { flex:1, backgroundColor:'#f8fafc' },
  header:       { backgroundColor:'#0f172a', padding:24, paddingTop:48, flexDirection:'row', justifyContent:'space-between', alignItems:'flex-end' },
  welcome:      { color:'#94a3b8', fontSize:13 },
  name:         { color:'#f8fafc', fontSize:20, fontWeight:'700' },
  logoutBtn:    { backgroundColor:'#1e293b', paddingHorizontal:12, paddingVertical:6, borderRadius:8 },
  logoutText:   { color:'#94a3b8', fontSize:12 },
  sectionTitle: { fontSize:13, fontWeight:'700', color:'#64748b', textTransform:'uppercase', letterSpacing:0.8, margin:16, marginBottom:8 },
  grid:         { flexDirection:'row', flexWrap:'wrap', paddingHorizontal:12, gap:8 },
  tile:         { backgroundColor:'#fff', borderRadius:10, padding:14, width:'47%', borderLeftWidth:3 },
  tileVal:      { fontSize:26, fontWeight:'800', marginBottom:4 },
  tileLabel:    { fontSize:12, color:'#64748b' },
  actionCard:   { backgroundColor:'#fff', marginHorizontal:16, marginBottom:8, borderRadius:10, padding:14, flexDirection:'row', justifyContent:'space-between', alignItems:'center' },
  actionLabel:  { fontSize:14, fontWeight:'600', color:'#0f172a', marginBottom:2 },
  actionDesc:   { fontSize:12, color:'#64748b' },
  arrow:        { color:'#94a3b8', fontSize:22 },
});
