import {Component, computed, inject, ViewEncapsulation} from '@angular/core';
import {UserService} from "../../../../shared/HttpServices";
import {QuestionService} from "../../../../shared/question.service";
import {toSignal} from "@angular/core/rxjs-interop";
import {SimpleQuestion} from "../../models/question";

@Component({
    selector: 'app-questions',
    templateUrl: './questions.component.html',
    styleUrl: './questions.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class QuestionsComponent {
    private readonly userService = inject(UserService)
    private readonly questionService = inject(QuestionService)

    questions = toSignal(this.questionService.getQuestions(), {initialValue: [] as SimpleQuestion[]})
    numberQuestions = computed(() => this.questions().length)

    public isLogin(): boolean {
        return this.userService.isLogin()
    }
}
