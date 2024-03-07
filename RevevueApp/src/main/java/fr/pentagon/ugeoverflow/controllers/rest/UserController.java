package fr.pentagon.ugeoverflow.controllers.rest;

import fr.pentagon.ugeoverflow.config.authorization.RequireUser;
import fr.pentagon.ugeoverflow.config.security.SecurityContext;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserFollowInfoDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserRegisterDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.UserIdDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import fr.pentagon.ugeoverflow.service.UserService;
import fr.pentagon.ugeoverflow.utils.Routes;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@RestController
public class UserController {
  private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());
  private final UserService userService;
  private final UserRepository userRepository;

  public UserController(UserService userService, UserRepository userRepository) {
    this.userService = Objects.requireNonNull(userService);
    this.userRepository = Objects.requireNonNull(userRepository);
  }

  @PostMapping(Routes.User.ROOT)
  public ResponseEntity<UserIdDTO> registerUser(@RequestBody UserRegisterDTO userRegisterDTO) {
    LOGGER.info("try to register");
    return ResponseEntity.ok(userService.register(userRegisterDTO));
  }

  @PostMapping(Routes.User.FOLLOW + "/{id}")
  @RequireUser
  public ResponseEntity<Void> followUser(@PathVariable("id") long id, Principal principal) {
    LOGGER.info("Trying to follow");
    if (principal == null) {
      throw HttpException.unauthorized("no user logged in");
    }
    var user = userRepository.findByLogin(principal.getName()).orElseThrow();
    userService.follow(user.getId(), id);
    return ResponseEntity.ok().build();
  }

  @PostMapping(Routes.User.UNFOLLOW + "/{id}")
  @RequireUser
  public ResponseEntity<Void> unfollowUser(@PathVariable("id") long id, Principal principal) {
    LOGGER.info("Trying to unfollow");
    if (principal == null) {
      throw HttpException.unauthorized("no user logged in");
    }
    var user = userRepository.findByLogin(principal.getName()).orElseThrow();
    userService.unfollow(user.getId(), id);
    return ResponseEntity.ok().build();
  }

  @GetMapping(Routes.User.ROOT)
  public ResponseEntity<List<UserFollowInfoDTO>> getAllRegisteredUsers() {
    LOGGER.info("Trying to get all registered Users");
    if (SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) { // TODO fix it
      return ResponseEntity.ok(userService.userRegisteredList());
    }
    var userConnected = SecurityContext.checkAuthentication();
    return ResponseEntity.ok(userService.userRegisteredList(userConnected.id()));
  }
}