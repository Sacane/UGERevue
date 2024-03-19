import {Component, computed, inject, signal, ViewEncapsulation} from '@angular/core';
import {LoginService} from "../../../../shared/HttpServices";
import {QuestionService} from "../../../../shared/question.service";
import {toSignal} from "@angular/core/rxjs-interop";
import {SimpleQuestion} from "../../../../shared/models/question";
import {Router} from '@angular/router';
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
    selector: 'app-questions',
    templateUrl: './questions.component.html',
    styleUrl: './questions.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class QuestionsComponent {
    numberQuestions = computed(() => this.questions().length)
    private readonly userService = inject(LoginService)
    private readonly questionService = inject(QuestionService)
    questions = /*signal([] as SimpleQuestion[])*/toSignal(this.questionService.getQuestions(), {initialValue: [] as SimpleQuestion[]})
    private router = inject(Router)

    form = new FormGroup({
        labelSearch: new FormControl( ''),
        usernameSearch: new FormControl('')
    });
    public search() {
        console.log(this.form.value.labelSearch)
        this.questionService.searchQuestion(this.form.value.labelSearch as string, this.form.value.usernameSearch as string)
            .subscribe(questions => this.questions = signal(questions))
    }

    public isLogin(): boolean {
        return this.userService.isLogin()
    }

    navigateTo(url: string): void {
        this.router.navigateByUrl(url).then();
    }
}
