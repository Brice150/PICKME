import { expect, test } from '@playwright/test';
import { connectedUser, stubApi } from './api-stub';

const API = 'http://localhost:5000';

test.describe('access rules', () => {
  test('sends a visitor without a session back to the connection screen', async ({
    page,
  }) => {
    await stubApi(page);
    await page.route(`${API}/user`, (route) =>
      route.fulfill({ status: 401, body: '' }),
    );

    await page.goto('/select');

    await expect(page).toHaveURL(/\/$/);
  });

  test('keeps a standard user out of the back office', async ({ page }) => {
    // The guard used to grant access to anybody whose account could be loaded, whatever the role.
    await stubApi(page, { role: 'ROLE_USER' });

    await page.goto('/admin');

    await expect(page).toHaveURL(/\/select$/);
  });

  test('lets an administrator reach the back office', async ({ page }) => {
    await stubApi(page, { role: 'ROLE_ADMIN' });
    await page.route(`${API}/admin/stats`, (route) =>
      route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          totalUsers: 12,
          totalDeletedAccounts: 2,
          totalRecentUsers: 3,
          totalRecentDeletedAccounts: 1,
        }),
      }),
    );
    await page.route(`${API}/admin/user/all/*`, (route) =>
      route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([connectedUser()]),
      }),
    );

    await page.goto('/admin');

    await expect(page).toHaveURL(/\/admin$/);
    await expect(page.getByText('12')).toBeVisible();
  });
});
