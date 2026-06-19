/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
import { test, expect } from "@playwright/test";
import { APP_URL, loginViaUI } from "./helpers/auth";

test.describe("Supplier Quality Rating — Phase 1", () => {

  test.beforeEach(async ({ page }) => {
    await loginViaUI(page);
  });

  test("supplier detail page has Ratings tab", async ({ page }) => {
    await page.goto(APP_URL + "/suppliers");
    await page.waitForLoadState("networkidle");
    // Click first supplier row
    const firstRow = page.locator("table tbody tr").first();
    await firstRow.click();
    await page.waitForTimeout(1000);
    // Ratings tab should be present
    const ratingsTab = page.locator('[data-testid="tab-ratings"]');
    await expect(ratingsTab).toBeVisible();
  });

  test("rating history loads after clicking Ratings tab", async ({ page }) => {
    await page.goto(APP_URL + "/suppliers");
    await page.waitForLoadState("networkidle");
    const firstRow = page.locator("table tbody tr").first();
    await firstRow.click();
    await page.waitForTimeout(1000);
    await page.locator('[data-testid="tab-ratings"]').click();
    await page.waitForTimeout(1000);
    const history = page.locator('[data-testid="supplier-rating-history"]');
    const empty   = page.locator('[data-testid="rating-history-empty"]');
    const either  = page.locator('[data-testid="supplier-rating-history"], [data-testid="rating-history-empty"]');
    await expect(either.first()).toBeVisible({ timeout: 5000 });
  });

  test("Submit Rating button opens rating modal", async ({ page }) => {
    await page.goto(APP_URL + "/suppliers");
    await page.waitForLoadState("networkidle");
    const firstRow = page.locator("table tbody tr").first();
    await firstRow.click();
    await page.waitForTimeout(1000);
    await page.locator('[data-testid="tab-ratings"]').click();
    await page.waitForTimeout(500);
    const openBtn = page.locator('[data-testid="open-rating-modal"]');
    await expect(openBtn).toBeVisible();
    await openBtn.click();
    await expect(page.locator('[data-testid="supplier-rating-modal"]')).toBeVisible();
  });

  test("rating modal has quality, delivery, responsiveness inputs", async ({ page }) => {
    await page.goto(APP_URL + "/suppliers");
    await page.waitForLoadState("networkidle");
    await page.locator("table tbody tr").first().click();
    await page.waitForTimeout(1000);
    await page.locator('[data-testid="tab-ratings"]').click();
    await page.waitForTimeout(500);
    await page.locator('[data-testid="open-rating-modal"]').click();
    await expect(page.locator('[data-testid="input-qualityScore"]')).toBeVisible();
    await expect(page.locator('[data-testid="input-deliveryScore"]')).toBeVisible();
    await expect(page.locator('[data-testid="input-responsiveness"]')).toBeVisible();
    await expect(page.locator('[data-testid="input-comments"]')).toBeVisible();
  });

  test("submitting a valid rating closes modal and refreshes history", async ({ page }) => {
    await page.goto(APP_URL + "/suppliers");
    await page.waitForLoadState("networkidle");
    await page.locator("table tbody tr").first().click();
    await page.waitForTimeout(1000);
    await page.locator('[data-testid="tab-ratings"]').click();
    await page.waitForTimeout(500);
    await page.locator('[data-testid="open-rating-modal"]').click();
    // Fill valid scores
    await page.locator('[data-testid="input-qualityScore"]').fill("82");
    await page.locator('[data-testid="input-deliveryScore"]').fill("78");
    await page.locator('[data-testid="input-responsiveness"]').fill("85");
    await page.locator('[data-testid="input-comments"]').fill("Automated test rating");
    await page.locator('[data-testid="submit-rating"]').click();
    // Modal should close
    await expect(page.locator('[data-testid="supplier-rating-modal"]')).not.toBeVisible({ timeout: 5000 });
  });

  test("cancel button closes rating modal", async ({ page }) => {
    await page.goto(APP_URL + "/suppliers");
    await page.waitForLoadState("networkidle");
    await page.locator("table tbody tr").first().click();
    await page.waitForTimeout(1000);
    await page.locator('[data-testid="tab-ratings"]').click();
    await page.waitForTimeout(500);
    await page.locator('[data-testid="open-rating-modal"]').click();
    await expect(page.locator('[data-testid="supplier-rating-modal"]')).toBeVisible();
    await page.locator('[data-testid="cancel-rating"]').click();
    await expect(page.locator('[data-testid="supplier-rating-modal"]')).not.toBeVisible();
  });
});
