import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { DeletedAccount } from '../../core/interfaces/deleted-account';
import { environment } from '../../../environments/environment';
import { DescriptionPipe } from '../../shared/pipes/description.pipe';
import { CustomDatePipe } from '../../shared/pipes/custom-date.pipe';

@Component({
    selector: 'app-deleted-account-card',
    imports: [CommonModule, CustomDatePipe, DescriptionPipe],
    templateUrl: './deleted-account-card.component.html',
    styleUrl: './deleted-account-card.component.css'
})
export class DeletedAccountCardComponent {
  imagePath: string = environment.imagePath;
  @Input() deletedAccount!: DeletedAccount;
}
