import {Component, inject, ViewEncapsulation} from '@angular/core';
import {UserService} from "../../../../shared/HttpServices";

@Component({
    selector: 'app-questions',
    templateUrl: './questions.component.html',
    styleUrl: './questions.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class QuestionsComponent {
    private readonly userService = inject(UserService)
    questions: any[] = [
        {
            id: 123,
            title: 'TITLE 1',
            description: 'DESCRIPTION 1',
            userName: 'qtdrake',
            tags: ['java', 'jee', 'spring'],
            date: '26/01/2024',
            nbVotes: 129222,
            nbAnswers: 1903,
            nbViews: 55378
        },
        {
            id: 1234,
            title: 'TITLE 1',
            description: 'DESCRIPTION 1',
            userName: 'qtdrake',
            tags: ['java', 'jee', 'spring'],
            date: '26/01/2024',
            nbVotes: 129222,
            nbAnswers: 1903,
            nbViews: 55378
        }
    ];
    public isLoggin(): boolean {
        return this.userService.isLogin()
    }
}
