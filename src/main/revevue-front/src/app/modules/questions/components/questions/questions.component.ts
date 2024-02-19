import { Component, computed, inject, ViewEncapsulation } from '@angular/core';
import { UserService } from "../../../../shared/HttpServices";
import { QuestionService } from "../../../../shared/question.service";
import { toSignal } from "@angular/core/rxjs-interop";
import { SimpleQuestion } from "../../models/question";
import { Router } from '@angular/router';

@Component({
    selector: 'app-questions',
    templateUrl: './questions.component.html',
    styleUrl: './questions.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class QuestionsComponent {
    private readonly userService = inject(UserService)
    private readonly questionService = inject(QuestionService)
    private router = inject(Router)
    questions = toSignal(this.questionService.getQuestions(), { initialValue: [] as SimpleQuestion[] })
    numberQuestions = computed(() => this.questions().length)

    public isLogin(): boolean {
        return this.userService.isLogin()
    }
    navigateTo(url: string): void {
        this.router.navigateByUrl(url);
    }
}
