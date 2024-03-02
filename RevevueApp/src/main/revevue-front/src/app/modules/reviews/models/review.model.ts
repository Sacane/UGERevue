export interface Review {
    id: string;
    author: string;
    creationDate: string;
    content: string;
    citedCode?: string;
    upvotes: number;
    downvotes: number;
    reviews?: Review[];
}
