import {Component, inject, ViewEncapsulation} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {QuestionService} from "../../../../shared/question.service";
import {Router} from "@angular/router";

@Component({
    selector: 'app-create-question',
    templateUrl: './create-question.component.html',
    styleUrl: './create-question.component.scss',
    encapsulation: ViewEncapsulation.None,
})
export class CreateQuestionComponent {
    form = new FormGroup({
        questionTitle: new FormControl('', [Validators.required]),
        questionContent: new FormControl('', [Validators.required]),
        javaClass: new FormControl<File | null>(null, [Validators.required]),
        testClass: new FormControl<File | null>(null),
    });

    private questionService = inject(QuestionService)
    private router = inject(Router)
    onJavaClassPicked(event: Event) {
        const files = (event.target as HTMLInputElement).files;
        if (files === null) {
            return;
        }
        const file = files[0];
        this.form.patchValue({ javaClass: file });
    }

    onJavaClassWipe() {
        this.form.patchValue({ javaClass: null });
    }

    onSubmit() {
        if (this.form.status === 'VALID') {
            this.questionService.createQuestion({
                title: this.form.value.questionTitle as string,
                description: this.form.value.questionContent as string,
                javaFile: this.form.value.javaClass as File,
                testFile: this.form.value.testClass as File | undefined
            }).subscribe(() => this.gotoQuestion());
        }
    }

    onTestClassPicked(event: Event) {
        const files = (event.target as HTMLInputElement).files;
        if (files === null) {
            return;
        }
        const file = files[0];
        this.form.patchValue({ testClass: file });
    }

    onTestClassWipe() {
        this.form.patchValue({ testClass: null });

        // Build FormData from form and send to HTTP endpoint
    }

    gotoQuestion(): void {
        this.router.navigateByUrl('/questions').then();
    }
}
