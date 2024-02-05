import {Component, ViewEncapsulation} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Question} from "../../models/question";
import {Review} from "../../models/review";

@Component({
    selector: 'app-question',
    templateUrl: './question.component.html',
    styleUrl: './question.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class QuestionComponent {
    question: Question = {
        author: "sebdu93",
        classContent: "package fr.pentagon.ugeoverflow.repository;\n" +
            "\n" +
            "import fr.pentagon.ugeoverflow.model.User;\n" +
            "import org.springframework.data.jpa.repository.JpaRepository;\n" +
            "import org.springframework.stereotype.Repository;\n" +
            "\n" +
            "import java.util.Optional;\n" +
            "\n" +
            "@Repository\n" +
            "public interface UserRepository extends JpaRepository<User, Long> {\n" +
            "    boolean existsByUsername(String username);\n" +
            "    Optional<User> findByLogin(String login)\n" +
            "}\n",
        commentCount: 2,
        creationDate: new Date(),
        id: 12,
        questionContent: "Mon code ne marche pas pourquoi?",
        title: "Code qui marche pas",
        voteCount: 375
    };
    reviews: Array<Review> = [
        {
            author: "seblafrite",
            content: "tu pourrais au moins faire l'effort d'expliquer ton problème...",
            upvotes: 23,
            downvotes: 5,
            reviews: [
                {
                    author: "teletubbies",
                    content: "c'est pas très gentil de dire ça",
                    upvotes: 7,
                    downvotes: 9,
                    reviews: []
                },
            ]
        },
        {
            author: "jesaispasquoimettreici",
            content: "il manque un point virgule là",
            citedCode: "Optional<User> findByLogin(String login)",
            upvotes: 245,
            downvotes: 1,
            reviews: []
        },
    ]
    private readonly id: string;

    constructor(private activatedRoute: ActivatedRoute) {
        this.id = this.activatedRoute.snapshot.params['id'];
    }
}
