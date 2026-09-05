import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Notification } from '../../core/interfaces/notification';
import { NotificationsComponent } from './notifications.component';

describe('NotificationsComponent', () => {
  const notifications: Notification[] = [
    {
      id: 1,
      content: 'New match with Alice',
      link: 'match',
      date: new Date(),
      seen: false,
    },
    {
      id: 2,
      content: 'Bob decided to unmatch',
      link: 'unmatch',
      date: new Date(),
      seen: true,
    },
  ];
  let fixture: ComponentFixture<NotificationsComponent>;
  let component: NotificationsComponent;
  let goTos: number;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotificationsComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(NotificationsComponent);
    component = fixture.componentInstance;
    goTos = 0;
    component.goToEvent.subscribe(() => goTos++);
  });

  it('lists every notification of the menu', () => {
    fixture.componentRef.setInput('notifications', notifications);
    fixture.detectChanges();

    expect(
      fixture.nativeElement.querySelectorAll('app-notification').length,
    ).toBe(2);
    expect(fixture.nativeElement.textContent).toContain('New match with Alice');
  });

  it('shows nothing when there is no notification', () => {
    fixture.componentRef.setInput('notifications', []);
    fixture.detectChanges();

    expect(
      fixture.nativeElement.querySelectorAll('app-notification').length,
    ).toBe(0);
  });

  it('opens the conversations when a notification is clicked', () => {
    fixture.componentRef.setInput('notifications', notifications);
    fixture.detectChanges();

    fixture.nativeElement.querySelector('app-notification').click();

    expect(goTos).toBe(1);
  });
});
