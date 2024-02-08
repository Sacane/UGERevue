package fr.pentagon.ugeoverflow.model.vote;

import fr.pentagon.ugeoverflow.model.Question;
import fr.pentagon.ugeoverflow.model.Review;
import fr.pentagon.ugeoverflow.model.User;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;

@Embeddable
public class ReviewVoteId implements Serializable {
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    public ReviewVoteId(){}

    public ReviewVoteId(User user, Review review) {
        this.author = user;
        this.review = review;
    }

    public User getAuthor() {
        return author;
    }

    public Review getReview() {
        return review;
    }

    public void setAuthor(User user) {
        this.author = user;
    }

    public void setReview(Review review) {
        this.review = review;
    }
}
