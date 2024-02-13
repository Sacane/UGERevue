import { Component, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Question } from "../../models/question";
import { Review } from "../../models/review";
import { QuestionService } from '../../../../shared/services/question.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
    selector: 'app-question',
    templateUrl: './question.component.html',
    styleUrl: './question.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class QuestionComponent {
    question: Question = {
        author: "sebdu93",
        tags: ['java', 'spring', 'jesuisnul'],
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
        testClassContent: 'package fr.uge.fifo;\n' +
            '\n' +
            'import static org.junit.jupiter.api.Assertions.assertEquals;\n' +
            '\n' +
            'import org.junit.jupiter.api.Test;\n' +
            '\n' +
            '@SuppressWarnings("static-method")\n' +
            'public class ResizeableFifoTest {\n' +
            '  @Test\n' +
            '  public void shouldResizeWhenAddingMoreThanCapacityElements() {\n' +
            '    var fifo = new ResizeableFifo<String>(1);\n' +
            '    fifo.offer("foo");\n' +
            '    fifo.offer("bar");\n' +
            '    assertEquals(2, fifo.size());\n' +
            '    assertEquals("foo", fifo.poll());\n' +
            '    assertEquals("bar", fifo.poll());\n' +
            '    assertEquals(0, fifo.size());\n' +
            '  }\n' +
            '}',
        testResults: 'Tests passed: 1 of 1 test - 23ms',
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

    private readonly id: string;

    constructor(private activatedRoute: ActivatedRoute, private questionService: QuestionService, private router: Router, private snackBar: MatSnackBar) {
        this.id = this.activatedRoute.snapshot.params['id'];
    }

    deleteQuestion(): void {
        this.questionService.deleteQuestion(this.id).subscribe(response => {
            if (!response.error) {
                this.snackBar.open('La question a été suprimée', 'OK');
                this.router.navigateByUrl('/questions');
            }
        });
    }
}
