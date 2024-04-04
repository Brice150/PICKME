import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatChipsModule } from '@angular/material/chips';
import { Preference } from '../../../core/interfaces/preference';
import { User } from '../../../core/interfaces/user';
import { ConnectService } from '../../../core/services/connect.service';

@Component({
  selector: 'app-preference',
  standalone: true,
  imports: [CommonModule, MatChipsModule, ReactiveFormsModule],
  templateUrl: './preference.component.html',
  styleUrl: './preference.component.css',
})
export class PreferenceComponent implements OnInit, OnChanges {
  @Input() preference!: Preference;
  @Input() user!: User;
  preferenceForm!: FormGroup;
  @Output() updateEvent: EventEmitter<void> = new EventEmitter<void>();
  initialValue!: string;

  constructor(
    private fb: FormBuilder,
    private connectService: ConnectService
  ) {}

  ngOnInit(): void {
    this.initialValue = (this.connectService.connectedUser!.preferences as any)[
      this.preference.name
    ];
    const value: string = (this.user.preferences as any)[this.preference.name];
    this.preferenceForm = this.fb.group({
      [this.preference.name]: value,
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!changes['firstChange']) {
      this.initialValue = (
        this.connectService.connectedUser!.preferences as any
      )[this.preference.name];
    }
  }

  isSelected(attribute: string, property: string): boolean {
    let selected: boolean = false;
    if (attribute && this.user) {
      selected = (this.user.preferences as any)[property] === attribute;
    }
    return selected;
  }

  select(attribute: string, property: string): void {
    if (attribute && this.user) {
      (this.user.preferences as any)[property] = attribute;
    }
  }

  updatePreferences(): void {
    this.updateEvent.emit();
    this.preferenceForm.markAsPristine();
  }

  cancel(): void {
    if (this.user) {
      (this.user.preferences as any)[this.preference.name] = this.initialValue;
      this.preferenceForm.patchValue({
        [this.preference.name]: this.initialValue,
      });
      this.preferenceForm.markAsPristine();
    }
  }
}
