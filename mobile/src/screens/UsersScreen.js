/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
﻿import React, { useEffect, useState } from 'react';
import { View, Text, FlatList, StyleSheet, RefreshControl, ActivityIndicator } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

const API = 'http://172.30.208.1:8089/supchain';

export default function UsersScreen() {
  const [users,      setUsers]      = useState([]);
  const [loading,    setLoading]    = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const fetchUsers = async () => {
    try {
      const token = await AsyncStorage.getItem('jwt_token');
      const res   = await fetch(API + '/api/users', { headers: { Authorization: 'Bearer ' + token } });
      if (res.ok) { const d = await res.json(); setUsers(Array.isArray(d) ? d : d.users || d.content || []); }
    } catch (e) { console.log('Users error:', e); }
    finally { setLoading(false); setRefreshing(false); }
  };

  useEffect(() => { fetchUsers(); }, []);

  if (loading) return <View style={s.center}><ActivityIndicator size="large" color="#2563eb" /></View>;

  return (
    <View style={s.container}>
      <Text style={s.header}>Users</Text>
      <Text style={s.sub}>{users.length} users</Text>
      <FlatList
        data={users}
        keyExtractor={(item, i) => (item.userId || item.id || i).toString()}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => { setRefreshing(true); fetchUsers(); }} />}
        renderItem={({ item }) => (
          <View style={s.card}>
            <View style={s.row}>
              <Text style={s.name}>{item.userName || item.name || item.userId}</Text>
              <View style={[s.badge,{backgroundColor:'#eff6ff'}]}>
                <Text style={[s.badgeText,{color:'#1d4ed8'}]}>{item.role || 'User'}</Text>
              </View>
            </View>
            <Text style={s.meta}>{item.email || ''}</Text>
          </View>
        )}
        ListEmptyComponent={<Text style={s.empty}>No users found</Text>}
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
  row:       { flexDirection:'row', justifyContent:'space-between', alignItems:'center', marginBottom:4 },
  name:      { fontSize:14, fontWeight:'600', color:'#0f172a' },
  badge:     { paddingHorizontal:8, paddingVertical:3, borderRadius:6 },
  badgeText: { fontSize:11, fontWeight:'600' },
  meta:      { fontSize:12, color:'#64748b' },
  empty:     { textAlign:'center', color:'#94a3b8', marginTop:40, fontSize:14 },
});
