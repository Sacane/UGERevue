package fr.pentagon.ugeoverflow.repository;

import fr.pentagon.ugeoverflow.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User userTest;

   /* private final UserRepository userRepository;

    public UserRepositoryTest(UserRepository repository) {
        this.userRepository = repository;
    }*/

    @BeforeEach
    void setup(){
        userTest = new User("verestah","verestah1","12345","verestah@gmail.com");
    }

    @Test
    @DisplayName("Should save the user in the database")
    void save(){
        var savedUser = userRepository.save(userTest);
        assertNotNull(savedUser);
        assertEquals("verestah",savedUser.getUsername());
    }

    @Test
    @DisplayName("Should find the user with the login verestah1")
    void findByLogin(){
        userRepository.save(userTest);
        var out = userRepository.findByLogin("verestah1");
        assertTrue(out.isPresent());
        var finded = out.get();
        assertNotNull(finded);
        assertEquals("verestah1", finded.getLogin());
    }

    @Test
    @DisplayName("Should find nothing")
    void shouldNotFindByLogin(){
        var out = userRepository.findByLogin("verestah1");
        assertTrue(out.isEmpty());
    }

    @Test
    @DisplayName("Should return that the user with the username verestah exist")
    void existByUsername(){
        userRepository.save(userTest);
        var exist = userRepository.existsByUsername("verestah");
        assertTrue(exist);
    }
}
