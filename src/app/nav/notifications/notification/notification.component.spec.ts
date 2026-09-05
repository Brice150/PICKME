import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Notification } from '../../../core/interfaces/notification';
import { NotificationComponent } from './notification.component';

describe('NotificationComponent', () => {
  let fixture: ComponentFixture<NotificationComponent>;

  /** Renders one notification of the menu. */
  function render(overrides: Partial<Notification> = {}): void {
    fixture.componentRef.setInput('notification', {
      id: 1,
      content: 'New match with Alice',
      link: 'match',
      date: new Date(),
      seen: false,
      ...overrides,
    });
    fixture.detectChanges();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotificationComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(NotificationComponent);
  });

  it('displays the content of the notification', () => {
    render();

    expect(fixture.nativeElement.textContent).toContain('New match with Alice');
  });

  it('highlights a notification that has not been seen yet', () => {
    render({ seen: false });

    expect(fixture.nativeElement.querySelector('.gold')).not.toBeNull();
  });

  it('tones down a notification already seen', () => {
    render({ seen: true });

    expect(fixture.nativeElement.querySelector('.gold')).toBeNull();
  });

  it('shows the hour alone for a notification of the day', () => {
    const today = new Date();
    today.setHours(9, 30, 0, 0);

    render({ date: today });

    expect(fixture.nativeElement.textContent).toContain('09:30');
  });
});
