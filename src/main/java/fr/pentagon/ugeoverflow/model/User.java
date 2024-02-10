package fr.pentagon.ugeoverflow.model;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

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
  @ManyToMany
  @JoinTable(name = "follows",
      joinColumns = @JoinColumn(name = "follows"),
      inverseJoinColumns = @JoinColumn(name = "isFollowed"))
  private Set<User> follows;
  @ManyToMany(mappedBy = "follows")
  private Set<User> followers;

  public User() {
  }

  public User(String username, String login, String password, String email) {
    this.username = Objects.requireNonNull(username);
    this.login = Objects.requireNonNull(login);
    this.password = Objects.requireNonNull(password);
    this.email = Objects.requireNonNull(email);
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
}
