import {Component, inject, Inject, signal} from "@angular/core";
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {MarkdownService} from "ngx-markdown";
import {ReviewService} from "../../services";
import {Observable, tap} from "rxjs";
import {ReviewQuestionTitleDTO} from "../../../modules/reviews/models/review.model";

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
    private reviewService = inject(ReviewService)
    tagInput = signal('')
    markdownContent = signal('');
    tags = new FormArray<FormControl<string | null>>([])

    reviews = signal<ReviewQuestionTitleDTO[]>([])

    constructor(public dialogRef: MatDialogRef<ReviewDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: { onQuestion: boolean },
                private formBuilder: FormBuilder) {
        this.tags = this.formBuilder.array<string>([]);
    }
    updateMarkdownPreview($event: any) {
        this.markdownContent.set($event.target.value)
        this.form.value.content = $event.target.value
    }
    close(): void {
        this.dialogRef.close();
    }
    confirm(): void {
        this.dialogRef.close({
            content: this.form.value.content,
            lineStart: this.form.value.lineStart,
            lineEnd: this.form.value.lineEnd,
            tags: this.tags.value
        });
    }
    searchReviewsByTag(tag: string) {
        this.reviewService
            .findByTag(tag)
            .pipe(tap(result => {
                this.reviews.set(result);
                console.log(this.reviews())
            })).subscribe();
    }
    show() {
        this.markdownService.reload();
    }
    addTag() {
        this.tags.push(this.formBuilder.control(''));
    }
    removeTag(index: number) {
        this.tags.removeAt(index);
    }
    showTags(): void {
        const uniqueTags = new Set(this.tags.value);
        console.log(Array.from(uniqueTags));
    }

    updateInputTag(event: any): void {
        this.tagInput.set(event.target.value)
    }
}
