import { Component, Input } from '@angular/core';
import { Review } from '../models/review.model';

@Component({
    selector: 'app-child-review',
    templateUrl: './child-review.component.html',
    styleUrls: ['./child-review.component.scss']
})
export class ChildReviewComponent {
    @Input() review: Review | null = null;
}
