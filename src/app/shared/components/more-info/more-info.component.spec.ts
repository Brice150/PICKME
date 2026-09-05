import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { userFixture } from '../../../core/testing/user.fixture';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';
import { MoreInfoComponent } from './more-info.component';

describe('MoreInfoComponent', () => {
  const user = userFixture({
    pictures: [{ id: 1, content: 'base64', isMainPicture: true }],
    preferences: {},
  });
  let dialogRef: jasmine.SpyObj<MatDialogRef<MoreInfoComponent>>;
  let dialog: jasmine.SpyObj<MatDialog>;

  /** Opens the profile sheet in one of the three modes the application uses. */
  async function build(
    mode: { adminMode?: boolean; matchMode?: boolean } = {},
  ): Promise<ComponentFixture<MoreInfoComponent>> {
    dialogRef = jasmine.createSpyObj<MatDialogRef<MoreInfoComponent>>(
      'MatDialogRef',
      ['close'],
    );
    dialog = jasmine.createSpyObj<MatDialog>('MatDialog', ['open']);
    await TestBed.configureTestingModule({
      imports: [MoreInfoComponent],
      providers: [
        provideNoopAnimations(),
        { provide: MatDialogRef, useValue: dialogRef },
        { provide: MatDialog, useValue: dialog },
        {
          provide: MAT_DIALOG_DATA,
          useValue: { user, adminMode: false, matchMode: false, ...mode },
        },
      ],
    }).compileComponents();
    const fixture = TestBed.createComponent(MoreInfoComponent);
    fixture.detectChanges();
    return fixture;
  }

  /** Makes the confirmation dialog answer with the choice of the user. */
  function answerConfirmation(confirmed: boolean): void {
    dialog.open.and.returnValue({
      afterClosed: () => of(confirmed),
    } as MatDialogRef<ConfirmationDialogComponent>);
  }

  it('displays the profile it has been opened on', async () => {
    const fixture = await build();

    expect(fixture.componentInstance.user).toBe(user);
    expect(fixture.nativeElement.textContent).toContain(user.nickname);
  });

  it('closes without an answer', async () => {
    const fixture = await build();

    fixture.componentInstance.close();

    expect(dialogRef.close).toHaveBeenCalledWith();
  });

  it('reports a like to the screen that opened it', async () => {
    const fixture = await build();

    fixture.componentInstance.like();

    expect(dialogRef.close).toHaveBeenCalledWith('like');
  });

  it('reports a dislike straight away on the selection screen', async () => {
    const fixture = await build({ matchMode: false });

    fixture.componentInstance.dislike();

    expect(dialogRef.close).toHaveBeenCalledWith('dislike');
    expect(dialog.open).not.toHaveBeenCalled();
  });

  it('asks for a confirmation before unmatching a profile', async () => {
    const fixture = await build({ matchMode: true });
    answerConfirmation(true);

    fixture.componentInstance.dislike();

    expect(dialog.open).toHaveBeenCalledWith(ConfirmationDialogComponent, {
      data: 'dislike ' + user.nickname,
    });
    expect(dialogRef.close).toHaveBeenCalledWith(true);
  });

  it('keeps the match when the unmatch is not confirmed', async () => {
    const fixture = await build({ matchMode: true });
    answerConfirmation(false);

    fixture.componentInstance.dislike();

    expect(dialogRef.close).not.toHaveBeenCalled();
  });

  it('asks for a confirmation before an administrator deletes the account', async () => {
    const fixture = await build({ adminMode: true });
    answerConfirmation(true);

    fixture.componentInstance.deleteUser();

    expect(dialog.open).toHaveBeenCalledWith(ConfirmationDialogComponent, {
      data: 'delete this user',
    });
    expect(dialogRef.close).toHaveBeenCalledWith(true);
  });

  it('keeps the account when the deletion is not confirmed', async () => {
    const fixture = await build({ adminMode: true });
    answerConfirmation(false);

    fixture.componentInstance.deleteUser();

    expect(dialogRef.close).not.toHaveBeenCalled();
  });
});
