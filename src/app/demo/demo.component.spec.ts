import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { createSpyObj, SpyObj } from '../../testing/spy';
import { Gender } from '../core/enums/gender';
import { ConnectService } from '../core/services/connect.service';
import { userFixture } from '../core/testing/user.fixture';
import { DemoComponent } from './demo.component';

describe('DemoComponent', () => {
  let fixture: ComponentFixture<DemoComponent>;
  let component: DemoComponent;
  let connectService: SpyObj<ConnectService>;

  /** Builds the screen for a visitor who just registered, or for one who did not. */
  async function build(registered: boolean): Promise<void> {
    connectService = {
      registeredUser: signal(
        registered
          ? userFixture({
              genderAge: {
                gender: Gender.WOMAN,
                genderSearch: Gender.MAN,
                minAge: 18,
                maxAge: 99,
              },
            })
          : undefined,
      ),
      login: vi.fn(),
    } as unknown as SpyObj<ConnectService>;
    TestBed.resetTestingModule();
    await TestBed.configureTestingModule({
      imports: [DemoComponent],
      providers: [
        provideNoopAnimations(),
        { provide: ConnectService, useValue: connectService },
        {
          provide: Router,
          useValue: createSpyObj<Router>(['navigate']),
        },
        {
          provide: ToastrService,
          useValue: createSpyObj<ToastrService>(['success']),
        },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(DemoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('walks the visitor through the four steps, starting on the first', async () => {
    await build(false);

    expect(component.demos).toEqual(['profile', 'select', 'match', 'start']);
    expect(component.currentIndex()).toBe(0);
  });

  it('tailors the samples to the account that has just been created', async () => {
    await build(true);

    expect(component.userGender()).toBe(Gender.WOMAN);
    expect(component.userGenderSearch()).toBe(Gender.MAN);
  });

  it('falls back on neutral samples for a visitor who did not register', async () => {
    await build(false);

    expect(component.userGender()).toBe(Gender.MAN);
    expect(component.userGenderSearch()).toBe(Gender.WOMAN);
  });

  it('moves forward through the steps', async () => {
    await build(false);

    component.next();

    expect(component.currentIndex()).toBe(1);
    expect(component.animationDirection).toBe('next');
  });

  it('stops on the last step', async () => {
    await build(false);
    component.currentIndex.set(3);

    component.next();

    expect(component.currentIndex()).toBe(3);
  });

  it('moves back through the steps', async () => {
    await build(false);
    component.next();

    component.previous();

    expect(component.currentIndex()).toBe(0);
    expect(component.animationDirection).toBe('previous');
  });

  it('stops on the first step', async () => {
    await build(false);

    component.previous();

    expect(component.currentIndex()).toBe(0);
  });
});
