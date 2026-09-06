import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { User } from '../../core/interfaces/user';
import { ConnectService } from '../../core/services/connect.service';
import { userFixture } from '../../core/testing/user.fixture';
import { PasswordComponent } from './password.component';

describe('PasswordComponent', () => {
  let fixture: ComponentFixture<PasswordComponent>;
  let component: PasswordComponent;
  let user: User;
  let updates: string[];

  /** Types the two fields of the form the way the user would. */
  function type(password: string, duplicate: string): void {
    component.passwordForm.patchValue({
      password,
      passwordDuplicate: duplicate,
    });
    component.passwordForm.markAsDirty();
  }

  beforeEach(async () => {
    user = userFixture();
    await TestBed.configureTestingModule({
      imports: [PasswordComponent],
      providers: [
        provideNoopAnimations(),
        {
          provide: ConnectService,
          useValue: { connectedUser: signal(userFixture()) },
        },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(PasswordComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('user', user);
    fixture.detectChanges();
    updates = [];
    component.updateEvent.subscribe((message) => updates.push(message));
  });

  it('opens on an empty form, hiding both fields', () => {
    expect(component.passwordForm.value).toEqual({
      password: '',
      passwordDuplicate: '',
    });
    expect(component.hide()).toBe(true);
    expect(component.hideDuplicate()).toBe(true);
  });

  it('requires a password of at least five characters', () => {
    type('abc', 'abc');

    expect(component.passwordForm.get('password')?.valid).toBe(false);
  });

  it('rejects a password longer than thirty characters', () => {
    type('a'.repeat(31), 'a'.repeat(31));

    expect(component.passwordForm.get('password')?.valid).toBe(false);
  });

  it('rejects two passwords that do not match', () => {
    type('password', 'different');

    expect(
      component.passwordForm
        .get('passwordDuplicate')
        ?.hasError('passwordMismatch'),
    ).toBe(true);
  });

  it('accepts two matching passwords', () => {
    type('password', 'password');

    expect(component.passwordForm.valid).toBe(true);
  });

  it('applies the new password and asks for a save', () => {
    type('newPassword', 'newPassword');

    component.updateConnectionInfos();

    expect(user.password).toBe('newPassword');
    expect(component.passwordForm.pristine).toBe(true);
    expect(updates).toEqual(['Connection Infos Updated']);
  });

  it('empties the form when the edition is cancelled', () => {
    type('newPassword', 'newPassword');

    component.cancel();

    expect(component.passwordForm.value).toEqual({
      password: null,
      passwordDuplicate: null,
    });
    expect(component.passwordForm.pristine).toBe(true);
    expect(updates).toEqual([]);
  });
});
