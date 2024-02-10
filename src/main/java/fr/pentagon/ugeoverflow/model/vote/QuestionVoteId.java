package fr.pentagon.ugeoverflow.model.vote;

import fr.pentagon.ugeoverflow.model.Question;
import fr.pentagon.ugeoverflow.model.User;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;

@Embeddable
public class QuestionVoteId implements Serializable {
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    public QuestionVoteId(User user, Question question) {
        this.author = user;
        this.question = question;
    }
    public QuestionVoteId(){}

    public Question getQuestion() {
        return question;
    }

    public User getAuthor() {
        return author;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setAuthor(User user) {
        this.author = user;
    }
}
