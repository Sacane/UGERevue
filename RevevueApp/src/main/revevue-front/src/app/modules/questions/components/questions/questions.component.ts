import {Component, computed, inject, OnInit, signal, ViewEncapsulation} from '@angular/core';
import {LoginService} from "../../../../shared/HttpServices";
import {QuestionService} from "../../../../shared/question.service";
import {SimpleQuestion} from "../../../../shared/models/question";
import {Router} from '@angular/router';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
    selector: 'app-questions',
    templateUrl: './questions.component.html',
    styleUrl: './questions.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class QuestionsComponent implements OnInit{
    numberQuestions = computed(() => this.questions().length)
    private readonly userService = inject(LoginService)
    private readonly questionService = inject(QuestionService)
    questions = signal([] as SimpleQuestion[])/*toSignal(this.questionService.getQuestions(), {initialValue: [] as SimpleQuestion[]})*/
    private router = inject(Router)

    form = new FormGroup({
        labelSearch: new FormControl( ''),
        usernameSearch: new FormControl('')
    });
    public search() {
        this.questionService.searchQuestion(this.form.value.labelSearch as string, this.form.value.usernameSearch as string)
            .subscribe(questions => this.questions = signal(questions))
    }

    public isLogin(): boolean {
        return this.userService.isLogin()
    }

    navigateTo(url: string): void {
        this.router.navigateByUrl(url).then();
    }
    sortByDate() {
        this.questions.set(this.questions().sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()
        ))
    }

    ngOnInit(): void {
        this.questionService.getQuestions().subscribe(questions => this.questions.set(questions))
    }
}
