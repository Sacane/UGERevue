package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserRegisterDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.UserIdDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = Objects.requireNonNull(userRepository);
    this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
  }

  @Transactional
  public ResponseEntity<UserIdDTO> register(UserRegisterDTO userDTO) {
    if (userRepository.existsByUsername(userDTO.username())) {
      throw HttpException.badRequest("User with this username already exist");
    }
    var newUser = userRepository.save(new User(userDTO.username(),
        userDTO.login(),
        passwordEncoder.encode(userDTO.password()),
        userDTO.email()
    ));
    return ResponseEntity.ok(new UserIdDTO(newUser.getId(), newUser.getUsername()));
  }

  @Transactional
  public void follow(long followerId, long followedId) {
    var follower = userRepository.findById(followerId).orElseThrow();
    var followed = userRepository.findById(followedId).orElseThrow(
        () -> HttpException.notFound("user " + followedId + " not found")
    );

    follower.follows(followed);
    userRepository.saveAll(List.of(follower, followed));
  }

  @Transactional
  public void unfollow(long followerId, long followedId) {
    var follower = userRepository.findById(followerId).orElseThrow();
    var followed = userRepository.findById(followedId).orElseThrow(
        () -> HttpException.notFound("user " + followedId + " not found")
    );

    follower.unfollows(followed);
    userRepository.saveAll(List.of(follower, followed));
  }
}
