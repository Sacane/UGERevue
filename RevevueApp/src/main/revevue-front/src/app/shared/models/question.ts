export interface Question {
    id: number;
    author: string;
    creationDate: string;
    tags: Array<String>;
    title: string;
    questionContent: string;
    classContent: string;
    testClassContent?: string;
    testResults?: string;
    voteCount: number;
    commentCount: number;
    upvotes?: number;
    downvotes?: number;
    vote?: boolean | null;
}
export interface SimpleQuestion {
    id: number,
    title: string,
    description: string,
    userName: string,
    date: Date,
    nbVotes: number,
    nbAnswers: number
}
