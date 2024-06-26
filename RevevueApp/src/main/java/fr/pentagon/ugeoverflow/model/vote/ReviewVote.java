package fr.pentagon.ugeoverflow.model.vote;

import fr.pentagon.ugeoverflow.model.Review;
import fr.pentagon.ugeoverflow.model.User;
import jakarta.persistence.*;

@Entity(name = "review_vote")
@Table(
        name = "review_vote",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"author_id", "review_id"})}
)
public class ReviewVote {
    @EmbeddedId
    private ReviewVoteId reviewVoteId;

    @Column(name = "is_up")
    private boolean isUp;

    public static ReviewVote upvote(User user, Review review) {
        var res = new ReviewVote();
        res.setReviewVoteId(new ReviewVoteId(user, review));
        res.setUp(true);
        return res;
    }
    public static ReviewVote downvote(User user, Review review) {
        var res = new ReviewVote();
        res.setReviewVoteId(new ReviewVoteId(user, review));
        res.setUp(false);
        return res;
    }


    public User getAuthor() {
        return reviewVoteId.getAuthor();
    }

    public Review getReview() {
        return reviewVoteId.getReview();
    }

    public void setReviewVoteId(ReviewVoteId reviewVoteId) {
        this.reviewVoteId = reviewVoteId;
    }

    public boolean isUp() {
        return isUp;
    }

    public void setUp(boolean up) {
        isUp = up;
    }
}
