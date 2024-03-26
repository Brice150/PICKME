import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { User } from '../../core/interfaces/user';
import { MatChipsModule } from '@angular/material/chips';
import { AlcoholDrinking } from '../../core/enums/alcohol-drinking';
import { Smokes } from '../../core/enums/smokes';
import { Parenthood } from '../../core/enums/parenthood';
import { Gamer } from '../../core/enums/gamer';
import { Animals } from '../../core/enums/animals';
import { Organised } from '../../core/enums/organised';
import { Personality } from '../../core/enums/personality';
import { ConnectService } from '../../core/services/connect.service';
import { SportPractice } from '../../core/enums/sport-practice';

@Component({
  selector: 'app-preferences',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatChipsModule],
  templateUrl: './preferences.component.html',
  styleUrl: './preferences.component.css',
})
export class PreferencesComponent implements OnInit {
  @Input() user!: User;
  preferencesForm!: FormGroup;
  @Output() updateEvent: EventEmitter<string> = new EventEmitter<string>();

  alcoholDrinking: string[] = Object.values(AlcoholDrinking);
  smokes: string[] = Object.values(Smokes);
  sportPractice: string[] = Object.values(SportPractice);
  parenthood: string[] = Object.values(Parenthood);
  gamer: string[] = Object.values(Gamer);
  animals: string[] = Object.values(Animals);
  organised: string[] = Object.values(Organised);
  personality: string[] = Object.values(Personality);

  constructor(
    private fb: FormBuilder,
    private connectService: ConnectService
  ) {}

  ngOnInit(): void {
    if (!this.user.preferences) {
      this.user.preferences = {};
    }
    this.preferencesForm = this.fb.group({
      alcoholDrinking: [this.user.preferences?.alcoholDrinking],
      smokes: [this.user.preferences?.smokes],
      organised: [this.user.preferences?.organised],
      personality: [this.user.preferences?.personality],
      sportPractice: [this.user.preferences?.sportPractice],
      animals: [this.user.preferences?.animals],
      parenthood: [this.user.preferences?.parenthood],
      gamer: [this.user.preferences?.gamer],
    });
  }

  updatePreferences(): void {
    this.updateEvent.emit('Preferences Updated');
    this.preferencesForm.markAsPristine();
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

  cancel(): void {
    if (this.user) {
      if (!this.user.preferences) {
        this.user.preferences = {};
      }
      this.user.preferences.alcoholDrinking =
        this.connectService.connectedUser!.preferences?.alcoholDrinking;
      this.user.preferences.smokes =
        this.connectService.connectedUser!.preferences?.smokes;
      this.user.preferences.sportPractice =
        this.connectService.connectedUser!.preferences?.sportPractice;
      this.user.preferences.parenthood =
        this.connectService.connectedUser!.preferences?.parenthood;
      this.user.preferences.gamer =
        this.connectService.connectedUser!.preferences?.gamer;
      this.user.preferences.animals =
        this.connectService.connectedUser!.preferences?.animals;
      this.user.preferences.organised =
        this.connectService.connectedUser!.preferences?.organised;
      this.user.preferences.personality =
        this.connectService.connectedUser!.preferences?.personality;
      this.preferencesForm.patchValue({
        alcoholDrinking: this.user.preferences.alcoholDrinking,
        smokes: this.user.preferences.smokes,
        sportPractice: this.user.preferences.sportPractice,
        parenthood: this.user.preferences.parenthood,
        gamer: this.user.preferences.gamer,
        animals: this.user.preferences.animals,
        organised: this.user.preferences.organised,
        personality: this.user.preferences.personality,
      });
      this.preferencesForm.markAsPristine();
    }
  }
}
