import {Component, Input} from '@angular/core';
import {Review} from "../../models/review";
import {ReviewComponent} from "../review/review.component";

@Component({
    selector: 'app-reviews',
    standalone: true,
    imports: [ReviewComponent],
    templateUrl: './reviews.component.html',
    styleUrl: './reviews.component.scss'
})
export class ReviewsComponent {
    @Input() reviews: Array<Review> = [];
}
