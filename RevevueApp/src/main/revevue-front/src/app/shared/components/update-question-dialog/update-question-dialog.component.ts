import { Component, Inject, ViewEncapsulation } from "@angular/core";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";

@Component({
    selector: 'update-question-dialog',
    templateUrl: './update-question-dialog.component.html',
    styleUrl: './update-question-dialog.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class UpdateQuestionDialogComponent {
    form: FormGroup;

    constructor(public dialogRef: MatDialogRef<UpdateQuestionDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any) {
        this.form = new FormGroup({
            description: new FormControl(data.content, [Validators.required]),
            testFile: new FormControl<File | null>(null)
        });
    }

    onFileSelected(event: any) {
        const files = (event.target as HTMLInputElement).files;

        if (files === null) {
            return;
        }

        const file = files[0];
        this.form.patchValue({ testFile: file });
    }

    close(): void {
        this.dialogRef.close();
    }

    confirm(): void {
        this.dialogRef.close({ ...this.form.value, testFile: this.form.value.testFile as File });
    }
}
