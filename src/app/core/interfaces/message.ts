export interface Message {
    id: number;
    content: string;
    date: Date;
    fromUser: string;
    toUser: string;
    fkReceiver: number;
    fkSender: number;
    sender: string | null;
}