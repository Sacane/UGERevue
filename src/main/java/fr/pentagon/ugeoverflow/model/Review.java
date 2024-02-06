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
    private String title;
    private byte[] javaFile;
    @Nullable
    private byte[] testFile;
    private boolean isOpen;
    private String status;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "review")
    private List<Comment> comments;
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
    private Date createdAt;

    public Review() {
    }

    public Review(String title, byte[] javaFile, byte[] testFile, boolean isOpen, String status, User author, Date createdAt) {
        Objects.requireNonNull(title);
        Objects.requireNonNull(javaFile);
        Objects.requireNonNull(status);
        Objects.requireNonNull(author);
        Objects.requireNonNull(createdAt);
        this.title = title;
        this.javaFile = javaFile;
        this.testFile = testFile;
        this.isOpen = isOpen;
        this.status = status;
        this.author = author;
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

    public byte[] getJavaFile() {
        return javaFile;
    }

    public void setJavaFile(byte[] javaFile) {
        this.javaFile = javaFile;
    }

    @Nullable
    public byte[] getTestFile() {
        return testFile;
    }

    public void setTestFile(@Nullable byte[] testFile) {
        this.testFile = testFile;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
