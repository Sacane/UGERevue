package fr.pentagon.ugeoverflow.controllers.rest;

import fr.pentagon.revevue.common.exception.HttpException;
import fr.pentagon.ugeoverflow.config.authorization.RequireUser;
import fr.pentagon.ugeoverflow.config.security.SecurityContext;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.*;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewContentDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.UserFollowingDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.UserIdDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.UserInfoDTO;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import fr.pentagon.ugeoverflow.service.UserService;
import fr.pentagon.ugeoverflow.utils.Routes;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<UserIdDTO> registerUser(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
    LOGGER.info("try to register");
    return ResponseEntity.ok(userService.register(userRegisterDTO));
  }

  @PostMapping(Routes.User.FOLLOW + "/{id}")
  @RequireUser
  public ResponseEntity<Void> followUser(@PathVariable("id") long id) {
    LOGGER.info("Trying to follow");
    var user = SecurityContext.checkAuthentication();
    userService.follow(user.id(), id);
    return ResponseEntity.ok().build();
  }

  @PostMapping(Routes.User.UNFOLLOW + "/{id}")
  @RequireUser
  public ResponseEntity<Void> unfollowUser(@PathVariable("id") long id) {
    LOGGER.info("Trying to unfollow");
    var user = SecurityContext.checkAuthentication();
    userService.unfollow(user.id(), id);
    return ResponseEntity.ok().build();
  }

  @GetMapping(Routes.User.FOLLOWING)
  @RequireUser
  public ResponseEntity<List<UserFollowingDTO>> getCurrentUserFollowing() {
    var user = SecurityContext.checkAuthentication();
    return ResponseEntity.ok(userService.getUserFollowings(user.id()));
  }


  @GetMapping(Routes.User.ROOT)
  public ResponseEntity<List<UserFollowInfoDTO>> getAllRegisteredUsers() {
    LOGGER.info("Trying to get all registered Users");
    var auth = SecurityContext.authentication();
    return auth.map(u -> ResponseEntity.ok(userService.userRegisteredList(u.id())))
        .orElse(ResponseEntity.ok(userService.userRegisteredList()));

  }

  @GetMapping(Routes.User.CURRENT_USER)
  @RequireUser
  public ResponseEntity<UserInfoDTO> getCurrentUserInformation() {
    var userInfo = SecurityContext.checkAuthentication();
    var user = userRepository.findById(userInfo.id()).orElseThrow();
    return ResponseEntity.ok(new UserInfoDTO(user.getUsername(), user.getLogin(), user.getEmail(), user.getRole()));
  }

  @PatchMapping(Routes.User.CURRENT_USER)
  @RequireUser
  public ResponseEntity<Void> updateCurrentAuthenticatedUserInformation(@RequestBody @Valid UserInfoUpdateDTO userInfoUpdateDTO) {
    var user = SecurityContext.checkAuthentication();
    userService.updateUser(user.id(), userInfoUpdateDTO);
    return ResponseEntity.ok().build();
  }

  @PostMapping(Routes.User.PASSWORD)
  @RequireUser
  public ResponseEntity<Void> updateCurrentUserPassword(@RequestBody @Valid UserPasswordUpdateDTO userPasswordUpdateDTO) {
    var user = SecurityContext.checkAuthentication();
    userService.updateUserPassword(user.id(), userPasswordUpdateDTO);
    return ResponseEntity.ok().build();
  }

  @PostMapping(Routes.User.RECOMMENDED_REVIEW)
  @RequireUser
  public ResponseEntity<List<ReviewContentDTO>> getRecommendedReview(@RequestBody @Valid QuestionUserIdDTO questionUserIdDTO) {
    var user = SecurityContext.checkAuthentication();
    var recommendedReview = userService.getRecommendedReviewForQuestion(user.id(), questionUserIdDTO.questionContent());
    return ResponseEntity.ok(recommendedReview);
  }
}
