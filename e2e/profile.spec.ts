import { expect, test, type Page } from '@playwright/test';
import { connectedUser, stubApi } from './api-stub';

const API = 'http://localhost:5000';

/** Opens the panel of a section and returns its component, the titles repeating from one to another. */
async function openSection(page: Page, title: string, selector: string) {
  // The header of the panel comes first in the document, before anything it may repeat inside.
  await page.getByText(title, { exact: true }).first().click();
  return page.locator(selector);
}

test.describe('profile', () => {
  test.beforeEach(async ({ page }) => {
    await stubApi(page);
  });

  test('opens on the account of the connected user', async ({ page }) => {
    await page.goto('/profile');

    const mainInfos = await openSection(page, 'Main Infos', 'app-main-infos');

    await expect(mainInfos.getByLabel('Nickname')).toHaveValue('Moi');
    await expect(mainInfos.getByLabel('Job')).toHaveValue('Architecte');
  });

  test('saves the identity of the account', async ({ page }) => {
    let submitted: { nickname?: string } | undefined;
    await page.route(`${API}/user`, (route) => {
      if (route.request().method() === 'PUT') {
        submitted = route.request().postDataJSON();
      }
      return route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify(connectedUser()),
      });
    });

    await page.goto('/profile');
    const mainInfos = await openSection(page, 'Main Infos', 'app-main-infos');
    await mainInfos.getByLabel('Nickname').fill('Alicia');
    await mainInfos.getByTitle('Update', { exact: true }).click();

    await expect(page.getByText('Main Infos Updated').first()).toBeVisible();
    expect(submitted?.nickname).toBe('Alicia');
  });

  test('restores the saved identity when the edition is cancelled', async ({
    page,
  }) => {
    await page.goto('/profile');

    const mainInfos = await openSection(page, 'Main Infos', 'app-main-infos');
    await mainInfos.getByLabel('Nickname').fill('Alicia');
    await mainInfos.getByTitle('Cancel', { exact: true }).click();

    await expect(mainInfos.getByLabel('Nickname')).toHaveValue('Moi');
  });

  test('never closes the account without asking first', async ({ page }) => {
    let deleted = false;
    await page.route(`${API}/user`, (route) => {
      if (route.request().method() === 'DELETE') {
        deleted = true;
        return route.fulfill({ status: 200, body: '' });
      }
      return route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify(connectedUser()),
      });
    });

    await page.goto('/profile');
    // The header of the panel carries the button role too, so the click is scoped to the section.
    const section = await openSection(
      page,
      'Delete Account',
      'app-delete-account',
    );
    await section.getByRole('button').click();

    await expect(
      page.getByText(/Are you sure you want to delete your account/),
    ).toBeVisible();
    await page.getByTitle('No').click();

    expect(deleted).toBe(false);
  });
});
