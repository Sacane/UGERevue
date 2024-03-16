package fr.pentagon.ugeoverflow.config;

import fr.pentagon.ugeoverflow.config.authorization.Role;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.logging.Logger;


// Source used: https://www.baeldung.com/role-and-privilege-for-spring-security-registration
@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private static final Logger LOGGER = Logger.getLogger(SetupDataLoader.class.getName());
  boolean alreadySetup = false;

  public SetupDataLoader(PasswordEncoder passwordEncoder, UserRepository userRepository) {
    this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
    this.userRepository = Objects.requireNonNull(userRepository);
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    if (alreadySetup) {
      return;
    }

    createAdminIfNotFound();
    alreadySetup = true;
  }

  @Transactional
  public void createAdminIfNotFound() {
    userRepository.deleteAll();
    var admin = userRepository.findByLogin("admin").orElse(null);
    if (admin == null) {
      LOGGER.info("Creating admin..");
      admin = new User("admin", "admin", passwordEncoder.encode("password"), "admin@admin.fr", Role.ADMIN);
      userRepository.save(admin);
    }
  }
}
