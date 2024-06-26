package fr.pentagon.ugeoverflow.controllers.rest;

import fr.pentagon.revevue.common.exception.HttpException;
import fr.pentagon.ugeoverflow.config.authorization.RequireUser;
import fr.pentagon.ugeoverflow.config.security.AuthenticationChecker;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.*;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.*;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import fr.pentagon.ugeoverflow.service.QuestionService;
import fr.pentagon.ugeoverflow.service.ReviewService;
import fr.pentagon.ugeoverflow.service.UserService;
import fr.pentagon.ugeoverflow.utils.Routes;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@RestController
public class UserController {
  private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());
  private final UserService userService;
  private final UserRepository userRepository;
  private final QuestionService questionService;
  private final ReviewService reviewService;

  public UserController(UserService userService, UserRepository userRepository, QuestionService questionService, ReviewService reviewService) {
    this.userService = Objects.requireNonNull(userService);
    this.userRepository = Objects.requireNonNull(userRepository);
    this.questionService = Objects.requireNonNull(questionService);
    this.reviewService = Objects.requireNonNull(reviewService);
  }

  @PostMapping(Routes.User.ROOT)
  public ResponseEntity<UserIdDTO> registerUser(@Valid @RequestBody UserRegisterDTO userRegisterDTO, BindingResult bindingResult) {
    LOGGER.info("try to register");
    if (bindingResult.hasErrors()) {
      throw HttpException.badRequest("Une erreur est survenue, votre formulaire est invalide");
    }
    return ResponseEntity.ok(userService.register(userRegisterDTO));
  }

  @PostMapping(Routes.User.FOLLOW + "/{id}")
  @RequireUser
  public ResponseEntity<Void> followUser(@PathVariable("id") long id) {
    LOGGER.info("Trying to follow");
    var user = AuthenticationChecker.checkAuthentication();
    userService.follow(user.id(), id);
    return ResponseEntity.ok().build();
  }

  @PostMapping(Routes.User.UNFOLLOW + "/{id}")
  @RequireUser
  public ResponseEntity<Void> unfollowUser(@PathVariable("id") long id) {
    LOGGER.info("Trying to unfollow");
    var user = AuthenticationChecker.checkAuthentication();
    userService.unfollow(user.id(), id);
    return ResponseEntity.ok().build();
  }

  @GetMapping(Routes.User.FOLLOWING)
  @RequireUser
  public ResponseEntity<List<UserFollowingDTO>> getCurrentUserFollowing() {
    var user = AuthenticationChecker.checkAuthentication();
    return ResponseEntity.ok(userService.getUserFollowings(user.id()));
  }


  @GetMapping(Routes.User.ROOT)
  public ResponseEntity<List<UserFollowInfoDTO>> getAllRegisteredUsers() {
    LOGGER.info("Trying to get all registered Users");
    var auth = AuthenticationChecker.authentication();
    return auth.map(u -> ResponseEntity.ok(userService.userRegisteredList(u.id())))
        .orElse(ResponseEntity.ok(userService.userRegisteredList()));

  }

  @GetMapping(Routes.User.CURRENT_USER)
  @RequireUser
  public ResponseEntity<UserInfoDTO> getCurrentUserInformation() {
    var userInfo = AuthenticationChecker.checkAuthentication();
    var user = userRepository.findById(userInfo.id()).orElseThrow();
    return ResponseEntity.ok(new UserInfoDTO(user.getUsername(), user.getLogin(), user.getEmail(), user.getRole()));
  }

  @PatchMapping(Routes.User.CURRENT_USER)
  @RequireUser
  public ResponseEntity<Void> updateCurrentAuthenticatedUserInformation(@RequestBody @Valid UserInfoUpdateDTO userInfoUpdateDTO) {
    var user = AuthenticationChecker.checkAuthentication();
    userService.updateUser(user.id(), userInfoUpdateDTO);
    return ResponseEntity.ok().build();
  }

  @PostMapping(Routes.User.PASSWORD)
  @RequireUser
  public ResponseEntity<Void> updateCurrentUserPassword(@RequestBody @Valid UserPasswordUpdateDTO userPasswordUpdateDTO) {
    var user = AuthenticationChecker.checkAuthentication();
    userService.updateUserPassword(user.id(), userPasswordUpdateDTO);
    return ResponseEntity.ok().build();
  }

  @PostMapping(Routes.User.RECOMMENDED_REVIEW)
  @RequireUser
  public ResponseEntity<List<ReviewContentDTO>> getRecommendedReview(@RequestBody @Valid QuestionUserIdDTO questionUserIdDTO) {
    var user = AuthenticationChecker.checkAuthentication();
    if (questionUserIdDTO.questionContent() == null || questionUserIdDTO.questionContent().isBlank()) {
      LOGGER.info("empty string");
      return ResponseEntity.ok(List.of());
    }
    var recommendedReview = userService.getRecommendedReviewForQuestion(user.id(), questionUserIdDTO.questionContent());
    return ResponseEntity.ok(recommendedReview);
  }

  @GetMapping(Routes.User.ROOT + "/{id:[0-9]+}" + "/profile")
  public ResponseEntity<UserInfoSecureDTO> getUserProfile(@PathVariable("id") long id) {
    return ResponseEntity.of(userService.getUserInfoSecure(id));
  }

  @GetMapping(Routes.User.ROOT + "/{id:[0-9]+}" + "/questions")
  public ResponseEntity<List<QuestionDTO>> getUserQuestions(@PathVariable("id") long id) {
    return ResponseEntity.ok(questionService.getQuestionsFromUser(id));
  }

  @GetMapping(Routes.User.ROOT + "/{id:[0-9]+}" + "/reviews")
  public ResponseEntity<List<UserReviewDTO>> getUserReviews(@PathVariable("id") long id) {
    return ResponseEntity.ok(reviewService.getReviewsFromUser(id));
  }
}
