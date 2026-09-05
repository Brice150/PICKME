import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { Gender } from '../../core/enums/gender';
import { ProfileDemoComponent } from './profile-demo.component';

describe('ProfileDemoComponent', () => {
  let fixture: ComponentFixture<ProfileDemoComponent>;
  let component: ProfileDemoComponent;

  function render(gender: Gender): void {
    fixture.componentRef.setInput('userGender', gender);
    fixture.detectChanges();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProfileDemoComponent],
      providers: [provideNoopAnimations()],
    }).compileComponents();
    fixture = TestBed.createComponent(ProfileDemoComponent);
    component = fixture.componentInstance;
  });

  it('shows a sample man profile to a man', () => {
    render(Gender.MAN);

    expect(component.imagePath).toBe('./assets/images/man-profile-demo/');
  });

  it('shows a sample woman profile to a woman', () => {
    render(Gender.WOMAN);

    expect(component.imagePath).toBe('./assets/images/woman-profile-demo/');
  });

  it('keeps the neutral samples for any other gender', () => {
    render(Gender.OTHER);

    expect(component.imagePath).toBe('./assets/images/');
  });
});
