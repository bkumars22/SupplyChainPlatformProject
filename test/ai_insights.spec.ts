/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
﻿import { test, expect } from "@playwright/test";
import { APP_URL, API_URL, loginViaUI, getJwtToken } from "./helpers/auth";

test.describe("AI Risk Insights", () => {

  test("AI health endpoint is up", async () => {
    const token = await getJwtToken();
    const res   = await fetch(API_URL + "/api/ai/health", {
      headers: { Authorization: "Bearer " + token }
    });
    expect(res.status).toBe(200);
    const data = await res.json();
    expect(data.status).toBe("ok");
  });

  test("Supplier risk analysis returns valid score", async () => {
    const token = await getJwtToken();
    const res   = await fetch(API_URL + "/api/ai/supplier/1/risk", {
      headers: { Authorization: "Bearer " + token }
    });
    expect(res.status).toBe(200);
    const data = await res.json();
    expect(data).toHaveProperty("riskScore");
    expect(data).toHaveProperty("riskLevel");
  });

  test("AI insights endpoint returns array", async () => {
    const token = await getJwtToken();
    const res   = await fetch(API_URL + "/api/ai/insights", {
      headers: { Authorization: "Bearer " + token }
    });
    expect(res.status).toBe(200);
    const data = await res.json();
    expect(Array.isArray(data)).toBeTruthy();
  });

  test("AI insights page renders", async ({ page }) => {
    await loginViaUI(page);
    await page.goto(APP_URL + "/ai-insights");
    await page.waitForLoadState("networkidle");
    await page.waitForTimeout(2000);
    const html = await page.content();
    expect(html.length).toBeGreaterThan(100);
  });

});
