import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';
import { ReviewDialogComponent, ReviewService } from '../../../shared';
import { catchError, concat, of, switchMap } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserService } from '../../../shared/HttpServices';
import { Role } from '../../questions/models/role.model';

@Component({
    selector: 'app-child-review',
    templateUrl: './child-review.component.html',
    styleUrls: ['./child-review.component.scss']
})
export class ChildReviewComponent implements OnInit {
    @Input() review: any = null;
    @Output() onDelete: EventEmitter<string> = new EventEmitter();

    canDelete: boolean = false;

    constructor(private router: Router, private userService: UserService, private reviewService: ReviewService, private dialog: MatDialog, private snackBar: MatSnackBar) { }

    ngOnInit(): void {
        this.canDelete = this.review.author === this.userService.getLogin() || this.userService.getRole() === Role.ADMIN;
    }

    addReview(): void {
        this.dialog.open(ReviewDialogComponent, {
            data: {
                onQuestion: false
            },
            disableClose: true
        }).afterClosed().pipe(
            switchMap(reviewValue => {
                if (reviewValue) {
                    return this.reviewService.addReview(this.review.id, reviewValue.content).pipe(
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
                this.review.reviews.push(response);
            }
        });
    }

    deleteReview(): void {
        this.dialog.open(ConfirmDialogComponent, {
            data: {
                title: 'Delete a review',
                message: 'Confirm that you want to delete this review ?'
            },
            disableClose: true
        }).afterClosed().pipe(
            switchMap(confirm => {
                if (confirm) {
                    return concat(
                        of({ deleting: true }),
                        this.reviewService.deleteReview(this.review.id).pipe(
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
            }
            else if (response && response.error) {
                this.snackBar.open('Error occurs while deleting the review', 'OK');
            }
            else {
                this.onDelete.emit(this.review.id);
                this.snackBar.open('The review have been deleted', 'OK');
            }
        });
    }

    onChildDelete(id: string): void {
        this.review = { ...this.review, reviews: this.review.reviews.filter((childReview: any) => childReview.id !== id) };
    }

    detailsReview(): void {
        this.router.navigateByUrl(`/reviews/${this.review?.id}`);
    }

    vote(review: any, up: boolean): void {
        if (review.vote === up) {
            this.reviewService.cancelVote(review.id).pipe(
                catchError(err => {
                    console.log(err);
                    return of(err);
                })
            ).subscribe(response => {
                if (!response) {
                    review.upvotes = review.vote ? review.upvotes - 1 : review.upvotes;
                    review.downvotes = review.vote ? review.downvotes : review.downvotes - 1;
                    review.vote = null;
                }
            });
        }
        else {
            this.reviewService.vote(review.id, up).pipe(
                catchError(err => {
                    console.log(err);
                    return of(err);
                })
            ).subscribe(response => {
                if (!response) {
                    review.upvotes = up ? review.upvotes + 1 : (review.vote === true ? review.upvotes - 1 : review.upvotes);
                    review.downvotes = up ? (review.vote === false ? review.downvotes - 1 : review.downvotes) : review.downvotes + 1;
                    review.vote = up;
                }
            });
        }
    }
}
