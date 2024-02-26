import { Component, OnDestroy, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { ReviewDialogComponent, ReviewService } from '../../shared';
import { BehaviorSubject, Subject, catchError, concat, of, switchMap, takeUntil } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { UserService } from '../../shared/HttpServices';
import { Role } from '../questions/models/role.model';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';
import { Location } from '@angular/common';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
    selector: 'app-review-detail',
    templateUrl: './reviews.component.html',
    styleUrls: ['./reviews.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class ReviewsComponent implements OnDestroy {
    deleting: boolean = false;
    canDelete: boolean = false;
    canReview: boolean;
    review$ = new BehaviorSubject<any>(null);
    private reviewId: string;
    private _onDestroy = new Subject<void>();

    constructor(private router: Router, private activatedRoute: ActivatedRoute, private userService: UserService, private reviewService: ReviewService, private dialog: MatDialog, private snackBar: MatSnackBar, private location: Location) {
        this.reviewId = this.activatedRoute.snapshot.params['id'];
        this.canReview = this.userService.getLogin() !== '';

        this.router.events.pipe(
            takeUntil(this._onDestroy)
        ).subscribe((e: any) => {
            if (e instanceof NavigationEnd) {
                this.initData();
            }
        });
    }

    ngOnDestroy(): void {
        this._onDestroy.next();
        this._onDestroy.complete();
    }

    initData(): void {
        this.reviewId = this.activatedRoute.snapshot.params['id'];

        this.reviewService.getDetails(this.reviewId).subscribe(response => {
            this.review$.next(response);
            this.canDelete = response.author === this.userService.getLogin() || this.userService.getRole() === Role.ADMIN;
        });
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
                    return this.reviewService.addReview(this.reviewId, reviewValue.content).pipe(
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
                const review = this.review$.getValue();

                this.review$.next({ ...review, reviews: [...review.reviews, response] });
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
                        this.reviewService.deleteReview(this.review$.getValue().id).pipe(
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
                this.snackBar.open('Error occurs while deleting the review', 'OK');
            }
            else {
                this.snackBar.open('The review have been deleted', 'OK');
                this.location.back();
            }
        });
    }

    onChildDelete(id: string): void {
        const review = this.review$.getValue();

        this.review$.next({ ...review, reviews: review.reviews.filter((childReview: any) => childReview.id !== id) });
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
