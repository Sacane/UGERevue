package fr.pentagon.ugeoverflow.controllers.rest;

import fr.pentagon.ugeoverflow.config.authorization.RequireUser;
import fr.pentagon.ugeoverflow.config.security.SecurityContext;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.*;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewContentDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.UserFollowingDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.UserIdDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.UserInfoDTO;
import fr.pentagon.revevue.common.exception.HttpException;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import fr.pentagon.ugeoverflow.service.UserService;
import fr.pentagon.ugeoverflow.utils.Routes;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
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
  public ResponseEntity<UserIdDTO> registerUser(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
    LOGGER.info("try to register");
    return ResponseEntity.ok(userService.register(userRegisterDTO));
  }

  @PostMapping(Routes.User.FOLLOW + "/{id}")
  @RequireUser
  public ResponseEntity<Void> followUser(@PathVariable("id") @Positive long id, Principal principal) {
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
  public ResponseEntity<Void> unfollowUser(@PathVariable("id") @Positive long id, Principal principal) {
    LOGGER.info("Trying to unfollow");
    if (principal == null) {
      throw HttpException.unauthorized("no user logged in");
    }
    var user = userRepository.findByLogin(principal.getName()).orElseThrow();
    userService.unfollow(user.getId(), id);
    return ResponseEntity.ok().build();
  }

  @GetMapping(Routes.User.FOLLOWING)
  @RequireUser
  public ResponseEntity<List<UserFollowingDTO>> getCurrentUserFollowing(Principal principal) {
    if (principal == null) {
      throw HttpException.forbidden("No user currently authenticated");
    }
    return ResponseEntity.ok(userService.getUserFollowings(principal.getName()));
  }


  @GetMapping(Routes.User.ROOT)
  public ResponseEntity<List<UserFollowInfoDTO>> getAllRegisteredUsers() {
    LOGGER.info("Trying to get all registered Users");
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      throw HttpException.forbidden("no authentication");
    }
    if (authentication.getName().equals("anonymousUser")) { // TODO fix it
      return ResponseEntity.ok(userService.userRegisteredList());
    }
    var userConnected = SecurityContext.checkAuthentication();
    return ResponseEntity.ok(userService.userRegisteredList(userConnected.id()));
  }

  @GetMapping(Routes.User.CURRENT_USER)
  @RequireUser
  public ResponseEntity<UserInfoDTO> getCurrentUserInformation(Principal principal) {
    if (principal == null) {
      throw HttpException.forbidden("No user currently authenticated");
    }
    var user = userRepository.findByLogin(principal.getName()).orElseThrow();
    return ResponseEntity.ok(new UserInfoDTO(user.getUsername(), user.getLogin(), user.getEmail(), user.getRole()));
  }

  @PatchMapping(Routes.User.CURRENT_USER)
  @RequireUser
  public ResponseEntity<Void> updateCurrentAuthenticatedUserInformation(@RequestBody @Valid UserInfoUpdateDTO userInfoUpdateDTO,
                                                                        Principal principal) {
    if (principal == null) {
      throw HttpException.forbidden("No user currently authenticated");
    }
    userService.updateUser(principal.getName(), userInfoUpdateDTO);
    return ResponseEntity.ok().build();
  }

  @PostMapping(Routes.User.PASSWORD)
  @RequireUser
  public ResponseEntity<Void> updateCurrentUserPassword(@RequestBody @Valid UserPasswordUpdateDTO userPasswordUpdateDTO,
                                                        Principal principal) {
    if (principal == null) {
      throw HttpException.forbidden("No user currently authenticated");
    }
    userService.updateUserPassword(principal.getName(), userPasswordUpdateDTO);
    return ResponseEntity.ok().build();
  }

  @GetMapping(Routes.User.RECOMMENDED_REVIEW)
  @RequireUser
  public ResponseEntity<List<ReviewContentDTO>> getRecommendedReview(@RequestBody @Valid QuestionUserIdDTO questionUserIdDTO) {
    var user = SecurityContext.checkAuthentication();
    var recommendedReview = userService.getRecommendedReviewForQuestion(user.id(), questionUserIdDTO.questionContent());
    return ResponseEntity.ok(recommendedReview);
  }
}
