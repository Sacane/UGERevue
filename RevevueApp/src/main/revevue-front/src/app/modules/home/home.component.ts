import {Component, OnInit, ViewEncapsulation, inject, signal} from '@angular/core';
import { LoginService } from "../../shared/HttpServices";
import { QuestionService } from '../../shared/question.service';
import {Observable, catchError, concat, map, of, tap} from 'rxjs';
import { Router } from '@angular/router';
import {UserService} from "../profil/services/user.service";
import {UserInfo} from "../profil/models/UserInfo";

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrl: './home.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class HomeComponent implements OnInit {
    questions$: Observable<any> = of({ lines: [], loading: false });
    private loginService = inject(LoginService);

    userInfo = signal<UserInfo|null>(null)
    private userService$ = inject(UserService).getCurrentUserInfo()
    constructor(private questionService: QuestionService, private router: Router) {
        if(this.isLogged()) {
            this.userService$.subscribe(response => this.userInfo.set(response))
        }
    }

    ngOnInit(): void {
        if (this.isLogged()) {
            this.questions$ = concat(
                of({ lines: [], loading: true }),
                this.questionService.getQuestionsFromFollowers().pipe(
                    map(response => ({ lines: response, loading: false })),
                    catchError(err => {
                        console.log(err);
                        return of([]);
                    })
                )
            );
        }
    }

    isLogged(): boolean {
        return this.loginService.isLogin();
    }

    navigateTo(url: string): void {
        this.router.navigateByUrl(url).then();
    }
}
