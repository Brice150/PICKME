import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { createSpyObj, SpyObj } from '../../../../testing/spy';
import { Picture } from '../../../core/interfaces/picture';
import { ConfirmationDialogComponent } from '../../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { PictureComponent } from './picture.component';

describe('PictureComponent', () => {
  let fixture: ComponentFixture<PictureComponent>;
  let component: PictureComponent;
  let dialog: SpyObj<MatDialog>;
  let selections: number;
  let deletions: number;

  /** Renders a picture of the album of the connected user. */
  function render(picture: Partial<Picture>, isLoading = false): void {
    fixture.componentRef.setInput('picture', {
      id: 1,
      content: 'base64',
      isMainPicture: false,
      ...picture,
    });
    fixture.componentRef.setInput('isLoading', isLoading);
    fixture.componentRef.setInput('display', true);
    fixture.detectChanges();
  }

  function answerConfirmation(confirmed: boolean): void {
    dialog.open.mockReturnValue({
      afterClosed: () => of(confirmed),
    } as MatDialogRef<ConfirmationDialogComponent>);
  }

  beforeEach(async () => {
    dialog = createSpyObj<MatDialog>(['open']);
    await TestBed.configureTestingModule({
      imports: [PictureComponent],
      providers: [
        provideNoopAnimations(),
        { provide: MatDialog, useValue: dialog },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(PictureComponent);
    component = fixture.componentInstance;
    selections = 0;
    deletions = 0;
    component.selectMainEvent.subscribe(() => selections++);
    component.deleteEvent.subscribe(() => deletions++);
  });

  it('promotes a secondary picture as the main one', () => {
    render({ isMainPicture: false });

    component.selectMainPicture();

    expect(selections).toBe(1);
  });

  it('does nothing when the picture is already the main one', () => {
    render({ isMainPicture: true });

    component.selectMainPicture();

    expect(selections).toBe(0);
  });

  it('ignores a promotion while another one is still running', () => {
    render({ isMainPicture: false }, true);

    component.selectMainPicture();

    expect(selections).toBe(0);
  });

  it('asks for a confirmation before deleting a picture', () => {
    render({});
    answerConfirmation(true);

    component.openDialog();

    expect(dialog.open).toHaveBeenCalledWith(ConfirmationDialogComponent, {
      data: 'delete this picture',
    });
    expect(deletions).toBe(1);
  });

  it('keeps the picture when the deletion is not confirmed', () => {
    render({});
    answerConfirmation(false);

    component.openDialog();

    expect(deletions).toBe(0);
  });

  it('ignores a deletion while another change is still running', () => {
    render({}, true);

    component.openDialog();

    expect(dialog.open).not.toHaveBeenCalled();
  });
});
