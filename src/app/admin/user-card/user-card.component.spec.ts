import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { of } from 'rxjs';
import { userFixture } from '../../core/testing/user.fixture';
import { MoreInfoComponent } from '../../shared/components/more-info/more-info.component';
import { UserCardComponent } from './user-card.component';

describe('UserCardComponent', () => {
  const user = userFixture({ id: 2, nickname: 'Alice' });
  let fixture: ComponentFixture<UserCardComponent>;
  let component: UserCardComponent;
  let dialog: jasmine.SpyObj<MatDialog>;
  let deletions: number;

  /** Makes the profile sheet close with the answer of the administrator. */
  function answerMoreInfo(deleted: boolean): void {
    dialog.open.and.returnValue({
      afterClosed: () => of(deleted),
    } as MatDialogRef<MoreInfoComponent>);
  }

  beforeEach(async () => {
    dialog = jasmine.createSpyObj<MatDialog>('MatDialog', ['open']);
    await TestBed.configureTestingModule({
      imports: [UserCardComponent],
      providers: [{ provide: MatDialog, useValue: dialog }],
    }).compileComponents();
    fixture = TestBed.createComponent(UserCardComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('user', user);
    fixture.detectChanges();
    deletions = 0;
    component.deleteEvent.subscribe(() => deletions++);
  });

  it('identifies the account by its email', () => {
    expect(fixture.nativeElement.textContent).toContain(user.email);
    expect(fixture.nativeElement.querySelector('img').alt).toBe('Alice');
  });

  it('falls back on a placeholder for an account without a picture', () => {
    expect(fixture.nativeElement.querySelector('img').src).toContain(
      'No-Image.png',
    );
  });

  it('shows the main picture of an account that has one', () => {
    fixture.componentRef.setInput(
      'user',
      userFixture({
        id: 2,
        pictures: [
          { id: 1, content: 'data:image/jpeg;base64,x', isMainPicture: true },
        ],
      }),
    );
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('img').src).toContain(
      'data:image/jpeg;base64,x',
    );
  });

  it('opens the profile sheet with the powers of an administrator', () => {
    answerMoreInfo(false);

    component.moreInfo();

    expect(dialog.open).toHaveBeenCalledWith(MoreInfoComponent, {
      data: { user, adminMode: true, matchMode: false },
    });
    expect(deletions).toBe(0);
  });

  it('reports the deletion decided from the profile sheet', () => {
    answerMoreInfo(true);

    component.moreInfo();

    expect(deletions).toBe(1);
  });
});
