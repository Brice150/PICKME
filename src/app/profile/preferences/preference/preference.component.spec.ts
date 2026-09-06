import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { Smokes } from '../../../core/enums/smokes';
import { Preference } from '../../../core/interfaces/preference';
import { User } from '../../../core/interfaces/user';
import { ConnectService } from '../../../core/services/connect.service';
import { userFixture } from '../../../core/testing/user.fixture';
import { PreferenceComponent } from './preference.component';

describe('PreferenceComponent', () => {
  const smokingPreference: Preference = {
    title: 'Smoking',
    name: 'smokes',
    elements: Object.values(Smokes),
    class: 'bx bx-wind',
  };
  let fixture: ComponentFixture<PreferenceComponent>;
  let component: PreferenceComponent;
  let user: User;
  let updates: number;

  beforeEach(async () => {
    user = userFixture({ preferences: { smokes: Smokes.NO } });
    await TestBed.configureTestingModule({
      imports: [PreferenceComponent],
      providers: [
        provideNoopAnimations(),
        {
          provide: ConnectService,
          useValue: {
            connectedUser: signal(
              userFixture({ preferences: { smokes: Smokes.NO } }),
            ),
          },
        },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(PreferenceComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('preference', smokingPreference);
    fixture.componentRef.setInput('user', user);
    fixture.detectChanges();
    updates = 0;
    component.updateEvent.subscribe(() => updates++);
  });

  it('opens on the answer already saved', () => {
    expect(component.preferenceForm.value).toEqual({ smokes: Smokes.NO });
    expect(component.initialValue).toBe(Smokes.NO);
  });

  it('tells which answer is currently selected', () => {
    expect(component.isSelected(Smokes.NO, 'smokes')).toBe(true);
    expect(component.isSelected(Smokes.YES, 'smokes')).toBe(false);
  });

  it('applies the answer the user picks', () => {
    component.select(Smokes.YES, 'smokes');

    expect(user.preferences?.smokes).toBe(Smokes.YES);
    expect(component.isSelected(Smokes.YES, 'smokes')).toBe(true);
  });

  it('asks for a save once an answer has been picked', () => {
    component.select(Smokes.YES, 'smokes');
    component.preferenceForm.markAsDirty();

    component.updatePreferences();

    expect(updates).toBe(1);
    expect(component.preferenceForm.pristine).toBe(true);
  });

  it('restores the saved answer when the edition is cancelled', () => {
    component.select(Smokes.YES, 'smokes');
    component.preferenceForm.markAsDirty();

    component.cancel();

    expect(user.preferences?.smokes).toBe(Smokes.NO);
    expect(component.preferenceForm.value).toEqual({ smokes: Smokes.NO });
    expect(component.preferenceForm.pristine).toBe(true);
  });

  it('handles a profile that never answered that question', () => {
    fixture.componentRef.setInput('user', userFixture({ preferences: {} }));
    fixture.detectChanges();

    expect(component.isSelected(Smokes.NO, 'smokes')).toBe(false);
  });
});
