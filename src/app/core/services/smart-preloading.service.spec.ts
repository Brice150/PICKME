import { TestBed } from '@angular/core/testing';
import { Route } from '@angular/router';
import { Observable, of } from 'rxjs';
import { Mock } from 'vitest';
import { createSpyObj, SpyObj } from '../../../testing/spy';
import { ConnectionService } from './connection.service';
import { SmartPreloading } from './smart-preloading.service';

describe('SmartPreloading', () => {
  const route: Route = { path: 'select' };
  let connection: SpyObj<ConnectionService>;
  let strategy: SmartPreloading;
  let load: Mock<() => Observable<unknown>>;

  beforeEach(() => {
    vi.useFakeTimers();
    connection = createSpyObj<ConnectionService>(['shouldPreload']);
    load = vi.fn().mockReturnValue(of('chunk'));
    TestBed.configureTestingModule({
      providers: [{ provide: ConnectionService, useValue: connection }],
    });
    strategy = TestBed.inject(SmartPreloading);
  });

  afterEach(() => vi.useRealTimers());

  it('preloads a route once the delay has elapsed', () => {
    connection.shouldPreload.mockReturnValue(true);
    let loaded: unknown;

    strategy.preload(route, load).subscribe((value) => (loaded = value));
    expect(load).not.toHaveBeenCalled();

    vi.advanceTimersByTime(1500);

    expect(load).toHaveBeenCalled();
    expect(loaded).toBe('chunk');
  });

  it('skips a route excluded from the preloading', () => {
    connection.shouldPreload.mockReturnValue(true);
    let completed = false;

    strategy
      .preload({ ...route, data: { preload: false } }, load)
      .subscribe({ complete: () => (completed = true) });

    vi.advanceTimersByTime(1500);

    expect(completed).toBe(true);
    expect(load).not.toHaveBeenCalled();
  });

  it('skips the preloading on a connection that cannot afford it', () => {
    connection.shouldPreload.mockReturnValue(false);

    strategy.preload(route, load).subscribe();
    vi.advanceTimersByTime(1500);

    expect(load).not.toHaveBeenCalled();
  });
});
