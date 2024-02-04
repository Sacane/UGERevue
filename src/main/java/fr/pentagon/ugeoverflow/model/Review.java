package fr.pentagon.ugeoverflow.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

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
    @OneToMany
    private List<Comment> comments;
    private Date createdAt;

    public Review() {}

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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
