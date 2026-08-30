import { Preferences } from './preferences';

export type PreferenceName = keyof Preferences;

export interface Preference {
  title: string;
  name: PreferenceName;
  elements: string[];
  class: string;
}
