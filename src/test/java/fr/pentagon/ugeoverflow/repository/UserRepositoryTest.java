package fr.pentagon.ugeoverflow.repository;

import fr.pentagon.ugeoverflow.config.authorization.Role;
import fr.pentagon.ugeoverflow.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;
  private List<User> userTest;

  @BeforeEach
  void setup() {
    userTest = List.of(
            new User("verestah", "verestah1", "12345", "verestah@gmail.com", Role.USER),
            new User("qtdrake", "qtdrake1", "12345", "qtdrake@gmail.com", Role.USER),
            new User("sacane", "sacane1", "12345", "sacane@gmail.com", Role.USER)
    );
  }

  @Test
  @DisplayName("Should save the user in the database")
  void save() {
    var savedUser = userRepository.save(userTest.get(0));
    assertNotNull(savedUser);
    assertEquals("verestah", savedUser.getUsername());
  }

  @Test
  @DisplayName("Should find the user with the login verestah1")
  void findByLogin() {
    userRepository.save(userTest.get(0));
    var out = userRepository.findByLogin("verestah1");
    assertTrue(out.isPresent());
    var finded = out.get();
    assertNotNull(finded);
    assertEquals("verestah1", finded.getLogin());
  }

  @Test
  @DisplayName("Should find nothing")
  void shouldNotFindByLogin() {
    var out = userRepository.findByLogin("verestah1");
    assertTrue(out.isEmpty());
  }

  @Test
  @DisplayName("Should return that the user with the username verestah exist")
  void existByUsername() {
    userRepository.save(userTest.get(0));
    var exist = userRepository.existsByUsername("verestah");
    assertTrue(exist);
  }

  @Test
  @DisplayName("Should find 3 users registered")
  void findAllRegisteredUser(){
      userRepository.saveAll(userTest);
      var users = userRepository.findAllUsers();
      assertEquals(3, users.size());
  }

  @Test
  @DisplayName("Should find 0 users registered")
  void noRegisteredUserFind(){
    var users = userRepository.findAllUsers();
    assertEquals(0, users.size());
  }
}
