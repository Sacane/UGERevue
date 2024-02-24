import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { catchError, Observable, of, tap, throwError } from "rxjs";
import { Review } from "../modules/questions/models/review";
import { environment } from "../environment";

@Injectable({
    providedIn: 'root',
})
export class ReviewService {
    private client = inject(HttpClient)
    public findReviewByQuestionId(questionId: number, onError: (error: Error) => any = (err) => { console.error(err) }): Observable<Review[]> {
        return this.client.get<Review[]>(environment.apiUrl + 'reviews/questions/' + questionId)
            .pipe(catchError(err => {
                return throwError(() => { onError(err); });
            }));
    }
}
