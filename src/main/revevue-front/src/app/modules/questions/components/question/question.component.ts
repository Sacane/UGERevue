import { Component, computed, inject, signal, Signal, ViewEncapsulation, WritableSignal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Review } from "../../models/review";
import { Role } from "../../models/role.model";
import { QuestionService } from "../../../../shared/question.service";
import { toSignal } from "@angular/core/rxjs-interop";
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import { catchError, concat, of, switchMap } from 'rxjs';
import { UserService } from '../../../../shared/HttpServices';
import { ReviewDialogComponent } from '../../../../shared/components/review-dialog/review-dialog.component';
import { ReviewService } from '../../../../shared';

@Component({
    selector: 'app-question',
    templateUrl: './question.component.html',
    styleUrl: './question.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class QuestionComponent {
    private questionService = inject(QuestionService);
    private reviewService = inject(ReviewService);
    private id = inject(ActivatedRoute).snapshot.params['id'];

    question = toSignal(this.questionService.findQuestionById(this.id), {
        initialValue: null
    });
    reviews: Signal<Review[]> = toSignal(this.reviewService.findReviewByQuestionId(this.id)) as Signal<Review[]>;
    canDelete: Signal<boolean> = computed(() => {
        return this.question()?.author === this.userService.getLogin() || this.userService.getRole() === Role.ADMIN;
    });
    canReview: boolean;
    deleting: boolean = false;

    constructor(private activatedRoute: ActivatedRoute, private userService: UserService, private router: Router, private snackBar: MatSnackBar, protected dialog: MatDialog) {
        this.canReview = this.userService.getLogin() !== '';
    }

    deleteQuestion(): void {
        this.dialog.open(ConfirmDialogComponent, {
            data: {
                title: 'Delete a question',
                message: 'Confirm that you want to delete this question ?'
            },
            disableClose: true
        }).afterClosed().pipe(
            switchMap(confirm => {
                if (confirm) {
                    return concat(
                        of({ deleting: true }),
                        this.questionService.deleteQuestion(this.id).pipe(
                            catchError(err => {
                                console.log(err);
                                return of({ error: err });
                            })
                        )
                    );
                }

                return of();
            })
        ).subscribe(response => {
            if (response && response.deleting) {
                this.deleting = true;
            }
            else if (response && response.error) {
                this.deleting = false;
            }
            else {
                this.snackBar.open('La question a été suprimée', 'OK');
                this.router.navigateByUrl('/questions');
            }
        });
    }

    addReview(): void {
        this.dialog.open(ReviewDialogComponent, {
            disableClose: true
        }).afterClosed().pipe(
            switchMap(reviewValue => {
                console.log(reviewValue);

                if (reviewValue) {
                    return this.questionService.addReview(this.id, reviewValue.content, reviewValue.lineStart, reviewValue.lineEnd).pipe(
                        catchError(err => {
                            console.log(err);
                            return of(err);
                        })
                    );
                }

                return of();
            })
        ).subscribe(response => {
            if (response && !response.error) {
                this.reviews = signal([...this.reviews(), response]);
            }
        });
    }

    onUpdateDelete(reviews: Review[]): void {
        this.reviews = signal(reviews);
    }
}
