import {Component, Input} from '@angular/core';
import {Review} from "../../models/review";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {Highlight} from "ngx-highlightjs";

@Component({
    selector: 'app-review',
    standalone: true,
    imports: [
        MatIconModule,
        MatButtonModule,
        Highlight
    ],
    templateUrl: './review.component.html',
    styleUrl: './review.component.scss'
})
export class ReviewComponent {
    @Input() review: Review = {
        author: "", content: "", downvotes: 0, reviews: [], upvotes: 0
    };
}
