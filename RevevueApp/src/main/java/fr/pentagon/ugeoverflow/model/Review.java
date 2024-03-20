package fr.pentagon.ugeoverflow.model;

import fr.pentagon.ugeoverflow.model.embed.CodePart;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue
    private long id;
    @Column(columnDefinition="text")
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "parentReview")
    private List<Review> reviews = new ArrayList<>();
    private Date createdAt;
    @Embedded
    private CodePart codePart;

    @ManyToOne(fetch = FetchType.LAZY)
    private Review parentReview;

    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(
            name = "review_tag",
            joinColumns = @JoinColumn(name = "review_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tagsList = new HashSet<>();

    public Review() {
    }

    public Review(String content, CodePart codePart, Date createdAt) {
        Objects.requireNonNull(content);
        Objects.requireNonNull(createdAt);
        this.content = content;
        this.codePart = codePart;
        this.createdAt = createdAt;
    }

    public void update(String newContent, CodePart codePart, Set<Tag> tags){
        this.content = newContent;
        this.codePart = codePart;
        this.tagsList = Set.copyOf(tags);
        tags.forEach(tag -> author.addTag(tag));
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
    public boolean comparedWithAnotherReviewListBasedOnContent(List<Review> reviewList){
        for (var review : reviewList) {
            if (review.content.equalsIgnoreCase(this.content)) {
                return false;
            }
        }
        return true;
    }

    public Set<Tag> getTagsList() {
        return tagsList;
    }

    public void setTagsList(Set<Tag> tagsList) {
        this.tagsList = tagsList;
    }

    public void addTag(Tag tag){
        this.tagsList.add(tag);
        tag.addReview(this);
    }

    public void removeTag(Tag tag){
        this.tagsList.remove(tag);
        tag.removeReview(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return id == review.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
