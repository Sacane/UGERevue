package fr.pentagon.ugeoverflow.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "answers")
public class Answer {
    @Id
    @GeneratedValue
    private long id;
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment comment;
    private Date createdAt;

    public Answer() {}

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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
