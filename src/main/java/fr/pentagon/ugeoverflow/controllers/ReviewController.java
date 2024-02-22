package fr.pentagon.ugeoverflow.controllers;

import fr.pentagon.ugeoverflow.config.authorization.RequireUser;
import fr.pentagon.ugeoverflow.config.security.SecurityContext;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionRemoveDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewRemoveDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewResponseChildrenDTO;
import fr.pentagon.ugeoverflow.service.ReviewService;
import fr.pentagon.ugeoverflow.utils.Routes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReviewController {
    private final ReviewService reviewService;
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping(Routes.Review.ROOT + Routes.Question.IDENT + "/{questionId}")
    public ResponseEntity<List<ReviewResponseChildrenDTO>> findAllReviews(
            @PathVariable long questionId
    ) {
        return ResponseEntity.ok(reviewService.findReviewsByQuestionId(questionId));
    }

    @DeleteMapping(Routes.Review.ROOT + "/{reviewId}")
    @RequireUser
    public ResponseEntity<Void> removeReview(@PathVariable long reviewId) {
        var user = SecurityContext.checkAuthentication();

        reviewService.remove(new ReviewRemoveDTO(user.id(), reviewId));

        return ResponseEntity.ok().build();
    }
}
