/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
import { test, expect } from "@playwright/test";
import { API_URL } from "./helpers/auth";

const ADMIN_USER = "admin";
const ADMIN_PASS = "Admin@2026";

async function getAdminToken(): Promise<string> {
  const res = await fetch(API_URL + "/api/auth/login", {
    method:  "POST",
    headers: { "Content-Type": "application/json" },
    body:    JSON.stringify({ username: ADMIN_USER, password: ADMIN_PASS })
  });
  const data = await res.json();
  return data.token || "";
}

test.describe("Demo Reset Endpoint", () => {

  test("POST /api/admin/reset-demo returns 200 for admin user", async () => {
    const token = await getAdminToken();
    expect(token).not.toBe("");

    const res = await fetch(API_URL + "/api/admin/reset-demo", {
      method:  "POST",
      headers: {
        "Content-Type":  "application/json",
        "Authorization": `Bearer ${token}`
      }
    });

    expect(res.status).toBe(200);

    const body = await res.json();
    expect(body.message).toBe("Database reset to demo state");
    expect(body.timestamp).toBeTruthy();
    expect(body.records_seeded).toBeDefined();
    expect(body.records_seeded.suppliers).toBe(5);
    expect(body.records_seeded.alerts).toBe(3);
  });

  test("Suppliers count is 5 after reset", async () => {
    const token = await getAdminToken();
    expect(token).not.toBe("");

    // Trigger reset
    await fetch(API_URL + "/api/admin/reset-demo", {
      method:  "POST",
      headers: {
        "Content-Type":  "application/json",
        "Authorization": `Bearer ${token}`
      }
    });

    // Verify supplier count
    const supplierRes = await fetch(API_URL + "/api/suppliers", {
      headers: { "Authorization": `Bearer ${token}` }
    });
    expect(supplierRes.status).toBe(200);

    const suppliers = await supplierRes.json();
    const list = Array.isArray(suppliers) ? suppliers : (suppliers.suppliers || suppliers.content || []);
    expect(list.length).toBe(5);
  });

  test("POST /api/admin/reset-demo returns 403 for non-admin user", async () => {
    // Login as kumar (MANAGER role, not ADMIN)
    const loginRes = await fetch(API_URL + "/api/auth/login", {
      method:  "POST",
      headers: { "Content-Type": "application/json" },
      body:    JSON.stringify({ username: "kumar", password: "Kumar@2026" })
    });
    const loginData = await loginRes.json();
    const token = loginData.token || "";

    const res = await fetch(API_URL + "/api/admin/reset-demo", {
      method:  "POST",
      headers: {
        "Content-Type":  "application/json",
        "Authorization": `Bearer ${token}`
      }
    });

    // Should be blocked — either by DemoGuardFilter (403) or role check (403)
    expect([403]).toContain(res.status);
  });

});
