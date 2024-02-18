import {Component, Input} from '@angular/core';
import {Review, ReviewFromReview} from "../../models/review";

@Component({
    selector: 'app-reviews',
    templateUrl: './reviews.component.html',
    styleUrl: './reviews.component.scss'
})
export class ReviewsComponent {
    @Input() reviews: Array<Review> = [];
}
