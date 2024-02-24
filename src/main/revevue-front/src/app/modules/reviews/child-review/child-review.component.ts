import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { ReviewService } from '../../../shared';
import { catchError, of } from 'rxjs';

@Component({
    selector: 'app-child-review',
    templateUrl: './child-review.component.html',
    styleUrls: ['./child-review.component.scss']
})
export class ChildReviewComponent {
    @Input() review: any = null;

    constructor(private router: Router, private reviewService: ReviewService) { }

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
