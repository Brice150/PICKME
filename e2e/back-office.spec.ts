import { expect, test, type Page } from '@playwright/test';
import { candidate, stubApi } from './api-stub';

const API = 'http://localhost:5000';

const STATS = {
  totalUsers: 12,
  totalDeletedAccounts: 2,
  totalRecentUsers: 3,
  totalRecentDeletedAccounts: 1,
};

const ARCHIVED = [
  {
    nickname: 'Bob',
    email: 'bob@pickme.com',
    deletionDate: new Date(2024, 2, 9).toISOString(),
    totalDislikes: 0,
    totalLikes: 5,
    totalMatches: 2,
    deletedBy: 'Admin',
  },
];

/** Answers the calls the back office makes, and records the searches it submits. */
async function stubBackOffice(page: Page): Promise<{ searches: unknown[] }> {
  const searches: unknown[] = [];
  await stubApi(page, { role: 'ROLE_ADMIN' });
  await page.route(`${API}/admin/stats`, (route) =>
    route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify(STATS),
    }),
  );
  await page.route(`${API}/admin/user/all/*`, (route) => {
    searches.push(route.request().postDataJSON());
    return route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify([candidate(2, 'Alice'), candidate(3, 'Chloé')]),
    });
  });
  await page.route(`${API}/admin/deleted-account/all/*`, (route) =>
    route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify(ARCHIVED),
    }),
  );
  return { searches };
}

test.describe('back office', () => {
  test('opens on the statistics and the accounts', async ({ page }) => {
    await stubBackOffice(page);

    await page.goto('/admin');

    await expect(page.getByText('12 Users')).toBeVisible();
    await expect(page.getByText('2 Deleted')).toBeVisible();
    await expect(page.getByText('alice@pickme.com')).toBeVisible();
  });

  test('submits the email typed in the search', async ({ page }) => {
    const { searches } = await stubBackOffice(page);
    await page.goto('/admin');
    await expect(page.getByText('alice@pickme.com')).toBeVisible();

    await page.getByLabel('Email', { exact: true }).fill('bob');
    await page.getByLabel('Email', { exact: true }).blur();

    await expect
      .poll(() =>
        searches.some(
          (search) => (search as { email: string }).email === 'bob',
        ),
      )
      .toBe(true);
  });

  test('switches to the archived accounts', async ({ page }) => {
    await stubBackOffice(page);
    await page.goto('/admin');
    await expect(page.getByText('alice@pickme.com')).toBeVisible();

    await page.getByTitle('View Deleted Users').click();

    await expect(page.getByText('bob@pickme.com')).toBeVisible();
  });

  test('opens the profile of an account and offers to delete it', async ({
    page,
  }) => {
    await stubBackOffice(page);
    await page.goto('/admin');

    await page.getByTitle('More Info').first().click();

    await expect(page.getByText('Alice').first()).toBeVisible();
    await expect(page.getByTitle('Delete User')).toBeVisible();
  });
});
