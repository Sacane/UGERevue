package fr.pentagon.ugeoverflow.model;

import fr.pentagon.ugeoverflow.model.embed.CodePart;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue
    private long id;
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "parentReview")
    private List<Review> reviews;
    private Date createdAt;
    @Embedded
    private CodePart codePart;

    @ManyToOne(fetch = FetchType.LAZY)
    private Review parentReview;

    public Review() {
    }

    public Review(String content, CodePart codePart, Date createdAt) {
        Objects.requireNonNull(content);
        Objects.requireNonNull(createdAt);
        this.content = content;
        this.codePart = codePart;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Review getParentReview() {
        return this.parentReview;
    }

    public void setParentReview(@Nullable Review parentReview) {
        this.parentReview = parentReview;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void addReview(Review review) {
        reviews.add(review);
        review.setParentReview(this);
    }

    public void removeReview(Review review) {
        reviews.remove(review);
        review.setParentReview(null);
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public CodePart getCodePart() {
        return codePart;
    }

    public void setCodePart(CodePart codePart) {
        this.codePart = codePart;
    }


    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
