package fr.pentagon.ugeoverflow.model.vote;

import fr.pentagon.ugeoverflow.model.Question;
import fr.pentagon.ugeoverflow.model.User;
import jakarta.persistence.*;

@Entity(name = "vote_question")
@Table(
        name = "vote_question",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"author_id", "question_id"})}
)
public class QuestionVote {
    @EmbeddedId
    private QuestionVoteId questionVoteId;
    @Column(name = "is_up")
    private boolean isUp;


    public User getAuthorId() {
        return questionVoteId.getAuthor();
    }

    public Question getQuestionId() {
        return questionVoteId.getQuestion();
    }

    public void setQuestionVoteId(QuestionVoteId questionVoteId) {
        this.questionVoteId = questionVoteId;
    }

    public boolean isUp() {
        return isUp;
    }

    public void setUp(boolean up) {
        isUp = up;
    }
    public static QuestionVote upvote(User author, Question question) {
        var questionVote = new QuestionVote();
        questionVote.setQuestionVoteId(new QuestionVoteId(author, question));
        questionVote.setUp(true);
        return questionVote;
    }
    public static QuestionVote downVote(User author, Question question) {
        var questionVote = new QuestionVote();
        questionVote.setQuestionVoteId(new QuestionVoteId(author, question));
        questionVote.setUp(false);
        return questionVote;
    }
}
