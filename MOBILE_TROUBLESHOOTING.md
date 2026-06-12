# 🔧 Mobile Access Troubleshooting Guide

## Error: "Site cannot be reached"

This means the React dashboard on port 3000 is not accessible from your mobile device. Follow these steps:

---

## **Step 1: Verify Your PC Network IP**

**On your PC (PowerShell):**
```powershell
# Get your IPv4 address
ipconfig

# Look for output like:
# Wireless LAN adapter WiFi:
#    IPv4 Address  . . . . . . . . . . . . : 192.168.1.100

# Copy this IP address (e.g., 192.168.1.100)
```

**Save this IP for the next steps!**

---

## **Step 2: Verify WiFi Connection**

- ✅ Is your mobile device **on the same WiFi network** as your PC?
- ✅ Can your PC ping your mobile device?

**Try from PC PowerShell:**
```powershell
# Get mobile device's IP from WiFi router/settings
ping 192.168.X.XXX  # (your mobile device IP)
```

---

## **Step 3: Ensure Backend (Port 8089) is Running**

**Terminal 1 - Start Backend:**
```bash
cd .

# Start the Spring Boot server
.\mvnw.cmd spring-boot:run

# Wait for output like:
# ============================================
# Started PcmApplication in X seconds
# Tomcat started on port 8089 with context path '/supchain'
# ============================================
```

**Verify it works locally:**
```bash
# From another PowerShell on same PC, test:
curl http://localhost:8089/supchain/api/items
# Should NOT show connection error
```

---

## **Step 4: Ensure Dashboard (Port 3000) is Running**

**Terminal 2 - Start React Dashboard:**
```bash
# Navigate to your React dashboard folder
cd C:\path\to\your\react-dashboard
# (or wherever your package.json is)

# Install dependencies (first time only)
npm install

# Start development server
npm start

# You should see:
# ✓ Compiled successfully
# Local:       http://localhost:3000
# On your network: http://192.168.X.XXX:3000
```

**Verify it works locally:**
```bash
# From another PowerShell on same PC:
curl http://localhost:3000
# Should return HTML (not connection error)
```

---

## **Step 5: Test from Mobile Device**

**On your mobile phone (same WiFi):**

1. Open Chrome or Safari
2. Go to: `http://192.168.1.100:3000`  (use YOUR PC's IP)
3. Wait 10-15 seconds for page to load

**If still "Cannot reach":**
- Go to next section

---

## **Common Firewall/Network Issues**

### **Issue: "Connection refused" or "Connection timeout"**

**Windows Firewall might be blocking port 3000 & 8089**

**Allow through Windows Firewall:**

1. Open **Windows Defender Firewall**
2. Go to **"Allow an app through firewall"**
3. Click **"Change settings"**
4. Click **"Allow another app"**
5. Select: `java.exe` (or `javaw.exe`)
6. Click **"Add"**
7. Restart Node.js server (Terminal 2)

**Alternative: Disable Firewall temporarily (testing only):**
```powershell
# Temporarily disable Windows Firewall (NOT recommended for production)
netsh advfirewall set allprofiles state off

# Re-enable later:
netsh advfirewall set allprofiles state on
```

---

### **Issue: Mobile on different WiFi network**

- **Solution:** Both PC and mobile MUST be on same WiFi network
- Check your WiFi router settings
- Ensure mobile is not on mobile data (4G/5G)

---

### **Issue: Antivirus blocking connections**

- Check antivirus settings
- Temporarily disable antivirus (for testing)
- Add Java/Node to antivirus whitelist

---

## **Step 6: Verify Both Servers from Mobile**

Once you can access port 3000, test both servers:

### **Test Backend API:**
```
http://192.168.1.100:8089/supchain/api/items
```
Should return JSON array: `[]` (empty initially) or list of items

### **Test Dashboard:**
```
http://192.168.1.100:3000
```
Should show login page

---

## **Complete Verification Checklist**

Use this to verify everything works:

```powershell
# On PC PowerShell - Run these tests:

# 1. Backend API
curl http://localhost:8089/supchain/api/items -ErrorAction Stop

# 2. Swagger API Docs
curl http://localhost:8089/supchain/swagger-ui.html -ErrorAction Stop

# 3. Dashboard
curl http://localhost:3000 -ErrorAction Stop

# 4. Check listening ports
netstat -ano | findstr "3000\|8089"
# Should show both ports listening

# 5. Check firewall status
Get-NetFirewallProfile -Profile Domain,Public,Private | Select Name, Enabled
```

---

## **Exact Steps to Success (Copy & Paste)**

### **Terminal 1 (Backend):**
```bash
cd .
.\mvnw.cmd spring-boot:run
# Wait for: "Tomcat started on port 8089"
```

### **Terminal 2 (Dashboard):**
```bash
# Replace with your actual React dashboard path
cd "C:\Users\<YourUsername>\Documents\react-dashboard"  
npm start
# Wait for: "Compiled successfully"
```

### **Terminal 3 (Verify):**
```bash
# Check both services are running
curl http://localhost:8089/supchain/api/items
curl http://localhost:3000

# Check firewall
Get-NetFirewallProfile | Select Name, Enabled
```

### **On Mobile:**
```
Open: http://192.168.1.100:3000
(Replace 192.168.1.100 with YOUR PC's IPv4 address from Step 1)
```

---

## **If Still Not Working**

Run this diagnostic script to identify the issue:

```powershell
# Get your PC's IP
$pcIP = (Get-NetIPAddress -AddressFamily IPv4 | Where-Object {$_.InterfaceAlias -like "*WiFi*" -or $_.InterfaceAlias -like "*Ethernet*"}).IPAddress | Select-Object -First 1
Write-Host "Your PC IP: $pcIP"

# Check if ports are listening
$listening = Get-NetTCPConnection -State Listen | Where-Object {$_.LocalPort -eq 3000 -or $_.LocalPort -eq 8089}
if ($listening) {
    Write-Host "✅ Ports listening:"
    $listening | Select-Object LocalPort, State
} else {
    Write-Host "❌ ERROR: Ports NOT listening. Make sure both services are running!"
}

# Check firewall
Write-Host "`nFirewall Status:"
Get-NetFirewallProfile | Select-Object Name, Enabled
```

---

## **Quick Checklist**

- [ ] PC and mobile on **same WiFi network**
- [ ] Backend running on port 8089 (see "Tomcat started on port 8089")
- [ ] Dashboard running on port 3000 (see "Compiled successfully")
- [ ] Firewall allows ports 3000 & 8089
- [ ] PC IPv4 address noted (from ipconfig)
- [ ] Mobile accessing: `http://<YOUR_PC_IP>:3000`

**Once working:**
- [ ] Dashboard loads (login page appears)
- [ ] Login with credentials
- [ ] See items list
- [ ] Create item from mobile
- [ ] Item appears in dashboard

