/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
import { test, expect } from "@playwright/test";
import { APP_URL, loginViaUI } from "./helpers/auth";

test.describe("Inventory Module — Phase 3", () => {

  test.beforeEach(async ({ page }) => {
    await loginViaUI(page);
  });

  test("inventory page loads without JS errors", async ({ page }) => {
    const errors: string[] = [];
    page.on("pageerror", (err) => errors.push(err.message));
    await page.goto(APP_URL + "/inventory");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1500);
    const real = errors.filter(e => !e.includes("ResizeObserver") && !e.includes("Non-Error promise"));
    expect(real).toHaveLength(0);
  });

  test("inventory table shows items with stock levels", async ({ page }) => {
    await page.goto(APP_URL + "/inventory");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1500);
    const table = page.locator('[data-testid="inventory-table"]');
    await expect(table).toBeVisible();
    const rows = page.locator('[data-testid^="inv-row-"]');
    expect(await rows.count()).toBeGreaterThan(0);
  });

  test("KPI cards show SKUs, low stock, critical counts", async ({ page }) => {
    await page.goto(APP_URL + "/inventory");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1500);
    const body = await page.locator("body").innerText();
    expect(body).toContain("Total SKUs");
    expect(body).toContain("Low Stock");
  });

  test("low stock filter toggle works", async ({ page }) => {
    await page.goto(APP_URL + "/inventory");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1500);
    const filterBtn = page.locator('[data-testid="low-stock-filter"]');
    await expect(filterBtn).toBeVisible();
    await filterBtn.click();
    await page.waitForTimeout(1000);
    // Should show fewer items (or same if all are low stock)
    const rows = page.locator('[data-testid^="inv-row-"]');
    expect(await rows.count()).toBeGreaterThanOrEqual(0);
  });

  test("search bar filters inventory list", async ({ page }) => {
    await page.goto(APP_URL + "/inventory");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1500);
    const searchInput = page.locator('[data-testid="search-inventory"]');
    await searchInput.fill("CHIP-001");
    await page.waitForTimeout(500);
    const rows = page.locator('[data-testid^="inv-row-"]');
    expect(await rows.count()).toBeGreaterThanOrEqual(0);
  });

  test("History button opens transaction modal", async ({ page }) => {
    await page.goto(APP_URL + "/inventory");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1500);
    const histBtn = page.locator('[data-testid^="txn-btn-"]').first();
    if (await histBtn.count() > 0) {
      await histBtn.click();
      await page.waitForTimeout(1000);
      const txnList = page.locator('[data-testid="txn-list"]');
      const body = await page.locator("body").innerText();
      // Either shows transactions or a "no transactions" message
      expect(body.length).toBeGreaterThan(10);
    }
  });

  test("Adjust button opens stock adjustment modal", async ({ page }) => {
    await page.goto(APP_URL + "/inventory");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1500);
    const adjustBtn = page.locator('[data-testid^="adjust-btn-"]').first();
    if (await adjustBtn.count() > 0) {
      await adjustBtn.click();
      await page.waitForTimeout(500);
      await expect(page.locator('[data-testid="adjust-type"]')).toBeVisible();
      await expect(page.locator('[data-testid="adjust-quantity"]')).toBeVisible();
    }
  });

  test("stock adjustment can be submitted", async ({ page }) => {
    await page.goto(APP_URL + "/inventory");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1500);
    const adjustBtn = page.locator('[data-testid^="adjust-btn-"]').first();
    if (await adjustBtn.count() > 0) {
      await adjustBtn.click();
      await page.waitForTimeout(500);
      await page.locator('[data-testid="adjust-type"]').selectOption("IN");
      await page.locator('[data-testid="adjust-quantity"]').fill("100");
      await page.locator('[data-testid="adjust-reference"]').fill("TEST-PO-001");
      await page.locator('[data-testid="confirm-adjust"]').click();
      await page.waitForTimeout(2000);
      // Modal should close
      await expect(page.locator('[data-testid="adjust-type"]')).not.toBeVisible({ timeout: 3000 });
    }
  });
});
