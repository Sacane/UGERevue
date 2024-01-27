package fr.pentagon.ugeoverflow.repositories;

import fr.pentagon.ugeoverflow.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

// TODO make it an actual repository when persistence is set up
@Repository
public class UserRepository {
  private final PasswordEncoder passwordEncoder;

  public UserRepository(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  public User findUserByEmail(String email) {
    return new User(email, passwordEncoder.encode("123456"), "FirstName", "LastName");
  }
}
