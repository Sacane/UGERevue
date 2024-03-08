import {Component, Input} from '@angular/core';
import {SimpleQuestion} from "../../../../../shared/models/question";
import {DatePipe, JsonPipe} from "@angular/common";
import {Router} from "@angular/router";

@Component({
    selector: 'app-profile-user-questions-content',
    standalone: true,
    imports: [
        JsonPipe,
        DatePipe
    ],
    templateUrl: './profile-user-questions-content.component.html',
    styleUrl: './profile-user-questions-content.component.scss'
})
export class ProfileUserQuestionsContentComponent {
    @Input({required: true}) questions!: Array<SimpleQuestion>

    constructor(private router: Router) {

    }

    navigateToQuestion(id: number) {
        this.router.navigateByUrl('/questions/' + id).then();
    }
}
