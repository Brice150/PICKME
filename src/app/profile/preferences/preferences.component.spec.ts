import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { AlcoholDrinking } from '../../core/enums/alcohol-drinking';
import { ConnectService } from '../../core/services/connect.service';
import { userFixture } from '../../core/testing/user.fixture';
import { PreferencesComponent } from './preferences.component';

describe('PreferencesComponent', () => {
  let fixture: ComponentFixture<PreferencesComponent>;
  let component: PreferencesComponent;
  let updates: string[];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreferencesComponent],
      providers: [
        provideNoopAnimations(),
        {
          provide: ConnectService,
          useValue: { connectedUser: signal(userFixture()) },
        },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(PreferencesComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('user', userFixture({ preferences: {} }));
    fixture.detectChanges();
    updates = [];
    component.updateEvent.subscribe((message) => updates.push(message));
  });

  it('walks the user through the eight questions, one at a time', () => {
    expect(component.preferences.length).toBe(8);
    expect(component.currentPreferenceIndex()).toBe(0);
    expect(
      fixture.nativeElement.querySelectorAll('app-preference').length,
    ).toBe(1);
  });

  it('offers every answer of the question it displays', () => {
    expect(component.preferences[0].name).toBe('alcoholDrinking');
    expect(component.preferences[0].elements).toEqual([
      AlcoholDrinking.NO,
      AlcoholDrinking.MAYBE,
      AlcoholDrinking.YES,
    ]);
  });

  it('moves to the question the paginator asks for', () => {
    component.handlePageEvent(3);

    expect(component.currentPreferenceIndex()).toBe(3);
  });

  it('drops the pending answer when the user moves to another question', () => {
    component.preference()!.select(AlcoholDrinking.YES, 'alcoholDrinking');
    component.preference()!.preferenceForm.markAsDirty();
    const previousQuestion = component.preference()!;

    component.handlePageEvent(1);

    expect(previousQuestion.preferenceForm.pristine).toBe(true);
  });

  it('asks for a save when an answer is confirmed', () => {
    component.updatePreferences();

    expect(updates).toEqual(['Preferences Updated']);
  });
});
