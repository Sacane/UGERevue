export interface Question {
    id: number;
    author: string;
    creationDate: Date;
    tags: Array<String>;
    title: string;
    questionContent: string;
    classContent: string;
    testClassContent?: string;
    testResults?: string;
    voteCount: number;
    commentCount: number;
}
