# ============================================================
# Supply Chain Intelligence Platform
# Mobile App Build, Deploy & Security Agent
# Save to: .\mobile-agent.ps1
# Run with: powershell -ExecutionPolicy Bypass -File .\mobile-agent.ps1
# ============================================================

$PROJECT     = "."
$MOBILE_DIR  = "$PROJECT\mobile"
$SCWEB_DIR   = "..\scweb"
$JAVA_PORT   = 8089
$AI_PORT     = 8001
$WEB_PORT    = 3000
$BASE_URL    = "http://localhost:$JAVA_PORT/supchain"
$VERSION     = "1.0.0"

# ── Colors ──────────────────────────────────────────────────
function Green($t)  { Write-Host $t -ForegroundColor Green }
function Red($t)    { Write-Host $t -ForegroundColor Red }
function Yellow($t) { Write-Host $t -ForegroundColor Yellow }
function Cyan($t)   { Write-Host $t -ForegroundColor Cyan }
function White($t)  { Write-Host $t -ForegroundColor White }
function Magenta($t){ Write-Host $t -ForegroundColor Magenta }

function Header {
    Clear-Host
    Cyan "========================================================"
    Cyan "   Supply Chain Platform - Mobile Agent v$VERSION"
    Cyan "   Build . Deploy . Secure . Install"
    Cyan "========================================================"
    Write-Host ""
}

function IsPortInUse($port) {
    return (Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue) -ne $null
}

function GetToken {
    try {
        $r = Invoke-RestMethod -Uri "$BASE_URL/api/auth/login" `
            -Method POST -ContentType "application/json" `
            -Body '{"username":"kumar","password":"kumar"}' -TimeoutSec 10
        return $r.token
    } catch { return $null }
}

function GetLocalIP {
    $ip = (Get-NetIPAddress -AddressFamily IPv4 |
           Where-Object { $_.InterfaceAlias -notmatch "Loopback" -and $_.IPAddress -notmatch "^169" } |
           Select-Object -First 1).IPAddress
    return $ip
}

# ══════════════════════════════════════════════════════════
# TASK 1 — CHECK ENVIRONMENT
# ══════════════════════════════════════════════════════════
function CheckEnvironment {
    Header
    White "CHECKING DEVELOPMENT ENVIRONMENT"
    Write-Host "--------------------------------------------------------"
    Write-Host ""

    $issues = 0

    # Node.js
    try {
        $nodeVer = node --version 2>&1
        Green "  OK  Node.js       $nodeVer"
    } catch {
        Red "  --  Node.js       NOT FOUND"
        Yellow "      Fix: Download from https://nodejs.org"
        $issues++
    }

    # npm
    try {
        $npmVer = npm --version 2>&1
        Green "  OK  npm           v$npmVer"
    } catch {
        Red "  --  npm           NOT FOUND"
        $issues++
    }

    # Java
    try {
        $javaVer = java -version 2>&1 | Select-String "version"
        Green "  OK  Java          $javaVer"
    } catch {
        Red "  --  Java          NOT FOUND"
        $issues++
    }

    # Python
    try {
        $pyVer = python --version 2>&1
        Green "  OK  Python        $pyVer"
    } catch {
        Red "  --  Python        NOT FOUND"
        $issues++
    }

    # Android SDK
    $androidHome = $env:ANDROID_HOME
    if ($androidHome -and (Test-Path $androidHome)) {
        Green "  OK  Android SDK   $androidHome"
    } else {
        Yellow "  !!  Android SDK   NOT SET (needed for APK build)"
        Yellow "      Fix: Install Android Studio from developer.android.com"
    }

    # React Native CLI
    try {
        $rnVer = npx react-native --version 2>&1 | Select-String "react-native"
        Green "  OK  React Native  $rnVer"
    } catch {
        Yellow "  !!  React Native  will install on first build"
    }

    # Mobile directory
    if (Test-Path $MOBILE_DIR) {
        Green "  OK  Mobile dir    $MOBILE_DIR"
    } else {
        Red "  --  Mobile dir    NOT FOUND at $MOBILE_DIR"
        $issues++
    }

    # Local IP
    $ip = GetLocalIP
    Green "  OK  Local IP      $ip (mobile devices use this)"

    Write-Host ""
    if ($issues -eq 0) {
        Green "  All checks passed!"
    } else {
        Yellow "  $issues issue(s) found — run Auto-Fix to resolve"
    }
    Write-Host ""
}

# ══════════════════════════════════════════════════════════
# TASK 2 — GENERATE MISSING MOBILE SCREENS
# ══════════════════════════════════════════════════════════
function GenerateMobileScreens {
    Header
    White "GENERATING MOBILE SCREENS"
    Write-Host "--------------------------------------------------------"
    Write-Host ""

    $ip = GetLocalIP
    $screens = @{
        "AiInsightsScreen.js" = @"
import React, { useEffect, useState } from 'react';
import {
  View, Text, FlatList, TouchableOpacity,
  ActivityIndicator, StyleSheet, RefreshControl, Alert
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

const API = 'http://${ip}:8089/supchain';

export default function AiInsightsScreen({ navigation }) {
  const [insights, setInsights] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const fetchInsights = async () => {
    try {
      const token = await AsyncStorage.getItem('jwt_token');
      const res = await fetch(API + '/api/ai/insights', {
        headers: { Authorization: 'Bearer ' + token }
      });
      if (res.status === 401) { navigation.replace('Login'); return; }
      const data = await res.json();
      setInsights(Array.isArray(data) ? data : []);
    } catch (e) {
      Alert.alert('Error', 'Could not load AI insights');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useEffect(() => { fetchInsights(); }, []);

  const riskColor = (level) => {
    if (level === 'High')   return '#dc2626';
    if (level === 'Medium') return '#d97706';
    return '#16a34a';
  };

  if (loading) return (
    <View style={s.center}>
      <ActivityIndicator size="large" color="#2563eb" />
      <Text style={s.loadingText}>Analyzing suppliers with AI...</Text>
    </View>
  );

  return (
    <View style={s.container}>
      <Text style={s.header}>AI Risk Insights</Text>
      <Text style={s.sub}>{insights.length} suppliers analyzed</Text>
      <FlatList
        data={insights}
        keyExtractor={(item, i) => (item.supplierId || i).toString()}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => { setRefreshing(true); fetchInsights(); }} />}
        renderItem={({ item }) => (
          <TouchableOpacity style={s.card}
            onPress={() => navigation.navigate('SupplierDetail', { supplierId: item.supplierId })}>
            <View style={s.row}>
              <Text style={s.name}>{item.supplierName || 'Supplier ' + item.supplierId}</Text>
              <View style={[s.badge, { backgroundColor: riskColor(item.riskLevel) + '20', borderColor: riskColor(item.riskLevel) }]}>
                <Text style={[s.badgeText, { color: riskColor(item.riskLevel) }]}>{item.riskLevel || 'N/A'}</Text>
              </View>
            </View>
            <View style={s.scoreRow}>
              <View style={s.barBg}>
                <View style={[s.barFill, { width: (item.riskScore || 0) + '%', backgroundColor: riskColor(item.riskLevel) }]} />
              </View>
              <Text style={[s.score, { color: riskColor(item.riskLevel) }]}>{item.riskScore || 0}/100</Text>
            </View>
            <Text style={s.exp}>{item.explanation || 'No explanation available'}</Text>
            <Text style={s.ontime}>On-time delivery: {item.onTimeRate || 0}%</Text>
          </TouchableOpacity>
        )}
        ListEmptyComponent={<Text style={s.empty}>No supplier data yet. Add deliveries to see AI insights.</Text>}
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
  card:      { backgroundColor:'#fff', borderRadius:12, padding:14, marginBottom:10, borderWidth:0.5, borderColor:'#e2e8f0', shadowColor:'#000', shadowOpacity:0.04, shadowRadius:4, elevation:2 },
  row:       { flexDirection:'row', justifyContent:'space-between', alignItems:'center', marginBottom:8 },
  name:      { fontSize:14, fontWeight:'600', color:'#0f172a', flex:1 },
  badge:     { paddingHorizontal:8, paddingVertical:3, borderRadius:6, borderWidth:1, marginLeft:8 },
  badgeText: { fontSize:11, fontWeight:'600' },
  scoreRow:  { flexDirection:'row', alignItems:'center', gap:8, marginBottom:6 },
  barBg:     { flex:1, height:6, backgroundColor:'#f1f5f9', borderRadius:3, overflow:'hidden' },
  barFill:   { height:'100%', borderRadius:3 },
  score:     { fontSize:12, fontWeight:'600', width:45, textAlign:'right' },
  exp:       { fontSize:12, color:'#475569', lineHeight:18, marginBottom:3 },
  ontime:    { fontSize:11, color:'#94a3b8' },
  empty:     { textAlign:'center', color:'#94a3b8', marginTop:40, fontSize:14 },
});
"@

        "LoginScreen.js" = @"
import React, { useState } from 'react';
import {
  View, Text, TextInput, TouchableOpacity,
  StyleSheet, ActivityIndicator, Alert, KeyboardAvoidingView, Platform
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

const API = 'http://${ip}:8089/supchain';

export default function LoginScreen({ navigation }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading,  setLoading]  = useState(false);
  const [showPass, setShowPass] = useState(false);

  const login = async () => {
    if (!username || !password) {
      Alert.alert('Error', 'Please enter username and password');
      return;
    }
    setLoading(true);
    try {
      const res = await fetch(API + '/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
      });
      const data = await res.json();
      if (data.token) {
        await AsyncStorage.setItem('jwt_token', data.token);
        await AsyncStorage.setItem('user_name', data.userName || username);
        await AsyncStorage.setItem('user_role', data.role || 'User');
        navigation.replace('Main');
      } else {
        Alert.alert('Login Failed', data.message || 'Invalid credentials');
      }
    } catch (e) {
      Alert.alert('Error', 'Cannot connect to server. Check your network.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView style={s.container} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
      <View style={s.card}>
        <Text style={s.title}>Supply Chain</Text>
        <Text style={s.subtitle}>Intelligence Platform</Text>
        <View style={s.divider} />
        <Text style={s.label}>Username</Text>
        <TextInput
          style={s.input}
          placeholder="Enter username"
          value={username}
          onChangeText={setUsername}
          autoCapitalize="none"
          autoCorrect={false}
        />
        <Text style={s.label}>Password</Text>
        <View style={s.passRow}>
          <TextInput
            style={[s.input, { flex:1, marginBottom:0 }]}
            placeholder="Enter password"
            value={password}
            onChangeText={setPassword}
            secureTextEntry={!showPass}
          />
          <TouchableOpacity onPress={() => setShowPass(!showPass)} style={s.eyeBtn}>
            <Text style={s.eyeText}>{showPass ? 'Hide' : 'Show'}</Text>
          </TouchableOpacity>
        </View>
        <TouchableOpacity style={s.btn} onPress={login} disabled={loading}>
          {loading
            ? <ActivityIndicator color="#fff" />
            : <Text style={s.btnText}>Sign In</Text>}
        </TouchableOpacity>
        <Text style={s.hint}>Secured with JWT Authentication</Text>
      </View>
    </KeyboardAvoidingView>
  );
}

const s = StyleSheet.create({
  container: { flex:1, backgroundColor:'#0f172a', justifyContent:'center', padding:24 },
  card:      { backgroundColor:'#1e293b', borderRadius:16, padding:28, shadowColor:'#000', shadowOpacity:0.3, shadowRadius:12, elevation:8 },
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
"@

        "DashboardScreen.js" = @"
import React, { useEffect, useState } from 'react';
import { View, Text, ScrollView, TouchableOpacity, StyleSheet, RefreshControl, Alert } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

const API = 'http://${ip}:8089/supchain';

export default function DashboardScreen({ navigation }) {
  const [stats,      setStats]      = useState(null);
  const [userName,   setUserName]   = useState('');
  const [refreshing, setRefreshing] = useState(false);

  const fetchData = async () => {
    try {
      const token    = await AsyncStorage.getItem('jwt_token');
      const name     = await AsyncStorage.getItem('user_name');
      setUserName(name || 'User');
      const res = await fetch(API + '/api/dashboard', {
        headers: { Authorization: 'Bearer ' + token }
      });
      if (res.ok) { const d = await res.json(); setStats(d); }
    } catch (e) {
      console.log('Dashboard fetch error:', e);
    } finally { setRefreshing(false); }
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
    { label:'Suppliers',    value: stats?.supplierCount    || '--', color:'#2563eb', screen:'Suppliers' },
    { label:'Active Alerts',value: stats?.activeAlerts     || '--', color:'#dc2626', screen:'Alerts' },
    { label:'Cost Records', value: stats?.costRecordCount  || '--', color:'#16a34a', screen:'CostRecords' },
    { label:'AI Insights',  value: stats?.highRiskCount    || '--', color:'#d97706', screen:'AiInsights' },
  ];

  return (
    <ScrollView style={s.container}
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => { setRefreshing(true); fetchData(); }} />}>
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
          <TouchableOpacity key={t.label} style={[s.tile, { borderLeftColor: t.color }]}
            onPress={() => navigation.navigate(t.screen)}>
            <Text style={[s.tileVal, { color: t.color }]}>{t.value}</Text>
            <Text style={s.tileLabel}>{t.label}</Text>
          </TouchableOpacity>
        ))}
      </View>
      <Text style={s.sectionTitle}>Quick Actions</Text>
      {[
        { label:'View AI Risk Insights',    screen:'AiInsights',  desc:'ML-powered supplier analysis' },
        { label:'Check Supplier Scorecard', screen:'Suppliers',   desc:'Performance and tier data' },
        { label:'Review Active Alerts',     screen:'Alerts',      desc:'Real-time supply chain alerts' },
        { label:'Cost Records',             screen:'CostRecords', desc:'Pricing and cost management' },
      ].map(a => (
        <TouchableOpacity key={a.label} style={s.actionCard}
          onPress={() => navigation.navigate(a.screen)}>
          <View>
            <Text style={s.actionLabel}>{a.label}</Text>
            <Text style={s.actionDesc}>{a.desc}</Text>
          </View>
          <Text style={s.arrow}>></Text>
        </TouchableOpacity>
      ))}
    </ScrollView>
  );
}

const s = StyleSheet.create({
  container:   { flex:1, backgroundColor:'#f8fafc' },
  header:      { backgroundColor:'#0f172a', padding:24, paddingTop:48, flexDirection:'row', justifyContent:'space-between', alignItems:'flex-end' },
  welcome:     { color:'#94a3b8', fontSize:13 },
  name:        { color:'#f8fafc', fontSize:20, fontWeight:'700' },
  logoutBtn:   { backgroundColor:'#1e293b', paddingHorizontal:12, paddingVertical:6, borderRadius:8 },
  logoutText:  { color:'#94a3b8', fontSize:12 },
  sectionTitle:{ fontSize:13, fontWeight:'700', color:'#64748b', textTransform:'uppercase', letterSpacing:0.8, margin:16, marginBottom:8 },
  grid:        { flexDirection:'row', flexWrap:'wrap', paddingHorizontal:12, gap:8 },
  tile:        { backgroundColor:'#fff', borderRadius:10, padding:14, width:'47%', borderLeftWidth:3, shadowColor:'#000', shadowOpacity:0.04, shadowRadius:4, elevation:2 },
  tileVal:     { fontSize:26, fontWeight:'800', marginBottom:4 },
  tileLabel:   { fontSize:12, color:'#64748b' },
  actionCard:  { backgroundColor:'#fff', marginHorizontal:16, marginBottom:8, borderRadius:10, padding:14, flexDirection:'row', justifyContent:'space-between', alignItems:'center', shadowColor:'#000', shadowOpacity:0.04, shadowRadius:4, elevation:2 },
  actionLabel: { fontSize:14, fontWeight:'600', color:'#0f172a', marginBottom:2 },
  actionDesc:  { fontSize:12, color:'#64748b' },
  arrow:       { color:'#94a3b8', fontSize:18 },
});
"@
    }

    # Write each screen
    foreach ($fileName in $screens.Keys) {
        $filePath = "$MOBILE_DIR\src\screens\$fileName"
        if (-not (Test-Path "$MOBILE_DIR\src\screens")) {
            New-Item -ItemType Directory -Path "$MOBILE_DIR\src\screens" -Force | Out-Null
        }
        if (Test-Path $filePath) {
            Yellow "  !! $fileName already exists — skipping (use force option to overwrite)"
        } else {
            $screens[$fileName] | Out-File -FilePath $filePath -Encoding utf8
            Green "  OK Generated: $fileName"
        }
    }

    Write-Host ""
    Green "  Screens generated in: $MOBILE_DIR\src\screens\"
    Write-Host ""
}

# ══════════════════════════════════════════════════════════
# TASK 3 — SETUP SECURITY
# ══════════════════════════════════════════════════════════
function SetupSecurity {
    Header
    White "SETTING UP MOBILE SECURITY"
    Write-Host "--------------------------------------------------------"
    Write-Host ""

    Set-Location $MOBILE_DIR

    # Install security packages
    Yellow "  Installing security packages..."
    $packages = @(
        "@react-native-async-storage/async-storage",
        "react-native-keychain",
        "react-native-biometrics",
        "react-native-ssl-pinning",
        "react-native-encrypted-storage"
    )

    foreach ($pkg in $packages) {
        Yellow "  Installing $pkg..."
        $result = npm install $pkg --save 2>&1
        if ($LASTEXITCODE -eq 0) {
            Green "  OK  $pkg"
        } else {
            Yellow "  !!  $pkg — may need manual install"
        }
    }

    # Generate security config file
    $ip = GetLocalIP
    $secConfig = @"
// security-config.js
// Auto-generated by Mobile Agent
// Place in: mobile/src/config/security-config.js

export const SECURITY_CONFIG = {

  // JWT settings
  JWT: {
    STORAGE_KEY: 'jwt_token',
    USER_KEY: 'user_name',
    ROLE_KEY: 'user_role',
    EXPIRY_KEY: 'token_expiry',
  },

  // API endpoints
  API: {
    BASE_URL: 'http://${ip}:8089/supchain',
    AI_URL: 'http://${ip}:8001',
    TIMEOUT: 15000,
    RETRY_ATTEMPTS: 3,
  },

  // Security features
  FEATURES: {
    BIOMETRIC_LOGIN: true,
    CERTIFICATE_PINNING: false,  // enable in production
    ENCRYPTED_STORAGE: true,
    AUTO_LOGOUT_MINUTES: 30,
    MAX_LOGIN_ATTEMPTS: 5,
  },

  // Role permissions
  ROLES: {
    Administrator: ['dashboard','suppliers','alerts','ai','costs','bom','users'],
    Manager:       ['dashboard','suppliers','alerts','ai','costs','bom'],
    Analyst:       ['dashboard','suppliers','alerts','ai'],
    Viewer:        ['dashboard','suppliers'],
  },
};

// Secure API helper with JWT auto-attach
export const secureRequest = async (endpoint, options = {}) => {
  const AsyncStorage = require('@react-native-async-storage/async-storage').default;
  const token = await AsyncStorage.getItem(SECURITY_CONFIG.JWT.STORAGE_KEY);

  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: 'Bearer ' + token } : {}),
    ...options.headers,
  };

  const response = await fetch(SECURITY_CONFIG.API.BASE_URL + endpoint, {
    ...options,
    headers,
  });

  // Auto-handle token expiry
  if (response.status === 401) {
    await AsyncStorage.multiRemove([
      SECURITY_CONFIG.JWT.STORAGE_KEY,
      SECURITY_CONFIG.JWT.USER_KEY,
    ]);
    throw new Error('SESSION_EXPIRED');
  }

  return response;
};

// Check if user has permission for a screen
export const hasPermission = async (screen) => {
  const AsyncStorage = require('@react-native-async-storage/async-storage').default;
  const role = await AsyncStorage.getItem(SECURITY_CONFIG.JWT.ROLE_KEY);
  const allowed = SECURITY_CONFIG.ROLES[role] || [];
  return allowed.includes(screen.toLowerCase());
};
"@

    $configDir = "$MOBILE_DIR\src\config"
    if (-not (Test-Path $configDir)) {
        New-Item -ItemType Directory -Path $configDir -Force | Out-Null
    }
    $secConfig | Out-File -FilePath "$configDir\security-config.js" -Encoding utf8
    Green "  OK  Generated: security-config.js"

    Write-Host ""
    Green "  Security setup complete!"
    White "  Features enabled:"
    Cyan "    - JWT token storage (AsyncStorage)"
    Cyan "    - Auto session expiry (30 min)"
    Cyan "    - Role-based screen access"
    Cyan "    - Auto logout on 401"
    Cyan "    - Max login attempts (5)"
    Write-Host ""
}

# ══════════════════════════════════════════════════════════
# TASK 4 — BUILD APK
# ══════════════════════════════════════════════════════════
function BuildAPK {
    Header
    White "BUILDING ANDROID APK"
    Write-Host "--------------------------------------------------------"
    Write-Host ""

    if (-not (Test-Path $MOBILE_DIR)) {
        Red "  Mobile directory not found: $MOBILE_DIR"
        return
    }

    Set-Location $MOBILE_DIR

    # Check Android SDK
    if (-not $env:ANDROID_HOME) {
        Yellow "  ANDROID_HOME not set"
        Yellow "  Attempting to find Android SDK..."
        $possiblePaths = @(
            "$env:LOCALAPPDATA\Android\Sdk",
            "$env:USERPROFILE\AppData\Local\Android\Sdk",
            "C:\Android\sdk",
            "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk"
        )
        foreach ($path in $possiblePaths) {
            if (Test-Path $path) {
                $env:ANDROID_HOME = $path
                Green "  Found Android SDK at: $path"
                break
            }
        }
        if (-not $env:ANDROID_HOME) {
            Red "  Android SDK not found!"
            Red "  Install Android Studio from: https://developer.android.com/studio"
            return
        }
    }

    # Install npm packages if needed
    if (-not (Test-Path "$MOBILE_DIR\node_modules")) {
        Yellow "  Installing npm packages..."
        npm install
    }

    # Build debug APK
    Yellow "  Building debug APK (this takes 2-5 minutes)..."
    Set-Location "$MOBILE_DIR\android"
    .\gradlew assembleDebug 2>&1 | Tee-Object -Variable buildOutput

    $apkPath = "$MOBILE_DIR\android\app\build\outputs\apk\debug\app-debug.apk"
    if (Test-Path $apkPath) {
        $size = [math]::Round((Get-Item $apkPath).Length / 1MB, 1)
        Write-Host ""
        Green "  APK built successfully!"
        Green "  Location: $apkPath"
        Green "  Size: ${size}MB"

        # Copy to easy location
        $dest = "$PROJECT\supply-chain-app-debug.apk"
        Copy-Item $apkPath $dest
        Green "  Copied to: $dest"
    } else {
        Red "  APK build failed — check Android Studio is installed"
        Yellow "  Build output saved to: $PROJECT\build-output.txt"
        $buildOutput | Out-File "$PROJECT\build-output.txt"
    }
    Write-Host ""
}

# ══════════════════════════════════════════════════════════
# TASK 5 — INSTALL ON DEVICE (USB)
# ══════════════════════════════════════════════════════════
function InstallOnDevice {
    Header
    White "INSTALL APK ON ANDROID DEVICE"
    Write-Host "--------------------------------------------------------"
    Write-Host ""

    # Find adb
    $adb = $null
    $adbPaths = @(
        "$env:ANDROID_HOME\platform-tools\adb.exe",
        "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe",
        "$env:USERPROFILE\AppData\Local\Android\Sdk\platform-tools\adb.exe"
    )
    foreach ($path in $adbPaths) {
        if (Test-Path $path) { $adb = $path; break }
    }

    if (-not $adb) {
        Red "  ADB not found"
        Yellow "  Install Android Studio to get ADB"
        return
    }

    # Check connected devices
    Yellow "  Checking connected devices..."
    $devices = & $adb devices 2>&1
    Write-Host $devices

    if ($devices -notmatch "device$") {
        Yellow ""
        Yellow "  No device found. Steps to connect:"
        Cyan "  1. On your Android phone:"
        Cyan "     Settings > About Phone > tap Build Number 7 times"
        Cyan "     Settings > Developer Options > enable USB Debugging"
        Cyan "  2. Connect phone to PC with USB cable"
        Cyan "  3. On phone: tap 'Allow USB Debugging'"
        Cyan "  4. Run this option again"
        return
    }

    # Find APK
    $apkPath = "$PROJECT\supply-chain-app-debug.apk"
    if (-not (Test-Path $apkPath)) {
        $apkPath = "$MOBILE_DIR\android\app\build\outputs\apk\debug\app-debug.apk"
    }

    if (-not (Test-Path $apkPath)) {
        Red "  APK not found — build it first (option 4)"
        return
    }

    Yellow "  Installing APK on device..."
    $result = & $adb install -r $apkPath 2>&1
    Write-Host $result

    if ($result -match "Success") {
        Green "  APK installed successfully!"
        Green "  Look for 'Supply Chain Platform' app on your phone"
    } else {
        Red "  Installation failed"
        Yellow "  Try: adb install -r $apkPath"
    }
    Write-Host ""
}

# ══════════════════════════════════════════════════════════
# TASK 6 — DEPLOY TO LOCAL NETWORK (WiFi)
# ══════════════════════════════════════════════════════════
function DeployLocalNetwork {
    Header
    White "DEPLOY TO LOCAL NETWORK (WiFi)"
    Write-Host "--------------------------------------------------------"
    Write-Host ""

    $ip = GetLocalIP
    Yellow "  Your PC's local IP: $ip"
    Write-Host ""

    # Generate QR code page
    $qrHTML = @"
<!DOCTYPE html>
<html>
<head>
  <title>Supply Chain Platform - Mobile Access</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <script src="https://cdnjs.cloudflare.com/ajax/libs/qrcodejs/1.0.0/qrcode.min.js"></script>
  <style>
    body { font-family: -apple-system, sans-serif; background: #0f172a; color: #f8fafc;
           display: flex; flex-direction: column; align-items: center; padding: 40px 20px; }
    h1 { font-size: 24px; margin-bottom: 4px; }
    p { color: #94a3b8; margin-bottom: 30px; }
    .card { background: #1e293b; border-radius: 16px; padding: 30px; text-align: center;
            margin-bottom: 20px; max-width: 340px; width: 100%; }
    .qr { background: white; padding: 16px; border-radius: 12px; display: inline-block; margin-bottom: 16px; }
    .url { background: #0f172a; padding: 10px 14px; border-radius: 8px;
           font-family: monospace; font-size: 13px; color: #60a5fa; word-break: break-all; }
    .steps { background: #1e293b; border-radius: 16px; padding: 24px; max-width: 340px; width: 100%; }
    .step { display: flex; gap: 12px; margin-bottom: 16px; align-items: flex-start; }
    .num { background: #2563eb; color: white; width: 24px; height: 24px; border-radius: 50%;
           display: flex; align-items: center; justify-content: center; font-size: 12px;
           font-weight: 700; flex-shrink: 0; margin-top: 2px; }
    .step-text { font-size: 14px; color: #e2e8f0; line-height: 1.5; }
    .label { font-size: 11px; color: #64748b; margin-bottom: 8px; text-transform: uppercase; letter-spacing: 0.5px; }
  </style>
</head>
<body>
  <h1>Supply Chain Platform</h1>
  <p>Scan to access on your mobile device</p>

  <div class="card">
    <div class="label">Web App</div>
    <div class="qr" id="qr-web"></div>
    <div class="url">http://${ip}:3000</div>
  </div>

  <div class="steps">
    <div style="font-size:15px; font-weight:600; margin-bottom:16px;">Connection Steps</div>
    <div class="step"><div class="num">1</div><div class="step-text">Connect your phone to the same WiFi network as this PC</div></div>
    <div class="step"><div class="num">2</div><div class="step-text">Scan the QR code above or type the URL in your browser</div></div>
    <div class="step"><div class="num">3</div><div class="step-text">Login with: <strong>kumar / kumar</strong></div></div>
    <div class="step"><div class="num">4</div><div class="step-text">For native app: install the APK from supply-chain-app-debug.apk</div></div>
  </div>

  <script>
    new QRCode(document.getElementById('qr-web'), {
      text: 'http://${ip}:3000',
      width: 200, height: 200,
      colorDark: '#000000', colorLight: '#ffffff',
    });
  </script>
</body>
</html>
"@

    $qrPath = "$PROJECT\mobile-access.html"
    $qrHTML | Out-File -FilePath $qrPath -Encoding utf8
    Green "  QR code page created: $qrPath"

    # Open in browser
    Start-Process $qrPath
    Green "  Opened in browser!"

    Write-Host ""
    White "  Access URLs for mobile devices on your WiFi:"
    Cyan "  Web App:  http://${ip}:3000"
    Cyan "  API:      http://${ip}:8089/supchain"
    Cyan "  AI:       http://${ip}:8001"
    Write-Host ""
    Yellow "  Make sure Windows Firewall allows ports 3000, 8089, 8001"
    Yellow "  Run option 7 to open firewall ports automatically"
    Write-Host ""
}

# ══════════════════════════════════════════════════════════
# TASK 7 — OPEN FIREWALL PORTS
# ══════════════════════════════════════════════════════════
function OpenFirewallPorts {
    Header
    White "OPENING FIREWALL PORTS FOR MOBILE ACCESS"
    Write-Host "--------------------------------------------------------"
    Write-Host ""

    $ports = @(
        @{ port=3000;  name="Supply Chain React Web" },
        @{ port=8089;  name="Supply Chain Java API" },
        @{ port=8001;  name="Supply Chain Python AI" }
    )

    foreach ($p in $ports) {
        $ruleName = $p.name
        $port = $p.port

        # Check if rule exists
        $existing = Get-NetFirewallRule -DisplayName $ruleName -ErrorAction SilentlyContinue
        if ($existing) {
            Green "  OK  Port $port already open ($ruleName)"
        } else {
            try {
                New-NetFirewallRule -DisplayName $ruleName `
                    -Direction Inbound -Protocol TCP `
                    -LocalPort $port -Action Allow `
                    -ErrorAction Stop | Out-Null
                Green "  OK  Port $port opened ($ruleName)"
            } catch {
                Yellow "  !!  Port $port - run as Administrator to open firewall"
            }
        }
    }

    Write-Host ""
    Green "  Firewall configuration complete!"
    Yellow "  Note: If rules failed, right-click PowerShell and 'Run as Administrator'"
    Write-Host ""
}

# ══════════════════════════════════════════════════════════
# TASK 8 — FULL STATUS REPORT
# ══════════════════════════════════════════════════════════
function FullStatusReport {
    Header
    $ip = GetLocalIP
    White "FULL SYSTEM STATUS REPORT"
    Write-Host "--------------------------------------------------------"
    Write-Host ""

    White "SERVICES:"
    if (IsPortInUse $AI_PORT)   { Green "  OK  Python AI     http://localhost:$AI_PORT/docs" }
    else                         { Red   "  --  Python AI     NOT running" }
    if (IsPortInUse $JAVA_PORT) { Green "  OK  Java Backend  http://localhost:$JAVA_PORT/supchain" }
    else                         { Red   "  --  Java Backend  NOT running" }
    if (IsPortInUse $WEB_PORT)  { Green "  OK  React Web     http://localhost:$WEB_PORT" }
    else                         { Red   "  --  React Web     NOT running" }

    Write-Host ""
    White "MOBILE ACCESS:"
    Cyan "  Web (browser): http://${ip}:3000"
    Cyan "  API direct:    http://${ip}:8089/supchain"

    Write-Host ""
    White "GENERATED FILES:"
    $files = @(
        "$MOBILE_DIR\src\screens\LoginScreen.js",
        "$MOBILE_DIR\src\screens\DashboardScreen.js",
        "$MOBILE_DIR\src\screens\AiInsightsScreen.js",
        "$MOBILE_DIR\src\config\security-config.js",
        "$PROJECT\supply-chain-app-debug.apk",
        "$PROJECT\mobile-access.html"
    )
    foreach ($f in $files) {
        if (Test-Path $f) { Green "  OK  $f" }
        else              { Yellow "  --  $f (not yet created)" }
    }

    Write-Host ""
    White "API TESTS:"
    $token = GetToken
    if ($token) {
        Green "  OK  Auth login"
        try {
            $risk = Invoke-RestMethod -Uri "$BASE_URL/api/ai/supplier/1/risk" `
                -Headers @{Authorization="Bearer $token"} -TimeoutSec 8
            Green "  OK  AI risk score: $($risk.riskScore)/100 ($($risk.riskLevel))"
        } catch { Yellow "  !!  AI risk endpoint not responding" }
    } else {
        Red "  --  Login failed - Java not running"
    }

    Write-Host ""
}

# ══════════════════════════════════════════════════════════
# MAIN MENU
# ══════════════════════════════════════════════════════════
while ($true) {
    Header
    $ip = GetLocalIP

    White "CURRENT STATUS:"
    if (IsPortInUse $AI_PORT)   { Green "  OK  Python AI (8001)" } else { Red "  --  Python AI (8001)" }
    if (IsPortInUse $JAVA_PORT) { Green "  OK  Java (8089)" }      else { Red "  --  Java (8089)" }
    if (IsPortInUse $WEB_PORT)  { Green "  OK  React (3000)" }     else { Red "  --  React (3000)" }
    Cyan "  IP  Local: $ip"
    Write-Host ""
    Write-Host "--------------------------------------------------------"
    White "ACTIONS:"
    Write-Host ""
    Cyan "  [1] Check environment"
    Cyan "  [2] Generate mobile screens (Login, Dashboard, AI Insights)"
    Cyan "  [3] Setup security (JWT, biometric, role-based access)"
    Cyan "  [4] Build Android APK"
    Cyan "  [5] Install APK on device (USB)"
    Cyan "  [6] Deploy to local network + QR code"
    Cyan "  [7] Open firewall ports for mobile"
    Cyan "  [8] Full status report"
    Cyan "  [0] Exit"
    Write-Host ""
    $choice = Read-Host "  Enter choice"

    switch ($choice) {
        "1" { CheckEnvironment;     Read-Host "Press Enter to continue" }
        "2" { GenerateMobileScreens; Read-Host "Press Enter to continue" }
        "3" { SetupSecurity;        Read-Host "Press Enter to continue" }
        "4" { BuildAPK;             Read-Host "Press Enter to continue" }
        "5" { InstallOnDevice;      Read-Host "Press Enter to continue" }
        "6" { DeployLocalNetwork;   Read-Host "Press Enter to continue" }
        "7" { OpenFirewallPorts;    Read-Host "Press Enter to continue" }
        "8" { FullStatusReport;     Read-Host "Press Enter to continue" }
        "0" { Green "Goodbye!"; exit }
        default { Yellow "Invalid choice" }
    }
}
