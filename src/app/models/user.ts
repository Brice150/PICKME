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
    relationshipType: string;
    birthDate: Date;
    city: string;
    height: string;
    languages: string;
    job: string;
    description: string;
    smokes: string;
    alcoholDrinking: string;
    organised: string;
    personality: string;
    sportPractice: string;
    animals: string;
    parenthood: string;
    gamer: string;
    activities: string;
}