import { Component, Input, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';

@Component({
    selector: 'app-questions-displayer',
    templateUrl: './questions-displayer.component.html',
    styleUrl: './questions-displayer.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class QuestionsDisplayerComponent {
    @Input() questions: any[] = [];

    constructor(private router: Router) { }

    navigateTo(url: string): void {
        this.router.navigateByUrl(url).then();
    }
}
