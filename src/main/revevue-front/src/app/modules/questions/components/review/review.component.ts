import {Component, Input} from '@angular/core';
import {Review, ReviewFromReview} from "../../models/review";

@Component({
    selector: 'app-review',
    templateUrl: './review.component.html',
    styleUrl: './review.component.scss'
})
export class ReviewComponent {
    @Input() review: Review = {
        author: "", creationDate: new Date(0), content: "", downvotes: 0, reviews: [], upvotes: 0
    };
}
