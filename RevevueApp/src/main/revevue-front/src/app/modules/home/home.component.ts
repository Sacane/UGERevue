import { Component, OnInit, inject } from '@angular/core';
import { UserService } from "../../shared/HttpServices";
import { QuestionService } from '../../shared/question.service';
import { catchError, of } from 'rxjs';

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
    private userService = inject(UserService);

    constructor(private questionService: QuestionService) { }

    ngOnInit(): void {
        if (this.isLogged()) {
            this.questionService.getQuestionsFromFollowers().pipe(
                catchError(err => {
                    console.log(err);
                    return of([]);
                })
            ).subscribe(response => {
                console.log(response);
            });
        }
    }

    isLogged(): boolean {
        return this.userService.isLogin();
    }
}
