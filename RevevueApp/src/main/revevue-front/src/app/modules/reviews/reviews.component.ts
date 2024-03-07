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
import {toObservable, toSignal} from "@angular/core/rxjs-interop";

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

    reviewDetail = signal<DetailReviewResponseDTO | undefined>(undefined)


    subReviews = signal<Review[]>([])
    isFontPositive = computed(() => this.reviewDetail()?.vote == true && (this.reviewDetail()!!.upvotes > 0))
    isFontNegative = computed(() => this.reviewDetail()?.vote == true && (this.reviewDetail()!!.downvotes > 0))
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
        console.log('Hm ?')
        this.reviewId = this.activatedRoute.snapshot.params['id'];
        this.reviewService.getDetails(this.reviewId).subscribe(response => {
            console.log(response)
            this.reviewDetail.set(response)
            this.subReviews.set(response.reviews as Review[])
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
        if (
            (up && review.upvotes > 0) ||
            (!up && review.downvotes > 0)
        ) {
            console.log("cancel vote")
            this.reviewService.cancelVote(review.id).pipe(
                catchError(err => {
                    console.log(err);
                    return of(err);
                })
            ).subscribe(() => {

                /*review.upvotes = review.vote ? review.upvotes - 1 : review.upvotes;
                review.downvotes = review.vote ? review.downvotes : review.downvotes - 1;*/
                let upvote: number = review.upvotes;
                let downvote: number = review.downvotes;
                if(!up) {
                    if(downvote > 0) {
                        downvote -= 1;
                    } else if(downvote === 0 ){
                        downvote += 1;
                    }
                    if(upvote > 0) {
                        upvote -= 1;
                    }
                } else {
                    if(upvote > 0) {
                        upvote -= 1;
                    } else if(upvote === 0) {
                        upvote += 1;
                    }
                }
                this.reviewDetail.update((old) => {
                    return {...old, upvotes: upvote, downvotes: downvote} as DetailReviewResponseDTO
                })

            });
        }
        else {
            console.log("vote => " + up);
            this.reviewService.vote(review.id, up).pipe(
                catchError(err => {
                    console.log(err);
                    return of(err);
                })
            ).subscribe(() => {
                let upvote: number = review.upvotes;
                let downvote: number = review.downvotes;
                if(up) {
                    if(upvote > 0) {
                        upvote -= 1;
                    } else if(upvote === 0) {
                        upvote += 1;
                    }
                    if(downvote > 0) {
                        downvote -= 1;
                    }
                }
                else{
                    if(downvote >= 1) {
                        console.log('> 1')
                        downvote -= 1;
                    } else if(downvote === 0) {
                        console.log('=== 0')
                        downvote += 1;
                    }
                    if(upvote > 0) {
                        upvote -= 1;
                    }
                }
                this.reviewDetail.update((old) => {
                    return {...old, upvotes: upvote, downvotes: downvote} as DetailReviewResponseDTO
                })
                review.vote = up;
            });
        }
    }
}