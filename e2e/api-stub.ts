import type { Page, Route } from '@playwright/test';

const API = 'http://localhost:5000';

/** Builds a candidate the selection screen can display. */
export function candidate(id: number, nickname: string) {
  return {
    id,
    userRole: 'HIDDEN',
    birthDate: '1995-06-15T00:00:00.000+00:00',
    gold: false,
    nickname,
    job: 'Architecte',
    height: 170,
    email: `${nickname.toLowerCase()}@pickme.com`,
    description: `Description de ${nickname}`,
    genderAge: {
      gender: 'Woman',
      genderSearch: 'Man',
      minAge: 18,
      maxAge: 99,
    },
    preferences: {},
    geolocation: {
      latitude: '48.8566',
      longitude: '2.3522',
      distanceSearch: 100,
      distance: 5,
    },
    pictures: [],
  };
}

/** Builds the account of the connected user. */
export function connectedUser(role = 'ROLE_USER') {
  return { ...candidate(1, 'Moi'), userRole: role, stats: {} };
}

function json(route: Route, body: unknown, status = 200): Promise<void> {
  return route.fulfill({
    status,
    contentType: 'application/json',
    body: JSON.stringify(body),
  });
}

/**
 * Answers every call the application makes, so that a scenario runs without a server or a
 * database. Each scenario overrides the routes it cares about before navigating.
 *
 * @param page       page under test
 * @param candidates candidates the selection screen receives on its first page
 * @param role       role of the connected user
 */
export async function stubApi(
  page: Page,
  { candidates = [candidate(2, 'Alice')], role = 'ROLE_USER' } = {},
): Promise<void> {
  // The registration and the profile screens look the visitor up by ip.
  await page.route('https://ipapi.co/json/', (route) =>
    json(route, {
      latitude: '48.8566',
      longitude: '2.3522',
      city: 'Rouen',
      country_capital: 'Paris',
    }),
  );

  await page.route(`${API}/login`, (route) => json(route, connectedUser(role)));
  await page.route(`${API}/user`, (route) => json(route, connectedUser(role)));
  await page.route(`${API}/notification/all`, (route) => json(route, []));
  // The menu opens a stream to be told when something changes. The scenarios drive the screens
  // themselves, so an empty stream is enough.
  await page.route(`${API}/notification/stream`, (route) =>
    route.fulfill({ status: 200, contentType: 'text/event-stream', body: '' }),
  );
  await page.route(`${API}/match/all`, (route) => json(route, []));
  // Only the first page holds candidates, the next ones are empty. Playwright matches the routes
  // in reverse order of registration, so the catch all has to be declared before the page it is
  // meant to fall back for.
  await page.route(`${API}/user/all/*`, (route) => json(route, []));
  await page.route(`${API}/user/all/0`, (route) => json(route, candidates));
}
