import { expect, test } from '@playwright/test';
import { candidate, stubApi } from './api-stub';

test.describe('connection', () => {
  test('lands on the selection screen and displays the candidates right away', async ({
    page,
  }) => {
    // This is the scenario the unit tests cannot cover: the candidates arrive from an HTTP
    // response, and nothing but the change detection of the application makes them appear.
    await stubApi(page, {
      candidates: [candidate(2, 'Alice'), candidate(3, 'Chloé')],
    });
    await page.goto('/');

    await page.getByLabel('Email', { exact: true }).fill('user@pickme.com');
    await page.getByLabel('Password', { exact: true }).fill('password');
    await page.getByRole('button', { name: "Let's Go" }).click();

    await expect(page).toHaveURL(/\/select$/);
    await expect(page.getByText('Alice').first()).toBeVisible();
  });

  test('refuses to submit an invalid email', async ({ page }) => {
    await stubApi(page);
    await page.goto('/');

    await page.getByLabel('Email', { exact: true }).fill('not-an-email');
    await page.getByLabel('Password', { exact: true }).fill('password');
    await page.getByLabel('Email', { exact: true }).blur();

    await expect(page.getByText('Email is invalid')).toBeVisible();
    await expect(page).toHaveURL(/\/$/);
  });

  test('shows the registration form on demand', async ({ page }) => {
    await stubApi(page);
    await page.goto('/');

    await page.getByTitle('Register').click();

    await expect(page.getByLabel('Nickname')).toBeVisible();
  });
});
