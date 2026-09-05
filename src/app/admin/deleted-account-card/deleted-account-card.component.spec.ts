import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DeletedAccount } from '../../core/interfaces/deleted-account';
import { DeletedAccountCardComponent } from './deleted-account-card.component';

describe('DeletedAccountCardComponent', () => {
  let fixture: ComponentFixture<DeletedAccountCardComponent>;

  /** Renders an archived account of the back office. */
  function render(overrides: Partial<DeletedAccount> = {}): void {
    fixture.componentRef.setInput('deletedAccount', {
      nickname: 'Alice',
      email: 'alice@pickme.com',
      deletionDate: new Date(2024, 2, 9),
      totalDislikes: 3,
      totalLikes: 5,
      totalMatches: 2,
      deletedBy: 'User',
      ...overrides,
    });
    fixture.detectChanges();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeletedAccountCardComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(DeletedAccountCardComponent);
  });

  it('identifies the archived account by its email', () => {
    render();

    expect(fixture.nativeElement.textContent).toContain('alice@pickme.com');
    expect(fixture.nativeElement.querySelector('img').alt).toBe('Alice');
  });

  it('truncates an email too long for the card', () => {
    render({ email: 'a-very-long-address@pickme.com' });

    expect(fixture.nativeElement.textContent).toContain('a-very-long-addr...');
  });

  it('displays the statistics the account left behind', () => {
    render({ totalLikes: 5, totalDislikes: 3, totalMatches: 2 });

    const text: string = fixture.nativeElement.textContent;
    expect(text).toContain('5');
    expect(text).toContain('3');
    expect(text).toContain('2');
  });

  it('says who closed the account and when', () => {
    render({ deletedBy: 'Admin' });

    expect(fixture.nativeElement.querySelector('.container').title).toBe(
      'Deleted By Admin',
    );
    expect(fixture.nativeElement.textContent).toContain('09/03/2024');
  });
});
