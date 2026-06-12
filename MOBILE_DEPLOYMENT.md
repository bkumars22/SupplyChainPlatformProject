# Mobile App Deployment Guide

## Complete Workflow: Item Entry → Dashboard → Mobile Access

### **Architecture Overview**

```
┌─────────────────────┐
│   Mobile Device     │  (Your phone)
│  Browser/App        │  Access dashboard & API
└──────────┬──────────┘
           │
           │ HTTP Requests
           │ (JSON API)
           ▼
┌──────────────────────────────────────────┐
│   Spring Boot Backend                    │
│   Port: 8089                             │
│   Context: /supchain                     │
│   URL: http://<YOUR_PC_IP>:8089/supchain│
├──────────────────────────────────────────┤
│  REST API Endpoints:                     │
│  - GET  /api/items                       │
│  - POST /api/items (create item)         │
│  - POST /api/items/avls (create AVL)     │
│  - GET  /api/items/{id}/avls             │
└──────────┬───────────────────────────────┘
           │
           │ Queries/Updates
           ▼
┌──────────────────────────┐
│   H2 In-Memory Database  │
│   (Local development)    │
│   Items & AVLs stored    │
└──────────────────────────┘

┌──────────────────────────────────────────┐
│   React Dashboard (Port 3000)            │
│   http://<YOUR_PC_IP>:3000/              │
│  - View items list                       │
│  - Create items (calls REST API)         │
│  - View AVLs                             │
└──────────────────────────────────────────┘
```

---

## **Deployment Steps**

### **1. Get Your Computer's Network IP Address**

**Windows CMD:**
```powershell
# In PowerShell, run:
ipconfig

# Look for IPv4 Address under your WiFi adapter
# Example: 192.168.1.100
```

**Example Output:**
```
Wireless LAN adapter WiFi:
   IPv4 Address  . . . . . . . . . . . . : 192.168.1.100
```

---

### **2. Start Backend (Spring Boot)**

```bash
cd .

# Run on port 8089 (accessible from any IP)
.\mvnw.cmd spring-boot:run

# You should see:
# ============================================
# Started PcmApplication in X seconds
# Tomcat started on port 8089 with context path '/supchain'
```

**Backend is now accessible at:**
```
http://192.168.1.100:8089/supchain
```

---

### **3. Start React Dashboard (Port 3000)**

**Open another terminal:**
```bash
# Navigate to your React frontend folder
cd <your-react-dashboard-path>

# Install dependencies (if not already done)
npm install

# Start development server on port 3000
npm start

# Browser automatically opens at: http://localhost:3000
```

**Dashboard is now accessible at:**
```
http://192.168.1.100:3000 (from mobile device)
```

---

### **4. Access from Mobile Device**

**On your mobile phone (same WiFi network):**

1. **Open browser** (Chrome/Safari)
2. **Navigate to:** `http://192.168.1.100:3000`
3. **Login** with your credentials (same as desktop)
4. **You see:**
   - Dashboard with existing items
   - "Create Item" button
   - Item list with search/filter

---

## **Usage Flow for Mobile**

### **Scenario 1: Create Item via Mobile Dashboard**

```
1. Mobile opens: http://192.168.1.100:3000
2. Navigate to "Create Item"
3. Fill form:
   - Item Number: "PART-001"
   - Item Type: "Standard"
   - Business Entity: "Supplier ABC"
   - Description: "Electronic Component"
4. Click "Save"
   ↓
5. Backend receives: POST /supchain/api/items
   (Stores in H2 database)
   ↓
6. Response: ✓ Item created successfully
7. Dashboard refreshes → Item appears in list
```

### **Scenario 2: Create AVL (Approved Vendor List)**

```
1. Mobile opens item detail
2. Navigate to "Add AVL" 
3. Fill form:
   - Select Supplier
   - Enter quantity, dates
4. Click "Save"
   ↓
5. Backend: POST /supchain/api/items/avls
   (Links supplier to item)
   ↓
6. AVL appears in item's AVL list
```

### **Scenario 3: View Items on Mobile**

```
1. Mobile: GET /supchain/api/items
   (Dashboard calls this)
   ↓
2. Backend returns:
   [
     {
       "itemKey": "ITEM-001",
       "itemNumber": "PART-001",
       "itemType": "Standard",
       "businessEntityName": "Supplier ABC"
     },
     ...
   ]
   ↓
3. Mobile displays paginated list with search
```

---

## **Integration with Other Modules**

### **Data Flow**

```
Items stored in H2 Database
    ↓
    └─→ Available for other modules:
        - Manufacturing module (Production orders)
        - Inventory module (Stock tracking)
        - Procurement module (Purchase orders)
        - Logistics module (Shipping)
```

### **Configuration**

All items/AVLs are automatically available to any Spring Bean that injects:
```java
@Autowired
private ItemRepository itemRepository;

@Autowired
private AvlRepository avlRepository;
```

---

## **Architecture Details**

### **REST API Endpoints**

| Method | Endpoint | Purpose |
|--------|----------|---------|
| `GET` | `/api/items?page=0&size=20&itemNumber=PART` | List items (searchable) |
| `POST` | `/api/items` | Create single item |
| `GET` | `/api/items/{itemKey}` | Get item details |
| `POST` | `/api/items/avls` | Create AVL entry |
| `GET` | `/api/items/{itemKey}/avls` | List item's AVLs |
| `POST` | `/api/items/upload` | Bulk upload XML file |

### **Request/Response Format**

**Create Item (POST /api/items):**
```json
Request Body:
{
  "itemNumber": "PART-001",
  "itemType": "Standard",
  "description": "Electronic Component",
  "businessEntityIdentifier": "SUPPLIER-001",
  "externalRefId": "EXT-REF-123",
  "quantity": 100,
  "unitOfMeasure": "piece",
  "revision": "1",
  "version": "1"
}

Response:
{
  "itemKey": "ITEM-001",
  "itemNumber": "PART-001",
  "itemType": "Standard",
  "businessEntityName": "Supplier ABC",
  "description": "Electronic Component",
  "revision": "1",
  "version": "1"
}
```

**Create AVL (POST /api/items/avls):**
```json
Request Body:
{
  "itemNumber": "PART-001",
  "supplierId": "SUP-001",
  "supplierName": "Supplier ABC",
  "supplierPartNumber": "ABC-123-XYZ",
  "leadTime": 30,
  "minimumOrderQuantity": 100,
  "effectiveStartDate": "2026-01-01",
  "effectiveEndDate": "2027-12-31"
}

Response:
{
  "avlKey": "AVL-001",
  "itemNumber": "PART-001",
  "supplierName": "Supplier ABC",
  "leadTime": 30,
  "minimumOrderQuantity": 100
}
```

---

## **Testing Checklist**

- [ ] Backend running on port 8089
- [ ] React dashboard running on port 3000
- [ ] Mobile device on same WiFi network
- [ ] Can access dashboard: `http://<YOUR_IP>:3000`
- [ ] Can login with credentials
- [ ] Can create item from mobile
- [ ] Item appears in dashboard list
- [ ] Can create AVL for item
- [ ] Data persists after refresh

---

## **Troubleshooting**

| Issue | Solution |
|-------|----------|
| "Cannot reach 192.168.1.100:8089" | Ensure backend is running and you're on same WiFi |
| "Connection refused" | Check Windows Firewall allows Java/port 8089 |
| "CORS error in dashboard" | Backend CORS config is already set, restart backend |
| "Items not appearing" | Refresh browser, check database connection |
| "Login fails" | Verify credentials in your auth system |

---

## **Summary**

✅ **Mobile-friendly deployment ready**
- Backend accessible from any network IP
- Dashboard responsive and accessible from mobile
- REST API for future native mobile apps
- Data stored and available for other modules
- CORS enabled for cross-origin requests

**Your mobile workflow:**
1. Open dashboard on phone
2. Create items/AVLs
3. Other modules access same database
4. Complete supply chain visibility on mobile

