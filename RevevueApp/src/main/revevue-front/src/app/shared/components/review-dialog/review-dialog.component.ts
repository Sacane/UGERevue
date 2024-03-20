import {Component, ElementRef, HostListener, inject, Inject, signal, ViewChild} from "@angular/core";
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {ReviewService} from "../../services";
import {catchError, map, Observable, of, startWith, tap} from "rxjs";
import {ReviewQuestionTitleDTO} from "../../../modules/reviews/models/review.model";
import {TagService} from "../../services/tag.service";
import {toSignal} from "@angular/core/rxjs-interop";
import {TagWrapperDTO} from "../../models/tag.model";
import {COMMA, ENTER} from "@angular/cdk/keycodes";
import {LiveAnnouncer} from "@angular/cdk/a11y";
import {MatChipInputEvent} from "@angular/material/chips";
import {MatAutocompleteSelectedEvent} from "@angular/material/autocomplete";
import {UpdateReviewDTO} from "../../models/reviews.model";
import {UserService} from "../../../modules/profil/services/user.service";
import {DomSanitizer, SafeHtml} from "@angular/platform-browser";


@Component({
    selector: 'review-dialog',
    templateUrl: './review-dialog.component.html',
    styleUrl: './review-dialog.component.scss',
})
export class ReviewDialogComponent {
    @ViewChild('contentRef', {static: false}) contentRef!: ElementRef<HTMLTextAreaElement>;
    @ViewChild('tagSearchBar', {static: false}) tagContentRef!: ElementRef<HTMLTextAreaElement>;
    public domSanitizer = inject(DomSanitizer)

    helpContent = 'Vous pouvez appliquer du gras en entourant votre texte de la balise html <b> ou <i> pour afficher en italique.\nVous pouvez également afficher du code comme ceci : ```java public final class Foo{}```';
    form = new FormGroup({
        content: new FormControl(this.data.template?.content ?? '', [Validators.required]),
        lineStart: new FormControl(this.data.template?.lineStart?.toString() ?? ''),
        lineEnd: new FormControl(this.data.template?.lineEnd?.toString() ?? ''),
        tag: new FormControl('')
    });
    tags = new FormArray<FormControl<string | null>>([])
    tagService = inject(TagService)
    selfTags = toSignal(this.tagService.getTags(), {initialValue: [] as TagWrapperDTO[]})
    reviews = signal<ReviewQuestionTitleDTO[]>([])
    /* ====================== chips management  ====================== */
    separatorKeysCodes: number[] = [ENTER, COMMA];
    //questions = toSignal(this.questionService.getQuestions(), {initialValue: [] as SimpleQuestion[]})
    tagCtrl = new FormControl('');
    filteredTag = toSignal(this.tagCtrl.valueChanges.pipe(
        startWith(null),
        map((tag: string | null) => tag ? this._filter(tag) : this.selfTags().map(t => t.tag).slice())
    ))
    inputTags: string[]
    recommendedReviews$: Observable<any[]>;
    @ViewChild('inputTag') inputTag: ElementRef<HTMLInputElement>;
    announcer = inject(LiveAnnouncer);
    private reviewService = inject(ReviewService)

    /* ================================================================ */
    constructor(public dialogRef: MatDialogRef<ReviewDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: {
                    onQuestion: boolean,
                    questionContent?: string,
                    template?: UpdateReviewDTO
                },
                private formBuilder: FormBuilder, private userService: UserService) {
        this.tags = this.formBuilder.array<string>([]);
        this.inputTags = this.data.template?.tags ?? []
        dialogRef.disableClose = false;

        if (data.onQuestion) {
            this.recommendedReviews$ = this.userService.getRecommendedReviews(data.questionContent!!).pipe(
                catchError(err => {
                    console.log(err);
                    return of([]);
                })
            );
        }
    }

    transform(): SafeHtml {
        return this.domSanitizer.bypassSecurityTrustHtml(this.form.value.content as string)
    }

    fillContent(content: string): void {
        this.form.patchValue({content});
    }

    /* Input tag functions */

    add(event: MatChipInputEvent): void {
        const value = (event.value || '').trim();
        if (value) {
            this.inputTags.push(value);
        }
        event.chipInput.clear();

        this.tagCtrl.setValue(null);
    }

    remove(tag: string): void {
        const index = this.inputTags.indexOf(tag);

        if (index >= 0) {
            this.inputTags.splice(index, 1);
            this.announcer.announce(`Removed ${tag}`).then();
        }
    }

    selected(event: MatAutocompleteSelectedEvent): void {
        this.inputTags.push(event.option.viewValue);
        this.inputTag.nativeElement.value = '';
        this.tagCtrl.setValue(null);
    }

    close(): void {
        this.dialogRef.close();
    }

    /* =================== */

    confirm(): void {
        this.dialogRef.close({
            content: this.form.value.content,
            lineStart: this.form.value.lineStart,
            lineEnd: this.form.value.lineEnd,
            tags: this.inputTags
        });
    }

    searchReviewsByTag() {
        this.reviewService
            .findByTag(this.form.value.tag as string)
            .pipe(tap(result => {
                this.reviews.set(result);
            })).subscribe();
    }

    updateInputTag(event: any): void {
        this.form.value.tag = event.target.value
    }

    handleReviewClick(review: ReviewQuestionTitleDTO): void {
        this.contentRef.nativeElement.value += review.reviewContent;
        this.form.value.content += review.reviewContent;
    }

    searchTag(tag: TagWrapperDTO): void {
        this.form.value.tag = tag.tag;
        this.tagContentRef.nativeElement.value = tag.tag
    }

    @HostListener('window:keyup.esc') onKeyUp() {
        this.dialogRef.close();
    }

    private _filter(value: string): string[] {
        const filterValue = value.toLowerCase();

        return this.selfTags().map(tag => tag.tag).filter(tag => tag.toLowerCase().includes(filterValue));
    }
}
