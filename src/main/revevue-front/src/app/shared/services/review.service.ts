import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, catchError, throwError } from "rxjs";
import { environment } from "../../environment";
import { Review } from "../../modules/questions/models/review";

@Injectable({
    providedIn: 'root',
})
export class ReviewService {
    private url: string = environment.apiUrl + 'reviews';

    constructor(private httpclient: HttpClient) { }

    public getDetails(reviewId: string): Observable<any> {
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

    public addReview(reviewId: string, content: string): Observable<any> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.httpclient.post<any>(this.url, { reviewId, content }, { headers });
    }
}
