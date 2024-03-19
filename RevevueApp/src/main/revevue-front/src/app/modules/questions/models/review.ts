export interface ReviewFromReview {
    author: string;
    content: string;
    upvotes: number;
    downvotes: number;
    creationDate: string;
}

export interface Review {
    id: string;
    author: string;
    creationDate: string;
    content: string;
    citedCode?: string;
    upvotes: number;
    downvotes: number;
    reviews: ReviewFromReview[];
    tags?: string[];
    lineStart?: number;
    lineEnd?: number;
}

export interface QuestionReviewCreateDTO{
    questionId: number;
    content: string;
    lineStart?: number;
    lineEnd?: number;
    tags: Array<string>
}
