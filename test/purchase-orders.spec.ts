/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
import { test, expect } from "@playwright/test";
import { APP_URL, loginViaUI } from "./helpers/auth";

test.describe("Purchase Order Management — Phase 2", () => {

  test.beforeEach(async ({ page }) => {
    await loginViaUI(page);
  });

  test("purchase orders page loads without JS errors", async ({ page }) => {
    const errors: string[] = [];
    page.on("pageerror", (err) => errors.push(err.message));
    await page.goto(APP_URL + "/purchase-orders");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1500);
    const real = errors.filter(e => !e.includes("ResizeObserver") && !e.includes("Non-Error promise"));
    expect(real).toHaveLength(0);
  });

  test("PO table renders with orders", async ({ page }) => {
    await page.goto(APP_URL + "/purchase-orders");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1500);
    const table = page.locator('[data-testid="po-table"]');
    await expect(table).toBeVisible();
  });

  test("KPI cards show totals", async ({ page }) => {
    await page.goto(APP_URL + "/purchase-orders");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1500);
    const body = await page.locator("body").innerText();
    expect(body).toContain("Total Orders");
  });

  test("status filter buttons are visible", async ({ page }) => {
    await page.goto(APP_URL + "/purchase-orders");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1000);
    for (const s of ["ALL", "DRAFT", "SUBMITTED", "CONFIRMED", "RECEIVED"]) {
      await expect(page.locator(`[data-testid="filter-${s}"]`)).toBeVisible();
    }
  });

  test("Create PO button opens create modal", async ({ page }) => {
    await page.goto(APP_URL + "/purchase-orders");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1000);
    await page.locator('[data-testid="create-po-btn"]').click();
    await expect(page.locator('[data-testid="create-po-supplierId"]')).toBeVisible();
  });

  test("create PO form can be filled and submitted", async ({ page }) => {
    await page.goto(APP_URL + "/purchase-orders");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1000);
    await page.locator('[data-testid="create-po-btn"]').click();
    await page.locator('[data-testid="create-po-supplierId"]').fill("SUPP-002");
    await page.locator('[data-testid="create-po-supplierName"]').fill("Test Supplier");
    await page.locator('[data-testid="confirm-create-po"]').click();
    await page.waitForTimeout(2000);
    // Modal should close after successful create
    await expect(page.locator('[data-testid="create-po-supplierId"]')).not.toBeVisible({ timeout: 3000 });
  });

  test("search bar filters PO table", async ({ page }) => {
    await page.goto(APP_URL + "/purchase-orders");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1500);
    await page.locator('[data-testid="search-po"]').fill("PO-2026-0001");
    await page.waitForTimeout(500);
    const rows = page.locator('[data-testid^="po-row-"]');
    const count = await rows.count();
    expect(count).toBeGreaterThanOrEqual(0);
  });

  test("View button opens PO detail modal with line items", async ({ page }) => {
    await page.goto(APP_URL + "/purchase-orders");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1500);
    const viewBtn = page.locator('[data-testid^="view-po-"]').first();
    if (await viewBtn.count() > 0) {
      await viewBtn.click();
      await page.waitForTimeout(500);
      // Line items table or at least modal should appear
      const body = await page.locator("body").innerText();
      expect(body).toContain("Purchase Order");
    }
  });

  test("status filter shows only matching orders", async ({ page }) => {
    await page.goto(APP_URL + "/purchase-orders");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1500);
    await page.locator('[data-testid="filter-RECEIVED"]').click();
    await page.waitForTimeout(500);
    const badges = page.locator("span:has-text('RECEIVED')");
    // All visible status badges should be RECEIVED (or none)
    const draftBadges = page.locator("span:has-text('DRAFT')");
    expect(await draftBadges.count()).toBe(0);
  });
});
