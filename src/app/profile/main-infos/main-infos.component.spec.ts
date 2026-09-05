import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { User } from '../../core/interfaces/user';
import { ConnectService } from '../../core/services/connect.service';
import { userFixture } from '../../core/testing/user.fixture';
import { MainInfosComponent } from './main-infos.component';

describe('MainInfosComponent', () => {
  let fixture: ComponentFixture<MainInfosComponent>;
  let component: MainInfosComponent;
  let user: User;
  let updates: string[];

  beforeEach(async () => {
    user = userFixture({ nickname: 'Alice', job: 'Architecte', height: 170 });
    await TestBed.configureTestingModule({
      imports: [MainInfosComponent],
      providers: [
        provideNoopAnimations(),
        {
          provide: ConnectService,
          useValue: {
            connectedUser: userFixture({
              nickname: 'Alice',
              job: 'Architecte',
              height: 170,
            }),
          },
        },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(MainInfosComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('user', user);
    fixture.detectChanges();
    updates = [];
    component.updateEvent.subscribe((message) => updates.push(message));
  });

  it('opens on the identity already saved', () => {
    expect(component.mainInfosForm.value).toEqual({
      nickname: 'Alice',
      job: 'Architecte',
      distanceSearch: 100,
      height: 170,
    });
  });

  it('requires a nickname and a job', () => {
    component.mainInfosForm.patchValue({ nickname: '', job: '' });

    expect(component.mainInfosForm.valid).toBeFalse();
  });

  it('rejects a nickname longer than thirty characters', () => {
    component.mainInfosForm.get('nickname')?.setValue('a'.repeat(31));

    expect(component.mainInfosForm.valid).toBeFalse();
  });

  it('applies the whole form and asks for a save', () => {
    component.mainInfosForm.patchValue({
      nickname: 'Alicia',
      job: 'Ingénieure',
      distanceSearch: 30,
      height: 165,
    });
    component.mainInfosForm.markAsDirty();

    component.updateMainInfos();

    expect(user.nickname).toBe('Alicia');
    expect(user.job).toBe('Ingénieure');
    expect(user.geolocation.distanceSearch).toBe(30);
    expect(user.height).toBe(165);
    expect(component.mainInfosForm.pristine).toBeTrue();
    expect(updates).toEqual(['Main Infos Updated']);
  });

  it('restores the saved identity when the edition is cancelled', () => {
    component.mainInfosForm.patchValue({ nickname: 'Alicia', job: 'Autre' });
    component.mainInfosForm.markAsDirty();

    component.cancel();

    expect(user.nickname).toBe('Alice');
    expect(user.job).toBe('Architecte');
    expect(component.mainInfosForm.get('nickname')?.value).toBe('Alice');
    expect(component.mainInfosForm.pristine).toBeTrue();
    expect(updates).toEqual([]);
  });
});
