# 📘 Use Cases
## Supply Chain Intelligence Platform (SCIP)

**Version:** 1.0  
**Author:** Kumara Swamy  
**Date:** June 2026

---

## Use Case Index

| UC-ID | Use Case | Actor | Module | Priority |
|---|---|---|---|---|
| UC-01 | User Login | All Users | Auth | P1 |
| UC-02 | User Logout | All Users | Auth | P1 |
| UC-03 | Change Password | All Users | Auth | P1 |
| UC-04 | View Dashboard | All Users | Dashboard | P1 |
| UC-05 | View Active Alerts | All Users | Alerts | P1 |
| UC-06 | Dismiss Alert | BUS_ADMIN, ADMIN | Alerts | P1 |
| UC-07 | View BOM List | All Users | BOM | P1 |
| UC-08 | View BOM Detail | All Users | BOM | P1 |
| UC-09 | Create Cost Record | BUS_ADMIN | MS3 Cost | P1 |
| UC-10 | Edit Draft Cost Record | BUS_ADMIN | MS3 Cost | P1 |
| UC-11 | Submit Cost Record for Approval | BUS_ADMIN | MS3 Cost | P1 |
| UC-12 | Approve Cost Record | BUS_ADMIN, ADMIN | MS3 Cost | P1 |
| UC-13 | Reject Cost Record | BUS_ADMIN, ADMIN | MS3 Cost | P1 |
| UC-14 | View Supplier Scorecard | All Users | MS3 Supplier | P1 |
| UC-15 | View Supplier Delivery History | All Users | MS3 Supplier | P2 |
| UC-16 | Add Supplier Delivery Record | BUS_ADMIN | MS3 Supplier | P2 |
| UC-17 | View AI Anomalies | All Users | AI Engine | P1 |
| UC-18 | Filter Anomalies | All Users | AI Engine | P2 |
| UC-19 | Manage Users (Admin) | ADMIN | User Mgmt | P2 |
| UC-20 | Mobile Approve/Reject Cost Record | BUS_ADMIN | Mobile / MS3 | P1 |

---

## UC-01: User Login

**Actor:** All Users (ADMIN, BUS_ADMIN, GUEST)  
**Trigger:** User navigates to the platform and is not authenticated  
**Precondition:** User account exists in PCM_USER table with isEnabled = true  

**Main Flow:**
1. User navigates to `http://localhost:3000`
2. System redirects to `/login` page
3. User enters username and password
4. System sends `POST /api/auth/login` with credentials
5. Backend validates user exists and is enabled
6. Backend checks password: if no BCrypt hash stored → accepts any password; if hash stored → validates BCrypt match
7. Backend generates JWT token (24-hour expiry)
8. System stores token in `localStorage.jwt_token` and user data in `localStorage.user_data`
9. System redirects to `/dashboard`

**Alternate Flow — Invalid Credentials:**
- Step 6: If user not found or password mismatch → return HTTP 401
- System displays "Invalid username or password" message
- User remains on login page

**Alternate Flow — Disabled Account:**
- Step 5: If `isEnabled = false` → return HTTP 401
- System displays error message

**Postcondition:** User is authenticated with JWT token, redirected to dashboard  
**Default Credentials:** `kumar / kumar`

---

## UC-02: User Logout

**Actor:** All Users  
**Trigger:** User clicks "Logout" button in sidebar  
**Precondition:** User is authenticated  

**Main Flow:**
1. User clicks Logout in sidebar
2. System removes `jwt_token` and `user_data` from localStorage
3. System redirects to `/login`
4. All subsequent API calls without token return 401

**Postcondition:** Session cleared, user on login page

---

## UC-03: Change Password

**Actor:** All Users (own password); ADMIN (any user's password)  
**Trigger:** User navigates to password change settings  
**Precondition:** User is authenticated  

**Main Flow (own password):**
1. User enters current password and new password (min 6 chars)
2. System sends `POST /api/auth/change-password` with X-User-Id header
3. Backend validates current password against stored BCrypt hash
4. Backend hashes new password with BCrypt
5. Backend updates PCM_USER.password
6. System confirms success

**Main Flow (admin sets password for any user):**
1. Admin navigates to User Management → selects user → Set Password
2. Admin enters new password
3. System sends `POST /api/admin/users/{userId}/set-password`
4. Backend hashes and stores new BCrypt password
5. System confirms success

**Admin Reset (any password on next login):**
1. Admin clicks Reset Password for a user
2. System sends `POST /api/admin/users/{userId}/reset-password`
3. Backend sets password field to null
4. User can now log in with any password

**Postcondition:** Password updated in PCM_USER table

---

## UC-04: View Dashboard

**Actor:** All Users  
**Trigger:** User logs in or navigates to `/dashboard`  
**Precondition:** User is authenticated; backend is running  

**Main Flow:**
1. System sends `GET /api/dashboard/summary` with JWT token
2. Backend queries: active alert count, BOM count, pending cost approval count, at-risk supplier count
3. System displays 4 KPI stat cards
4. System loads recent alerts list
5. System displays quick action links

**KPI Cards:**
- **Active Alerts** — count of ALERT_DETAIL records where state ≠ DISMISSED
- **Total BOMs** — count of BOM_HEADER records
- **Pending Approvals** — count of MS3_COST_RECORD where status = PENDING_APPROVAL
- **At-Risk Suppliers** — count of SUPPLIER_PROFILE where atRisk = true

**Postcondition:** Dashboard displays live data from backend

---

## UC-05: View Active Alerts

**Actor:** All Users  
**Trigger:** User navigates to `/alerts`  
**Precondition:** User is authenticated  

**Main Flow:**
1. System sends `GET /api/alerts/active`
2. Backend returns all ALERT_DETAIL records where state ≠ DISMISSED, ordered by created date desc
3. System displays alert list with type, label, short summary, and creation date
4. User can scroll through all alerts

**Postcondition:** User sees all non-dismissed alerts

---

## UC-06: Dismiss Alert

**Actor:** BUS_ADMIN, ADMIN  
**Trigger:** User clicks Dismiss button on an alert  
**Precondition:** User is authenticated; alert exists and is not already dismissed  

**Main Flow:**
1. User clicks Dismiss on an alert row
2. System sends `PUT /api/alerts/{id}/dismiss`
3. Backend updates ALERT_DETAIL.state = DISMISSED
4. System removes alert from the active list
5. Alert count on dashboard decrements

**Postcondition:** Alert no longer appears in active list; dashboard count updated

---

## UC-07: View BOM List

**Actor:** All Users  
**Trigger:** User navigates to `/bom`  
**Precondition:** User is authenticated  

**Main Flow:**
1. System sends `GET /api/bom?page=0&size=20`
2. Backend returns paginated list of BOM_HEADER records
3. System displays BOM list with key, name, status, and item
4. User can search by BOM key or name (client-side filter)
5. User can scroll through pages

**Postcondition:** User sees list of all BOMs

---

## UC-08: View BOM Detail

**Actor:** All Users  
**Trigger:** User clicks on a BOM row in the list  
**Precondition:** User is authenticated; BOM exists  

**Main Flow:**
1. User clicks BOM row
2. System navigates to `/bom/{bomKey}`
3. System sends `GET /api/bom/{bomKey}`
4. Backend returns BOM header + all BOM_LINE_ITEM records
5. System displays BOM header: key, name, description, version, status
6. System displays component lines table: item code, description, quantity, lead time

**Postcondition:** User sees full BOM with all component lines

---

## UC-09: Create Cost Record

**Actor:** BUS_ADMIN  
**Trigger:** User navigates to Cost Records and clicks "New Cost Record"  
**Precondition:** User is authenticated with BUS_ADMIN role; item exists in ITEM_MASTER  

**Main Flow:**
1. User opens new cost record form
2. User selects item from ITEM_MASTER lookup
3. System displays current unit cost for selected item
4. User enters proposed cost (decimal, 4 decimal places)
5. User enters justification (min 10 chars)
6. User optionally enters effective date
7. User clicks Save Draft
8. System sends `POST /api/costs` with status = DRAFT
9. Backend creates MS3_COST_RECORD with status = DRAFT
10. System confirms creation and returns to cost record list

**Validation:**
- Proposed cost must be positive number
- Justification minimum 10 characters
- Item must exist in ITEM_MASTER

**Postcondition:** New cost record in DRAFT status in MS3_COST_RECORD table

---

## UC-10: Edit Draft Cost Record

**Actor:** BUS_ADMIN  
**Trigger:** User clicks Edit on a DRAFT cost record  
**Precondition:** Cost record exists with status = DRAFT  

**Main Flow:**
1. User clicks Edit on a DRAFT record
2. System opens CostRecordEditModal pre-populated with current values
3. User modifies proposed cost and/or justification
4. System displays % change vs current ITEM_MASTER unit cost
5. User clicks Save Draft
6. System sends `PUT /api/costs/{id}` with updated values
7. Record remains in DRAFT status

**Business Rule:** Records in PENDING_APPROVAL, APPROVED, or REJECTED cannot be edited

**Postcondition:** Cost record updated, still in DRAFT status

---

## UC-11: Submit Cost Record for Approval

**Actor:** BUS_ADMIN  
**Trigger:** User clicks "Submit for Approval" on a DRAFT cost record  
**Precondition:** Cost record exists with status = DRAFT; proposed cost and justification are valid  

**Main Flow:**
1. User clicks Submit for Approval (from list view or edit modal)
2. System sends `PUT /api/costs/{id}/submit`
3. Backend updates status: DRAFT → PENDING_APPROVAL
4. System sends email notification to approvers (if email configured)
5. Dashboard "Pending Approvals" count increments

**Postcondition:** Cost record in PENDING_APPROVAL status; appears in approver's queue

---

## UC-12: Approve Cost Record

**Actor:** BUS_ADMIN, ADMIN  
**Trigger:** Approver clicks Approve on a PENDING_APPROVAL cost record  
**Precondition:** Cost record status = PENDING_APPROVAL; approver has BUS_ADMIN or ADMIN role  

**Main Flow:**
1. Approver views cost record in pending state
2. Approver reviews proposed cost, justification, and % change
3. Approver clicks Approve
4. System sends `PUT /api/costs/{id}/approve` with X-User-Id header
5. Backend validates status = PENDING_APPROVAL
6. Backend updates cost record: status = APPROVED, approvedBy = userId, approvedAt = now()
7. **Backend automatically updates ITEM_MASTER.unitCost = proposedCost**
8. System sends approval email to record creator (if email configured)
9. Dashboard "Pending Approvals" count decrements

**Critical Business Rule:** Step 7 — ITEM_MASTER unit cost update is automatic and immediate on approval

**Postcondition:** Cost record APPROVED; ITEM_MASTER unit cost reflects new approved value

---

## UC-13: Reject Cost Record

**Actor:** BUS_ADMIN, ADMIN  
**Trigger:** Approver clicks Reject on a PENDING_APPROVAL cost record  
**Precondition:** Cost record status = PENDING_APPROVAL  

**Main Flow:**
1. Approver clicks Reject
2. System opens rejection reason modal
3. Approver enters rejection reason (required)
4. System sends `PUT /api/costs/{id}/reject` with reason in body
5. Backend updates: status = REJECTED, rejectedBy = userId, rejectionReason = reason
6. System sends rejection email to record creator (if email configured)
7. ITEM_MASTER is NOT updated

**Postcondition:** Cost record REJECTED with reason stored; ITEM_MASTER unchanged

---

## UC-14: View Supplier Scorecard

**Actor:** All Users  
**Trigger:** User navigates to `/suppliers`  
**Precondition:** User is authenticated  

**Main Flow:**
1. System sends `GET /api/suppliers`
2. Backend returns all SUPPLIER_PROFILE records with OTD score, quality score, tier, and at-risk flag
3. System displays supplier cards with:
   - Supplier name
   - OTD score percentage
   - Quality score percentage
   - Tier badge (PREFERRED = green / CONDITIONAL = amber / PROBATION = red)
   - At-risk warning badge if applicable
4. User can search suppliers by name

**Tier Classification (auto-calculated):**
- OTD ≥ 90% → **PREFERRED** (green)
- OTD 70–89% → **CONDITIONAL** (amber)
- OTD < 70% → **PROBATION** (red) + at-risk flag

**Postcondition:** User sees all supplier performance data

---

## UC-15: View Supplier Delivery History

**Actor:** All Users  
**Trigger:** User clicks on a supplier card  
**Precondition:** User is authenticated; supplier exists  

**Main Flow:**
1. User clicks supplier card
2. System navigates to supplier detail view
3. System sends `GET /api/suppliers/{id}/deliveries`
4. System displays two tabs:
   - **Scorecard tab**: OTD %, quality score, total deliveries, on-time count, late count
   - **Delivery History tab**: List of all delivery records with date, on-time status, PO number, quantities

**Postcondition:** User sees full delivery history for selected supplier

---

## UC-16: Add Supplier Delivery Record

**Actor:** BUS_ADMIN  
**Trigger:** User clicks "Add Delivery" on supplier detail screen  
**Precondition:** User has BUS_ADMIN role; supplier exists  

**Main Flow:**
1. User clicks Add Delivery
2. User enters: delivery date, PO number, ordered quantity, received quantity, on-time (yes/no)
3. System sends `POST /api/suppliers/{id}/deliveries`
4. Backend creates SUPPLIER_DELIVERY record
5. Backend recalculates OTD score from all deliveries
6. Backend recalculates supplier tier based on new OTD score
7. System refreshes delivery history and scorecard

**OTD Score Formula:** (On-Time Deliveries / Total Deliveries) × 100

**Postcondition:** New delivery recorded; OTD score and tier recalculated

---

## UC-17: View AI Anomalies

**Actor:** All Users  
**Trigger:** User navigates to `/ai` or `/ai-engines`  
**Precondition:** User is authenticated; AI service running  

**Main Flow:**
1. System sends `GET /api/ai/anomalies`
2. Backend calls Python AI service, returns anomaly list
3. System displays 4 summary cards: Total, Critical, Warnings, Categories
4. System displays anomaly grid cards, each showing:
   - Severity badge (CRITICAL / WARNING / INFO)
   - Item code
   - Description
   - Confidence score bar
   - Affected items count
5. Page auto-refreshes every 60 seconds

**Postcondition:** User sees all detected anomalies with severity and confidence

---

## UC-18: Filter Anomalies

**Actor:** All Users  
**Trigger:** User interacts with filter controls on AI Engine page  
**Precondition:** Anomaly list is loaded  

**Main Flow:**
1. User types in search box → filters by item code or description
2. User selects severity from dropdown → shows only matching severity
3. User selects category from dropdown → shows only matching category
4. Result count label updates in real time
5. User clicks on anomaly card → detail modal opens with:
   - Full description
   - Confidence score bar with colour (red = high confidence)
   - Affected items list
   - Recommendation text

**Postcondition:** User sees filtered anomaly list; can view full detail in modal

---

## UC-19: Manage Users (Admin)

**Actor:** ADMIN  
**Trigger:** Admin navigates to `/admin/users`  
**Precondition:** User is authenticated with ADMIN role  

**Main Flow — View Users:**
1. System sends `GET /api/admin/users`
2. System displays user table: userId, name, email, role, password status, account status
3. Admin can search/filter by name or userId

**Main Flow — Create User:**
1. Admin clicks "Create User"
2. Admin enters: userId, name, email (optional), role, initial password (optional)
3. System sends `POST /api/admin/users`
4. Backend creates PCM_USER record with isEnabled = true

**Main Flow — Edit User:**
1. Admin clicks Edit on a user row
2. Admin modifies name, email, role, or enabled status
3. System sends `PUT /api/admin/users/{userId}`
4. Backend updates user record

**Main Flow — Disable User:**
1. Admin clicks Disable on a user row
2. System sends `DELETE /api/admin/users/{userId}`
3. Backend sets isEnabled = false (soft delete — record preserved)
4. User can no longer log in

**Business Rule:** Users are never hard-deleted. Disable is the only removal action.

**Postcondition:** User account created, updated, or disabled in PCM_USER table

---

## UC-20: Mobile Approve/Reject Cost Record

**Actor:** BUS_ADMIN  
**Trigger:** User opens Cost Records tab in mobile app and sees PENDING_APPROVAL records  
**Precondition:** User authenticated in mobile app; cost records exist in PENDING_APPROVAL  

**Main Flow — Approve:**
1. User opens Cost Records screen on mobile
2. PENDING_APPROVAL records show Approve and Reject action buttons
3. User taps Approve
4. System shows confirmation Alert with proposed cost details
5. User confirms
6. System sends `PUT /api/costs/{id}/approve`
7. Backend approves record and updates ITEM_MASTER
8. List refreshes — record disappears from pending queue

**Main Flow — Reject:**
1. User taps Reject on a pending record
2. System opens bottom sheet modal
3. User enters rejection reason
4. User taps Confirm Reject
5. System sends `PUT /api/costs/{id}/reject` with reason
6. List refreshes

**Postcondition:** Cost record approved or rejected from mobile; ITEM_MASTER updated if approved

---

## Appendix: Actor Permissions Matrix

| Feature | GUEST | BUS_ADMIN | ADMIN |
|---|---|---|---|
| View Dashboard | ✅ | ✅ | ✅ |
| View Alerts | ✅ | ✅ | ✅ |
| Dismiss Alert | ❌ | ✅ | ✅ |
| View BOM List/Detail | ✅ | ✅ | ✅ |
| Create Cost Record | ❌ | ✅ | ✅ |
| Edit Draft Cost Record | ❌ | ✅ | ✅ |
| Submit Cost Record | ❌ | ✅ | ✅ |
| Approve/Reject Cost Record | ❌ | ✅ | ✅ |
| View Supplier Scorecard | ✅ | ✅ | ✅ |
| Add Delivery Record | ❌ | ✅ | ✅ |
| View AI Anomalies | ✅ | ✅ | ✅ |
| Create/Edit Users | ❌ | ❌ | ✅ |
| Disable Users | ❌ | ❌ | ✅ |
| Set/Reset Passwords | ❌ | ❌ | ✅ |
| Change Own Password | ✅ | ✅ | ✅ |
