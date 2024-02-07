package fr.pentagon.ugeoverflow.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue
    private long id;
    private String title;
    private String description;
    private byte[] file;
    @Nullable
    private byte[] testFile;
    @Nullable
    private String testResult;
    private boolean open;
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
    private Date createdAt;

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
        this.testFile = testFile;
        this.testResult = testResult;
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

    @Nullable
    public byte[] getTestFile() {
        return testFile;
    }

    public void setTestFile(@Nullable byte[] testFile) {
        this.testFile = testFile;
    }

    @Nullable
    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(@Nullable String testResult) {
        this.testResult = testResult;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
