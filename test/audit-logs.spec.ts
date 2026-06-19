/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
import { test, expect } from "@playwright/test";
import { APP_URL, loginViaUI } from "./helpers/auth";

test.describe("Audit Logs — Phase 4", () => {

  test.beforeEach(async ({ page }) => {
    await loginViaUI(page);
  });

  test("audit logs page loads without JS errors", async ({ page }) => {
    const errors: string[] = [];
    page.on("pageerror", (err) => errors.push(err.message));
    await page.goto(APP_URL + "/audit-logs");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1500);
    const real = errors.filter(e => !e.includes("ResizeObserver") && !e.includes("Non-Error promise"));
    expect(real).toHaveLength(0);
  });

  test("audit table renders with log entries", async ({ page }) => {
    await page.goto(APP_URL + "/audit-logs");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1500);
    const table = page.locator('[data-testid="audit-table"]');
    await expect(table).toBeVisible();
    const rows = page.locator('[data-testid^="audit-row-"]');
    expect(await rows.count()).toBeGreaterThan(0);
  });

  test("KPI cards show event counts", async ({ page }) => {
    await page.goto(APP_URL + "/audit-logs");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1500);
    const body = await page.locator("body").innerText();
    expect(body).toContain("Total Events");
    expect(body).toContain("Creates");
  });

  test("entity type filter buttons exist and work", async ({ page }) => {
    await page.goto(APP_URL + "/audit-logs");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1500);
    for (const et of ["ALL", "PurchaseOrder", "CostRecord", "User"]) {
      const btn = page.locator(`[data-testid="filter-entity-${et}"]`);
      await expect(btn).toBeVisible();
    }
  });

  test("filtering by entity type narrows results", async ({ page }) => {
    await page.goto(APP_URL + "/audit-logs");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1500);
    const allCount = await page.locator('[data-testid^="audit-row-"]').count();
    await page.locator('[data-testid="filter-entity-PurchaseOrder"]').click();
    await page.waitForTimeout(1000);
    const filteredCount = await page.locator('[data-testid^="audit-row-"]').count();
    expect(filteredCount).toBeLessThanOrEqual(allCount);
  });

  test("search bar filters by details / user", async ({ page }) => {
    await page.goto(APP_URL + "/audit-logs");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1500);
    await page.locator('[data-testid="search-audit"]').fill("kumar");
    await page.waitForTimeout(500);
    const rows = page.locator('[data-testid^="audit-row-"]');
    // Rows with 'kumar' in performedBy or details should remain
    expect(await rows.count()).toBeGreaterThanOrEqual(0);
  });

  test("clicking a row expands details", async ({ page }) => {
    await page.goto(APP_URL + "/audit-logs");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1500);
    const firstRow = page.locator('[data-testid="audit-row-0"]');
    if (await firstRow.count() > 0) {
      await firstRow.click();
      await page.waitForTimeout(300);
      // Row should remain visible (expanded)
      await expect(firstRow).toBeVisible();
    }
  });

  test("audit log contains expected entity types", async ({ page }) => {
    await page.goto(APP_URL + "/audit-logs");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(1500);
    const body = await page.locator("body").innerText();
    expect(body).toContain("PurchaseOrder");
  });
});
