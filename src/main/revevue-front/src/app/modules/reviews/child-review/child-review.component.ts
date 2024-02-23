import { Component, Input } from '@angular/core';
import { Review } from '../models/review.model';
import { Router } from '@angular/router';

@Component({
    selector: 'app-child-review',
    templateUrl: './child-review.component.html',
    styleUrls: ['./child-review.component.scss']
})
export class ChildReviewComponent {
    @Input() review: Review | null = null;

    constructor(private router: Router) { }

    detailsReview(): void {
        this.router.navigateByUrl(`/reviews/${this.review?.id}`);
    }
}
