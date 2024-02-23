package fr.pentagon.ugeoverflow.controllers;

import fr.pentagon.ugeoverflow.config.authorization.RequireUser;
import fr.pentagon.ugeoverflow.config.security.SecurityContext;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.*;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.DetailReviewResponseDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewQuestionResponseDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewResponseChildrenDTO;
import fr.pentagon.ugeoverflow.service.ReviewService;
import fr.pentagon.ugeoverflow.utils.Routes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping(Routes.Review.ROOT + "/{reviewId}")
    public ResponseEntity<DetailReviewResponseDTO> findDetailsReview(@PathVariable long reviewId) {
        return ResponseEntity.ok(reviewService.findDetailFromReviewId(reviewId));
    }

    @GetMapping(Routes.Review.ROOT + Routes.Question.IDENT + "/{questionId}")
    public ResponseEntity<List<ReviewResponseChildrenDTO>> findAllReviews(@PathVariable long questionId) {
        return ResponseEntity.ok(reviewService.findReviewsByQuestionId(questionId));
    }

    @PostMapping(Routes.Review.ROOT)
    public ResponseEntity<ReviewQuestionResponseDTO> addReview(@RequestBody ReviewOnReviewBodyDTO reviewOnReviewBodyDTO) {
        var userDetail = SecurityContext.checkAuthentication();

        return ResponseEntity.ok(reviewService.addReview(new ReviewOnReviewDTO(userDetail.id(), reviewOnReviewBodyDTO.reviewId(), reviewOnReviewBodyDTO.content())));
    }

    @DeleteMapping(Routes.Review.ROOT + "/{reviewId}")
    @RequireUser
    public ResponseEntity<Void> removeReview(@PathVariable long reviewId) {
        var user = SecurityContext.checkAuthentication();

        reviewService.remove(new ReviewRemoveDTO(user.id(), reviewId));

        return ResponseEntity.ok().build();
    }
}
