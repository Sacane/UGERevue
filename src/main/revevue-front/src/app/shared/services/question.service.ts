import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, delay, of } from "rxjs";
import { environment } from "../../environment";

@Injectable({
    providedIn: 'root',
})
export class QuestionService {
    private url: string = environment.apiUrl + '/questions';

    constructor(private httpclient: HttpClient) { }

    public deleteQuestion(questionId: string): Observable<any> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');
        console.log('SERVICE DELETE');

        return of({ delete: true }).pipe(delay(1000));
        // return this.httpclient.delete<any>(this.url + '/questionId', { headers });
    }
}
