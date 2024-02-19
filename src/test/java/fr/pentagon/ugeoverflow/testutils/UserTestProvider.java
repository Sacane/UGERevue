package fr.pentagon.ugeoverflow.testutils;

import fr.pentagon.ugeoverflow.config.authorization.Role;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.List;

public class UserTestProvider {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserTestProvider (
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public HashMap<String, Long> addSomeUserIntoDatabase() {
        var userToId = new HashMap<String, Long>();
        List<User> users = List.of(
                new User("Sacane", "loginSacane", passwordEncoder.encode("SacanePassword"), "sacane.test@gmail.com", Role.USER),
                new User("Sacane2", "loginSacane2", passwordEncoder.encode("SacanePassword2"), "sacane2.test@gmail.com", Role.USER),
                new User("Sacane3", "loginSacane3", passwordEncoder.encode("SacanePassword3"), "sacane3.test@gmail.com", Role.USER),
                new User("Sacane4", "loginSacane4", passwordEncoder.encode("SacanePassword4"), "sacane4.test@gmail.com", Role.USER)
        );
        userRepository.saveAll(users);
        return userToId;
    }
}
