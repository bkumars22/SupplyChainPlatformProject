# 📡 API Reference
## Supply Chain Intelligence Platform (SCIP)

**Base URL:** `http://localhost:8089/supchain`  
**Auth:** All endpoints (except `/api/auth/login`) require `Authorization: Bearer {jwt_token}`  
**Swagger UI:** `http://localhost:8089/supchain/swagger-ui/index.html`

---

## Authentication

### POST /api/auth/login
Login and receive JWT token.

**Request:**
```json
{
  "username": "kumar",
  "password": "kumar"
}
```

**Response 200:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": "kumar",
  "role": "ADMIN",
  "expiresIn": 86400
}
```

**Response 401:** Invalid credentials

---

### POST /api/auth/change-password
Change own password.

**Headers:** `X-User-Id: {userId}`

**Request:**
```json
{
  "currentPassword": "oldpass",
  "newPassword": "newpass123"
}
```

**Response 200:** `{ "message": "Password updated successfully" }`

---

## Dashboard

### GET /api/dashboard/summary
Returns KPI summary for dashboard cards.

**Response 200:**
```json
{
  "activeAlerts": 0,
  "totalBoms": 3,
  "pendingApprovals": 2,
  "atRiskSuppliers": 2
}
```

---

## Alerts

### GET /api/alerts/active
Returns all non-dismissed alerts.

**Response 200:**
```json
{
  "alerts": [
    {
      "id": 1,
      "alertType": "SUPPLIER_RISK",
      "alertLabel": "Supplier At Risk",
      "shortSummary": "Supplier OTD score below threshold",
      "state": "ACTIVE",
      "created": "2026-06-13"
    }
  ],
  "count": 1
}
```

---

### GET /api/alerts/count
Returns active alert count.

**Response 200:** `{ "count": 5 }`

---

### PUT /api/alerts/{id}/dismiss
Dismiss a single alert.

**Response 200:** `{ "message": "Alert dismissed" }`

---

## BOM Management

### GET /api/bom
Returns paginated BOM list.

**Query Params:** `page=0&size=20`

**Response 200:**
```json
{
  "content": [
    {
      "bomKey": "BOM-001",
      "bomName": "Widget Assembly",
      "bomDesc": "Main widget BOM",
      "status": "ACTIVE",
      "bomVersion": "1.0",
      "isTopLevel": true
    }
  ],
  "totalElements": 3,
  "totalPages": 1
}
```

---

### GET /api/bom/{bomKey}
Returns BOM detail with all component lines.

**Response 200:**
```json
{
  "bomKey": "BOM-001",
  "bomName": "Widget Assembly",
  "lines": [
    {
      "bomLineKey": "LINE-001",
      "itemKey": "COMP-001",
      "description": "Component A",
      "itemQty": 2.0,
      "leadTime": 5
    }
  ]
}
```

---

## Cost Records (MS3)

### GET /api/costs
Returns paginated cost record list.

**Query Params:** `page=0&size=20&status=PENDING_APPROVAL`

**Response 200:**
```json
{
  "content": [
    {
      "id": 1,
      "itemCode": "COMP-001",
      "proposedCost": 12.5000,
      "currentCost": 10.0000,
      "status": "PENDING_APPROVAL",
      "justification": "Market price increase",
      "createdBy": "kumar"
    }
  ],
  "totalElements": 2
}
```

---

### POST /api/costs
Create new DRAFT cost record.

**Request:**
```json
{
  "itemKey": "COMP-001",
  "proposedCost": 12.5000,
  "justification": "Supplier price increase Q2 2026",
  "effectiveDate": "2026-07-01",
  "createdBy": "kumar"
}
```

**Response 201:**
```json
{
  "id": 5,
  "status": "DRAFT",
  "message": "Cost record created"
}
```

---

### PUT /api/costs/{id}
Update DRAFT cost record.

**Request:**
```json
{
  "proposedCost": 13.0000,
  "justification": "Updated based on supplier confirmation"
}
```

**Response 200:** `{ "message": "Cost record updated" }`

---

### PUT /api/costs/{id}/submit
Submit DRAFT record for approval.

**Response 200:** `{ "status": "PENDING_APPROVAL" }`

---

### PUT /api/costs/{id}/approve
Approve cost record and update ITEM_MASTER.

**Headers:** `X-User-Id: {userId}`

**Response 200:**
```json
{
  "id": 5,
  "status": "APPROVED",
  "approvedBy": "kumar",
  "message": "Approved and ITEM_MASTER updated"
}
```

---

### PUT /api/costs/{id}/reject
Reject cost record with reason.

**Headers:** `X-User-Id: {userId}`

**Request:**
```json
{ "reason": "Proposed cost exceeds budget threshold by 25%" }
```

**Response 200:**
```json
{
  "id": 5,
  "status": "REJECTED",
  "message": "Cost record rejected"
}
```

---

## Suppliers (MS3)

### GET /api/suppliers
Returns all supplier profiles.

**Response 200:**
```json
[
  {
    "id": 1,
    "supplierName": "Acme Components Ltd",
    "otdScore": 92.5,
    "qualityScore": 88.0,
    "tier": "PREFERRED",
    "atRisk": false
  }
]
```

---

### GET /api/suppliers/{id}/deliveries
Returns delivery history for a supplier.

**Response 200:**
```json
{
  "deliveries": [
    {
      "id": 1,
      "deliveryDate": "2026-06-01",
      "onTime": true,
      "poNumber": "PO-1001",
      "quantityOrdered": 100,
      "quantityReceived": 100
    }
  ]
}
```

---

### POST /api/suppliers/{id}/deliveries
Add new delivery record for a supplier.

**Request:**
```json
{
  "deliveryDate": "2026-06-13",
  "onTime": true,
  "poNumber": "PO-1005",
  "quantityOrdered": 50,
  "quantityReceived": 50
}
```

**Response 200:** `{ "message": "Delivery recorded and tier recalculated" }`

---

### POST /api/suppliers/recalculate-tiers
Manually trigger supplier tier recalculation for all suppliers.

**Response 200:**
```json
{
  "message": "Tier recalculation complete",
  "suppliersUpdated": 3
}
```

---

## AI Engine

### GET /api/ai/anomalies
Returns detected anomalies from AI model.

**Response 200:**
```json
{
  "anomalies": [
    {
      "id": 1,
      "severity": "CRITICAL",
      "category": "COST",
      "itemCode": "COMP-007",
      "description": "Unit cost spike 45% above 90-day moving average",
      "confidenceScore": 92,
      "detectedAt": "2026-06-13T14:30:00",
      "affectedItems": [
        { "itemCode": "COMP-007", "itemName": "Capacitor 100uF" }
      ],
      "recommendation": "Review supplier contract and request justification"
    }
  ]
}
```

---

## User Management (Admin)

### GET /api/admin/users
Returns all system users.

**Response 200:**
```json
{
  "users": [
    {
      "userId": "kumar",
      "userName": "Kumara Swamy",
      "emailId": "kumar@company.com",
      "roleName": "ADMIN",
      "isEnabled": true,
      "hasPassword": true
    }
  ],
  "total": 3
}
```

---

### POST /api/admin/users
Create new user.

**Request:**
```json
{
  "userId": "jsmith",
  "userName": "John Smith",
  "emailId": "jsmith@company.com",
  "roleName": "BUS_ADMIN",
  "password": "welcome123"
}
```

**Response 201:** `{ "message": "User created", "userId": "jsmith" }`

---

### PUT /api/admin/users/{userId}
Update existing user.

**Request:**
```json
{
  "userName": "John R Smith",
  "roleName": "GUEST",
  "isEnabled": true
}
```

**Response 200:** `{ "message": "User updated" }`

---

### DELETE /api/admin/users/{userId}
Disable user (soft delete).

**Response 200:** `{ "message": "User disabled" }`

---

### GET /api/admin/roles
Returns available roles.

**Response 200:**
```json
{
  "roles": [
    { "roleName": "ADMIN", "roleKey": 1 },
    { "roleName": "BUS_ADMIN", "roleKey": 2 },
    { "roleName": "GUEST", "roleKey": 3 }
  ]
}
```

---

### POST /api/admin/users/{userId}/set-password
Set password for any user (admin only).

**Request:**
```json
{ "newPassword": "newpass123" }
```

**Response 200:** `{ "message": "Password set for: jsmith" }`

---

### POST /api/admin/users/{userId}/reset-password
Reset password to null (user can log in with any password).

**Response 200:** `{ "message": "Password reset - any password accepted on next login" }`

---

## Forecasting (MS4)

### GET /api/forecasts
Returns all forecasts.

**Response 200:**
```json
{
  "forecasts": [
    {
      "id": 1,
      "itemKey": "COMP-001",
      "forecastType": "DEMAND",
      "startPeriod": "2026-01",
      "endPeriod": "2026-12"
    }
  ]
}
```

---

### GET /api/forecasts/{id}
Returns forecast detail with period values and variance.

**Response 200:**
```json
{
  "id": 1,
  "itemKey": "COMP-001",
  "values": [
    {
      "period": "2026-01",
      "periodLabel": "Jan 2026",
      "forecastQty": 500,
      "actualQty": 480,
      "variance": -20,
      "variancePct": -4.0
    }
  ]
}
```

---

## HTTP Status Codes

| Code | Meaning |
|---|---|
| 200 | Success |
| 201 | Created |
| 204 | Success, no content |
| 400 | Bad Request — validation error |
| 401 | Unauthorized — missing or invalid JWT |
| 403 | Forbidden — insufficient role |
| 404 | Not Found |
| 500 | Internal Server Error |
