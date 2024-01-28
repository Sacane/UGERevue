import {inject, Injectable} from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {environment} from "../environment";
import {UserCredentials, UserRegister} from "./models-in";
import {UserConnectedDTO, UserIdDTO} from "./models-out";

@Injectable({
  providedIn: 'root',
})
export class UserService {

    private readonly HEADERS = new HttpHeaders().set('Content-Type', 'application/json');

    private readonly ROOT = environment.apiUrl + 'users/'

    private readonly AUTH = this.ROOT + 'auth/'

    private client = inject(HttpClient)

    public registerUser(registerInfos: UserRegister, onError: (error: Error) => any = (err) => {console.error(err)}): Observable<UserIdDTO> {
        return this.client.post<UserIdDTO>(this.ROOT, registerInfos, { headers : this.HEADERS }).pipe(catchError(err => {
            return throwError(() => {onError(err);});
        }));
    }

    public login(userCredentials : UserCredentials, onError: (error: Error) => any = (err) => {console.error(err)}): Observable<UserConnectedDTO> {
        return this.client.post<UserConnectedDTO>(this.AUTH, userCredentials, { headers : this.HEADERS }).pipe(catchError(err => {
            return throwError(() => {onError(err);});
        }));
    }

}
