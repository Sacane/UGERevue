import { Component } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';

@Component({
    selector: 'app-question',
    templateUrl: './question.component.html',
    styleUrl: './question.component.scss'
})
export class QuestionComponent {
    id: string;

    constructor(private activatedRoute: ActivatedRoute) {
        this.id = this.activatedRoute.snapshot.params['id'];
    }
}
