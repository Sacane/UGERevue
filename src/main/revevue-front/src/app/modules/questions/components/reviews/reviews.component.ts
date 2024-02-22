import { Component, Input } from '@angular/core';
import { Review } from "../../models/review";

@Component({
    selector: 'app-reviews',
    templateUrl: './reviews.component.html',
    styleUrl: './reviews.component.scss'
})
export class ReviewsComponent {
    @Input() reviews: Review[] = [];

    deleteReview(deleteReview: Review): void {
        this.reviews = this.reviews.filter(review => review.id !== deleteReview.id);
    }
}
