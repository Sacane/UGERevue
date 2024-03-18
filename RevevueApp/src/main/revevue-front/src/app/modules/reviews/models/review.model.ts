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
    vote: boolean | null
}

/**
 * public record ReviewOnReviewBodyDTO(long reviewId,
 * @NotNull @NotBlank String content, List<String> tagList) {
 * }
 */

export interface ReviewOnReviewBodyDTO {
    reviewId: number;
    content: String;
    tagList: Array<string>;
}

export interface ReviewQuestionTitleDTO {
    reviewContent: string;
    questionTitle: string;
}
