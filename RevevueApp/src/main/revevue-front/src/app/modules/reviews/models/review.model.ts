export interface Review {
    id: string;
    author: string;
    creationDate: string;
    content: string;
    citedCode: string | null;
    upvotes: number;
    downvotes: number;
    reviews?: Review[];
}

export interface DetailReviewResponseDTO extends Review{
    vote?: boolean
}
