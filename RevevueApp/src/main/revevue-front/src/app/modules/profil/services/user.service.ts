import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {UserInfo} from "../models/UserInfo";
import {Observable} from "rxjs";
import {UserInfoUpdate} from "../models/UserInfoUpdate";
import {SimpleQuestion} from "../../../shared/models/question";
import {UserPasswordUpdate} from "../models/UserPasswordUpdate"
import {UserFollowing} from "../models/UserFollowing";
import {environment} from "../../../../environments/environment";
import {UserInfoSecure} from "../models/UserInfoSecure";
import {UserReview} from "../models/UserReview";

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

    getCurrentUserFollowing(): Observable<Array<UserFollowing>> {
        return this.http.get<Array<UserFollowing>>(environment.apiUrl + 'users/current/following');
    }

    getRecommendedReviews(questionContent: string): Observable<any> {
        return this.http.post<any>(environment.apiUrl + 'users/current/recommendedReview', {questionContent});
    }

    getUserInfo(userId: number): Observable<UserInfoSecure> {
        return this.http.get<UserInfoSecure>(environment.apiUrl + `users/${userId}/profile`);
    }

    getUserQuestions(userId: number): Observable<Array<SimpleQuestion>> {
        return this.http.get<Array<SimpleQuestion>>(environment.apiUrl + `users/${userId}/questions`);
    }

    getUserReviews(userId: number): Observable<Array<UserReview>> {
        return this.http.get<Array<UserReview>>(environment.apiUrl + `users/${userId}/reviews`);
    }
}

