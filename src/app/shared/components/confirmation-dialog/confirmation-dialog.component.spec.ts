import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { createSpyObj, SpyObj } from '../../../../testing/spy';
import { ConfirmationDialogComponent } from './confirmation-dialog.component';

describe('ConfirmationDialogComponent', () => {
  let dialogRef: SpyObj<MatDialogRef<ConfirmationDialogComponent>>;

  /** Builds the dialog with the action it has been opened for. */
  async function build(
    data: string | null,
  ): Promise<ComponentFixture<ConfirmationDialogComponent>> {
    dialogRef = createSpyObj<MatDialogRef<ConfirmationDialogComponent>>([
      'close',
    ]);
    await TestBed.configureTestingModule({
      imports: [ConfirmationDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: dialogRef },
        { provide: MAT_DIALOG_DATA, useValue: data },
      ],
    }).compileComponents();
    const fixture = TestBed.createComponent(ConfirmationDialogComponent);
    fixture.detectChanges();
    return fixture;
  }

  it('asks about the action it has been opened for', async () => {
    const fixture = await build('delete this user');

    expect(fixture.componentInstance.action).toBe('delete this user');
    expect(fixture.nativeElement.querySelector('h1').textContent).toContain(
      'Are you sure you want to delete this user ?',
    );
  });

  it('falls back on a deletion when it is opened without an action', async () => {
    const fixture = await build(null);

    expect(fixture.componentInstance.action).toBe('delete');
  });

  it('closes on a refusal when the user says no', async () => {
    const fixture = await build('delete');

    fixture.componentInstance.cancel();

    expect(dialogRef.close).toHaveBeenCalledWith(false);
  });

  it('closes on an agreement when the user says yes', async () => {
    const fixture = await build('delete');

    fixture.componentInstance.confirm();

    expect(dialogRef.close).toHaveBeenCalledWith(true);
  });
});
