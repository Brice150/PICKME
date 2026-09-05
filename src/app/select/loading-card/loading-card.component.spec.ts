import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoadingCardComponent } from './loading-card.component';

describe('LoadingCardComponent', () => {
  let fixture: ComponentFixture<LoadingCardComponent>;
  let component: LoadingCardComponent;
  let emitted: string[];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoadingCardComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(LoadingCardComponent);
    component = fixture.componentInstance;
    emitted = [];
    component.goToActionEvent.subscribe((action) => emitted.push(action));
  });

  it('shows the spinner while the candidates are loading', () => {
    fixture.componentRef.setInput('loading', true);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('app-loading')).not.toBeNull();
  });

  it('offers to go back to the first profile once the deck is exhausted', () => {
    fixture.componentRef.setInput('loading', false);
    fixture.componentRef.setInput('usersNumber', 12);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain(
      'No more users to show',
    );
    fixture.nativeElement.querySelector('a').click();

    expect(emitted).toEqual(['first']);
  });

  it('sends the user widen their criteria when no profile matches at all', () => {
    fixture.componentRef.setInput('loading', false);
    fixture.componentRef.setInput('usersNumber', 0);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('No user to show');
    fixture.nativeElement.querySelector('a').click();

    expect(emitted).toEqual(['profile']);
  });
});
