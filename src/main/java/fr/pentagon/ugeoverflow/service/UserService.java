package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.config.authorization.Role;
import fr.pentagon.ugeoverflow.config.security.SecurityContext;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserFollowInfoDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserRegisterDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.UserIdDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import jakarta.transaction.Transactional;
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
  public UserIdDTO register(UserRegisterDTO userDTO) {
    if (userRepository.existsByUsername(userDTO.username())) {
      throw HttpException.badRequest("User with this username already exist");
    }
    var user = new User(userDTO.username(),
        userDTO.login(),
        passwordEncoder.encode(userDTO.password()),
        userDTO.email(),
        Role.USER
    );
    var newUser = userRepository.save(user);
    return new UserIdDTO(newUser.getId(), newUser.getUsername());
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

  @Transactional
  public List<UserFollowInfoDTO> userRegisteredList(long userId){
    var followers = userRepository.findFollowersById(userId);
    return userRepository.findAllUsers()
            .stream()
            .map(user -> user.toUserFollowInfoDTO(followers.contains(user)))
            .toList();
  }

  @Transactional
  public List<UserFollowInfoDTO> userRegisteredList(){
    return userRepository.findAllUsers()
            .stream()
            .map(user -> user.toUserFollowInfoDTO(false))
            .toList();
  }
}
