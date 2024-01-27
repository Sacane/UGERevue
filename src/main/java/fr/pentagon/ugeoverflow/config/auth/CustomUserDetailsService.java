package fr.pentagon.ugeoverflow.config.auth;

import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String login) {
    var userResponse = userRepository.findByLogin(login);
    if(userResponse.isEmpty()) throw HttpException.notFound("User with login " + login + " does not exists");
    var user = userResponse.get();
    return User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            //TODO add roles
            .build();
  }
}
