package fr.pentagon.ugeoverflow.config.auth;

import fr.pentagon.ugeoverflow.repositories.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  // TODO adapter avec le vrai userRepository
  @Override
  public UserDetails loadUserByUsername(String email) {
    var user = userRepository.findUserByEmail(email);
    return User.builder()
        .username(user.getEmail())
        .password(user.getPassword())
        .roles("USER")
        .build();
  }
}
