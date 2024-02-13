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
}
//TODO rajouter les tags plus tard
export interface SimpleQuestion {
    id: number,
    title: string,
    description: string,
    userName: string,
    date: string,
    nbVotes: number,
    nbAnswers: number
}
