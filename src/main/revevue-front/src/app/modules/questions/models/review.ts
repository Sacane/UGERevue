export interface Review {
    author: string;
    creationDate: Date;
    content: string;
    citedCode?: string;
    upvotes: number;
    downvotes: number;
    reviews: Array<Review>;
}
