import {Component, ViewEncapsulation} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";

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

    onJavaClassPicked(event: Event) {
        const files = (event.target as HTMLInputElement).files;
        if (files === null) {
            return;
        }
        const file = files[0];
        this.form.patchValue({javaClass: file});
    }

    onJavaClassWipe() {
        this.form.patchValue({javaClass: null});
    }

    onSubmit() {
        console.warn(this.form.value);
    }

    onTestClassPicked(event: Event) {
        const files = (event.target as HTMLInputElement).files;
        if (files === null) {
            return;
        }
        const file = files[0];
        this.form.patchValue({testClass: file});
    }

    onTestClassWipe() {
        this.form.patchValue({testClass: null});

        // Build FormData from form and send to HTTP endpoint
    }
}
