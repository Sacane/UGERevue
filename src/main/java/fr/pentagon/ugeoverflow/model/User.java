package fr.pentagon.ugeoverflow.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public final class User {
    @Id
    @GeneratedValue()
    private long id;
    private String username;
    private String login;
    private String password;
    private String email;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
    private List<Question> questions;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
    private List<Review> reviews;

    public User() {
    }

    public User(String username, String login, String password, String email) {
        this.username = Objects.requireNonNull(username);
        this.login = Objects.requireNonNull(login);
        this.password = Objects.requireNonNull(password);
        this.email = Objects.requireNonNull(email);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addQuestion(Question question) {
        questions.add(question);
        question.setAuthor(this);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void addReview(Review review) {
        this.reviews.add(review);
        review.setAuthor(this);
    }
}
