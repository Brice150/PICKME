import { defineConfig, devices } from '@playwright/test';

/**
 * The end to end suite drives the real application in a real browser: routing, change detection
 * and rendering are exercised the way a user meets them, which the unit tests cannot do.
 *
 * The API is stubbed at the network level rather than started for real, so the suite needs no
 * database and stays fast enough to run on every push.
 */
export default defineConfig({
  testDir: './e2e',
  // One worker on purpose: the scenarios share the development server, which compiles the lazy
  // chunks on demand and times the parallel workers out. The whole suite runs in a few seconds.
  workers: 1,
  forbidOnly: !!process.env['CI'],
  retries: process.env['CI'] ? 1 : 0,
  reporter: process.env['CI'] ? 'github' : 'list',
  use: {
    baseURL: 'http://localhost:4200',
    trace: 'on-first-retry',
  },
  projects: [{ name: 'chromium', use: { ...devices['Desktop Chrome'] } }],
  webServer: {
    command: 'npm start -- --port 4200',
    url: 'http://localhost:4200',
    reuseExistingServer: !process.env['CI'],
    timeout: 180_000,
  },
});
