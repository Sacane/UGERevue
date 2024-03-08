import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {UserInfo} from "../models/UserInfo";
import {Observable} from "rxjs";
import {environment} from "../../../environment";
import {UserInfoUpdate} from "../models/UserInfoUpdate";
import {SimpleQuestion} from "../../../shared/models/question";
import {UserPasswordUpdate} from "../models/UserPasswordUpdate"

@Injectable({
    providedIn: 'root'
})
export class UserService {
    constructor(private http: HttpClient) {
    }

    getCurrentUserInfo(): Observable<UserInfo> {
        return this.http.get<UserInfo>(environment.apiUrl + 'users/current');
    }

    changeCurrentUserInfo(userInfoUpdate: UserInfoUpdate): Observable<any> {
        return this.http.patch(environment.apiUrl + 'users/current', userInfoUpdate);
    }

    changeCurrentUserPassword(userPasswordUpdate: UserPasswordUpdate): Observable<any> {
        return this.http.post(environment.apiUrl + 'users/current/password', userPasswordUpdate);
    }

    getCurrentUserQuestions(): Observable<Array<SimpleQuestion>> {
        return this.http.get<Array<SimpleQuestion>>(environment.apiUrl + 'questions/currentUser');
    }
}

