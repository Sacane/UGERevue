import {Component, ElementRef, inject, Inject, signal, ViewChild} from "@angular/core";
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {ReviewService} from "../../services";
import {tap} from "rxjs";
import {ReviewQuestionTitleDTO} from "../../../modules/reviews/models/review.model";
import {TagService} from "../../services/tag.service";
import {toSignal} from "@angular/core/rxjs-interop";
import {TagWrapperDTO} from "../../models/tag.model";

@Component({
    selector: 'review-dialog',
    templateUrl: './review-dialog.component.html',
    styleUrl: './review-dialog.component.scss'
})
export class ReviewDialogComponent {
    @ViewChild('contentRef', { static: false }) contentRef!: ElementRef<HTMLTextAreaElement>;
    @ViewChild('tagSearchBar', { static: false }) tagContentRef!: ElementRef<HTMLTextAreaElement>;


    form = new FormGroup({
        content: new FormControl('', [Validators.required]),
        lineStart: new FormControl(''),
        lineEnd: new FormControl(''),
        tag: new FormControl('')
    });
    private reviewService = inject(ReviewService)
    markdownContent = signal('');
    tags = new FormArray<FormControl<string | null>>([])
    tagService = inject(TagService)
    selfTags = toSignal(this.tagService.getTags(), {initialValue: [] as TagWrapperDTO[]})
    //questions = toSignal(this.questionService.getQuestions(), {initialValue: [] as SimpleQuestion[]})

    reviews = signal<ReviewQuestionTitleDTO[]>([])

    constructor(public dialogRef: MatDialogRef<ReviewDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: { onQuestion: boolean },
                private formBuilder: FormBuilder) {
        this.tags = this.formBuilder.array<string>([]);
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
    searchReviewsByTag() {
        this.reviewService
            .findByTag(this.form.value.tag as string)
            .pipe(tap(result => {
                this.reviews.set(result);
                console.log(this.reviews())
            })).subscribe();
    }
    addTag() {
        this.tags.push(this.formBuilder.control(''));
    }
    removeTag(index: number) {
        this.tags.removeAt(index);
    }
    updateInputTag(event: any): void {
        this.form.value.tag = event.target.value
    }

    handleReviewClick(review: ReviewQuestionTitleDTO): void {
        this.contentRef.nativeElement.value += review.reviewContent;
        this.form.value.content += review.reviewContent;
    }

    searchTag(tag: TagWrapperDTO): void {
        console.log(tag.tag)
        this.form.value.tag = tag.tag;
        this.tagContentRef.nativeElement.value = tag.tag
    }
}
