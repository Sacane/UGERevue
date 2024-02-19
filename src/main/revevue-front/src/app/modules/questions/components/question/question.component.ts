import {Component, inject, Signal, ViewEncapsulation} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Review} from "../../models/review";
import {QuestionService} from "../../../../shared/question.service";
import {toSignal} from "@angular/core/rxjs-interop";
import {ReviewService} from "../../../../shared/review.service";

@Component({
    selector: 'app-question',
    templateUrl: './question.component.html',
    styleUrl: './question.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class QuestionComponent {
    private questionService = inject(QuestionService)
    private reviewService = inject(ReviewService)
    private id = inject(ActivatedRoute).snapshot.params['id']
    question = toSignal(this.questionService.findQuestionById(this.id))
    reviews: Signal<Review[]> = toSignal(this.reviewService.findReviewByQuestionId(this.id)) as Signal<Review[]>
}
