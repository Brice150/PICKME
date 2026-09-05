import { expect, test } from '@playwright/test';
import { candidate, stubApi } from './api-stub';

const API = 'http://localhost:5000';

test.describe('selection', () => {
  test.beforeEach(async ({ page }) => {
    await stubApi(page, {
      candidates: [candidate(2, 'Alice'), candidate(3, 'Chloé')],
    });
  });

  test('displays the candidates as soon as they arrive', async ({ page }) => {
    await page.goto('/select');

    await expect(page.getByText('Alice').first()).toBeVisible();
    await expect(page.getByText('Architecte').first()).toBeVisible();
  });

  test('drops the profile and congratulates the user on a match', async ({
    page,
  }) => {
    await page.route(`${API}/like/2`, (route) =>
      route.fulfill({ status: 201, contentType: 'text/plain', body: 'Alice' }),
    );
    await page.goto('/select');
    await expect(page.getByText('Alice').first()).toBeVisible();

    await page.getByTitle('Like', { exact: true }).first().click();

    await expect(page.getByText('Matched Alice').first()).toBeVisible();
  });

  test('drops the profile on a dislike', async ({ page }) => {
    await page.route(`${API}/dislike/2`, (route) =>
      route.fulfill({
        status: 201,
        contentType: 'application/json',
        body: 'null',
      }),
    );
    await page.goto('/select');
    await expect(page.getByText('Alice').first()).toBeVisible();

    await page.getByTitle('Dislike', { exact: true }).first().click();

    await expect(page.getByText('Disliked Alice').first()).toBeVisible();
  });

  test('invites the user to widen their criteria when nobody matches', async ({
    page,
  }) => {
    await page.route(`${API}/user/all/0`, (route) =>
      route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: '[]',
      }),
    );

    await page.goto('/select');

    await expect(page.getByText('No user to show')).toBeVisible();
  });
});
