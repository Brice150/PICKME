import { Gender } from '../enums/gender';

export interface GenderAge {
  gender: Gender;
  genderSearch: Gender;
  minAge: number;
  maxAge: number;
}
