import {Component, computed, inject, signal, Signal, ViewEncapsulation} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Review} from "../../models/review";
import {Role} from "../../models/role.model";
import {QuestionService} from "../../../../shared/question.service";
import {toSignal} from "@angular/core/rxjs-interop";
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatDialog} from '@angular/material/dialog';
import {ConfirmDialogComponent} from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import {catchError, concat, of, switchMap} from 'rxjs';
import {LoginService} from '../../../../shared/HttpServices';
import {ReviewDialogComponent} from '../../../../shared/components/review-dialog/review-dialog.component';
import {ReviewService} from '../../../../shared';
import {
    UpdateQuestionDialogComponent
} from '../../../../shared/components/update-question-dialog/update-question-dialog.component';
import {ToastrService} from "ngx-toastr";

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
    private toastService = inject(ToastrService)
    question = toSignal(this.questionService.findQuestionById(this.id), {
        initialValue: null
    });
    reviews: Signal<Review[]> = toSignal(this.reviewService.findReviewByQuestionId(this.id)) as Signal<Review[]>;
    canDelete: Signal<boolean> = computed(() => {
        return this.question()?.author === this.userService.getLogin() || this.userService.getRole() === Role.ADMIN;
    });
    canReview: boolean;
    deleting: boolean = false;

    constructor(private activatedRoute: ActivatedRoute, private userService: LoginService, private router: Router, private snackBar: MatSnackBar, protected dialog: MatDialog) {
        this.canReview = this.userService.getLogin() !== '';
    }

    deleteQuestion(): void {
        this.dialog.open(ConfirmDialogComponent, {
            data: {
                title: 'Supprimer une question',
                message: 'Confirmer la suppression de cette question ?'
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
            data: {
                onQuestion: true,
                questionContent: this.question()!!.questionContent
            },
            disableClose: true
        }).afterClosed().pipe(
            switchMap(reviewValue => {
                if (reviewValue) {
                    return this.questionService.addReview(this.id, reviewValue.content, reviewValue.lineStart, reviewValue.lineEnd, reviewValue.tags).pipe(
                        catchError(err => {
                            this.toastService.error(err.error.message);
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

    update(): void {
        this.dialog.open(UpdateQuestionDialogComponent, {
            data: {
                content: this.question()!!.questionContent
            },
            disableClose: true
        }).afterClosed().pipe(
            switchMap(response => {
                if (response) {
                    return this.questionService.updateQuestion(this.question()!!.id.toString(), response).pipe(
                        catchError(err => {
                            console.log(err);
                            return of(err);
                        })
                    );
                }

                return of();
            })
        ).subscribe(response => {
            if (!response.error) {
                this.question()!!.questionContent = response.description;
                this.question()!!.testClassContent = response.testFile ? response.testFile : this.question()!!.testClassContent;
                this.question()!!.testResults = response.testFileResult ? response.testFileResult : this.question()!!.testResults;

                this.snackBar.open('Mise à jour effectué', 'OK', { duration: 5000 });
            }
            else {
                this.snackBar.open('Erreur lors de la mise à jour', 'OK', { duration: 5000 });
            }
        });
    }

    vote(up: boolean): void {
        if (this.userService.isLogin()) {
            if (this.question()!!.vote === null || (this.question()!!.vote !== null && this.question()!!.vote !== up)) {
                this.questionService.vote(this.question()!!.id.toString(), up).subscribe(() => {
                    if (up) {
                        if (this.question()!!.vote !== null) {
                            this.question()!!.downvotes!! -= 1;
                        }

                        this.question()!!.upvotes!! += 1;
                    }
                    else {
                        if (this.question()!!.vote !== null) {
                            this.question()!!.upvotes!! -= 1;
                        }

                        this.question()!!.downvotes!! += 1;
                    }
                    if (this.question()!!.vote === null) {
                        this.question()!!.voteCount += 1;
                    }

                    this.question()!!.vote = up;
                });
            }
            else {
                this.questionService.cancelVote(this.question()!!.id.toString()).subscribe(() => {
                    if (up) {
                        this.question()!!.upvotes!! -= 1;
                    }
                    else {
                        this.question()!!.downvotes!! -= 1;
                    }

                    this.question()!!.voteCount -= 1;
                    this.question()!!.vote = null;
                });
            }
        }
        else {
            this.snackBar.open('Vous ne pouvez pas voter en étant déconnecter', 'OK', { duration: 5000 });
        }
    }
}
