import { GenderAdmin } from '../enums/gender-admin';

export interface AdminSearch {
  nickname: string;
  genders: GenderAdmin[];
  minAge: number;
  maxAge: number;
  distance: number;
}
