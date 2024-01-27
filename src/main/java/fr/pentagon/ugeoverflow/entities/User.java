package fr.pentagon.ugeoverflow.entities;

import java.util.Objects;

// TODO turn into JPA Entity once persistence is set up
public class User {
  private String email;
  private String password;
  private String firstName;
  private String lastName;

  public User() {
  }

  public User(String email, String password, String firstName, String lastName) {
    this.email = Objects.requireNonNull(email);
    this.password = Objects.requireNonNull(password);
    this.firstName = Objects.requireNonNull(firstName);
    this.lastName = Objects.requireNonNull(lastName);
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
}
