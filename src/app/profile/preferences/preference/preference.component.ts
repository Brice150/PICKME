import {
  ChangeDetectionStrategy,
  Component,
  inject,
  input,
  OnChanges,
  OnInit,
  output,
  SimpleChanges,
} from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatChipsModule } from '@angular/material/chips';
import {
  Preference,
  PreferenceName,
} from '../../../core/interfaces/preference';
import { Preferences } from '../../../core/interfaces/preferences';
import { User } from '../../../core/interfaces/user';
import { ConnectService } from '../../../core/services/connect.service';

type PreferenceValues = Record<PreferenceName, string | undefined>;

@Component({
  selector: 'app-preference',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MatChipsModule, ReactiveFormsModule],
  templateUrl: './preference.component.html',
  styleUrl: './preference.component.css',
})
export class PreferenceComponent implements OnInit, OnChanges {
  private readonly fb = inject(FormBuilder);
  private readonly connectService = inject(ConnectService);

  readonly preference = input.required<Preference>();
  readonly user = input.required<User>();
  preferenceForm!: FormGroup;
  readonly updateEvent = output<void>();
  initialValue?: string;

  ngOnInit(): void {
    this.initialValue = this.readConnectedUserValue();
    const value = this.valuesOf(this.user().preferences)[
      this.preference().name
    ];
    this.preferenceForm = this.fb.group({
      [this.preference().name]: value,
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!changes['firstChange']) {
      this.initialValue = this.readConnectedUserValue();
    }
  }

  isSelected(attribute: string, property: PreferenceName): boolean {
    let selected = false;
    const user = this.user();
    if (attribute && user) {
      selected = this.valuesOf(user.preferences)[property] === attribute;
    }
    return selected;
  }

  select(attribute: string, property: PreferenceName): void {
    const user = this.user();
    if (attribute && user) {
      this.valuesOf(user.preferences)[property] = attribute;
    }
  }

  updatePreferences(): void {
    this.updateEvent.emit();
    this.preferenceForm.markAsPristine();
  }

  cancel(): void {
    const user = this.user();
    if (user) {
      this.valuesOf(user.preferences)[this.preference().name] =
        this.initialValue;
      this.preferenceForm.patchValue({
        [this.preference().name]: this.initialValue,
      });
      this.preferenceForm.markAsPristine();
    }
  }

  private readConnectedUserValue(): string | undefined {
    return this.valuesOf(this.connectService.connectedUser()?.preferences)[
      this.preference().name
    ];
  }

  private valuesOf(preferences: Preferences | undefined): PreferenceValues {
    return (preferences ?? {}) as PreferenceValues;
  }
}
