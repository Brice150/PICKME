export interface DeletedAccount {
  nickname: string;
  email: string;
  deletionDate: Date;
  totalDislikes: number;
  totalLikes: number;
  totalMatches: number;
  deletedBy: string;
}
