import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
  input
} from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatChipsModule } from '@angular/material/chips';
import { Preference } from '../../../core/interfaces/preference';
import { User } from '../../../core/interfaces/user';
import { ConnectService } from '../../../core/services/connect.service';

@Component({
    selector: 'app-preference',
    imports: [CommonModule, MatChipsModule, ReactiveFormsModule],
    templateUrl: './preference.component.html',
    styleUrl: './preference.component.css'
})
export class PreferenceComponent implements OnInit, OnChanges {
  readonly preference = input.required<Preference>();
  readonly user = input.required<User>();
  preferenceForm!: FormGroup;
  @Output() updateEvent: EventEmitter<void> = new EventEmitter<void>();
  initialValue!: string;

  constructor(
    private fb: FormBuilder,
    private connectService: ConnectService
  ) {}

  ngOnInit(): void {
    this.initialValue = (this.connectService.connectedUser!.preferences as any)[
      this.preference().name
    ];
    const value: string = (this.user().preferences as any)[this.preference().name];
    this.preferenceForm = this.fb.group({
      [this.preference().name]: value,
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!changes['firstChange']) {
      this.initialValue = (
        this.connectService.connectedUser!.preferences as any
      )[this.preference().name];
    }
  }

  isSelected(attribute: string, property: string): boolean {
    let selected: boolean = false;
    const user = this.user();
    if (attribute && user) {
      selected = (user.preferences as any)[property] === attribute;
    }
    return selected;
  }

  select(attribute: string, property: string): void {
    const user = this.user();
    if (attribute && user) {
      (user.preferences as any)[property] = attribute;
    }
  }

  updatePreferences(): void {
    this.updateEvent.emit();
    this.preferenceForm.markAsPristine();
  }

  cancel(): void {
    const user = this.user();
    if (user) {
      (user.preferences as any)[this.preference().name] = this.initialValue;
      this.preferenceForm.patchValue({
        [this.preference().name]: this.initialValue,
      });
      this.preferenceForm.markAsPristine();
    }
  }
}
