/**
 * Capture remaining screenshots: Forecasting, Supplier Ratings, Voice Commands
 * run: node take-remaining.js
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
  await page.waitForTimeout(4000);
  const inputs = await page.locator("input").all();
  await inputs[0].fill(USERNAME);
  await inputs[1].fill(PASSWORD);
  await page.locator("button").last().click();
  await page.waitForTimeout(5000);
}

async function main() {
  const browser = await chromium.launch({ headless: true });
  const ctx     = await browser.newContext({ viewport: { width: 1440, height: 900 } });
  const page    = await ctx.newPage();
  page.on("dialog", d => d.dismiss());
  await login(page);

  // --- Forecasting ---
  process.stdout.write("📸  16-forecasting.png ...");
  // Try scrolling sidebar to find "Forecasting" link
  const forecLink = page.locator("nav a, aside a, [class*='sidebar'] *").filter({ hasText: /^Forecasting$/i }).first();
  if (await forecLink.count() === 0) {
    // Try any element with text Forecasting
    const anyForec = page.locator("text=Forecasting").first();
    if (await anyForec.count() > 0) {
      await anyForec.scrollIntoViewIfNeeded();
      await anyForec.click();
    }
  } else {
    await forecLink.scrollIntoViewIfNeeded();
    await forecLink.click();
  }
  await page.waitForTimeout(3500);
  await page.screenshot({ path: path.join(OUT_DIR, "16-forecasting.png") });
  console.log(" ✓  URL:", page.url());

  // --- Supplier Ratings tab ---
  process.stdout.write("📸  17-supplier-ratings.png ...");
  await page.locator("text=Supplier Scorecard").first().click();
  await page.waitForLoadState("networkidle");
  await page.waitForTimeout(3000);
  const row = page.locator("table tbody tr").first();
  if (await row.count() > 0) {
    await row.click();
    await page.waitForTimeout(2500);
    const ratingsTab = page.locator('[data-testid="tab-ratings"]');
    if (await ratingsTab.count() > 0) { await ratingsTab.click(); await page.waitForTimeout(2500); }
    await page.screenshot({ path: path.join(OUT_DIR, "17-supplier-ratings.png") });
    console.log(" ✓");
  } else {
    await page.screenshot({ path: path.join(OUT_DIR, "17-supplier-ratings.png") });
    console.log(" ✓ (list view)");
  }

  // --- Voice Commands ---
  process.stdout.write("📸  18-voice-commands.png ...");
  await page.locator("text=Dashboard").first().click();
  await page.waitForTimeout(2500);
  const voicePill = page.locator("text=Voice Commands").first();
  if (await voicePill.count() > 0) { await voicePill.click(); await page.waitForTimeout(1500); }
  await page.screenshot({ path: path.join(OUT_DIR, "18-voice-commands.png") });
  console.log(" ✓");

  await browser.close();
  console.log("\n✅  Done.");
}

main().catch(e => { console.error(e); process.exit(1); });
