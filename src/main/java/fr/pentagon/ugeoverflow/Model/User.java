package fr.pentagon.ugeoverflow.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Arrays;
import java.util.Objects;

@Entity
public final class User {

    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String login;
    private byte[] password;

    public User(){}
    public User(String username, String login, byte[] password){
        this.username = Objects.requireNonNull(username);
        this.login = Objects.requireNonNull(login);
        this.password = Objects.requireNonNull(Arrays.copyOf(password, password.length));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }
}
