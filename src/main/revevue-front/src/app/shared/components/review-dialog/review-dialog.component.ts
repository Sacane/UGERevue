import { Component, Inject } from "@angular/core";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";

@Component({
    selector: 'review-dialog',
    templateUrl: './review-dialog.component.html'
})
export class ReviewDialogComponent {
    form = new FormGroup({
        content: new FormControl('', [Validators.required]),
        lineStart: new FormControl(''),
        lineEnd: new FormControl('')
    });

    constructor(public dialogRef: MatDialogRef<ReviewDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any) {
    }

    close(): void {
        this.dialogRef.close();
    }

    confirm(): void {
        this.dialogRef.close(this.form.value);
    }
}
