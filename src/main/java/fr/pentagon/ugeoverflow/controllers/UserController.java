package fr.pentagon.ugeoverflow.controllers;

import fr.pentagon.ugeoverflow.config.authorization.RequireUser;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserRegisterDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.UserIdDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import fr.pentagon.ugeoverflow.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Objects;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());
  private final UserService userService;
  private final UserRepository userRepository;

  public UserController(UserService userService, UserRepository userRepository) {
    this.userService = Objects.requireNonNull(userService);
    this.userRepository = Objects.requireNonNull(userRepository);
  }

  @PostMapping
  public ResponseEntity<UserIdDTO> registerUser(@RequestBody UserRegisterDTO userRegisterDTO) {
    return ResponseEntity.ok(userService.register(userRegisterDTO));
  }

  @PostMapping("/follow/{id}")
  @RequireUser
  public ResponseEntity<Void> followUser(@PathVariable long id, Principal principal) {
    if (principal == null) {
      throw HttpException.unauthorized("no user logged in");
    }
    var user = userRepository.findByLogin(principal.getName()).orElseThrow();
    userService.follow(user.getId(), id);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/unfollow/{id}")
  @RequireUser
  public ResponseEntity<Void> unfollowUser(@PathVariable long id, Principal principal) {
    if (principal == null) {
      throw HttpException.unauthorized("no user logged in");
    }
    var user = userRepository.findByLogin(principal.getName()).orElseThrow();
    userService.unfollow(user.getId(), id);
    return ResponseEntity.ok().build();
  }
}
