import { Component, Input, ViewEncapsulation } from '@angular/core';
import { Review } from './models/review.model';

@Component({
    selector: 'app-review-detail',
    templateUrl: './reviews.component.html',
    styleUrls: ['./reviews.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class ReviewsComponent {
    @Input() review: Review | null = {
        id: '1',
        author: 'qeutninauthor',
        creationDate: '21/02/2024',
        content: 'blablabla contetn review',
        citedCode: 'var a = 1;\nvar b = 2;',
        upvotes: 2,
        downvotes: 3,
        reviews: [
            {
                id: '7',
                author: 'autre author',
                creationDate: '21/02/2024',
                content: 'autre content',
                upvotes: 282,
                downvotes: 3
            },
            {
                id: '2',
                author: 'qeutninauthor',
                creationDate: '21/02/2024',
                content: 'blablabla contetn review',
                upvotes: 2,
                downvotes: 3,
                reviews: [
                    {
                        id: '3',
                        author: 'autre author',
                        creationDate: '21/02/2024',
                        content: 'autre content',
                        upvotes: 282,
                        downvotes: 3
                    }
                ]
            }
        ]
    };
    // null;
}
