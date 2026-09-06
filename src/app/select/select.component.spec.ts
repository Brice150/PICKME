import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { of, throwError } from 'rxjs';
import { createSpyObj, SpyObj } from '../../testing/spy';
import { User } from '../core/interfaces/user';
import { SelectService } from '../core/services/select.service';
import { userFixture } from '../core/testing/user.fixture';
import { SelectComponent } from './select.component';

describe('SelectComponent', () => {
  let fixture: ComponentFixture<SelectComponent>;
  let component: SelectComponent;
  let selectService: SpyObj<SelectService>;
  let toastr: SpyObj<ToastrService>;
  let router: SpyObj<Router>;

  beforeEach(async () => {
    selectService = createSpyObj<SelectService>([
      'getAllSelectedUsers',
      'addLike',
      'addDislike',
    ]);
    toastr = createSpyObj<ToastrService>(['success']);
    router = createSpyObj<Router>(['navigate']);
    selectService.getAllSelectedUsers.mockReturnValue(of([]));
    await TestBed.configureTestingModule({
      imports: [SelectComponent],
      providers: [
        provideNoopAnimations(),
        { provide: SelectService, useValue: selectService },
        { provide: ToastrService, useValue: toastr },
        { provide: Router, useValue: router },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(SelectComponent);
    component = fixture.componentInstance;
  });

  /** Starts the screen with a first page of candidates. */
  function start(users: User[]): void {
    selectService.getAllSelectedUsers.mockReturnValue(of(users));
    fixture.detectChanges();
  }

  /** Returns the title of the last toast that has been raised. */
  function lastToastTitle(): string {
    return toastr.success.mock.lastCall![1] as string;
  }

  it('loads the first page of candidates on arrival', () => {
    start([userFixture({ id: 2 }), userFixture({ id: 3 })]);

    expect(selectService.getAllSelectedUsers).toHaveBeenCalledWith(0);
    expect(component.users.length).toBe(2);
    expect(component.initLoading).toBe(false);
    expect(component.loading).toBe(false);
  });

  it('stops the loader when the candidates cannot be read', () => {
    selectService.getAllSelectedUsers.mockReturnValue(
      throwError(() => new Error('offline')),
    );

    fixture.detectChanges();

    expect(component.initLoading).toBe(false);
    expect(component.loading).toBe(false);
  });

  it('keeps showing the empty state when nobody matches the criteria', () => {
    start([]);

    expect(component.users).toEqual([]);
    expect(
      fixture.nativeElement.querySelector('app-loading-card'),
    ).not.toBeNull();
  });

  it('drops the profile and congratulates the user on a match', () => {
    vi.useFakeTimers();
    const candidate = userFixture({ id: 2, nickname: 'Alice' });
    start([candidate]);
    selectService.addLike.mockReturnValue(of('Alice'));

    component.like(candidate);

    expect(component.activeMatchAnimation).toBe(true);
    expect(lastToastTitle()).toBe('Matched Alice');

    vi.advanceTimersByTime(3000);

    expect(component.activeMatchAnimation).toBe(false);
    expect(component.users).toEqual([]);
    expect(component.isLoading).toBe(false);
  });

  it('drops the profile without any animation when the like is not returned', () => {
    const candidate = userFixture({ id: 2, nickname: 'Alice' });
    start([candidate]);
    selectService.addLike.mockReturnValue(of(''));

    component.like(candidate);

    expect(component.activeMatchAnimation).toBe(false);
    expect(component.users).toEqual([]);
    expect(lastToastTitle()).toBe('Liked Alice');
  });

  it('drops the profile on a dislike', () => {
    const candidate = userFixture({ id: 2, nickname: 'Alice' });
    start([candidate]);
    selectService.addDislike.mockReturnValue(of(undefined));

    component.dislike(candidate);

    expect(component.users).toEqual([]);
    expect(component.isLoading).toBe(false);
    expect(lastToastTitle()).toBe('Disliked Alice');
  });

  it('leaves the deck untouched when the answered profile is already gone', () => {
    start([userFixture({ id: 2 })]);

    component.removeSlide(99);

    expect(component.users.length).toBe(1);
  });

  it('sends the user to their profile to widen the criteria', () => {
    start([]);

    component.goTo('profile');

    expect(router.navigate).toHaveBeenCalledWith(['/profile']);
  });

  it('stays on the selection screen when going back to the first profile', () => {
    start([]);

    component.goTo('first');

    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('ignores a slide change while the carousel is not mounted', () => {
    start([userFixture({ id: 2 })]);

    component.onSlideChange();

    expect(selectService.getAllSelectedUsers).toHaveBeenCalledTimes(1);
  });
});
