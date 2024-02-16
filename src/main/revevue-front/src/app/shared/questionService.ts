import {inject, Injectable} from "@angular/core";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {environment} from "../environment";
import {catchError, Observable, of, tap, throwError} from "rxjs";
import {NewQuestionDTO} from "./models-out";
import {SimpleQuestion} from "../modules/questions/models/question";

@Injectable({
    providedIn: 'root',
})
export class QuestionService {
    private HEADERS = new HttpHeaders().set('Content-Type', 'application/json');
    private readonly ROOT = environment.apiUrl + 'questions'

    private client = inject(HttpClient)
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
    public getQuestions(onError: (error: Error) => any = (err) => console.error(err)): Observable<SimpleQuestion> {
        return this.client.get<SimpleQuestion>(this.ROOT)
            .pipe(tap(), catchError(err => throwError(() => onError(err))))
    }
}
