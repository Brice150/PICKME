import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { Route } from '@angular/router';
import { Observable, of } from 'rxjs';
import { ConnectionService } from './connection.service';
import { SmartPreloading } from './smart-preloading.service';

describe('SmartPreloading', () => {
  const route: Route = { path: 'select' };
  let connection: jasmine.SpyObj<ConnectionService>;
  let strategy: SmartPreloading;
  let load: jasmine.Spy<() => Observable<unknown>>;

  beforeEach(() => {
    connection = jasmine.createSpyObj<ConnectionService>('ConnectionService', [
      'shouldPreload',
    ]);
    load = jasmine.createSpy('load').and.returnValue(of('chunk'));
    TestBed.configureTestingModule({
      providers: [{ provide: ConnectionService, useValue: connection }],
    });
    strategy = TestBed.inject(SmartPreloading);
  });

  it('preloads a route once the delay has elapsed', fakeAsync(() => {
    connection.shouldPreload.and.returnValue(true);
    let loaded: unknown;

    strategy.preload(route, load).subscribe((value) => (loaded = value));
    expect(load).not.toHaveBeenCalled();

    tick(1500);

    expect(load).toHaveBeenCalled();
    expect(loaded).toBe('chunk');
  }));

  it('skips a route excluded from the preloading', fakeAsync(() => {
    connection.shouldPreload.and.returnValue(true);
    let completed = false;

    strategy
      .preload({ ...route, data: { preload: false } }, load)
      .subscribe({ complete: () => (completed = true) });

    tick(1500);

    expect(completed).toBeTrue();
    expect(load).not.toHaveBeenCalled();
  }));

  it('skips the preloading on a connection that cannot afford it', fakeAsync(() => {
    connection.shouldPreload.and.returnValue(false);

    strategy.preload(route, load).subscribe();
    tick(1500);

    expect(load).not.toHaveBeenCalled();
  }));
});
