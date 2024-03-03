import {Component, computed, inject, Inject, signal} from "@angular/core";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import {MarkdownService} from "ngx-markdown";

@Component({
    selector: 'review-dialog',
    templateUrl: './review-dialog.component.html',
    styleUrl: './review-dialog.component.scss'
})
export class ReviewDialogComponent {
    form = new FormGroup({
        content: new FormControl('', [Validators.required]),
        lineStart: new FormControl(''),
        lineEnd: new FormControl('')
    });
    private markdownService = inject(MarkdownService)
    markdownContent = signal('');
    constructor(public dialogRef: MatDialogRef<ReviewDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: { onQuestion: boolean }) {
    }
    updateMarkdownPreview($event: any) {
        this.markdownContent.set($event.target.value)
        this.form.value.content = $event.target.value
    }
    close(): void {
        this.dialogRef.close();
    }

    confirm(): void {
        this.dialogRef.close(this.form.value);
    }

    show() {
        this.markdownService.reload();
    }
}
