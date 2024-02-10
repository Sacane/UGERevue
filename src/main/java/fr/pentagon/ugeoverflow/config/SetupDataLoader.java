package fr.pentagon.ugeoverflow.config;

import fr.pentagon.ugeoverflow.config.auth.Roles;
import fr.pentagon.ugeoverflow.model.Role;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.repository.RoleRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;


// Source used: https://www.baeldung.com/role-and-privilege-for-spring-security-registration
@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  boolean alreadySetup = false;

  public SetupDataLoader(RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserRepository userRepository) {
    this.roleRepository = Objects.requireNonNull(roleRepository);
    this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
    this.userRepository = Objects.requireNonNull(userRepository);
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    if (alreadySetup) {
      return;
    }
    createRoleIfNotFound(Roles.ADMIN.roleName());
    createRoleIfNotFound(Roles.USER.roleName());

    createAdminIfNotFound();

    alreadySetup = true;
  }

  @Transactional
  public void createAdminIfNotFound() {
    var adminRole = roleRepository.findByName(Roles.ADMIN.roleName()).orElseThrow();
    var userRole = roleRepository.findByName(Roles.USER.roleName()).orElseThrow();

    var admin = userRepository.findByLogin("admin").orElse(null);
    if (admin == null) {
      admin = new User("admin", "admin", passwordEncoder.encode("password"), "admin@admin.fr");
      admin.addRole(adminRole);
      admin.addRole(userRole);
      userRepository.save(admin);
    }
  }

  @Transactional
  public void createRoleIfNotFound(String name) {
    var role = roleRepository.findByName(name).orElse(null);
    if (role == null) {
      role = new Role(name);
      roleRepository.save(role);
    }
  }
}
