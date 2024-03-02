import {Component, computed, inject, OnDestroy, signal, ViewEncapsulation} from '@angular/core';
import {ActivatedRoute, NavigationEnd, Router} from '@angular/router';
import {ReviewDialogComponent, ReviewService} from '../../shared';
import {BehaviorSubject, catchError, concat, of, Subject, switchMap, takeUntil, tap} from 'rxjs';
import {MatDialog} from '@angular/material/dialog';
import {UserService} from '../../shared/HttpServices';
import {Role} from '../questions/models/role.model';
import {ConfirmDialogComponent} from '../../shared/components/confirm-dialog/confirm-dialog.component';
import {Location} from '@angular/common';
import {MatSnackBar} from '@angular/material/snack-bar';
import {DetailReviewResponseDTO, Review} from "./models/review.model";
import {toSignal} from "@angular/core/rxjs-interop";

@Component({
    selector: 'app-review-detail',
    templateUrl: './reviews.component.html',
    styleUrls: ['./reviews.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class ReviewsComponent implements OnDestroy {
    deleting: boolean = false;
    canDelete: boolean = false;
    private _onDestroy = new Subject<void>();

    private router = inject(Router);
    private activatedRoute = inject(ActivatedRoute)
    private userService = inject(UserService)
    private reviewService = inject(ReviewService)
    private dialog = inject(MatDialog)
    private snackBar = inject(MatSnackBar)
    private location: Location = inject(Location)
    reviewId: string = this.activatedRoute.snapshot.params['id'];
    canReview: boolean = this.userService.isLogin();

    details$ = this.reviewService.getDetails(this.reviewId)

    reviewDetail = signal<DetailReviewResponseDTO | undefined>(undefined)
    reviewCall$ = toSignal<DetailReviewResponseDTO>(this.details$.pipe(tap(review => {
            console.log('ah?')
            this.reviewDetail.set(review);
            this.subReviews.set(review.reviews as Review[])
            this.canDelete = review.author === this.userService.getLogin() || this.userService.getRole() === Role.ADMIN;
        })))

    subReviews = signal<Review[]>([])

    review$ = new BehaviorSubject<any>(null);
    constructor() {
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
            this.reviewDetail.set(response)
            this.subReviews.set(response.reviews as Review[])
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
                    return this.reviewService.addReview(this.reviewId, reviewValue.content).pipe(tap(response => this.subReviews.update(old => [...old, response])),
                        catchError(err => {
                            console.log(err);
                            return of(err);
                        })
                    );
                }
                return of();
            })
        ).subscribe();
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
                        this.reviewService.deleteReview(this.reviewId).pipe(
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
        this.subReviews.update(old => old.filter((review) => review.id !== id))
    }

    vote(review: DetailReviewResponseDTO, up: boolean): void {
        if (review.vote === up) {
            this.reviewService.cancelVote(review.id).pipe(
                catchError(err => {
                    console.log(err);
                    return of(err);
                })
            ).subscribe(response => {
                if (!response) {
                    /*review.upvotes = review.vote ? review.upvotes - 1 : review.upvotes;
                    review.downvotes = review.vote ? review.downvotes : review.downvotes - 1;*/
                    this.reviewDetail.update((old) => {
                        return {...old, upvotes: review.vote ? review.upvotes - 1 : review.upvotes, downvotes: review.vote ? review.downvotes : review.downvotes - 1} as DetailReviewResponseDTO
                    })
                    review.vote = undefined;
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
                    this.reviewDetail.update((old) => {
                        return {...old, upvotes: up ? review.upvotes + 1 : (review.vote === true ? review.upvotes - 1 : review.upvotes), downvotes: up ? (review.vote === false ? review.downvotes - 1 : review.downvotes) : review.downvotes + 1} as DetailReviewResponseDTO
                    })
                    review.vote = up;
                }
            });
        }
    }
}
