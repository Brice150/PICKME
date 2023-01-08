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
    mainPicture: string | ArrayBuffer | null;
    gender: string;
    genderSearch: string;
    relationshipType: string;
    birthDate: Date;
    city: string;
    height: string | null;
    languages: string | null;
    job: string | null;
    description: string | null;
    smokes: string | null;
    alcoholDrinking: string | null;
    organised: string | null;
    personality: string | null;
    sportPractice: string | null;
    animals: string | null;
    parenthood: string | null;
    gamer: string | null;
    activities: string | null;
}