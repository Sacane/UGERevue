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
}