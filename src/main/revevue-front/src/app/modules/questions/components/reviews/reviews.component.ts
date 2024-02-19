import {Component, Input, Signal} from '@angular/core';
import {Review, ReviewFromReview} from "../../models/review";

@Component({
    selector: 'app-reviews',
    templateUrl: './reviews.component.html',
    styleUrl: './reviews.component.scss'
})
export class ReviewsComponent {
    @Input() reviews: Array<Review> = [];
    deleteReview(deleteReview: Review): void {
        this.reviews = this.reviews.filter(review => review.id !== deleteReview.id);
    }
}
