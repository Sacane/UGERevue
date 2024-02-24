package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.config.authorization.Role;
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
import java.util.logging.Logger;

@Service
public class UserService {
  private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = Objects.requireNonNull(userRepository);
    this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
  }

  @Transactional
  public UserIdDTO register(UserRegisterDTO userDTO) {
    if (userRepository.existsByUsernameOrLogin(userDTO.username(), userDTO.login())) {
      throw HttpException.badRequest("User with this username or login already exist");
    }
    var user = new User(userDTO.username(),
        userDTO.login(),
        passwordEncoder.encode(userDTO.password()),
        userDTO.email(),
        Role.USER
    );
    var newUser = userRepository.save(user);
    return new UserIdDTO(newUser.getId(), newUser.getUsername(), newUser.getRole().toString());
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
    var follows = userRepository.findFollowsById(userId);
    return userRepository.findAllUsers()
            .stream()
            .map(user -> user.toUserFollowInfoDTO(follows.contains(user)))
            .toList();
  }

  @Transactional
  public List<UserFollowInfoDTO> userRegisteredList(){
    return userRepository.findAllUsers()
            .stream()
            .filter(u -> u.getRole() != Role.ADMIN)
            .map(user -> user.toUserFollowInfoDTO(false))
            .toList();
  }
}
