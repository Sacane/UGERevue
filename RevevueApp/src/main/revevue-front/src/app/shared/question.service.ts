import {inject, Injectable} from "@angular/core";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {environment} from "../environment";
import {catchError, Observable, tap, throwError} from "rxjs";
import {NewQuestionDTO} from "./models-out";
import {Question, SimpleQuestion} from "./models/question";


@Injectable({
    providedIn: 'root',
})
export class QuestionService {
    private HEADERS = new HttpHeaders().set('Content-Type', 'application/json');
    private readonly ROOT = environment.apiUrl + 'questions'
    private readonly SEARCH = this.ROOT + '/search'
    private client = inject(HttpClient)

    public searchQuestion(label: string, onError: (error: Error) => any = (err) => console.error(err)): Observable<SimpleQuestion[]> {
        let params = new HttpParams().append('label', label)
        // params.set('', '') TODO put username
        return this.client.get<SimpleQuestion[]>(this.SEARCH, {params: params})
            .pipe(tap(), catchError(err => throwError(() => onError(err))))
    }
    public createQuestion(newQuestionDTO: NewQuestionDTO, onError: (error: Error) => any = (err) => console.error(err)): Observable<number> {
        const formData = new FormData();
        formData.append('title', newQuestionDTO.title);
        formData.append('description', newQuestionDTO.description);
        formData.append('javaFile', newQuestionDTO.javaFile);
        if (newQuestionDTO.testFile) {
            formData.append('testFile', newQuestionDTO.testFile);
        }
        const headers = new HttpHeaders().set('Content-Type', 'multipart/form-data');
        return this.client.post<number>(this.ROOT, formData)
            .pipe(tap(), catchError(err => throwError(() => onError(err))))
    }

    public deleteQuestion(questionId: string): Observable<any> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.client.delete<any>(`${this.ROOT}/${questionId}`, {headers});
    }

    public getQuestions(onError: (error: Error) => any = (err) => console.error(err)): Observable<SimpleQuestion[]> {
        return this.client.get<SimpleQuestion[]>(this.ROOT)
            .pipe(tap(), catchError(err => throwError(() => onError(err))))
    }

    public findQuestionById(questionId: number, onError: (error: Error) => any = (err) => console.error(err)): Observable<Question> {
        return this.client.get<Question>(`${this.ROOT}/${questionId}`)
            .pipe(catchError(err => throwError(() => onError(err))))
    }

    public addReview(questionId: string, content: string, lineStart?: string, lineEnd?: string): Observable<any> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.client.post<any>(`${this.ROOT}/reviews`, {questionId, content, lineStart, lineEnd}, {headers});
    }
}
