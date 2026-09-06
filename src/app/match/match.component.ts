import { NgClass } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  ElementRef,
  OnInit,
  computed,
  inject,
  signal,
  viewChild,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { ToastrService } from 'ngx-toastr';
import { EMPTY, catchError, filter, startWith, switchMap } from 'rxjs';
import { Match } from '../core/interfaces/match';
import { Message } from '../core/interfaces/message';
import { MatchService } from '../core/services/match.service';
import { NotificationService } from '../core/services/notification.service';
import { SelectService } from '../core/services/select.service';
import { ConfirmationDialogComponent } from '../shared/components/confirmation-dialog/confirmation-dialog.component';
import { LoadingComponent } from '../shared/components/loading/loading.component';
import { MoreInfoComponent } from '../shared/components/more-info/more-info.component';
import { MatchCardComponent } from './match-card/match-card.component';
import { MessageComponent } from './message/message.component';

@Component({
  selector: 'app-match',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    NgClass,
    FormsModule,
    MatchCardComponent,
    MessageComponent,
    ReactiveFormsModule,
    LoadingComponent,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './match.component.html',
  styleUrl: './match.component.css',
})
export class MatchComponent implements OnInit {
  private readonly toastr = inject(ToastrService);
  private readonly dialog = inject(MatDialog);
  private readonly fb = inject(FormBuilder);
  private readonly matchService = inject(MatchService);
  private readonly notificationService = inject(NotificationService);
  private readonly selectService = inject(SelectService);
  private readonly destroyRef = inject(DestroyRef);

  messageForm!: FormGroup;
  readonly search = signal('');
  readonly isModifying = signal(false);
  readonly updatedMessage = signal<Message | undefined>(undefined);
  readonly matches = signal<Match[]>([]);
  readonly loading = signal(true);

  readonly filteredMatches = computed(() => {
    const search = this.search().toLocaleLowerCase();
    if (search === '') {
      return this.matches();
    }
    return this.matches().filter((match: Match) =>
      match.user.nickname.toLocaleLowerCase().includes(search),
    );
  });

  /**
   * The conversation being read, named by the profile it belongs to rather than held apart. The
   * list is republished on every signal from the server and on every message written: reading the
   * conversation back from it keeps the two in step, and closes it on its own when the profile
   * has unmatched in the meantime.
   */
  private readonly selectedUserId = signal<number | undefined>(undefined);
  readonly selectedMatch = computed(() =>
    this.matches().find(
      (match: Match) => match.user.id === this.selectedUserId(),
    ),
  );

  previousMessages?: Message[];
  // Absent as long as the selected conversation holds no message.
  private readonly messagesContainer =
    viewChild<ElementRef<HTMLElement>>('messagesContainer');

  ngOnInit(): void {
    this.messageForm = this.fb.group({
      content: [
        '',
        [
          Validators.required,
          Validators.maxLength(500),
          Validators.minLength(2),
        ],
      ],
    });

    // Read once on arrival, then again on every signal the server sends: a new message or a
    // profile that unmatched both raise one.
    this.notificationService.serverEvents$
      .pipe(
        startWith(undefined),
        // The failure is caught around the request rather than left to travel up: the interceptor
        // has already reported it, and swallowing it here costs one refresh instead of ending the
        // subscription and leaving the screen frozen until the user navigates away.
        switchMap(() =>
          this.matchService.getAllUserMatches().pipe(
            catchError(() => {
              this.loading.set(false);
              return EMPTY;
            }),
          ),
        ),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: (matches: Match[]) => {
          this.matches.set(matches);
          this.loading.set(false);
          this.scrollDownOnNewMessages();
        },
      });
  }

  dislike(): void {
    const selectedMatch = this.selectedMatch();
    if (!selectedMatch?.user.id) {
      return;
    }
    this.selectService.addDislike(selectedMatch.user.id).subscribe({
      next: () => {
        this.matches.update((matches: Match[]) =>
          matches.filter(
            (match: Match) => match.user.id !== selectedMatch.user.id,
          ),
        );
        this.selectedUserId.set(undefined);
        this.previousMessages = undefined;
        this.toastr.success(
          'You have disliked ' + selectedMatch.user.nickname,
          'Disliked ' + selectedMatch.user.nickname,
          {
            positionClass: 'toast-bottom-center',
            toastClass: 'ngx-toastr custom',
          },
        );
      },
    });
  }

  selectMatch(match: Match): void {
    this.selectedUserId.set(match.user.id);
    this.previousMessages = match.messages;
    setTimeout(() => {
      this.scrollDown();
    });
  }

  moreInfo(): void {
    const dialogRef = this.dialog.open(MoreInfoComponent, {
      data: {
        user: this.selectedMatch()!.user,
        adminMode: false,
        matchMode: true,
      },
    });

    dialogRef
      .afterClosed()
      .pipe(filter((res: boolean) => res))
      .subscribe(() => {
        this.dislike();
      });
  }

  back(): void {
    this.selectedUserId.set(undefined);
    this.previousMessages = undefined;
  }

  modifyMessage(message: Message): void {
    if (
      message.sender !== this.selectedMatch()?.user.nickname &&
      message.content
    ) {
      this.updatedMessage.set(message);
      this.isModifying.set(true);
    }
  }

  unModifyMessage(): void {
    this.messageForm.get('content')?.reset();
    this.messageForm.get('content')?.setErrors(null);
    this.messageForm.markAsPristine();
    this.updatedMessage.set(undefined);
    this.isModifying.set(false);
  }

  sendMessage(message: Message): void {
    const selectedMatch = this.selectedMatch();
    message.fkReceiver = selectedMatch?.user.id;
    this.matchService.addMessage(message).subscribe({
      next: (newMessage: Message) => {
        // The conversation is republished with its new message, then moved back to the top of the
        // list, where the most recent exchange belongs.
        const updated: Match = {
          ...selectedMatch!,
          messages: [...selectedMatch!.messages, newMessage],
        };
        this.matches.update((matches: Match[]) => [
          updated,
          ...matches.filter(
            (match: Match) => match.user.id !== updated.user.id,
          ),
        ]);
        this.previousMessages = updated.messages;
        this.unModifyMessage();
        setTimeout(() => {
          this.scrollDown();
        });
      },
      complete: () => {
        this.toastr.success('You have sent a message', 'Message Sent', {
          positionClass: 'toast-bottom-center',
          toastClass: 'ngx-toastr custom',
        });
      },
    });
  }

  updateMessage(message: Message): void {
    const edited: Message = {
      ...this.updatedMessage()!,
      content: message.content,
    };
    this.matchService.updateMessage(edited).subscribe({
      next: (updatedMessage: Message) => {
        this.replaceMessage(edited.id, updatedMessage.content);
        this.unModifyMessage();
      },
      complete: () => {
        this.toastr.success(
          'You have updated your message',
          'Message Updated',
          {
            positionClass: 'toast-bottom-center',
            toastClass: 'ngx-toastr custom',
          },
        );
      },
    });
  }

  deleteMessage(messageToDelete: Message): void {
    this.matchService.deleteMessage(messageToDelete.id).subscribe({
      next: () => {
        // A deleted message keeps its place in the conversation, emptied of its content.
        this.replaceMessage(messageToDelete.id, undefined);
        this.unModifyMessage();
      },
      complete: () => {
        this.toastr.success(
          'You have deleted your message',
          'Message Deleted',
          {
            positionClass: 'toast-bottom-center',
            toastClass: 'ngx-toastr custom',
          },
        );
      },
    });
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: 'delete your message',
    });

    dialogRef
      .afterClosed()
      .pipe(filter((res: boolean) => res))
      .subscribe(() => {
        this.deleteMessage(this.updatedMessage()!);
      });
  }

  getPreview(match: Match): string | undefined {
    if (!match.messages || match.messages.length === 0) {
      return undefined;
    }
    const messagesWithContent = match.messages.filter(
      (message) => message.content,
    );
    if (messagesWithContent.length === 0) {
      return 'Message deleted';
    }
    return messagesWithContent[messagesWithContent.length - 1].content;
  }

  scrollDown(): void {
    const container = this.messagesContainer()?.nativeElement;
    if (container) {
      container.scrollTop = container.scrollHeight;
    }
  }

  /** Republishes one message of the open conversation, and the list that carries it. */
  private replaceMessage(messageId: number, content: string | undefined): void {
    const selectedUserId = this.selectedUserId();
    this.matches.update((matches: Match[]) =>
      matches.map((match: Match) =>
        match.user.id !== selectedUserId
          ? match
          : {
              ...match,
              messages: match.messages.map((message: Message) =>
                message.id === messageId ? { ...message, content } : message,
              ),
            },
      ),
    );
  }

  /** Follows the conversation down when the server has brought new messages to it. */
  private scrollDownOnNewMessages(): void {
    const selectedMatch = this.selectedMatch();
    if (!selectedMatch) {
      this.previousMessages = undefined;
      return;
    }
    if (this.previousMessages?.length !== selectedMatch.messages.length) {
      this.previousMessages = selectedMatch.messages;
      setTimeout(() => {
        this.scrollDown();
      });
    }
  }
}
