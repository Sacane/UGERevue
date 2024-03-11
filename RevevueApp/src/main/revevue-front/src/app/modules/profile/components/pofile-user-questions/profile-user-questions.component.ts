import {Component, OnInit} from '@angular/core';
import {AsyncPipe, NgIf} from "@angular/common";
import {ProfileInfoContentComponent} from "../profile-info/profile-info-content/profile-info-content.component";
import {Observable} from "rxjs";
import {UserService} from "../../services/user.service";
import {SimpleQuestion} from "../../../../shared/models/question";
import {
    ProfileUserQuestionsContentComponent
} from "./profile-user-questions-content/profile-user-questions-content.component";

@Component({
    selector: 'app-profile-user-questions',
    standalone: true,
    imports: [
        AsyncPipe,
        NgIf,
        ProfileInfoContentComponent,
        ProfileUserQuestionsContentComponent
    ],
    templateUrl: './profile-user-questions.component.html',
    styleUrl: './profile-user-questions.component.scss'
})
export class ProfileUserQuestionsComponent implements OnInit{
    userQuestions$!: Observable<Array<SimpleQuestion>>;

    constructor(private userService: UserService) {
    }

    ngOnInit(): void {
        this.userQuestions$ = this.userService.getCurrentUserQuestions();
    }
}
