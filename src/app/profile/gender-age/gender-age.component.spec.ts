import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { Gender } from '../../core/enums/gender';
import { User } from '../../core/interfaces/user';
import { ConnectService } from '../../core/services/connect.service';
import { userFixture } from '../../core/testing/user.fixture';
import { GenderAgeComponent } from './gender-age.component';

describe('GenderAgeComponent', () => {
  let fixture: ComponentFixture<GenderAgeComponent>;
  let component: GenderAgeComponent;
  let user: User;
  let updates: string[];

  beforeEach(async () => {
    user = userFixture();
    await TestBed.configureTestingModule({
      imports: [GenderAgeComponent],
      providers: [
        provideNoopAnimations(),
        { provide: ConnectService, useValue: { connectedUser: userFixture() } },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(GenderAgeComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('user', user);
    fixture.detectChanges();
    updates = [];
    component.updateEvent.subscribe((message) => updates.push(message));
  });

  it('offers every gender the application knows', () => {
    expect(component.genders).toEqual(['Man', 'Woman', 'Other']);
  });

  it('opens on the criteria already saved', () => {
    expect(component.genderAgeForm.value).toEqual({
      gender: Gender.MAN,
      genderSearch: Gender.WOMAN,
      minAge: 18,
      maxAge: 99,
    });
  });

  it('requires every criterion', () => {
    component.genderAgeForm.patchValue({ gender: null, minAge: null });

    expect(component.genderAgeForm.valid).toBeFalse();
  });

  it('applies the new criteria and asks for a save', () => {
    component.genderAgeForm.patchValue({
      gender: Gender.WOMAN,
      genderSearch: Gender.MAN,
      minAge: 25,
      maxAge: 40,
    });
    component.genderAgeForm.markAsDirty();

    component.updateGenderAge();

    expect(user.genderAge).toEqual({
      gender: Gender.WOMAN,
      genderSearch: Gender.MAN,
      minAge: 25,
      maxAge: 40,
    });
    expect(component.genderAgeForm.pristine).toBeTrue();
    expect(updates).toEqual(['Gender and Age Updated']);
  });

  it('restores the saved criteria when the edition is cancelled', () => {
    component.genderAgeForm.patchValue({ minAge: 25, maxAge: 40 });
    component.genderAgeForm.markAsDirty();

    component.cancel();

    expect(user.genderAge.minAge).toBe(18);
    expect(user.genderAge.maxAge).toBe(99);
    expect(component.genderAgeForm.get('minAge')?.value).toBe(18);
    expect(component.genderAgeForm.pristine).toBeTrue();
    expect(updates).toEqual([]);
  });
});
