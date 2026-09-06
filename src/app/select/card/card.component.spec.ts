import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { createSpyObj, SpyObj } from '../../../testing/spy';
import { Picture } from '../../core/interfaces/picture';
import { User } from '../../core/interfaces/user';
import { SelectService } from '../../core/services/select.service';
import { userFixture } from '../../core/testing/user.fixture';
import { MoreInfoComponent } from '../../shared/components/more-info/more-info.component';
import { CardComponent } from './card.component';

describe('CardComponent', () => {
  const pictures: Picture[] = [
    { id: 1, content: 'base64', isMainPicture: true },
  ];
  let fixture: ComponentFixture<CardComponent>;
  let component: CardComponent;
  let dialog: SpyObj<MatDialog>;
  let selectService: SpyObj<SelectService>;
  let router: SpyObj<Router>;
  let likes: number;
  let dislikes: number;

  beforeEach(async () => {
    dialog = createSpyObj<MatDialog>(['open']);
    selectService = createSpyObj<SelectService>(['getUserPictures']);
    router = createSpyObj<Router>(['navigate']);
    await TestBed.configureTestingModule({
      imports: [CardComponent],
      providers: [
        provideNoopAnimations(),
        { provide: SelectService, useValue: selectService },
        { provide: Router, useValue: router },
      ],
    })
      // The component imports MatDialogModule, whose own MatDialog would otherwise take
      // precedence over the one declared on the testing module.
      .overrideComponent(CardComponent, {
        add: { providers: [{ provide: MatDialog, useValue: dialog }] },
      })
      .compileComponents();
    fixture = TestBed.createComponent(CardComponent);
    component = fixture.componentInstance;
    likes = 0;
    dislikes = 0;
    component.likeEvent.subscribe(() => likes++);
    component.dislikeEvent.subscribe(() => dislikes++);
  });

  /** Renders the card of a candidate. */
  function render(user: User): void {
    fixture.componentRef.setInput('user', user);
    fixture.componentRef.setInput('display', true);
    fixture.detectChanges();
  }

  /** Makes the profile sheet close with the given answer. */
  function answerMoreInfo(answer: string | undefined): void {
    dialog.open.mockReturnValue({
      afterClosed: () => of(answer),
    } as MatDialogRef<MoreInfoComponent>);
  }

  it('displays the identity of the candidate', () => {
    render(userFixture({ nickname: 'Alice', job: 'Architecte' }));

    expect(fixture.nativeElement.textContent).toContain('Alice');
    expect(fixture.nativeElement.textContent).toContain('Architecte');
  });

  it('loads the album before opening the profile sheet', () => {
    const user = userFixture({ id: 2 });
    render(user);
    selectService.getUserPictures.mockReturnValue(of(pictures));
    answerMoreInfo(undefined);

    component.moreInfo();

    expect(selectService.getUserPictures).toHaveBeenCalledWith(2);
    expect(user.pictures).toEqual(pictures);
    expect(user.picturesLoaded).toBe(true);
    expect(dialog.open).toHaveBeenCalled();
  });

  it('does not load the album twice', () => {
    render(userFixture({ id: 2, picturesLoaded: true, pictures }));
    answerMoreInfo(undefined);

    component.moreInfo();

    expect(selectService.getUserPictures).not.toHaveBeenCalled();
    expect(dialog.open).toHaveBeenCalled();
  });

  it('still opens the profile sheet when the album cannot be loaded', () => {
    render(userFixture({ id: 2 }));
    selectService.getUserPictures.mockReturnValue(
      throwError(() => new Error('offline')),
    );
    answerMoreInfo(undefined);

    component.moreInfo();

    expect(dialog.open).toHaveBeenCalled();
  });

  it('forwards a like decided from the profile sheet', () => {
    render(userFixture({ id: 2, picturesLoaded: true }));
    answerMoreInfo('like');

    component.moreInfo();

    expect(likes).toBe(1);
    expect(dislikes).toBe(0);
  });

  it('forwards a dislike decided from the profile sheet', () => {
    render(userFixture({ id: 2, picturesLoaded: true }));
    answerMoreInfo('dislike');

    component.moreInfo();

    expect(dislikes).toBe(1);
    expect(likes).toBe(0);
  });

  it('decides nothing when the profile sheet is simply closed', () => {
    render(userFixture({ id: 2, picturesLoaded: true }));
    answerMoreInfo(undefined);

    component.moreInfo();

    expect(likes).toBe(0);
    expect(dislikes).toBe(0);
  });

  it('emits a like and a dislike from its own buttons', () => {
    render(userFixture());

    component.like();
    component.dislike();

    expect(likes).toBe(1);
    expect(dislikes).toBe(1);
  });

  it('opens the conversation of a fresh match', () => {
    render(userFixture());

    component.viewMatch();

    expect(router.navigate).toHaveBeenCalledWith(['match']);
  });
});
