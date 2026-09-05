import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PaginatorDemoComponent } from './paginator-demo.component';

describe('PaginatorDemoComponent', () => {
  let fixture: ComponentFixture<PaginatorDemoComponent>;
  let component: PaginatorDemoComponent;
  let nexts: number;
  let previouses: number;

  /** Puts the paginator on one of the four steps of the demonstration. */
  function onStep(currentIndex: number, listLength = 4): void {
    fixture.componentRef.setInput('currentIndex', currentIndex);
    fixture.componentRef.setInput('listLength', listLength);
    fixture.detectChanges();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaginatorDemoComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(PaginatorDemoComponent);
    component = fixture.componentInstance;
    nexts = 0;
    previouses = 0;
    component.nextEvent.subscribe(() => nexts++);
    component.previousEvent.subscribe(() => previouses++);
  });

  it('moves forward from any step but the last', () => {
    onStep(1);

    component.next();

    expect(nexts).toBe(1);
  });

  it('stops on the last step', () => {
    onStep(3);

    component.next();

    expect(nexts).toBe(0);
  });

  it('moves back from any step but the first', () => {
    onStep(1);

    component.previous();

    expect(previouses).toBe(1);
  });

  it('stops on the first step', () => {
    onStep(0);

    component.previous();

    expect(previouses).toBe(0);
  });
});
