import { Component, OnDestroy, OnInit } from '@angular/core';
import { Match } from '../core/interfaces/match';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatchCardComponent } from './match-card/match-card.component';
import { ToastrService } from 'ngx-toastr';
import { MessageComponent } from './message/message.component';
import { Message } from '../core/interfaces/message';
import { MoreInfoComponent } from '../shared/components/more-info/more-info.component';
import { Subject, filter, takeUntil } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmationDialogComponent } from '../shared/components/confirmation-dialog/confirmation-dialog.component';
import { ConnectService } from '../core/services/connect.service';
import { MatchService } from '../core/services/match.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-match',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatchCardComponent,
    MessageComponent,
    ReactiveFormsModule,
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

  constructor(
    private toastr: ToastrService,
    public dialog: MatDialog,
    private fb: FormBuilder,
    private connectService: ConnectService,
    private matchService: MatchService
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
      .pipe(takeUntil(this.destroyed$))
      .subscribe({
        next: (matches: Match[]) => {
          this.matches = matches;
          this.searchByNickname();
        },
        error: (error: HttpErrorResponse) => {
          this.toastr.error(error.message, 'Error', {
            positionClass: 'toast-bottom-center',
            toastClass: 'ngx-toastr custom',
          });
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
    if (message.sender !== this.selectedMatch?.user.nickname) {
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
    const newMessage: Message = {
      id: 100,
      content: message.content,
      date: new Date(),
      sender: this.connectService.connectedUser!.nickname,
    };
    this.selectedMatch!.messages.push(newMessage);
    this.unModifyMessage();
    this.toastr.success('You have sent a message', 'Message Sent', {
      positionClass: 'toast-bottom-center',
      toastClass: 'ngx-toastr custom',
    });
  }

  updateMessage(message: Message): void {
    this.selectedMatch!.messages.find(
      (message: Message) => message.id === this.updatedMessage?.id
    )!.content = message.content;
    this.unModifyMessage();
    this.toastr.success('You have updated your message', 'Message Updated', {
      positionClass: 'toast-bottom-center',
      toastClass: 'ngx-toastr custom',
    });
  }

  deleteMessage(messageId: number): void {
    const messageIndex = this.selectedMatch!.messages.findIndex(
      (message: Message) => message.id === messageId
    );
    if (messageIndex !== -1) {
      this.selectedMatch?.messages.splice(messageIndex, 1);
      this.unModifyMessage();
      this.toastr.success('You have deleted your message', 'Message Deleted', {
        positionClass: 'toast-bottom-center',
        toastClass: 'ngx-toastr custom',
      });
    }
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: 'delete your message',
    });

    dialogRef
      .afterClosed()
      .pipe(filter((res: boolean) => res))
      .subscribe(() => {
        this.deleteMessage(this.updatedMessage!.id);
      });
  }
}
