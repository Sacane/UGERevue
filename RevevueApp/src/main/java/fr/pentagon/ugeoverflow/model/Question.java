package fr.pentagon.ugeoverflow.model;

import fr.pentagon.ugeoverflow.model.embed.TestKit;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue
    private long id;
    private String title;
    private String description;
    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] file;
    private String fileName;
    @Embedded
    private TestKit testKit;

    private boolean open;
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "question")
    private List<Review> reviews = new ArrayList<>();
    private Date createdAt;
    @Version
    Long version;

    public Question() {
    }

    public Question(String title, String description, byte[] file, byte[] testFile, String testResult, boolean open, Date createdAt) {
        Objects.requireNonNull(title);
        Objects.requireNonNull(description);
        Objects.requireNonNull(file);
        Objects.requireNonNull(createdAt);
        this.title = title;
        this.description = description;
        this.file = file;
        this.testKit = new TestKit(testFile, testResult);
        this.open = open;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Nullable
    public byte[] getTestFile() {
        return (testKit == null) ? null : testKit.getTestFile();
    }

    public void setTestFile(@Nullable byte[] testFile) {
        this.testKit.setTestFile(testFile);
    }

    public String getTestFileName() {
        return (testKit == null) ? "" : testKit.getTestFileName();
    }

    public void setTestFileName(String name) {
        testKit.setTestFileName(name);
    }

    @Nullable
    public String getTestResult() {
        return testKit.getTestResult();
    }

    public void setTestResult(@Nullable String testResult) {
        if(testKit == null) return;
        this.testKit.setTestResult(testResult);
    }

    public void setTestKit(TestKit testKit) {
        this.testKit = testKit;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void addReview(Review review) {
        reviews.add(review);
        review.setQuestion(this);
    }

    public void removeReview(Review review) {
        reviews.remove(review);
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", testResult='" + testKit.getTestResult() + '\'' +
                ", open=" + open +
                ", author=" + author +
                ", reviews=" + reviews +
                ", createdAt=" + createdAt +
                ", version=" + version +
                '}';
    }
}
