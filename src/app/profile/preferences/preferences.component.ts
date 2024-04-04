import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  Input,
  Output,
  ViewChild,
} from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { AlcoholDrinking } from '../../core/enums/alcohol-drinking';
import { Animals } from '../../core/enums/animals';
import { Gamer } from '../../core/enums/gamer';
import { Organised } from '../../core/enums/organised';
import { Parenthood } from '../../core/enums/parenthood';
import { Personality } from '../../core/enums/personality';
import { Smokes } from '../../core/enums/smokes';
import { SportPractice } from '../../core/enums/sport-practice';
import { Preference } from '../../core/interfaces/preference';
import { User } from '../../core/interfaces/user';
import { PaginatorComponent } from '../../shared/components/paginator/paginator.component';
import { PreferenceComponent } from './preference/preference.component';

@Component({
  selector: 'app-preferences',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    PreferenceComponent,
    PaginatorComponent,
  ],
  templateUrl: './preferences.component.html',
  styleUrl: './preferences.component.css',
})
export class PreferencesComponent {
  @Input() user!: User;
  @Output() updateEvent: EventEmitter<string> = new EventEmitter<string>();
  @ViewChild('preference') preference?: PreferenceComponent;
  currentPreferenceIndex: number = 0;
  alcoholDrinking: string[] = Object.values(AlcoholDrinking);
  smokes: string[] = Object.values(Smokes);
  sportPractice: string[] = Object.values(SportPractice);
  parenthood: string[] = Object.values(Parenthood);
  gamer: string[] = Object.values(Gamer);
  animals: string[] = Object.values(Animals);
  organised: string[] = Object.values(Organised);
  personality: string[] = Object.values(Personality);
  preferences: Preference[] = [
    {
      title: 'Alcohol Drinking',
      name: 'alcoholDrinking',
      elements: this.alcoholDrinking,
      class: 'bx bxs-drink',
    },
    {
      title: 'Smoking',
      name: 'smokes',
      elements: this.smokes,
      class: 'bx bx-wind',
    },
    {
      title: 'Sport Pratice',
      name: 'sportPractice',
      elements: this.sportPractice,
      class: 'bx bx-dumbbell',
    },
    {
      title: 'Parenthood',
      name: 'parenthood',
      elements: this.parenthood,
      class: 'bx bx-child',
    },
    {
      title: 'Gamer',
      name: 'gamer',
      elements: this.gamer,
      class: 'bx bxs-joystick',
    },
    {
      title: 'Animals',
      name: 'animals',
      elements: this.animals,
      class: 'bx bxs-cat',
    },
    {
      title: 'Organised',
      name: 'organised',
      elements: this.organised,
      class: 'bx bxs-layer',
    },
    {
      title: 'Personality',
      name: 'personality',
      elements: this.personality,
      class: 'bx bxs-user-voice',
    },
  ];

  updatePreferences(): void {
    this.updateEvent.emit('Preferences Updated');
  }

  handlePageEvent(pageIndex: number) {
    this.currentPreferenceIndex = pageIndex;
    if (this.preference?.preferenceForm.dirty) {
      this.preference?.cancel();
    }
  }
}
