import {Component, inject, ViewEncapsulation} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Review} from "../../models/review";
import {QuestionService} from "../../../../shared/question.service";
import {toSignal} from "@angular/core/rxjs-interop";

@Component({
    selector: 'app-question',
    templateUrl: './question.component.html',
    styleUrl: './question.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class QuestionComponent {
    private questionService = inject(QuestionService)
    private id = inject(ActivatedRoute).snapshot.params['id']

    question = toSignal(this.questionService.findQuestionById(this.id))
    reviews: Array<Review> = [
        {
            author: "seblafrite",
            creationDate: new Date(),
            content: "tu pourrais au moins faire l'effort d'expliquer ton problème...",
            upvotes: 23,
            downvotes: 5,
            reviews: [
                {
                    author: "teletubbies",
                    creationDate: new Date(),
                    content: "c'est pas très gentil de dire ça",
                    upvotes: 7,
                    downvotes: 9,
                    reviews: [
                        {
                            author: "noob",
                            creationDate: new Date(),
                            content: "osef mon gars",
                            upvotes: 0,
                            downvotes: 15,
                            reviews: []
                        },
                    ]
                },
            ]
        },
        {
            author: "jesaispasquoimettreici",
            creationDate: new Date(),
            content: "il manque un point virgule là",
            citedCode: "Optional<User> findByLogin(String login)",
            upvotes: 245,
            downvotes: 1,
            reviews: []
        },
    ]

}
