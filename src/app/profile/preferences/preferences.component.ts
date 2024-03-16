import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { User } from '../../core/interfaces/user';
import { MatChipsModule } from '@angular/material/chips';

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

  alcoholDrinking: string[] = [
    'Never drinks alcohol',
    'Drinks sometimes alcohol',
    'Drinks a lot alcohol',
  ];
  smokes: string[] = ['Never smokes', 'Smokes sometimes', 'Smokes a lot'];
  sportPractice: string[] = [
    'Never practice sport',
    'Practices sport sometimes',
    'Athlete',
  ];
  parenthood: string[] = [
    "Doesn't want children",
    'Will want children someday',
    'Has children',
  ];
  gamer: string[] = [
    'Never play video games',
    'Play video games sometimes',
    'Play video games a lot',
  ];
  animals: string[] = ["Doesn't like animals", 'Likes animals', 'Has animals'];
  organised: string[] = ['Messy', 'Reasonably organised', 'Very organised'];
  personality: string[] = ['Introvert', 'Ambivert', 'Extravert'];

  constructor(private fb: FormBuilder) {}

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
    this.setPreferences();
    this.updateEvent.emit('Preferences Updated');
    this.preferencesForm.markAsPristine();
  }

  setPreferences() {
    if (this.user) {
      //TODO
    }
  }

  isSelected(attribute: string, property: string): boolean {
    let selected: boolean = false;
    if (attribute && this.user) {
      selected = (this.user as any)[property] === attribute;
    }
    return selected;
  }

  select(attribute: string, property: string) {
    if (attribute && this.user) {
      (this.user as any)[property] = attribute;
    }
  }
}
