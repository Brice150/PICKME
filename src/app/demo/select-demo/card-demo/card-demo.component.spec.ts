import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { CardDemoComponent } from './card-demo.component';

describe('CardDemoComponent', () => {
  let fixture: ComponentFixture<CardDemoComponent>;
  let component: CardDemoComponent;
  let likes: number;
  let dislikes: number;

  /** Renders the demonstration card of one of the three sample profiles. */
  function render(image: string): void {
    fixture.componentRef.setInput('image', image);
    fixture.componentRef.setInput('display', true);
    fixture.detectChanges();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CardDemoComponent],
      providers: [provideNoopAnimations()],
    }).compileComponents();
    fixture = TestBed.createComponent(CardDemoComponent);
    component = fixture.componentInstance;
    likes = 0;
    dislikes = 0;
    component.likeEvent.subscribe(() => likes++);
    component.dislikeEvent.subscribe(() => dislikes++);
  });

  it('answers a like straight away on a profile that does not match', () => {
    render('./assets/images/woman-select-demo/Picture1.jpg');

    component.like();

    expect(component.activeMatchAnimation).toBe(false);
    expect(likes).toBe(1);
  });

  it('plays the match animation on the profile scripted to match', () => {
    vi.useFakeTimers();
    render('./assets/images/woman-select-demo/Picture2.jpg');

    component.like();

    expect(component.activeMatchAnimation).toBe(true);
    expect(likes).toBe(0);

    vi.advanceTimersByTime(2000);

    expect(component.activeMatchAnimation).toBe(false);
    expect(likes).toBe(1);
  });

  it('answers a dislike straight away', () => {
    render('./assets/images/woman-select-demo/Picture2.jpg');

    component.dislike();

    expect(dislikes).toBe(1);
    expect(component.activeMatchAnimation).toBe(false);
  });
});
