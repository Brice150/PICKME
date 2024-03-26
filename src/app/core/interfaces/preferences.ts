import { AlcoholDrinking } from '../enums/alcohol-drinking';
import { Animals } from '../enums/animals';
import { Gamer } from '../enums/gamer';
import { Organised } from '../enums/organised';
import { Parenthood } from '../enums/parenthood';
import { Personality } from '../enums/personality';
import { Smokes } from '../enums/smokes';
import { SportPractice } from '../enums/sport-practice';

export interface Preferences {
  alcoholDrinking?: AlcoholDrinking;
  smokes?: Smokes;
  organised?: Organised;
  personality?: Personality;
  sportPractice?: SportPractice;
  animals?: Animals;
  parenthood?: Parenthood;
  gamer?: Gamer;
}
