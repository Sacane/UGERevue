package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.DatasourceTestConfig;
import fr.pentagon.ugeoverflow.config.authorization.Role;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(DatasourceTestConfig.class)
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @AfterEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Get empty list of registered users without Admin ")
    void getAllRegisteredUserNonUnauthenticatedEmpty(){
        var users = userService.userRegisteredList();
        assertEquals(0, users.size()-1);
    }

    @Test
    @DisplayName("Get list of users registered (including admin)")
    void getAllRegisteredUserNonUnauthenticated(){
        var registered = userRepository.saveAll(
                List.of(
                        new User("qtdrake", "qtdrake", "qtellier", "qt@email.com", Role.USER),
                        new User("sacane", "sacane", "qtellier", "qt@email.com", Role.USER),
                        new User("verestah", "verestah", "qtellier", "qt@email.com", Role.USER),
                        new User("qtdrake", "qtdrake", "qtellier", "qt@email.com", Role.USER)
                )
        );
        var users = userService.userRegisteredList();
        assertEquals(users.size(), registered.size());
    }
}
