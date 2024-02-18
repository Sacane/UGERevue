
export interface ReviewFromReview {
    author: string;
    content: string;
    upvotes: number;
    downvotes: number;
    creationDate: Date
}

export interface Review {
    author: string;
    creationDate: Date;
    content: string;
    citedCode?: string;
    upvotes: number;
    downvotes: number;
    reviews: Array<ReviewFromReview>
}

