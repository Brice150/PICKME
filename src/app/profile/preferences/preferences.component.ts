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
  @Input() user?: User;
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
    this.preferencesForm = this.fb.group({
      alcoholDrinking: [this.user?.alcoholDrinking],
      smokes: [this.user?.smokes],
      organised: [this.user?.organised],
      personality: [this.user?.personality],
      sportPractice: [this.user?.sportPractice],
      animals: [this.user?.animals],
      parenthood: [this.user?.parenthood],
      gamer: [this.user?.gamer],
    });
  }

  updatePreferences(): void {
    this.updateEvent.emit('Preferences Updated');
    this.preferencesForm.markAsPristine();
  }

  isSelected(attribute: string, property: string): boolean {
    let selected: boolean = false;
    if (attribute && this.user) {
      selected = (this.user as any)[property] === attribute;
    }
    return selected;
  }

  select(attribute: string, property: string): void {
    if (attribute && this.user) {
      (this.user as any)[property] = attribute;
    }
  }

  cancel(): void {
    if (this.user) {
      this.user.alcoholDrinking =
        this.connectService.connectedUser?.alcoholDrinking;
      this.user.smokes = this.connectService.connectedUser?.smokes;
      this.user.sportPractice =
        this.connectService.connectedUser?.sportPractice;
      this.user.parenthood = this.connectService.connectedUser?.parenthood;
      this.user.gamer = this.connectService.connectedUser?.gamer;
      this.user.animals = this.connectService.connectedUser?.animals;
      this.user.organised = this.connectService.connectedUser?.organised;
      this.user.personality = this.connectService.connectedUser?.personality;
      this.preferencesForm.patchValue({
        alcoholDrinking: this.user.alcoholDrinking,
        smokes: this.user.smokes,
        sportPractice: this.user.sportPractice,
        parenthood: this.user.parenthood,
        gamer: this.user.gamer,
        animals: this.user.animals,
        organised: this.user.organised,
        personality: this.user.personality,
      });
      this.preferencesForm.markAsPristine();
    }
  }
}
