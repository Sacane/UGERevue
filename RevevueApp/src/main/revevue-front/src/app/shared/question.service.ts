import {inject, Injectable} from "@angular/core";
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {catchError, Observable, tap, throwError} from "rxjs";
import {NewQuestionDTO} from "./models-out";
import {Question, SimpleQuestion} from "./models/question";
import {QuestionReviewCreateDTO} from "../modules/questions/models/review";
import {environment} from "../../environments/environment";


@Injectable({
    providedIn: 'root',
})
export class QuestionService {
    private HEADERS = new HttpHeaders().set('Content-Type', 'application/json');
    private readonly ROOT = environment.apiUrl + 'questions'
    private readonly SEARCH = this.ROOT + '/search'
    private client = inject(HttpClient)

    public searchQuestion(label: string, username?: string, onError: (error: HttpErrorResponse) => any = (err) => console.error(err)): Observable<SimpleQuestion[]> {
        return this.client.post<SimpleQuestion[]>(this.SEARCH, {questionLabelSearch: label, usernameSearch: username})
            .pipe(tap(), catchError(err => throwError(() => onError(err))))
    }

    public createQuestion(newQuestionDTO: NewQuestionDTO, onError: (error: HttpErrorResponse) => any = (err) => console.error(err)): Observable<number> {
        const formData = new FormData();
        formData.append(encodeURIComponent('title'), newQuestionDTO.title);
        formData.append(encodeURIComponent('description'), newQuestionDTO.description);
        formData.append('javaFile', newQuestionDTO.javaFile);
        if (newQuestionDTO.testFile) {
            formData.append('testFile', newQuestionDTO.testFile);
        }
        const headers = new HttpHeaders().set('Content-Type', 'multipart/form-data');
        return this.client.post<number>(this.ROOT, formData)
            .pipe(tap(), catchError(err => throwError(() => onError(err))))
    }

    public updateQuestion(questionId: string, question: any): Observable<any> {
        const formData = new FormData();
        formData.append('description', question.description);
        formData.append('testFile', question.testFile);

        return this.client.post<any>(`${this.ROOT}/${questionId}`, formData);
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

    public addReview(questionId: string, content: string, lineStart?: string, lineEnd?: string, tagList: Array<String> = []): Observable<any> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');
        return this.client.post<QuestionReviewCreateDTO>(`${this.ROOT}/reviews`, {
            questionId: questionId, content: content, lineStart: lineStart, lineEnd: lineEnd, tags: tagList
        }, {headers});
    }

    public getQuestionsFromFollowers(): Observable<any[]> {
        return this.client.get<any[]>(`${this.ROOT}/followers`);
    }

    public vote(questionId: string, up: boolean): Observable<any> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        if (up) {
            return this.client.post<void>(`${environment.apiUrl}votes/upvote/questions/${questionId}`, {}, {headers});
        }
        else {
            return this.client.post<void>(`${environment.apiUrl}votes/downvote/questions/${questionId}`, {}, {headers});
        }
    }

    public cancelVote(questionId: string): Observable<any> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.client.delete<void>(`${this.ROOT}/${questionId}/cancelVote`, {headers});
    }
}
