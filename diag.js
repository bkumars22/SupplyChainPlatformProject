const { chromium } = require("playwright");
(async () => {
  const b = await chromium.launch();
  const p = await b.newPage();
  await p.goto("http://localhost:3000/login");
  await p.waitForLoadState("networkidle");
  await p.locator("input[type=text]").fill("kumar");
  await p.locator("input[type=password]").fill("Kumar@2026");
  await p.locator("button[type=submit]").click();
  try {
    await p.waitForURL(u => !u.toString().includes("/login"), { timeout: 10000 });
    console.log("Login OK:", p.url());
  } catch(e) {
    console.log("Login FAILED:", p.url());
  }
  await p.goto("http://localhost:3000/purchase-orders");
  await p.waitForTimeout(3000);
  const tb = await p.locator('[data-testid="po-table"]').count();
  const btn = await p.locator('[data-testid="create-po-btn"]').count();
  const rows = await p.locator('[data-testid^="po-row-"]').count();
  console.log("PO page - table:", tb, "  create-btn:", btn, "  rows:", rows);
  await p.goto("http://localhost:3000/inventory");
  await p.waitForTimeout(3000);
  const inv2 = await p.locator('[data-testid="inventory-table"]').count();
  const invRows = await p.locator('[data-testid^="inv-row-"]').count();
  console.log("Inventory page - table:", inv2, "  rows:", invRows);
  await p.goto("http://localhost:3000/audit-logs");
  await p.waitForTimeout(3000);
  const aud = await p.locator('[data-testid="audit-table"]').count();
  console.log("Audit page - audit-table:", aud);
  await b.close();
})().catch(e => console.error(e.message));
