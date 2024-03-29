import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { Subject, distinctUntilChanged, filter, repeat, takeUntil } from 'rxjs';
import { Match } from '../core/interfaces/match';
import { Message } from '../core/interfaces/message';
import { MatchService } from '../core/services/match.service';
import { SelectService } from '../core/services/select.service';
import { ConfirmationDialogComponent } from '../shared/components/confirmation-dialog/confirmation-dialog.component';
import { LoadingComponent } from '../shared/components/loading/loading.component';
import { MoreInfoComponent } from '../shared/components/more-info/more-info.component';
import { MatchCardComponent } from './match-card/match-card.component';
import { MessageComponent } from './message/message.component';

@Component({
  selector: 'app-match',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatchCardComponent,
    MessageComponent,
    ReactiveFormsModule,
    LoadingComponent,
  ],
  templateUrl: './match.component.html',
  styleUrl: './match.component.css',
})
export class MatchComponent implements OnInit, OnDestroy {
  search!: string;
  messageForm!: FormGroup;
  isModifying: boolean = false;
  updatedMessage?: Message;
  matches: Match[] = [];
  filteredMatches: Match[] = [];
  selectedMatch?: Match;
  destroyed$: Subject<void> = new Subject<void>();
  loading: boolean = true;

  constructor(
    private toastr: ToastrService,
    public dialog: MatDialog,
    private fb: FormBuilder,
    private matchService: MatchService,
    private selectService: SelectService
  ) {}

  ngOnInit(): void {
    this.messageForm = this.fb.group({
      content: [
        '',
        [
          Validators.required,
          Validators.maxLength(500),
          Validators.minLength(5),
        ],
      ],
    });

    this.matchService
      .getAllUserMatches()
      .pipe(
        repeat({ delay: 10000 }),
        distinctUntilChanged(),
        takeUntil(this.destroyed$)
      )
      .subscribe({
        next: (matches: Match[]) => {
          this.matches = matches;
          this.searchByNickname();
          this.loading = false;
          if (this.selectedMatch) {
            const matchIndex = this.matches.findIndex(
              (match: Match) => this.selectedMatch?.user.id === match.user.id
            );
            if (matchIndex !== -1) {
              this.selectedMatch = this.matches[matchIndex];
            }
          }
        },
      });
  }

  ngOnDestroy(): void {
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  searchByNickname(): void {
    if (!this.search || this.search === '') {
      this.filteredMatches = [...this.matches];
    } else {
      this.filteredMatches = [...this.matches].filter((match: Match) =>
        match.user.nickname
          .toLocaleLowerCase()
          .includes(this.search.toLocaleLowerCase())
      );
    }
  }

  dislike(): void {
    this.selectService.addDislike(this.selectedMatch?.user.id!).subscribe({
      next: () => {
        const matchIndex = this.matches.findIndex(
          (match: Match) => match.user.id === this.selectedMatch!.user.id
        );
        if (matchIndex !== -1) {
          this.matches.splice(matchIndex, 1);
          this.searchByNickname();
          this.toastr.success(
            'You have disliked ' + this.selectedMatch!.user.nickname,
            'Disliked ' + this.selectedMatch!.user.nickname,
            {
              positionClass: 'toast-bottom-center',
              toastClass: 'ngx-toastr custom',
            }
          );
          this.selectedMatch = undefined;
        }
      },
    });
  }

  selectMatch(match: Match): void {
    this.selectedMatch = match;
  }

  moreInfo(): void {
    const dialogRef = this.dialog.open(MoreInfoComponent, {
      data: {
        user: this.selectedMatch!.user,
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
    this.selectedMatch = undefined;
  }

  modifyMessage(message: Message): void {
    if (
      message.sender !== this.selectedMatch?.user.nickname &&
      message.content
    ) {
      this.updatedMessage = message;
      this.isModifying = true;
    }
  }

  unModifyMessage(): void {
    this.messageForm.get('content')?.reset();
    this.messageForm.markAsPristine();
    this.updatedMessage = undefined;
    this.isModifying = false;
  }

  sendMessage(message: Message): void {
    message.fkReceiver = this.selectedMatch?.user.id;
    this.matchService.addMessage(message).subscribe({
      next: (newMessage: Message) => {
        this.selectedMatch!.messages.push(newMessage);
        const index = this.matches.findIndex(
          (match) => match.user.id === this.selectedMatch!.user.id
        );
        if (index !== -1) {
          const selected = this.matches.splice(index, 1)[0];
          this.matches.unshift(selected);
          this.searchByNickname();
        }
        this.unModifyMessage();
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
    this.updatedMessage!.content = message.content;
    this.matchService.updateMessage(this.updatedMessage!).subscribe({
      next: (updatedMessage: Message) => {
        this.selectedMatch!.messages.find(
          (message: Message) => message.id === this.updatedMessage?.id
        )!.content = updatedMessage.content;
        this.unModifyMessage();
      },
      complete: () => {
        this.toastr.success(
          'You have updated your message',
          'Message Updated',
          {
            positionClass: 'toast-bottom-center',
            toastClass: 'ngx-toastr custom',
          }
        );
      },
    });
  }

  deleteMessage(messageToDelete: Message): void {
    this.matchService.deleteMessage(messageToDelete.id).subscribe({
      next: () => {
        const messageIndex = this.selectedMatch!.messages.findIndex(
          (message: Message) => message.id === messageToDelete.id
        );
        if (messageIndex !== -1) {
          this.selectedMatch!.messages[messageIndex].content = undefined;
          this.unModifyMessage();
        }
      },
      complete: () => {
        this.toastr.success(
          'You have deleted your message',
          'Message Deleted',
          {
            positionClass: 'toast-bottom-center',
            toastClass: 'ngx-toastr custom',
          }
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
        this.deleteMessage(this.updatedMessage!);
      });
  }

  getPreview(match: Match): string | undefined {
    if (!match.messages || match.messages.length === 0) {
      return undefined;
    }
    const messagesWithContent = match.messages.filter(
      (message) => message.content
    );
    if (messagesWithContent.length === 0) {
      return 'Message deleted';
    }
    return messagesWithContent[messagesWithContent.length - 1].content;
  }
}
