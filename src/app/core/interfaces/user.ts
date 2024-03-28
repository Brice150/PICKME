import { GenderAge } from './gender-age';
import { Geolocation } from './geolocation';
import { Notification } from './notification';
import { Picture } from './picture';
import { Preferences } from './preferences';
import { Stats } from './stats';

export interface User {
  id?: number;
  userRole?: string;
  birthDate: Date;
  gold?: boolean;
  nickname: string;
  job: string;
  height?: number;
  email: string;
  password?: string;
  description?: string;
  genderAge: GenderAge;
  preferences?: Preferences;
  geolocation: Geolocation;
  pictures?: Picture[];
  stats?: Stats;
  notifications?: Notification[];
}
