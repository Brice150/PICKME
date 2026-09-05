import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { NavButtonsComponent } from './nav-buttons.component';

describe('NavButtonsComponent', () => {
  let fixture: ComponentFixture<NavButtonsComponent>;
  let component: NavButtonsComponent;
  let toggles: number;
  let logouts: number;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavButtonsComponent],
      providers: [provideRouter([])],
    }).compileComponents();
    fixture = TestBed.createComponent(NavButtonsComponent);
    component = fixture.componentInstance;
    toggles = 0;
    logouts = 0;
    component.toggleMenuEvent.subscribe(() => toggles++);
    component.logoutEvent.subscribe(() => logouts++);
  });

  it('hides the back office from a standard user', () => {
    fixture.componentRef.setInput('hasAdminRole', false);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('a[title="Admin"]')).toBeNull();
  });

  it('offers the back office to an administrator', () => {
    fixture.componentRef.setInput('hasAdminRole', true);
    fixture.detectChanges();

    expect(
      fixture.nativeElement.querySelector('a[title="Admin"]'),
    ).not.toBeNull();
  });

  it('asks the menu to close', () => {
    component.toggleMenu();

    expect(toggles).toBe(1);
  });

  it('asks for a logout', () => {
    component.logout();

    expect(logouts).toBe(1);
  });
});
