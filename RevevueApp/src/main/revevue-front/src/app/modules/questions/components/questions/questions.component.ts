import {Component, computed, inject, ViewEncapsulation} from '@angular/core';
import {UserService} from "../../../../shared/HttpServices";
import {QuestionService} from "../../../../shared/question.service";
import {toSignal} from "@angular/core/rxjs-interop";
import {SimpleQuestion} from "../../../../shared/models/question";
import {Router} from '@angular/router';

@Component({
    selector: 'app-questions',
    templateUrl: './questions.component.html',
    styleUrl: './questions.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class QuestionsComponent {
    numberQuestions = computed(() => this.questions().length)
    private readonly userService = inject(UserService)
    private readonly questionService = inject(QuestionService)
    questions = toSignal(this.questionService.getQuestions(), {initialValue: [] as SimpleQuestion[]})
    private router = inject(Router)

    public isLogin(): boolean {
        return this.userService.isLogin()
    }

    navigateTo(url: string): void {
        this.router.navigateByUrl(url).then();
    }
}
