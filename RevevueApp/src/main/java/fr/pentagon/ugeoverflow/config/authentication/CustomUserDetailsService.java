package fr.pentagon.ugeoverflow.config.authentication;

import fr.pentagon.revevue.common.exception.HttpException;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String login) {
    var user = userRepository.findByLogin(login)
        .orElseThrow(() -> HttpException.notFound("User with login " + login + " does not exists"));
    return new RevevueUserDetail(user.getId(), user.getPassword(), user.getLogin(), user.getRole(), user.getUsername());
  }
}
