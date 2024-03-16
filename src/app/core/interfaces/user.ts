import { Picture } from './picture';

export interface User {
  id?: number;
  email: string;
  password?: string;
  userRole?: string;
  gender: string;
  genderSearch: string;
  minAge: number;
  maxAge: number;
  gold?: boolean;

  // Main
  nickname: string;
  job: string;
  birthDate: Date;
  city: string;
  mainPicture?: string | ArrayBuffer;

  // Description
  description?: string;

  // Chips
  height?: number;
  alcoholDrinking?: string;
  smokes?: string;
  organised?: string;
  personality?: string;
  sportPractice?: string;
  animals?: string;
  parenthood?: string;
  gamer?: string;

  // Attached to a user
  pictures?: Picture[];

  // Stats
  totalDislikes?: number;
  totalLikes?: number;
  totalMatches?: number;
}
