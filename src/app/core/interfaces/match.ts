import { Message } from './message';
import { User } from './user';

export interface Match {
  user: User;
  messages: Message[];
}
