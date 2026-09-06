import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { Gender } from '../../core/enums/gender';
import { SelectDemoComponent } from './select-demo.component';

describe('SelectDemoComponent', () => {
  let fixture: ComponentFixture<SelectDemoComponent>;
  let component: SelectDemoComponent;

  /** Renders the demonstration for someone looking for that gender. */
  function render(genderSearch: Gender): void {
    fixture.componentRef.setInput('userGenderSearch', genderSearch);
    fixture.detectChanges();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectDemoComponent],
      providers: [provideNoopAnimations()],
    }).compileComponents();
    fixture = TestBed.createComponent(SelectDemoComponent);
    component = fixture.componentInstance;
  });

  it('shows the sample women to someone looking for a woman', () => {
    render(Gender.WOMAN);

    expect(component.images().length).toBe(3);
    expect(component.images()[0]).toContain('woman-select-demo/Picture1.jpg');
  });

  it('shows the sample men to someone looking for a man', () => {
    render(Gender.MAN);

    expect(component.images()[0]).toContain('man-select-demo/Picture1.jpg');
  });

  it('keeps a neutral sample for any other search', () => {
    render(Gender.OTHER);

    expect(component.images()[0]).toBe('./assets/images/Picture1.jpg');
  });

  it('drops the profile that has been liked', () => {
    render(Gender.WOMAN);
    const liked = component.images()[1];

    component.like(liked);

    expect(component.images()).not.toContain(liked);
    expect(component.images().length).toBe(2);
  });

  it('drops the profile that has been disliked', () => {
    render(Gender.WOMAN);
    const disliked = component.images()[0];

    component.dislike(disliked);

    expect(component.images()).not.toContain(disliked);
  });

  it('leaves the deck untouched when the profile is already gone', () => {
    render(Gender.WOMAN);

    component.removeSlide('unknown.jpg');

    expect(component.images().length).toBe(3);
  });

  it('highlights no card while the carousel is not mounted', () => {
    render(Gender.WOMAN);

    expect(component.isCurrentView(component.images()[0])).toBe(false);
  });
});
