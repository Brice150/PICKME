import { Picture } from './picture';

export interface User {
  id?: number;
  userRole?: string;
  birthDate: Date;
  gold?: boolean;

  // Pictures
  pictures?: Picture[];
  mainPicture?: string | ArrayBuffer;

  // Main Infos
  nickname: string;
  job: string;
  city: string;
  height?: number;

  // Gender and Age
  gender: string;
  genderSearch: string;
  minAge: number;
  maxAge: number;

  // Connection Infos
  email: string;
  password?: string;

  // Description
  description?: string;

  // Preferences
  alcoholDrinking?: string;
  smokes?: string;
  organised?: string;
  personality?: string;
  sportPractice?: string;
  animals?: string;
  parenthood?: string;
  gamer?: string;

  // Stats
  totalDislikes?: number;
  totalLikes?: number;
  totalMatches?: number;
}
