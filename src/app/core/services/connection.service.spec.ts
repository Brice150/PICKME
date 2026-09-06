import { PLATFORM_ID } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Mock } from 'vitest';
import { ConnectionService } from './connection.service';

interface FakeConnection {
  effectiveType: string;
  downlink: number;
  saveData: boolean;
  addEventListener: Mock;
}

describe('ConnectionService', () => {
  /** Publishes a fake Network Information API on the navigator of the test browser. */
  function stubConnection(
    overrides: Partial<Omit<FakeConnection, 'addEventListener'>> = {},
  ): FakeConnection {
    const connection: FakeConnection = {
      effectiveType: '4g',
      downlink: 10,
      saveData: false,
      addEventListener: vi.fn(),
      ...overrides,
    };
    Object.defineProperty(navigator, 'connection', {
      value: connection,
      configurable: true,
    });
    return connection;
  }

  function serviceWith(platformId = 'browser'): ConnectionService {
    TestBed.configureTestingModule({
      providers: [{ provide: PLATFORM_ID, useValue: platformId }],
    });
    return TestBed.inject(ConnectionService);
  }

  afterEach(() => {
    Reflect.deleteProperty(navigator, 'connection');
  });

  it('preloads by default when the browser exposes no connection information', () => {
    // The property is shadowed rather than deleted: the test browser implements the real API.
    Object.defineProperty(navigator, 'connection', {
      value: undefined,
      configurable: true,
    });

    expect(serviceWith().shouldPreload()).toBe(true);
  });

  it('preloads on a fast connection', () => {
    stubConnection();

    expect(serviceWith().shouldPreload()).toBe(true);
  });

  it('does not preload when the user asked to save data', () => {
    stubConnection({ saveData: true });

    expect(serviceWith().shouldPreload()).toBe(false);
  });

  it('does not preload on a slow connection type', () => {
    stubConnection({ effectiveType: '3g' });

    expect(serviceWith().shouldPreload()).toBe(false);
  });

  it('does not preload under the minimum bandwidth', () => {
    stubConnection({ downlink: 1 });

    expect(serviceWith().shouldPreload()).toBe(false);
  });

  it('follows the connection when it changes', () => {
    const connection = stubConnection();
    const service = serviceWith();
    expect(service.shouldPreload()).toBe(true);

    connection.effectiveType = '2g';
    connection.addEventListener.mock.lastCall![1]();

    expect(service.shouldPreload()).toBe(false);
  });

  it('publishes the preloading decision as an observable', () => {
    stubConnection({ saveData: true });
    let canPreload: boolean | undefined;

    serviceWith().canPreload$.subscribe((value) => (canPreload = value));

    expect(canPreload).toBe(false);
  });

  it('stays neutral when it does not run in a browser', () => {
    stubConnection({ saveData: true });

    expect(serviceWith('server').shouldPreload()).toBe(true);
  });
});
