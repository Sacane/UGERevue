package fr.pentagon.ugeoverflow.model;

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
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "userRoles")
    private Set<Role> roles = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
    private List<Question> questions;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
    private List<Review> reviews = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "follows",
            joinColumns = @JoinColumn(name = "follows"),
            inverseJoinColumns = @JoinColumn(name = "isFollowed"))
    private Set<User> follows = new HashSet<>();
    @ManyToMany(mappedBy = "follows")
    private Set<User> followers = new HashSet<>();

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

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
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

    public void addRole(Role role) {
        roles.add(role);
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
}

