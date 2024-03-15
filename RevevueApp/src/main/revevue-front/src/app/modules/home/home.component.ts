import { Component, OnInit, ViewEncapsulation, inject } from '@angular/core';
import { UserService } from "../../shared/HttpServices";
import { QuestionService } from '../../shared/question.service';
import { Observable, catchError, concat, map, of } from 'rxjs';
import { Router } from '@angular/router';

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrl: './home.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class HomeComponent implements OnInit {
    questions$: Observable<any> = of({ lines: [], loading: false });
    private userService = inject(UserService);

    constructor(private questionService: QuestionService, private router: Router) { }

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
        return this.userService.isLogin();
    }

    navigateTo(url: string): void {
        this.router.navigateByUrl(url).then();
    }
}
