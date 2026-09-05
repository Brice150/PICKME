import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { of } from 'rxjs';
import { userFixture } from '../../core/testing/user.fixture';
import { ConfirmationDialogComponent } from '../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { DeleteAccountComponent } from './delete-account.component';

describe('DeleteAccountComponent', () => {
  let fixture: ComponentFixture<DeleteAccountComponent>;
  let component: DeleteAccountComponent;
  let dialog: jasmine.SpyObj<MatDialog>;
  let deletions: number;

  /** Makes the confirmation dialog answer with the choice of the user. */
  function answerConfirmation(confirmed: boolean): void {
    dialog.open.and.returnValue({
      afterClosed: () => of(confirmed),
    } as MatDialogRef<ConfirmationDialogComponent>);
  }

  beforeEach(async () => {
    dialog = jasmine.createSpyObj<MatDialog>('MatDialog', ['open']);
    await TestBed.configureTestingModule({
      imports: [DeleteAccountComponent],
      providers: [{ provide: MatDialog, useValue: dialog }],
    }).compileComponents();
    fixture = TestBed.createComponent(DeleteAccountComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('user', userFixture());
    fixture.detectChanges();
    deletions = 0;
    component.deleteEvent.subscribe(() => deletions++);
  });

  it('never deletes the account without asking first', () => {
    answerConfirmation(false);

    component.openDialog();

    expect(dialog.open).toHaveBeenCalledWith(ConfirmationDialogComponent, {
      data: 'delete your account',
    });
    expect(deletions).toBe(0);
  });

  it('asks for the deletion once the user has confirmed', () => {
    answerConfirmation(true);

    component.openDialog();

    expect(deletions).toBe(1);
  });
});
