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
  private static final Logger LOGGER = Logger.getLogger(SetupDataLoader.class.getName());
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
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
    createArnaudIfNotFound();
    alreadySetup = true;
  }

  @Transactional
  public void createAdminIfNotFound() {
    var admin = userRepository.findByLogin("admin").orElse(null);
    if (admin == null) {
      LOGGER.info("Creating admin..");
      admin = new User("admin", "admin", passwordEncoder.encode("admin"), "admin@admin.com", Role.ADMIN);
      userRepository.save(admin);
    }
  }

  @Transactional
  public void createArnaudIfNotFound() {
    var arnaud = userRepository.findByLogin("arnaud").orElse(null);
    if (arnaud == null) {
      LOGGER.info("Creating arnaud..");
      arnaud = new User("arnaud", "arnaud", passwordEncoder.encode("arnaud"), "arnaud.carayol@u-pem.fr", Role.USER);
      userRepository.save(arnaud);
    }
  }
}
