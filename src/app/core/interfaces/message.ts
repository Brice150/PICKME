export interface Message {
  id: number;
  content?: string;
  date: Date;
  sender: string;
  fkReceiver?: number;
}
