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
    private readonly ROOT = environment.apiUrl + 'users'
    private readonly LOGIN = environment.apiUrl + 'login'
    private readonly LOGOUT = environment.apiUrl + 'logout'
    private readonly FOLLOW = this.ROOT + '/follow'
    private readonly UNFOLLOW = this.ROOT + '/unfollow'
    private client = inject(HttpClient)

    public registerUser(registerInfos: UserRegister, onError: (error: Error) => any = (err) => {console.error(err)}): Observable<UserIdDTO> {
        return this.client.post<UserIdDTO>(this.ROOT, registerInfos, { headers : this.HEADERS }).pipe(tap(() => {}), catchError(err => {
            return throwError(() => {onError(err);});
        }));
    }

    public login(userCredentials : UserCredentials, onError: (error: Error) => any = (err) => {console.error(err)}): Observable<UserConnectedDTO> {
        return this.client.post<UserConnectedDTO>(this.LOGIN, userCredentials, { headers : this.HEADERS })
            .pipe(tap(() => {
                localStorage.setItem("isLoggin", "true")
                console.log(document.cookie)
            }), catchError(err => {
            return throwError(() => {onError(err);});
        }));
    }

    public logout(onError: (error: Error) => any = (err) => {console.error(err)}) {
        return this.client.post(this.LOGOUT, null, { headers : this.HEADERS })
            .pipe(tap(() => localStorage.setItem("isLoggin", "false")), catchError(err => {
            return throwError(() => {onError(err);});
        }));
    }

    public getAllRegisteredUsers(onError: (error: Error) => any = (err) => {console.error(err)}) {
        return this.client.get<UserFollowInfo[]>(this.ROOT,{ headers : this.HEADERS })
            .pipe(tap(data => console.log('Data received:', data)),
                catchError(err => {
                return throwError(() => {onError(err);});
            }));
    }

    public follow(id: string, onError: (error: Error) => any = (err) => {console.error(err)}) {
        console.log(this.FOLLOW + '/' + id);
        return this.client.post(this.FOLLOW + '/' + id, null, { headers: this.HEADERS })
            .pipe(
                tap(response => console.log('Response from server:', response)),
                catchError(err => {
                    return throwError(() => { onError(err); });
                })
            );
    }

    public unfollow(userId: string, onError: (error: Error) => any = (err) => {console.error(err)}) {
        console.log(this.UNFOLLOW + '/' + userId)
        return this.client.post(this.UNFOLLOW + '/' + userId, null, { headers : this.HEADERS })
            .pipe(catchError(err => {
                return throwError(() => {onError(err);});
            }));
    }

    public isLogin() : boolean {
        const isLoggin = localStorage.getItem("isLoggin")
        return isLoggin !== null && isLoggin === "true"
    }

    public getALotFakeUserInfos(): Observable<UserFollowInfo[]> {
        return of([
            { username: 'Mathis', isFollowing: true, id: "1"},
            { username: 'Johan', isFollowing: true, id: "2"},
            { username: 'Yohann', isFollowing: true, id: "3"},
            { username: 'Quentin', isFollowing: true, id: "4"},
            { username: 'Clement', isFollowing: true, id: "5"},
            { username: 'Arnaud', isFollowing: true, id: "6"},
            { username: 'Remi', isFollowing: true, id: "7"},
            { username: 'Drake', isFollowing: true, id: "8"},
            { username: 'Kylian', isFollowing: true, id: "9"},
            { username: 'Kylian', isFollowing: true, id: "10"},
            { username: 'Kylian', isFollowing: true, id: "11"},
            { username: 'Kylian', isFollowing: true, id: "12"},
            { username: 'Kylian', isFollowing: true, id: "13"},
            { username: 'Kylian', isFollowing: true, id: "14"},
            { username: 'Yohann', isFollowing: true, id: "15"},
            { username: 'Yohann', isFollowing: true, id: "16"},
            { username: 'Yohann', isFollowing: true, id: "17"},
            { username: 'Yohann', isFollowing: true, id: "18"},
            { username: 'Yohann', isFollowing: true, id: "19"},
        ]);
    }
    public getFewFakeUserInfos(): Observable<UserFollowInfo[]> {
        return of([
            { username: 'Mathis', isFollowing: true, id: "1"},
            { username: 'Johan', isFollowing: true, id: "2"},
            { username: 'Yohann', isFollowing: true, id: "3"},
            { username: 'Quentin', isFollowing: true, id: "4"},
            { username: 'Clement', isFollowing: true, id: "5"},
        ]);
    }
}
