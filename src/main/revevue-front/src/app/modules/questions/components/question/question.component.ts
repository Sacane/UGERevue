import {Component, ViewEncapsulation} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

@Component({
    selector: 'app-question',
    templateUrl: './question.component.html',
    styleUrl: './question.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class QuestionComponent {
    id: string;
    title: string = "TestQuestion";
    content: string = "class Dragon { }"
    score: number = 1337;
    author: string = "DaLeetHaxorz";
    creationDate: Date = new Date();

    constructor(private activatedRoute: ActivatedRoute) {
        this.id = this.activatedRoute.snapshot.params['id'];
    }
}
