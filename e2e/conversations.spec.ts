import { expect, test } from '@playwright/test';
import { candidate, stubApi } from './api-stub';

const API = 'http://localhost:5000';

/** Builds a conversation the messaging screen can display. */
function conversation(id: number, nickname: string, messages: unknown[] = []) {
  return { user: candidate(id, nickname), messages };
}

function message(id: number, sender: string, content: string | null) {
  return {
    id,
    content,
    date: new Date().toISOString(),
    sender,
    fkReceiver: 1,
  };
}

test.describe('conversations', () => {
  test.beforeEach(async ({ page }) => {
    await stubApi(page);
  });

  test('lists the conversations with a preview of the last message', async ({
    page,
  }) => {
    await page.route(`${API}/match/all`, (route) =>
      route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([
          conversation(2, 'Alice', [message(1, 'Alice', 'see you tomorrow')]),
          conversation(3, 'Chloé'),
        ]),
      }),
    );

    await page.goto('/match');

    await expect(page.getByText('Alice').first()).toBeVisible();
    await expect(page.getByText('see you tomorrow')).toBeVisible();
  });

  test('says so when there is no conversation yet', async ({ page }) => {
    await page.goto('/match');

    await expect(page.getByText('No match')).toBeVisible();
  });

  test('opens a conversation and sends a message', async ({ page }) => {
    await page.route(`${API}/match/all`, (route) =>
      route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([
          conversation(2, 'Alice', [message(1, 'Alice', 'bonjour')]),
        ]),
      }),
    );
    await page.route(`${API}/message`, (route) =>
      route.fulfill({
        status: 201,
        contentType: 'application/json',
        body: JSON.stringify(message(2, 'Moi', 'à demain')),
      }),
    );

    await page.goto('/match');
    await page.getByTitle('View Messages').first().click();

    await expect(page.getByText('bonjour')).toBeVisible();

    await page.getByLabel('Message', { exact: true }).fill('à demain');
    await page.getByTitle('Send').click();

    await expect(page.getByText('Message Sent').first()).toBeVisible();
    await expect(page.getByText('à demain').first()).toBeVisible();
  });

  test('goes back to the list of conversations', async ({ page }) => {
    await page.route(`${API}/match/all`, (route) =>
      route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([conversation(2, 'Alice')]),
      }),
    );

    await page.goto('/match');
    await page.getByTitle('View Messages').first().click();
    await expect(page.getByText('No message')).toBeVisible();

    await page.getByTitle('Back').click();

    await expect(page.getByLabel('Search by nickname')).toBeVisible();
  });
});
