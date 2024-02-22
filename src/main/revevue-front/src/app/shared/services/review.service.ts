import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../environment";

@Injectable({
    providedIn: 'root',
})
export class ReviewService {
    private url: string = environment.apiUrl + 'reviews';

    constructor(private httpclient: HttpClient) { }

    public deleteReview(reviewId: string): Observable<any> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.httpclient.delete<any>(`${this.url}/${reviewId}`, { headers });
    }
}
