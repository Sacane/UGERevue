package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionCreateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionReviewCreateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewOnReviewDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserRegisterDTO;
import fr.pentagon.ugeoverflow.repository.QuestionRepository;
import fr.pentagon.ugeoverflow.repository.ReviewRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReviewServiceTest {
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private UserService userService;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Review on review")
    void reviewOnReview() {
        var quentinResponse = userService.register(new UserRegisterDTO("qtdrake", "qt@email.com", "qtellier", "123"));
        assertSame(quentinResponse.getStatusCode(), HttpStatus.OK);
        var quentin = quentinResponse.getBody();
        var questionResponse = questionService.create(new QuestionCreateDTO(quentin.id(), "TITLE", "DESCRIPTION", new byte[0], null));
        assertSame(questionResponse.getStatusCode(), HttpStatus.OK);
        var questionId = questionResponse.getBody();

        var reviewParentResponse = questionService.addReview(new QuestionReviewCreateDTO(quentin.id(), questionId, "CONTENT", null, null));
        assertSame(reviewParentResponse.getStatusCode(), HttpStatus.OK);
        var reviewParentId = reviewParentResponse.getBody();

        var reviewResponse = reviewService.addReview(new ReviewOnReviewDTO(quentin.id(), reviewParentId, "CONTENT CHILD REVIEW"));
        assertSame(reviewResponse.getStatusCode(), HttpStatus.OK);

        var questionOptional = questionRepository.findByIdWithReviews(questionId);
        assertTrue(questionOptional.isPresent());
        var question = questionOptional.get();
        assertEquals(1, question.getReviews().size());

        var reviewParentOptional = reviewRepository.findByIdWithReviews(reviewParentId);
        assertTrue(reviewParentOptional.isPresent());
        var reviewParent = reviewParentOptional.get();
        assertEquals(1, reviewParent.getReviews().size());
        assertNull(reviewParent.getParentReview());

        assertNotNull(reviewResponse.getBody());
        var reviewOptional = reviewRepository.findByIdWithReviews(reviewResponse.getBody());
        assertTrue(reviewOptional.isPresent());
        var review = reviewOptional.get();
        assertEquals(0, review.getReviews().size());
        assertNotNull(review.getParentReview());
        assertEquals(reviewParent.getId(), review.getParentReview().getId());
    }
}
