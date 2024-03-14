import { Component, ViewEncapsulation } from '@angular/core';
import { Observable } from 'rxjs';
import { SimpleQuestion } from '../../../../shared/models/question';
import { UserService } from '../../../profile/services/user.service';

@Component({
    selector: 'app-profil-questions',
    templateUrl: './profil-questions.component.html',
    styleUrl: './profil-questions.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class ProfilQuestionsComponent {
    userQuestions$!: Observable<SimpleQuestion[]>;

    constructor(private userService: UserService) {}

    ngOnInit(): void {
        this.userQuestions$ = this.userService.getCurrentUserQuestions();
    }
}
