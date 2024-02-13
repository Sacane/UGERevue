import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, Observable, of, tap, throwError} from 'rxjs';
import {environment} from "../environment";
import {UserCredentials, UserFollowInfo, UserRegister} from "./models-in";
import {UserConnectedDTO, UserIdDTO} from "./models-out";

@Injectable({
  providedIn: 'root',
})
export class UserService {

    private readonly HEADERS = new HttpHeaders().set('Content-Type', 'application/json');
    private isLoggedIn = false;
    private readonly ROOT = environment.apiUrl + 'users'

    private readonly AUTH = this.ROOT + 'auth/'
    private readonly LOGIN = environment.apiUrl + 'login'
    private readonly LOGOUT = environment.apiUrl + 'logout'
    private readonly TEST = this.ROOT + "/test"

    private client = inject(HttpClient)

    public registerUser(registerInfos: UserRegister, onError: (error: Error) => any = (err) => {console.error(err)}): Observable<UserIdDTO> {
        return this.client.post<UserIdDTO>(this.ROOT, registerInfos, { headers : this.HEADERS }).pipe(tap(() => this.isLoggedIn = true), catchError(err => {
            return throwError(() => {onError(err);});
        }));
    }

    public login(userCredentials : UserCredentials, onError: (error: Error) => any = (err) => {console.error(err)}): Observable<UserConnectedDTO> {
        return this.client.post<UserConnectedDTO>(this.LOGIN, userCredentials, { headers : this.HEADERS })
            .pipe(tap(() => localStorage.setItem("isLoggin", "true")), catchError(err => {
            return throwError(() => {onError(err);});
        }));
    }

    public logout(onError: (error: Error) => any = (err) => {console.error(err)}) {
        return this.client.post(this.LOGOUT, null, { headers : this.HEADERS })
            .pipe(tap(() => localStorage.setItem("isLoggin", "false")), catchError(err => {
            return throwError(() => {onError(err);});
        }));
    }

    public isLogin() : boolean {
        const isLoggin = localStorage.getItem("isLoggin")
        return isLoggin !== null && isLoggin === "true"
    }

    public getALotFakeUserInfos(): Observable<UserFollowInfo[]> {
        return of([
            { username: 'Mathis', isFollowing: true},
            { username: 'Johan', isFollowing: true},
            { username: 'Yohann', isFollowing: true},
            { username: 'Quentin', isFollowing: true},
            { username: 'Clement', isFollowing: true},
            { username: 'Arnaud', isFollowing: true},
            { username: 'Remi', isFollowing: true},
            { username: 'Drake', isFollowing: true},
            { username: 'Kylian', isFollowing: true},
            { username: 'Kylian', isFollowing: true},
            { username: 'Kylian', isFollowing: true},
            { username: 'Kylian', isFollowing: true},
            { username: 'Kylian', isFollowing: true},
            { username: 'Kylian', isFollowing: true},
            { username: 'Yohann', isFollowing: true},
            { username: 'Yohann', isFollowing: true},
            { username: 'Yohann', isFollowing: true},
            { username: 'Yohann', isFollowing: true},
            { username: 'Yohann', isFollowing: true},
        ]);
    }
    public getFewFakeUserInfos(): Observable<UserFollowInfo[]> {
        return of([
            { username: 'Mathis', isFollowing: true},
            { username: 'Johan', isFollowing: true},
            { username: 'Yohann', isFollowing: true},
            { username: 'Quentin', isFollowing: true},
            { username: 'Clement', isFollowing: true},
        ]);
    }
}
