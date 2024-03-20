export interface ProfileModelDTO {
    username: string,
    role: string
}

export interface ReviewWithTitleAndIdDTO{
    reviewId: number,
    reviewContent: string,
    questionTitle: string,
    creationDate: Date
}
