/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
﻿import { defineConfig } from '@playwright/test';
export default defineConfig({
  testDir: './test',
  timeout: 30000,
  retries: 1,
  workers: 2,
  reporter: [['list'],['json',{outputFile:'test-results/results.json'}]],
  use: { baseURL:'http://localhost:3000', browserName:'chromium', headless:true, screenshot:'only-on-failure' },
});
