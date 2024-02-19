import { Component, computed, inject, Signal, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Review } from "../../models/review";
import { QuestionService } from "../../../../shared/question.service";
import { toSignal } from "@angular/core/rxjs-interop";
import { ReviewService } from "../../../../shared/review.service";
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import { catchError, concat, of, switchMap } from 'rxjs';
import { UserService } from '../../../../shared/HttpServices';

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
        return this.question()?.author === this.userService.getLogin();
    });
    deleting: boolean = false;

    constructor(private activatedRoute: ActivatedRoute, private userService: UserService, private router: Router, private snackBar: MatSnackBar, protected dialog: MatDialog) {
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
            console.log(response);
            if (response.deleting) {
                this.deleting = true;
            }
            else if (!response.error) {
                this.snackBar.open('La question a été suprimée', 'OK');
                this.router.navigateByUrl('/questions');
            }
            else {
                this.deleting = false;
            }
        });
    }
}
