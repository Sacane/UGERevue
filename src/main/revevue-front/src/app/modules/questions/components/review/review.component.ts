import { Component, Input, OnInit } from '@angular/core';
import { Review } from "../../models/review";
import { UserService } from '../../../../shared/HttpServices';

@Component({
    selector: 'app-review',
    templateUrl: './review.component.html',
    styleUrl: './review.component.scss'
})
export class ReviewComponent implements OnInit {
    @Input() review: Review = {
        author: "", creationDate: new Date(0), content: "", downvotes: 0, reviews: [], upvotes: 0
    };
    canDelete: boolean = false;

    constructor(private userService: UserService) {
    }

    ngOnInit(): void {
        this.canDelete = this.userService.getLogin() === this.review.author;
    }
}
