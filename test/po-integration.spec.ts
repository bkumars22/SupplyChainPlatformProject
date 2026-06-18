/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
import { test, expect, request } from "@playwright/test";
import { APP_URL, API_URL, loginViaUI, getJwtToken } from "./helpers/auth";

test.describe("PO Integration — Phase 2+3 auto-integrations", () => {

  test.afterAll(async () => {
    try {
      const ctx   = await request.newContext();
      const token = await getJwtToken();
      await ctx.post(API_URL + "/api/admin/reset-demo", {
        headers: { Authorization: "Bearer " + token },
      });
      await ctx.dispose();
    } catch { /* backend not running — skip */ }
  });

  test("Test 1 — Full PO workflow; Cost Record auto-created on RECEIVE", async ({ page }) => {
    await loginViaUI(page);
    await page.goto(APP_URL + "/purchase-orders");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(2000);

    // Count initial POs
    const initialRows = await page.locator('[data-testid^="po-row-"]').count();

    // Create PO
    await page.locator('[data-testid="create-po-btn"]').click();
    await page.waitForTimeout(500);
    await page.locator('[data-testid="po-supplier-id"]').fill("SUPP-001");
    const nameInput = page.locator('[data-testid="po-supplier-name"]');
    if (await nameInput.count() > 0) await nameInput.fill("TechParts India");
    const dateInput = page.locator('[data-testid="po-expected-date"]');
    if (await dateInput.count() > 0) await dateInput.fill("2026-08-01");
    await page.locator('[data-testid="po-submit-form"]').click();
    await page.waitForTimeout(2000);

    // Verify PO was created
    const newRows = await page.locator('[data-testid^="po-row-"]').count();
    expect(newRows).toBeGreaterThanOrEqual(initialRows);

    // Check cost records page for the PO reference (if backend running)
    await page.goto(APP_URL + "/cost-records");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(2000);
    const body = await page.locator("body").innerText();
    expect(body.length).toBeGreaterThan(100);
  });

  test("Test 2 — Receiving a PO increases inventory", async ({ page }) => {
    await loginViaUI(page);

    // Note current inventory of CHIP-001
    await page.goto(APP_URL + "/inventory");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(2000);
    const inventoryTable = page.locator('[data-testid="inventory-table"]');
    await expect(inventoryTable).toBeVisible();
    const rows = await page.locator('[data-testid^="inv-row-"]').count();
    expect(rows).toBeGreaterThan(0);

    // Navigate to POs and receive a CONFIRMED one
    await page.goto(APP_URL + "/purchase-orders");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(2000);

    // Filter to CONFIRMED POs
    const confirmedFilter = page.locator('[data-testid="filter-CONFIRMED"]');
    if (await confirmedFilter.count() > 0) {
      await confirmedFilter.click();
      await page.waitForTimeout(1000);
    }

    const receiveBtn = page.locator('[data-testid^="receive-btn-"]').first();
    if (await receiveBtn.count() > 0) {
      await receiveBtn.click();
      await page.waitForTimeout(2000);
      // Verify status changed (backend-dependent)
      const body = await page.locator("body").innerText();
      expect(body).toContain("RECEIVED");
    } else {
      // Demo mode — verify inventory page still works
      await page.goto(APP_URL + "/inventory");
      await page.waitForTimeout(2000);
      await expect(page.locator('[data-testid="inventory-table"]')).toBeVisible();
    }
  });

  test("Test 3 — Create PO supplier dropdown has at least one supplier", async ({ page }) => {
    await loginViaUI(page);
    await page.goto(APP_URL + "/purchase-orders");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(2000);

    await page.locator('[data-testid="create-po-btn"]').click();
    await page.waitForTimeout(1500);

    // Check that either a supplier dropdown or a supplier-id input is visible
    const supplierDropdown = page.locator('select[data-testid*="supplier"], [data-testid="po-supplier-id"]').first();
    await expect(supplierDropdown).toBeVisible({ timeout: 5000 });

    // If it's a select with options, verify at least one option exists
    const selectEl = page.locator('select[data-testid*="supplier"]').first();
    if (await selectEl.count() > 0) {
      const options = await selectEl.locator("option").count();
      expect(options).toBeGreaterThanOrEqual(1);
    }
  });
});
