package fr.pentagon.ugeoverflow.controllers;

import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewResponseChildrenDTO;
import fr.pentagon.ugeoverflow.service.ReviewService;
import fr.pentagon.ugeoverflow.utils.Routes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
}
