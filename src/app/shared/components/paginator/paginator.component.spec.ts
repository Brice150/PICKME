import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PaginatorComponent } from './paginator.component';

describe('PaginatorComponent', () => {
  const maxPerPage = 5;
  let fixture: ComponentFixture<PaginatorComponent>;
  let component: PaginatorComponent;
  let emitted: number[];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaginatorComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(PaginatorComponent);
    component = fixture.componentInstance;
    emitted = [];
    component.handlePageEvent.subscribe((page) => emitted.push(page));
  });

  /** Puts the paginator in the state it has once a full page has been loaded. */
  function fullPage(overrides: Record<string, unknown> = {}): void {
    fixture.componentRef.setInput('loading', false);
    fixture.componentRef.setInput('usersNumber', maxPerPage);
    fixture.componentRef.setInput('maxPerPage', maxPerPage);
    Object.entries(overrides).forEach(([input, value]) =>
      fixture.componentRef.setInput(input, value),
    );
    fixture.detectChanges();
  }

  it('moves to the next page when the current one is full', () => {
    fullPage();

    component.next();

    expect(component.page).toBe(1);
    expect(emitted).toEqual([1]);
  });

  it('stays on the last page when it is not full', () => {
    fullPage({ usersNumber: maxPerPage - 1 });

    component.next();

    expect(component.page).toBe(0);
    expect(emitted).toEqual([]);
  });

  it('does not move while a page is still loading', () => {
    fullPage({ loading: true });

    component.next();

    expect(component.page).toBe(0);
    expect(emitted).toEqual([]);
  });

  it('stops on the last page of a bounded pagination', () => {
    fullPage({ maxPages: 2 });
    component.next();
    expect(component.page).toBe(1);

    component.next();

    expect(component.page).toBe(1);
    expect(emitted).toEqual([1]);
  });

  it('goes back to the previous page', () => {
    fullPage();
    component.next();

    component.previous();

    expect(component.page).toBe(0);
    expect(emitted).toEqual([1, 0]);
  });

  it('never goes before the first page', () => {
    fullPage();

    component.previous();

    expect(component.page).toBe(0);
    expect(emitted).toEqual([]);
  });

  it('stays hidden while the first page is loading', () => {
    fixture.componentRef.setInput('loading', true);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.container')).toBeNull();
  });

  it('displays the page number and the number of results', () => {
    fullPage();

    expect(fixture.nativeElement.querySelector('span').textContent).toContain(
      'Page : 1 (5)',
    );
  });

  it('displays the total number of pages of a bounded pagination', () => {
    fullPage({ maxPages: 4 });

    expect(fixture.nativeElement.querySelector('span').textContent).toContain(
      'Page : 1 / 4 (5)',
    );
  });
});
