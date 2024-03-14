import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {catchError, Observable, throwError} from "rxjs";
import {environment} from "../../environment";
import {DetailReviewResponseDTO, Review} from "../../modules/reviews/models/review.model";

@Injectable({
    providedIn: 'root',
})
export class ReviewService {
    private url: string = environment.apiUrl + 'reviews';

    constructor(private httpclient: HttpClient) { }

    public getDetails(reviewId: string): Observable<DetailReviewResponseDTO> {
        return this.httpclient.get<any>(`${this.url}/${reviewId}`);
    }

    public deleteReview(reviewId: string): Observable<any> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.httpclient.delete<any>(`${this.url}/${reviewId}`, { headers });
    }

    public findReviewByQuestionId(questionId: number, onError: (error: Error) => any = (err) => { console.error(err) }): Observable<Review[]> {
        return this.httpclient.get<Review[]>(environment.apiUrl + 'reviews/questions/' + questionId).pipe(
            catchError(err => {
                return throwError(() => { onError(err); });
            })
        );
    }

    public addReview(reviewId: string, content: string, tagList: Array<string> = []): Observable<Review> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.httpclient.post<Review>(this.url, { reviewId: reviewId, content: content, tagList: tagList }, { headers });
    }

    public vote(reviewId: string, up: boolean): Observable<any> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.httpclient.post<void>(`${this.url}/${reviewId}/vote`, { up }, { headers });
    }

    public cancelVote(reviewId: string): Observable<any> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.httpclient.delete<void>(`${this.url}/${reviewId}/cancelVote`, { headers });
    }
}
