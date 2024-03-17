package fr.pentagon.ugeoverflow.model;

import fr.pentagon.ugeoverflow.config.authorization.Role;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserFollowInfoDTO;
import jakarta.persistence.*;

import java.util.*;

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
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
    private List<Question> questions = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
    private List<Review> reviews = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "follows",
            joinColumns = @JoinColumn(name = "follows"),
            inverseJoinColumns = @JoinColumn(name = "isFollowed"))
    private Set<User> follows = new HashSet<>();
    @ManyToMany(mappedBy = "follows")
    private Set<User> followers = new HashSet<>();
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "usersOf")
    private Set<Tag> tagsCreated = new HashSet<>();
    @Version
    Long version;

    public User() {
    }

    public User(String username, String login, String password, String email, Role role) {
        this.username = Objects.requireNonNull(username);
        this.login = Objects.requireNonNull(login);
        this.password = Objects.requireNonNull(password);
        this.email = Objects.requireNonNull(email);
        this.role = Objects.requireNonNull(role);
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role roles) {
        this.role = role;
    }

    // Logic: if entity is persisted, compare id, else use default implementation
    @Override
    public int hashCode() {
        if (id != 0) {
            return Objects.hash(id);
        }
        return super.hashCode();
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
        reviews.add(review);
        review.setAuthor(this);
    }

    public void removeReview(Review review) {
        reviews.remove(review);
    }

    @Override
    public boolean equals(Object obj) {
        if (id != 0) {
            return obj instanceof User other && other.id == id;
        }
        return super.equals(obj);
    }

    public void follows(User followed) {
        follows.add(followed);
        followed.followers.add(this);
    }

    public void unfollows(User followed) {
        follows.remove(followed);
        followed.followers.remove(this);
    }

    public Set<User> getFollows() {
        return follows;
    }

    public void setFollows(Set<User> follows) {
        this.follows = follows;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    public UserFollowInfoDTO toUserFollowInfoDTO(boolean isFollowing) {
        return new UserFollowInfoDTO(this.username, isFollowing, this.id);
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public Set<Tag> getTagsCreated() {
        return tagsCreated;
    }

    public void setTagsCreated(Set<Tag> tagsCreated) {
        this.tagsCreated = tagsCreated;
    }

    public void addTag(Tag tag) {
        this.tagsCreated.add(tag);
        tag.addUser(this);
    }

    public void removeTag(Tag tag) {
        this.tagsCreated.remove(tag);
        tag.removeUser(this);
    }
}

