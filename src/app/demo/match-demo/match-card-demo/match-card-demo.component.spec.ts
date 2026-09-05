import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatchCardDemoComponent } from './match-card-demo.component';

describe('MatchCardDemoComponent', () => {
  let fixture: ComponentFixture<MatchCardDemoComponent>;
  let component: MatchCardDemoComponent;
  let clicks: number;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatchCardDemoComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(MatchCardDemoComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('picture', './assets/images/Picture1.jpg');
    fixture.componentRef.setInput('match', 'Alice');
    fixture.componentRef.setInput('preview', 'see you tomorrow');
    fixture.detectChanges();
    clicks = 0;
    component.clickEvent.subscribe(() => clicks++);
  });

  it('displays the match and the preview of the conversation', () => {
    expect(fixture.nativeElement.textContent).toContain('Alice');
    expect(fixture.nativeElement.textContent).toContain('see you tomorrow');
  });

  it('shows the picture of the match', () => {
    expect(fixture.nativeElement.querySelector('img').src).toContain(
      'Picture1.jpg',
    );
  });

  it('reports the click that opens the conversation', () => {
    component.click();

    expect(clicks).toBe(1);
  });
});
