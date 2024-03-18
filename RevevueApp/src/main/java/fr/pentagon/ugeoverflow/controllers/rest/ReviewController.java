package fr.pentagon.ugeoverflow.controllers.rest;

import fr.pentagon.ugeoverflow.config.authorization.RequireUser;
import fr.pentagon.ugeoverflow.config.security.SecurityContext;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewOnReviewBodyDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewOnReviewDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewRemoveDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.VoteBodyDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.DetailReviewResponseDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewQuestionResponseDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewQuestionTitleDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewResponseChildrenDTO;
import fr.pentagon.ugeoverflow.service.ReviewService;
import fr.pentagon.ugeoverflow.utils.Routes;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.logging.Logger;

@RestController
public class ReviewController {
  private static final Logger LOGGER = Logger.getLogger(ReviewController.class.getName());
  private final ReviewService reviewService;

  public ReviewController(ReviewService reviewService) {
    this.reviewService = reviewService;
  }

  @GetMapping(Routes.Review.ROOT + "/{reviewId}")
  public ResponseEntity<DetailReviewResponseDTO> findDetailsReview(@PathVariable(name = "reviewId") long reviewId) {
    LOGGER.info("fetch on " + Routes.Review.ROOT + " => " + reviewId);
    var auth = SecurityContext.authentication();
    ResponseEntity<DetailReviewResponseDTO> detailReviewResponseDTOResponseEntity = auth.map(revevueUserDetail ->
                    ResponseEntity.ok(reviewService.findDetailFromReviewId(revevueUserDetail.id(), reviewId)))
            .orElseGet(() -> ResponseEntity.ok(reviewService.findDetailFromReviewId(null, reviewId)));
    LOGGER.info("TEST => " + detailReviewResponseDTOResponseEntity.getBody());
    return detailReviewResponseDTOResponseEntity;
  }

  @GetMapping(Routes.Review.ROOT + Routes.Question.IDENT + "/{questionId}")
  public ResponseEntity<List<ReviewResponseChildrenDTO>> findAllReviews(@PathVariable(name = "questionId") long questionId) {
    return ResponseEntity.ok(reviewService.findReviewsByQuestionId(questionId));
  }

    @PostMapping(Routes.Review.ROOT)
    @RequireUser
    public ResponseEntity<ReviewQuestionResponseDTO> addReview(@Valid @RequestBody ReviewOnReviewBodyDTO reviewOnReviewBodyDTO) {
        var userDetail = SecurityContext.checkAuthentication();
        return ResponseEntity.ok(reviewService.addReview(new ReviewOnReviewDTO(userDetail.id(), reviewOnReviewBodyDTO.reviewId(), reviewOnReviewBodyDTO.content(), reviewOnReviewBodyDTO.tagList())));
    }

  @DeleteMapping(Routes.Review.ROOT + "/{reviewId}")
  @RequireUser
  public ResponseEntity<Void> removeReview(@PathVariable(name = "reviewId") long reviewId) {
    var user = SecurityContext.authentication().orElseThrow();
    LOGGER.info("perform delete on " + Routes.Review.ROOT);
    reviewService.remove(new ReviewRemoveDTO(user.id(), reviewId));

    return ResponseEntity.ok().build();
  }

    @PostMapping(Routes.Review.ROOT + "/{reviewId}/vote")
    @RequireUser
    public ResponseEntity<Void> voteReview(@PathVariable(name = "reviewId") long reviewId, @Valid @RequestBody VoteBodyDTO voteBodyDTO) {
        var user = SecurityContext.checkAuthentication();

    reviewService.vote(user.id(), reviewId, voteBodyDTO.up());

    return ResponseEntity.ok().build();
  }

  @DeleteMapping(Routes.Review.ROOT + "/{reviewId}/cancelVote")
  @RequireUser
  public ResponseEntity<Void> cancelVoteReview(@PathVariable(name = "reviewId") long reviewId) {
    var user = SecurityContext.checkAuthentication();

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
