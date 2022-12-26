import { Authority } from "./authority";
import { Message } from "./message";
import { Token } from "./token";

export interface User {
    id: number;
    email: string;
    enabled: boolean;
    nickname: string;
    locked: boolean;
    password: string;
    tokens: Token[];
    userRole: string;
    messagesSended: Message[];
    messagesReceived: Message[];
    credentialsNonExpired: boolean;
    accountNonExpired: boolean;
    username: string;
    accountNonLocked: boolean;
    authorities: Authority[];
    gender: string;
    genderSearch: string;
    relationshipSearch: string;
    birthDate: Date;
    city: string;
}