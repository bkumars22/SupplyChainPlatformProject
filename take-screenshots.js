/**
 * Screenshot capture — run with: node take-screenshots.js
 * Navigates via sidebar clicks to avoid SPA redirect issues on GitHub Pages
 */
const { chromium } = require("playwright");
const fs   = require("fs");
const path = require("path");

const BASE     = "https://bkumars22.github.io/SupplyChainPlatformProject";
const OUT_DIR  = path.join(__dirname, "github-docs", "screenshots");
const USERNAME = "kumar";
const PASSWORD = "Kumar@2026";

async function login(page) {
  await page.goto(BASE + "/login", { waitUntil: "networkidle" });
  await page.waitForTimeout(3000);
  const inputs = await page.locator("input").all();
  await inputs[0].fill(USERNAME);
  await inputs[1].fill(PASSWORD);
  const btn = page.locator("button").last();
  await btn.click();
  await page.waitForTimeout(5000);
  console.log("   Logged in. URL:", page.url());
}

async function nav(page, linkText, filename) {
  process.stdout.write(`📸  ${filename} ...`);
  // Click sidebar link
  const link = page.locator(`text=${linkText}`).first();
  await link.click();
  await page.waitForLoadState("networkidle");
  await page.waitForTimeout(3500);
  await page.screenshot({ path: path.join(OUT_DIR, filename) });
  console.log(" ✓");
}

async function main() {
  if (!fs.existsSync(OUT_DIR)) fs.mkdirSync(OUT_DIR, { recursive: true });
  const browser = await chromium.launch({ headless: true });
  const ctx     = await browser.newContext({ viewport: { width: 1440, height: 900 } });
  const page    = await ctx.newPage();
  page.on("dialog", d => d.dismiss());

  await login(page);

  // Screenshot of landing page (dashboard)
  process.stdout.write("📸  01-dashboard.png ...");
  await page.waitForTimeout(1000);
  await page.screenshot({ path: path.join(OUT_DIR, "01-dashboard.png") });
  console.log(" ✓");

  // Click each sidebar link and screenshot
  await nav(page, "Alerts",              "02-alerts.png");
  await nav(page, "Supplier Scorecard",  "03-suppliers.png");
  await nav(page, "Cost Records",        "04-cost-records.png");
  await nav(page, "Bill of Materials",   "05-bom.png");
  await nav(page, "Reports",             "06-reports.png");
  await nav(page, "AI Anomaly Engine",   "07-ai-engines.png");
  await nav(page, "User Management",     "08-user-management.png");
  await nav(page, "Purchase Orders",     "09-purchase-orders.png");
  await nav(page, "Inventory",           "10-inventory.png");
  await nav(page, "Audit Logs",          "11-audit-logs.png");
  await nav(page, "Help / Guide",        "12-help-guide.png");
  await nav(page, "Test Dashboard",      "13-test-dashboard.png");
  await nav(page, "Eval Dashboard",      "14-eval-dashboard.png");
  await nav(page, "Setup",              "15-setup-wizard.png");
  await nav(page, "Forecasting",         "16-forecasting.png");

  // Go back to suppliers for the Ratings tab
  process.stdout.write("📸  17-supplier-ratings.png ...");
  await page.locator("text=Supplier Scorecard").first().click();
  await page.waitForLoadState("networkidle");
  await page.waitForTimeout(3000);
  const firstRow = page.locator("table tbody tr").first();
  if (await firstRow.count() > 0) {
    await firstRow.click();
    await page.waitForTimeout(2000);
    const ratingsTab = page.locator('[data-testid="tab-ratings"]');
    if (await ratingsTab.count() > 0) { await ratingsTab.click(); await page.waitForTimeout(2000); }
    await page.screenshot({ path: path.join(OUT_DIR, "17-supplier-ratings.png") });
    console.log(" ✓");
  } else {
    await page.screenshot({ path: path.join(OUT_DIR, "17-supplier-ratings.png") });
    console.log(" ✓ (no supplier rows — captured list view)");
  }

  // Voice commands panel
  process.stdout.write("📸  18-voice-commands.png ...");
  await page.locator("text=Dashboard").first().click();
  await page.waitForTimeout(2500);
  const voicePill = page.locator("text=Voice Commands").first();
  if (await voicePill.count() > 0) { await voicePill.click(); await page.waitForTimeout(1200); }
  await page.screenshot({ path: path.join(OUT_DIR, "18-voice-commands.png") });
  console.log(" ✓");

  await browser.close();
  console.log(`\n✅  Screenshots saved to: ${OUT_DIR}`);
}

main().catch(e => { console.error(e); process.exit(1); });
