package fr.pentagon.ugeoverflow.controllers.rest;

import fr.pentagon.revevue.common.exception.HttpException;
import fr.pentagon.ugeoverflow.config.authorization.RequireUser;
import fr.pentagon.ugeoverflow.config.security.AuthenticationChecker;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewOnReviewBodyDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewOnReviewDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewRemoveDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.VoteBodyDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.*;
import fr.pentagon.ugeoverflow.service.ReviewService;
import fr.pentagon.ugeoverflow.utils.Routes;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
public class ReviewController {
  private static final Logger LOGGER = Logger.getLogger(ReviewController.class.getName());
  private final ReviewService reviewService;

  public ReviewController(ReviewService reviewService) {
    this.reviewService = reviewService;
  }

  @PatchMapping(Routes.Review.ROOT + "/{reviewId}")
  public ResponseEntity<ReviewUpdateDTO> updateById(@PathVariable(name = "reviewId") long reviewId, @RequestBody ReviewUpdateDTO reviewUpdateDTO){
    LOGGER.info("fetch on [PATCH] " + Routes.Review.ROOT + " with variable " + reviewId);
    AuthenticationChecker.checkAuthentication();

    return ResponseEntity.ok(reviewService.updateById(reviewId, reviewUpdateDTO));
  }
  @GetMapping(Routes.Review.ROOT + "/{reviewId}")
  public ResponseEntity<DetailReviewResponseDTO> findDetailsReview(@PathVariable(name = "reviewId") long reviewId) {
    LOGGER.info("fetch on " + Routes.Review.ROOT + " => " + reviewId);
    var auth = AuthenticationChecker.authentication();
      return auth.map(revevueUserDetail ->
                    ResponseEntity.ok(reviewService.findDetailFromReviewId(revevueUserDetail.id(), reviewId)))
            .orElseGet(() -> ResponseEntity.ok(reviewService.findDetailFromReviewId(null, reviewId)));
  }

  @GetMapping(Routes.Review.ROOT + Routes.Question.IDENT + "/{questionId}")
  public ResponseEntity<List<ReviewResponseChildrenDTO>> findAllReviews(@PathVariable(name = "questionId") long questionId) {
    return ResponseEntity.ok(reviewService.findReviewsByQuestionId(questionId));
  }

  @PostMapping(Routes.Review.ROOT)
  @RequireUser
  public ResponseEntity<ReviewQuestionResponseDTO> addReview(@Valid @RequestBody ReviewOnReviewBodyDTO reviewOnReviewBodyDTO, BindingResult bindingResult) {
      var userDetail = AuthenticationChecker.checkAuthentication();
      if(bindingResult.hasErrors()) {
          throw HttpException.badRequest("La review est invalide");
      }
      LOGGER.info("add review of review");
      return ResponseEntity.ok(reviewService.addReview(new ReviewOnReviewDTO(userDetail.id(), reviewOnReviewBodyDTO.reviewId(), reviewOnReviewBodyDTO.content(), reviewOnReviewBodyDTO.tagList())));
  }

  @DeleteMapping(Routes.Review.ROOT + "/{reviewId}")
  @RequireUser
  public ResponseEntity<Void> removeReview(@PathVariable(name = "reviewId") long reviewId) {
    var user = AuthenticationChecker.authentication().orElseThrow();
    LOGGER.info("perform delete on " + Routes.Review.ROOT);
    reviewService.remove(new ReviewRemoveDTO(user.id(), reviewId));

    return ResponseEntity.ok().build();
  }

  @PostMapping(Routes.Review.ROOT + "/{reviewId}/vote")
  @RequireUser
  public ResponseEntity<Void> voteReview(@PathVariable(name = "reviewId") long reviewId, @Valid @RequestBody VoteBodyDTO voteBodyDTO) {
    var user = AuthenticationChecker.checkAuthentication();

    reviewService.vote(user.id(), reviewId, voteBodyDTO.up());

    return ResponseEntity.ok().build();
  }

  @DeleteMapping(Routes.Review.ROOT + "/{reviewId}/cancelVote")
  @RequireUser
  public ResponseEntity<Void> cancelVoteReview(@PathVariable(name = "reviewId") long reviewId) {
    var user = AuthenticationChecker.checkAuthentication();

    reviewService.cancelVote(user.id(), reviewId);

    return ResponseEntity.ok().build();
  }

  @GetMapping(Routes.Review.ROOT + "/tags/{tag}")
  @RequireUser
  public ResponseEntity<List<ReviewQuestionTitleDTO>> findByTag(
      @PathVariable(name = "tag") @NotNull @NotBlank String tag
  ) {
    LOGGER.info("perform request on " + Routes.Review.ROOT + "/tags/" + tag);
    List<ReviewQuestionTitleDTO> byTag = reviewService.findByTag(tag);
    return ResponseEntity.ok(byTag);
  }
}
