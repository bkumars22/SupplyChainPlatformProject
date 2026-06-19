/**
 * Screenshot capture script — saves PNG screenshots of every SCIP page
 * Run: npx ts-node test/take-screenshots.ts
 */
import { chromium, Page } from "@playwright/test";
import * as fs from "fs";
import * as path from "path";

const BASE     = "https://bkumars22.github.io/SupplyChainPlatformProject";
const OUT_DIR  = path.join(__dirname, "..", "github-docs", "screenshots");
const USERNAME = "kumar";
const PASSWORD = "Kumar@2026";

async function login(page: Page) {
  await page.goto(BASE + "/login");
  await page.waitForLoadState("networkidle");
  await page.waitForTimeout(2000);
  const userInput = page.locator('input[type="text"], input[name="username"], input[placeholder*="user" i]').first();
  const passInput = page.locator('input[type="password"]').first();
  await userInput.fill(USERNAME);
  await passInput.fill(PASSWORD);
  await page.locator('button[type="submit"], button:has-text("Sign"), button:has-text("Login")').first().click();
  await page.waitForTimeout(3000);
}

async function shot(page: Page, route: string, filename: string, extra?: () => Promise<void>) {
  console.log(`📸  ${filename} ...`);
  await page.goto(BASE + route);
  await page.waitForLoadState("networkidle");
  await page.waitForTimeout(2500);
  if (extra) await extra();
  await page.screenshot({ path: path.join(OUT_DIR, filename), fullPage: false });
  console.log(`    ✓ saved`);
}

async function main() {
  if (!fs.existsSync(OUT_DIR)) fs.mkdirSync(OUT_DIR, { recursive: true });

  const browser = await chromium.launch({ headless: true });
  const ctx     = await browser.newContext({ viewport: { width: 1440, height: 900 } });
  const page    = await ctx.newPage();

  // Dismiss any dialogs automatically
  page.on("dialog", d => d.dismiss());

  await login(page);

  // Core modules
  await shot(page, "/dashboard",     "01-dashboard.png");
  await shot(page, "/suppliers",     "02-suppliers.png");
  await shot(page, "/alerts",        "03-alerts.png");
  await shot(page, "/cost-records",  "04-cost-records.png");
  await shot(page, "/bom",           "05-bom.png");
  await shot(page, "/reports",       "06-reports.png");
  await shot(page, "/forecasts",     "07-forecasting.png");
  await shot(page, "/ai",            "08-ai-engines.png");
  await shot(page, "/admin/users",   "09-user-management.png");

  // Phase 1 — Supplier Ratings (navigate to supplier detail then click Ratings tab)
  await page.goto(BASE + "/suppliers");
  await page.waitForLoadState("networkidle");
  await page.waitForTimeout(2000);
  const firstRow = page.locator("table tbody tr").first();
  if (await firstRow.count() > 0) {
    await firstRow.click();
    await page.waitForTimeout(1500);
    const ratingsTab = page.locator('[data-testid="tab-ratings"]');
    if (await ratingsTab.count() > 0) {
      await ratingsTab.click();
      await page.waitForTimeout(1500);
    }
    await page.screenshot({ path: path.join(OUT_DIR, "10-supplier-ratings.png"), fullPage: false });
    console.log("📸  10-supplier-ratings.png ✓");
  }

  // Phase 2 — Purchase Orders
  await shot(page, "/purchase-orders", "11-purchase-orders.png");

  // Phase 3 — Inventory
  await shot(page, "/inventory", "12-inventory.png");

  // Phase 4 — Audit Logs
  await shot(page, "/audit-logs", "13-audit-logs.png");

  // Help page
  await shot(page, "/help", "14-help-guide.png");

  // Test dashboard
  await shot(page, "/tests", "15-test-dashboard.png");

  // Eval dashboard
  await shot(page, "/eval", "16-eval-dashboard.png");

  // Setup wizard
  await shot(page, "/setup", "17-setup-wizard.png");

  // Voice commands — open the panel
  await page.goto(BASE + "/dashboard");
  await page.waitForLoadState("networkidle");
  await page.waitForTimeout(2000);
  const voicePill = page.locator("text=Voice Commands").first();
  if (await voicePill.count() > 0) {
    await voicePill.click();
    await page.waitForTimeout(1000);
    await page.screenshot({ path: path.join(OUT_DIR, "18-voice-commands.png"), fullPage: false });
    console.log("📸  18-voice-commands.png ✓");
  }

  await browser.close();
  console.log(`\n✅  All screenshots saved to: ${OUT_DIR}`);
}

main().catch(e => { console.error(e); process.exit(1); });
