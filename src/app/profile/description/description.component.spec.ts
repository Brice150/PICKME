import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { User } from '../../core/interfaces/user';
import { ConnectService } from '../../core/services/connect.service';
import { userFixture } from '../../core/testing/user.fixture';
import { DescriptionComponent } from './description.component';

describe('DescriptionComponent', () => {
  let fixture: ComponentFixture<DescriptionComponent>;
  let component: DescriptionComponent;
  let user: User;
  let updates: string[];

  beforeEach(async () => {
    user = userFixture({ description: 'saved description' });
    await TestBed.configureTestingModule({
      imports: [DescriptionComponent],
      providers: [
        provideNoopAnimations(),
        {
          provide: ConnectService,
          useValue: {
            connectedUser: userFixture({ description: 'saved description' }),
          },
        },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(DescriptionComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('user', user);
    fixture.detectChanges();
    updates = [];
    component.updateEvent.subscribe((message) => updates.push(message));
  });

  it('opens on the description already saved', () => {
    expect(component.descriptionForm.get('description')?.value).toBe(
      'saved description',
    );
  });

  it('rejects a description shorter than two characters', () => {
    component.descriptionForm.get('description')?.setValue('a');

    expect(component.descriptionForm.valid).toBe(false);
  });

  it('rejects a description longer than five hundred characters', () => {
    component.descriptionForm.get('description')?.setValue('a'.repeat(501));

    expect(component.descriptionForm.valid).toBe(false);
  });

  it('applies the new description and asks for a save', () => {
    component.descriptionForm.get('description')?.setValue('new description');
    component.descriptionForm.markAsDirty();

    component.updateDescription();

    expect(user.description).toBe('new description');
    expect(component.descriptionForm.pristine).toBe(true);
    expect(updates).toEqual(['Description Updated']);
  });

  it('restores the saved description when the edition is cancelled', () => {
    component.descriptionForm.get('description')?.setValue('new description');
    component.descriptionForm.markAsDirty();

    component.cancel();

    expect(user.description).toBe('saved description');
    expect(component.descriptionForm.get('description')?.value).toBe(
      'saved description',
    );
    expect(component.descriptionForm.pristine).toBe(true);
    expect(updates).toEqual([]);
  });
});
