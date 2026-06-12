/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
﻿import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, ActivityIndicator, Alert, KeyboardAvoidingView, Platform } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

const API = 'http://172.30.208.1:8089/supchain';

export default function LoginScreen({ navigation }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading,  setLoading]  = useState(false);
  const [showPass, setShowPass] = useState(false);

  const login = async () => {
    if (!username || !password) { Alert.alert('Error', 'Enter username and password'); return; }
    setLoading(true);
    try {
      const res  = await fetch(API + '/api/auth/login', {
        method: 'POST', headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
      });
      const data = await res.json();
      if (data.token) {
        await AsyncStorage.setItem('jwt_token', data.token);
        await AsyncStorage.setItem('user_name', data.userName || username);
        await AsyncStorage.setItem('user_role', data.role    || 'User');
        navigation.replace('Main');
      } else {
        Alert.alert('Login Failed', data.message || 'Invalid credentials');
      }
    } catch (e) { Alert.alert('Error', 'Cannot connect to server. Check WiFi.'); }
    finally { setLoading(false); }
  };

  return (
    <KeyboardAvoidingView style={s.container} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
      <View style={s.card}>
        <Text style={s.title}>Supply Chain</Text>
        <Text style={s.subtitle}>Intelligence Platform</Text>
        <View style={s.divider} />
        <Text style={s.label}>Username</Text>
        <TextInput style={s.input} placeholder="Enter username" value={username} onChangeText={setUsername} autoCapitalize="none" autoCorrect={false} />
        <Text style={s.label}>Password</Text>
        <View style={s.passRow}>
          <TextInput style={[s.input,{flex:1,marginBottom:0}]} placeholder="Enter password" value={password} onChangeText={setPassword} secureTextEntry={!showPass} />
          <TouchableOpacity onPress={() => setShowPass(!showPass)} style={s.eyeBtn}>
            <Text style={s.eyeText}>{showPass ? 'Hide' : 'Show'}</Text>
          </TouchableOpacity>
        </View>
        <TouchableOpacity style={s.btn} onPress={login} disabled={loading}>
          {loading ? <ActivityIndicator color="#fff" /> : <Text style={s.btnText}>Sign In</Text>}
        </TouchableOpacity>
        <Text style={s.hint}>Secured with JWT Authentication</Text>
      </View>
    </KeyboardAvoidingView>
  );
}

const s = StyleSheet.create({
  container: { flex:1, backgroundColor:'#0f172a', justifyContent:'center', padding:24 },
  card:      { backgroundColor:'#1e293b', borderRadius:16, padding:28 },
  title:     { fontSize:26, fontWeight:'800', color:'#f8fafc', textAlign:'center' },
  subtitle:  { fontSize:14, color:'#94a3b8', textAlign:'center', marginTop:4 },
  divider:   { height:1, backgroundColor:'#334155', marginVertical:20 },
  label:     { fontSize:12, fontWeight:'600', color:'#94a3b8', marginBottom:6, textTransform:'uppercase', letterSpacing:0.5 },
  input:     { backgroundColor:'#0f172a', borderWidth:1, borderColor:'#334155', borderRadius:8, padding:12, color:'#f8fafc', fontSize:14, marginBottom:14 },
  passRow:   { flexDirection:'row', alignItems:'center', gap:8, marginBottom:14 },
  eyeBtn:    { padding:12, backgroundColor:'#0f172a', borderRadius:8, borderWidth:1, borderColor:'#334155' },
  eyeText:   { color:'#94a3b8', fontSize:12 },
  btn:       { backgroundColor:'#2563eb', borderRadius:8, padding:14, alignItems:'center', marginTop:8 },
  btnText:   { color:'#fff', fontWeight:'700', fontSize:15 },
  hint:      { color:'#475569', fontSize:11, textAlign:'center', marginTop:12 },
});
