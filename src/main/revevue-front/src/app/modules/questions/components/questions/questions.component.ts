import { Component, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';

@Component({
    selector: 'app-questions',
    templateUrl: './questions.component.html',
    styleUrl: './questions.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class QuestionsComponent {
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

    constructor(private router: Router) { }

    navigateTo(url: string): void {
        this.router.navigateByUrl(url);
    }
}
