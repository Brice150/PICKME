import { AlcoholDrinking } from '../enums/alcohol-drinking';
import { Animals } from '../enums/animals';
import { Gamer } from '../enums/gamer';
import { Gender } from '../enums/gender';
import { Organised } from '../enums/organised';
import { Parenthood } from '../enums/parenthood';
import { Personality } from '../enums/personality';
import { Smokes } from '../enums/smokes';
import { SportPractice } from '../enums/sport-practice';
import { Picture } from './picture';

export interface User {
  id?: number;
  userRole?: string;
  birthDate: Date;
  gold?: boolean;

  // Pictures
  pictures?: Picture[];

  // Main Infos
  nickname: string;
  job: string;
  city: string;
  height?: number;

  // Gender and Age
  gender: Gender;
  genderSearch: Gender;
  minAge: number;
  maxAge: number;

  // Connection Infos
  email: string;
  password?: string;

  // Description
  description?: string;

  // Preferences
  alcoholDrinking?: AlcoholDrinking;
  smokes?: Smokes;
  organised?: Organised;
  personality?: Personality;
  sportPractice?: SportPractice;
  animals?: Animals;
  parenthood?: Parenthood;
  gamer?: Gamer;

  // Stats
  totalDislikes?: number;
  totalLikes?: number;
  totalMatches?: number;
}
