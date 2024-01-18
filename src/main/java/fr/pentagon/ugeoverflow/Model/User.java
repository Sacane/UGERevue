package fr.pentagon.ugeoverflow.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.hibernate.type.descriptor.jdbc.ObjectNullResolvingJdbcType;

import java.util.Objects;

@Entity
public final class User {

    @Id
    @GeneratedValue()
    private long id;
    private String username;
    private String login;
    private String password;
    private String email;

    public User(){}
    public User(String username, String login, String password, String email){
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
}
