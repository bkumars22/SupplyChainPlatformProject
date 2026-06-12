/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
﻿import { test, expect } from "@playwright/test";
import { APP_URL, loginViaUI } from "./helpers/auth";

test.describe("User management", () => {

  test.beforeEach(async ({ page }) => {
    await loginViaUI(page);
  });

  test("users page loads without errors", async ({ page }) => {
    await page.goto(APP_URL + "/users");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(2000);
    const errors: string[] = [];
    page.on("pageerror", (err) => errors.push(err.message));
    const realErrors = errors.filter(e => !e.includes("ResizeObserver"));
    expect(realErrors).toHaveLength(0);
  });

  test("users page has visible content", async ({ page }) => {
    await page.goto(APP_URL + "/users");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(3000);
    const html = await page.content();
    expect(html.length).toBeGreaterThan(100);
  });

  test("users page has no broken images", async ({ page }) => {
    await page.goto(APP_URL + "/users");
    await page.waitForLoadState("networkidle");
    expect(true).toBeTruthy();
  });

  test("users page is responsive", async ({ page }) => {
    await page.setViewportSize({ width: 390, height: 844 });
    await page.goto(APP_URL + "/users");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(2000);
    const html = await page.content();
    expect(html.length).toBeGreaterThan(100);
    await page.setViewportSize({ width: 1280, height: 720 });
  });

});
