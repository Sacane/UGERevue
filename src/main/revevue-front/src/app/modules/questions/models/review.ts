export interface Review {
    author: string;
    content: string;
    citedCode?: string;
    upvotes: number;
    downvotes: number;
    reviews: Array<Review>;
}
