package fr.pentagon.ugeoverflow.model;

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
    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    private Review parentReview;
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "parentReview")
    private List<Review> reviews;
    @Nullable
    private Integer lineStart;
    @Nullable
    private Integer lineEnd;
    private Date createdAt;

    public Review() {
    }

    public Review(String content, @Nullable Integer lineStart, @Nullable Integer lineEnd, Date createdAt) {
        Objects.requireNonNull(content);
        Objects.requireNonNull(createdAt);
        this.content = content;
        this.lineStart = lineStart;
        this.lineEnd = lineEnd;
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
        return parentReview;
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
    }

    @Nullable
    public Integer getLineStart() {
        return lineStart;
    }

    public void setLineStart(@Nullable Integer lineStart) {
        this.lineStart = lineStart;
    }

    @Nullable
    public Integer getLineEnd() {
        return lineEnd;
    }

    public void setLineEnd(@Nullable Integer lineEnd) {
        this.lineEnd = lineEnd;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
