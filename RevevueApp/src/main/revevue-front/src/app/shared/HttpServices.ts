import {Injectable, inject} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {catchError, Observable, switchMap, tap, throwError} from 'rxjs';
import {UserCredentials, UserFollowInfo, UserRegister} from "./models-in";
import {UserConnectedDTO, UserIdDTO} from "./models-out";
import {environment} from "../../environments/environment";
import {ToastrService} from "ngx-toastr";

@Injectable({
    providedIn: 'root'
})
export class LoginService {
    private readonly HEADERS = new HttpHeaders().set('Content-Type', 'application/json');
    private readonly ROOT = environment.apiUrl + 'users'
    private readonly LOGIN = environment.apiUrl + 'login'
    private readonly LOGOUT = environment.apiUrl + 'logout'
    private readonly FOLLOW = this.ROOT + '/follow'
    private readonly UNFOLLOW = this.ROOT + '/unfollow'
    private readonly IS_LOGGED = environment.apiUrl + 'logged'

    private toastrService = inject(ToastrService)
    constructor(private http: HttpClient) {
    }

    public registerUser(registerInfos: UserRegister, onError: (error: HttpErrorResponse) => any = (err) => {
        console.error(err.error.message)
    }): Observable<UserIdDTO> {
        return this.http.post<UserIdDTO>(this.ROOT, registerInfos, {headers: this.HEADERS}).pipe(
            switchMap(response => {
                return this.login({login: registerInfos.login, password: registerInfos.password}).pipe(
                    tap(response => {
                        localStorage.setItem('isLoggin', 'true');
                        localStorage.setItem('username', response.username);
                        localStorage.setItem('role', response.role);
                    }),
                    catchError(err => {
                        return throwError(() => {
                            onError(err);
                        });
                    })
                );
            }), catchError(err => {
                return throwError(() => {
                    onError(err);
                });
            })
        );
    }

    public login(userCredentials: UserCredentials, onError: (error: Error) => any = (err) => {
        console.error(err)
    }): Observable<UserConnectedDTO> {
        return this.http.post<UserConnectedDTO>(this.LOGIN, userCredentials, {headers: this.HEADERS}).pipe(
            tap(response => {
                localStorage.setItem('isLoggin', 'true');
                localStorage.setItem('username', response.displayName);
                localStorage.setItem('role', response.role);
            }), catchError(err => {
                return throwError(() => {
                    onError(err);
                });
            })
        );
    }

    public logout(onError: (error: Error) => any = (err) => {
        console.error(err)
    }) {
        return this.http.post(this.LOGOUT, null, {headers: this.HEADERS})
            .pipe(tap(() => {
                localStorage.setItem("isLoggin", "false");
                localStorage.setItem('username', '');
                localStorage.setItem('role', '');
                localStorage.setItem('id', '')
                localStorage.clear();
            }), catchError(err => {
                return throwError(() => {
                    onError(err);
                });
            }));
    }

    public getId(): string | null {
        return localStorage.getItem('id')
    }

    public getRole(): string {
        const role = localStorage.getItem('role');
        return role ? role : '';
    }

    public getLogin(): string {
        const username = localStorage.getItem('username');
        return username ? username : '';
    }

    public isLogin(): boolean {
        const isLoggin = localStorage.getItem("isLoggin")
        return isLoggin !== null && isLoggin === "true"
    }

    public getAllRegisteredUsers(onError: (error: Error) => any = (err) => {
        console.error(err)
    }) {
        return this.http.get<UserFollowInfo[]>(this.ROOT, {headers: this.HEADERS})
            .pipe(tap(data => console.log('Data received:', data)),
                catchError(err => {
                    return throwError(() => {
                        onError(err);
                    });
                }));
    }

    public follow(id: string, onError: (error: Error) => any = (err) => {
        console.error(err)
    }) {
        return this.http.post(this.FOLLOW + '/' + id, null, {headers: this.HEADERS})
            .pipe(
                tap(response => console.log('Response from server:', response)),
                catchError(err => {
                    return throwError(() => {
                        onError(err);
                    });
                })
            );
    }

    public unfollow(userId: string, onError: (error: Error) => any = (err) => {
        console.error(err)
    }) {
        return this.http.post(this.UNFOLLOW + '/' + userId, null, {headers: this.HEADERS})
            .pipe(catchError(err => {
                return throwError(() => {
                    onError(err);
                });
            }));
    }

    public isLogged(onSuccess: () => void = () => {}, onError: (error: HttpErrorResponse) => any = (err) => {
        this.toastrService.error('Votre session a expiré')
    }){
        this.http.get(this.IS_LOGGED).pipe(
            tap(() => onSuccess()),
            catchError(err => throwError(() => onError(err)))
        ).subscribe();
    }

}
