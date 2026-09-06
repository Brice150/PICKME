import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { ToastrService } from 'ngx-toastr';
import { Subject, of, throwError } from 'rxjs';
import { createSpyObj, SpyObj } from '../../testing/spy';
import { Match } from '../core/interfaces/match';
import { Message } from '../core/interfaces/message';
import { MatchService } from '../core/services/match.service';
import { NotificationService } from '../core/services/notification.service';
import { SelectService } from '../core/services/select.service';
import { userFixture } from '../core/testing/user.fixture';
import { ConfirmationDialogComponent } from '../shared/components/confirmation-dialog/confirmation-dialog.component';
import { MoreInfoComponent } from '../shared/components/more-info/more-info.component';
import { MatchComponent } from './match.component';

describe('MatchComponent', () => {
  let fixture: ComponentFixture<MatchComponent>;
  let component: MatchComponent;
  let matchService: SpyObj<MatchService>;
  let selectService: SpyObj<SelectService>;
  let toastr: SpyObj<ToastrService>;
  let dialog: SpyObj<MatDialog>;
  let serverEvents$: Subject<void>;
  let notificationService: SpyObj<NotificationService>;

  /** Builds a conversation with the given profile. */
  function match(
    id: number,
    nickname: string,
    messages: Message[] = [],
  ): Match {
    return { user: userFixture({ id, nickname }), messages };
  }

  function message(id: number, sender: string, content?: string): Message {
    return { id, sender, content, date: new Date(), fkReceiver: 1 };
  }

  /** Starts the screen on a first read of the conversations. */
  function start(matches: Match[]): void {
    matchService.getAllUserMatches.mockReturnValue(of(matches));
    fixture.detectChanges();
  }

  /** Plays a signal from the server, which makes the screen read its conversations again. */
  function serverSignals(matches: Match[]): void {
    matchService.getAllUserMatches.mockReturnValue(of(matches));
    serverEvents$.next();
  }

  /** Makes the next dialog close with the given answer. */
  function answerDialog(answer: boolean): void {
    dialog.open.mockReturnValue({
      afterClosed: () => of(answer),
    } as MatDialogRef<unknown>);
  }

  function lastToastTitle(): string {
    return toastr.success.mock.lastCall![1] as string;
  }

  beforeEach(async () => {
    serverEvents$ = new Subject<void>();
    matchService = createSpyObj<MatchService>([
      'getAllUserMatches',
      'addMessage',
      'updateMessage',
      'deleteMessage',
    ]);
    selectService = createSpyObj<SelectService>(['addDislike']);
    toastr = createSpyObj<ToastrService>(['success']);
    dialog = createSpyObj<MatDialog>(['open']);
    matchService.getAllUserMatches.mockReturnValue(of([]));
    notificationService = {
      serverEvents$,
    } as unknown as SpyObj<NotificationService>;
    await TestBed.configureTestingModule({
      imports: [MatchComponent],
      providers: [
        provideNoopAnimations(),
        { provide: MatchService, useValue: matchService },
        { provide: NotificationService, useValue: notificationService },
        { provide: SelectService, useValue: selectService },
        { provide: ToastrService, useValue: toastr },
        { provide: MatDialog, useValue: dialog },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(MatchComponent);
    component = fixture.componentInstance;
  });

  it('lists the conversations of the connected user', () => {
    start([match(2, 'Alice'), match(3, 'Bob')]);

    expect(component.matches.length).toBe(2);
    expect(component.filteredMatches.length).toBe(2);
    expect(component.loading).toBe(false);
  });

  it('stops the loader and keeps listening when a read fails', () => {
    matchService.getAllUserMatches.mockReturnValue(
      throwError(() => new Error('offline')),
    );

    fixture.detectChanges();

    expect(component.loading).toBe(false);

    serverSignals([match(2, 'Alice')]);

    expect(component.matches.length).toBe(1);
  });

  it('filters the conversations on the nickname, whatever the case', () => {
    start([match(2, 'Alice'), match(3, 'Bob')]);

    component.search = 'ali';
    component.searchByNickname();

    expect(component.filteredMatches.map((m) => m.user.nickname)).toEqual([
      'Alice',
    ]);
  });

  it('shows every conversation back when the search is cleared', () => {
    start([match(2, 'Alice'), match(3, 'Bob')]);
    component.search = 'ali';
    component.searchByNickname();

    component.search = '';
    component.searchByNickname();

    expect(component.filteredMatches.length).toBe(2);
  });

  it('opens a conversation and closes it again', async () => {
    const alice = match(2, 'Alice', [message(1, 'Alice', 'hello')]);
    start([alice]);

    component.selectMatch(alice);
    await Promise.resolve();
    fixture.detectChanges();

    expect(component.selectedMatch).toBe(alice);
    expect(fixture.nativeElement.textContent).toContain('hello');

    component.back();

    expect(component.selectedMatch).toBeUndefined();
    expect(component.previousMessages).toBeUndefined();
  });

  it('keeps the open conversation in sync with the polling', () => {
    const alice = match(2, 'Alice');
    start([alice]);
    component.selectMatch(alice);

    const refreshed = match(2, 'Alice', [message(1, 'Alice', 'hello')]);
    serverSignals([refreshed]);

    expect(component.selectedMatch).toBe(refreshed);
  });

  it('closes the conversation of a profile that unmatched in the meantime', () => {
    const alice = match(2, 'Alice');
    start([alice]);
    component.selectMatch(alice);

    serverSignals([match(3, 'Bob')]);

    expect(component.selectedMatch).toBeUndefined();
  });

  it('previews the last message that still has a content', () => {
    const alice = match(2, 'Alice', [
      message(1, 'Alice', 'hello'),
      message(2, 'Bob', undefined),
    ]);

    expect(component.getPreview(alice)).toBe('hello');
  });

  it('previews a deletion when every message is gone', () => {
    const alice = match(2, 'Alice', [message(1, 'Alice', undefined)]);

    expect(component.getPreview(alice)).toBe('Message deleted');
  });

  it('previews nothing on a conversation that has not started', () => {
    expect(component.getPreview(match(2, 'Alice'))).toBeUndefined();
  });

  it('sends a message and brings the conversation back to the top', async () => {
    const alice = match(2, 'Alice');
    start([match(3, 'Bob'), alice]);
    component.selectMatch(alice);
    const sent = message(9, 'Bob', 'hi');
    matchService.addMessage.mockReturnValue(of(sent));

    component.sendMessage({ content: 'hi' } as Message);
    await Promise.resolve();

    expect(matchService.addMessage).toHaveBeenCalledWith(
      expect.objectContaining({ content: 'hi', fkReceiver: 2 }),
    );
    expect(alice.messages).toEqual([sent]);
    expect(component.matches[0]).toBe(alice);
    expect(lastToastTitle()).toBe('Message Sent');
  });

  it('only offers to edit a message written by the connected user', () => {
    const alice = match(2, 'Alice', [message(1, 'Alice', 'hello')]);
    start([alice]);
    component.selectMatch(alice);

    component.modifyMessage(message(1, 'Alice', 'hello'));
    expect(component.isModifying).toBe(false);

    component.modifyMessage(message(2, 'Bob', 'hi'));
    expect(component.isModifying).toBe(true);
  });

  it('never offers to edit a message already deleted', () => {
    const alice = match(2, 'Alice');
    start([alice]);
    component.selectMatch(alice);

    component.modifyMessage(message(2, 'Bob', undefined));

    expect(component.isModifying).toBe(false);
  });

  it('applies the new content of an edited message', () => {
    const edited = message(2, 'Bob', 'hi');
    const alice = match(2, 'Alice', [edited]);
    start([alice]);
    component.selectMatch(alice);
    component.modifyMessage(edited);
    matchService.updateMessage.mockReturnValue(of(message(2, 'Bob', 'hello')));

    component.updateMessage({ content: 'hello' } as Message);

    expect(edited.content).toBe('hello');
    expect(component.isModifying).toBe(false);
    expect(lastToastTitle()).toBe('Message Updated');
  });

  it('empties a deleted message instead of removing it', () => {
    const deleted = message(2, 'Bob', 'hi');
    const alice = match(2, 'Alice', [deleted]);
    start([alice]);
    component.selectMatch(alice);
    matchService.deleteMessage.mockReturnValue(of(undefined));

    component.deleteMessage(deleted);

    expect(alice.messages.length).toBe(1);
    expect(deleted.content).toBeUndefined();
    expect(lastToastTitle()).toBe('Message Deleted');
  });

  it('asks for a confirmation before deleting a message', () => {
    const deleted = message(2, 'Bob', 'hi');
    const alice = match(2, 'Alice', [deleted]);
    start([alice]);
    component.selectMatch(alice);
    component.modifyMessage(deleted);
    matchService.deleteMessage.mockReturnValue(of(undefined));
    answerDialog(true);

    component.openDialog();

    expect(dialog.open).toHaveBeenCalledWith(ConfirmationDialogComponent, {
      data: 'delete your message',
    });
    expect(matchService.deleteMessage).toHaveBeenCalledWith(2);
  });

  it('keeps the message when the deletion is not confirmed', () => {
    const alice = match(2, 'Alice', [message(2, 'Bob', 'hi')]);
    start([alice]);
    component.selectMatch(alice);
    answerDialog(false);

    component.openDialog();

    expect(matchService.deleteMessage).not.toHaveBeenCalled();
  });

  it('drops the conversation when the profile is disliked', () => {
    const alice = match(2, 'Alice');
    start([alice, match(3, 'Bob')]);
    component.selectMatch(alice);
    selectService.addDislike.mockReturnValue(of(undefined));

    component.dislike();

    expect(component.matches.map((m) => m.user.id)).toEqual([3]);
    expect(component.selectedMatch).toBeUndefined();
    expect(lastToastTitle()).toBe('Disliked Alice');
  });

  it('does nothing when no conversation is open', () => {
    start([match(2, 'Alice')]);

    component.dislike();

    expect(selectService.addDislike).not.toHaveBeenCalled();
  });

  it('unmatches from the profile sheet when the user confirms', () => {
    const alice = match(2, 'Alice');
    start([alice]);
    component.selectMatch(alice);
    selectService.addDislike.mockReturnValue(of(undefined));
    answerDialog(true);

    component.moreInfo();

    expect(dialog.open).toHaveBeenCalledWith(MoreInfoComponent, {
      data: { user: alice.user, adminMode: false, matchMode: true },
    });
    expect(selectService.addDislike).toHaveBeenCalledWith(2);
  });

  it('keeps the match when the profile sheet is simply closed', () => {
    const alice = match(2, 'Alice');
    start([alice]);
    component.selectMatch(alice);
    answerDialog(false);

    component.moreInfo();

    expect(selectService.addDislike).not.toHaveBeenCalled();
  });

  it('clears the edition form when the user gives up', () => {
    const edited = message(2, 'Bob', 'hi');
    const alice = match(2, 'Alice', [edited]);
    start([alice]);
    component.selectMatch(alice);
    component.modifyMessage(edited);

    component.unModifyMessage();

    expect(component.isModifying).toBe(false);
    expect(component.updatedMessage).toBeUndefined();
    expect(component.messageForm.get('content')?.value).toBeNull();
  });
});
