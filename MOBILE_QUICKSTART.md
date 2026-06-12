# 📱 Quick Start: Mobile Access

## Get Your PC IP Address
```powershell
ipconfig
# Look for: IPv4 Address = 192.168.X.X
```

## Start Everything (3 Commands)

### Terminal 1: Backend
```bash
cd .
.\mvnw.cmd spring-boot:run
```
✅ Backend runs on: `http://192.168.1.100:8089/supchain`

### Terminal 2: Dashboard  
```bash
cd <react-dashboard-folder>
npm start
```
✅ Dashboard runs on: `http://192.168.1.100:3000`

### Terminal 3: (Optional) Swagger API Docs
```
http://192.168.1.100:8089/supchain/swagger-ui.html
```

---

## On Mobile Phone (Same WiFi)

1. Open browser
2. Go to: `http://192.168.1.100:3000`
3. Login with your credentials
4. Create items/AVLs from dashboard
5. Data syncs with backend automatically

---

## REST API Endpoints (for Native Apps)

```
BASE_URL = http://192.168.1.100:8089/supchain

GET    /api/items                    → List all items (searchable)
POST   /api/items                    → Create new item
GET    /api/items/{itemKey}          → Get item details
POST   /api/items/avls               → Create AVL
GET    /api/items/{itemKey}/avls     → List item's suppliers
POST   /api/items/upload             → Bulk upload XML
```

---

## Example Mobile API Call (Swift / Kotlin / React Native)

```javascript
// JavaScript/React Native Example
const createItem = async () => {
  const response = await fetch(
    'http://192.168.1.100:8089/supchain/api/items',
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer YOUR_JWT_TOKEN'
      },
      body: JSON.stringify({
        itemNumber: 'PART-001',
        itemType: 'Standard',
        description: 'Electronic Component',
        businessEntityIdentifier: 'SUPPLIER-001'
      })
    }
  );
  
  const item = await response.json();
  console.log('Item created:', item);
};
```

---

## Integration Confirmed ✅

- Items stored in database
- AVLs linked to suppliers
- Data available to all modules
- Mobile-friendly API ready
- Dashboard responsive

**Ready to deploy!**

