export interface Question {
    id: number;
    author: string;
    creationDate: Date;
    title: string;
    questionContent: string;
    classContent: string;
    testClassContent?: string;
    testResults?: string;
    voteCount: number;
    commentCount: number;
}
