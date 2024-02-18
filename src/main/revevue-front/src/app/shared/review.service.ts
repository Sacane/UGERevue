import {Injectable, inject} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable, of} from "rxjs";
import {Review} from "../modules/questions/models/review";

@Injectable({
    providedIn: 'root',
})
export class ReviewService {
    private client = inject(HttpClient)
    public findReviewByQuestionId(): Observable<Review> {

        return of()
    }
}
