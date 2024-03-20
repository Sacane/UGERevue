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

export interface SimpleQuestion {
    id: number,
    title: string,
    description: string,
    userName: string,
    date: string,
    nbVotes: number,
    nbAnswers: number
}
