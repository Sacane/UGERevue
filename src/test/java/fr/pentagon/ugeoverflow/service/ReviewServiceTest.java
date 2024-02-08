package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionCreateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionReviewCreateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewOnReviewDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserRegisterDTO;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.repository.QuestionRepository;
import fr.pentagon.ugeoverflow.repository.ReviewRepository;
import fr.pentagon.ugeoverflow.repository.ReviewVoteRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.List;

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
    @Autowired
    private ReviewVoteRepository reviewVoteRepository;

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

    @Test
    @DisplayName("Get reviews from review")
    void reviewsFromReview() {
        var quentinResponse = userService.register(new UserRegisterDTO("qtdrake", "qt@email.com", "qtellier", "123"));
        assertSame(quentinResponse.getStatusCode(), HttpStatus.OK);
        var quentin = quentinResponse.getBody();
        var questionResponse = questionService.create(new QuestionCreateDTO(quentin.id(), "TITLE", "DESCRIPTION", new byte[0], null));
        assertSame(questionResponse.getStatusCode(), HttpStatus.OK);
        var questionId = questionResponse.getBody();

        var reviewParentResponse = questionService.addReview(new QuestionReviewCreateDTO(quentin.id(), questionId, "CONTENT", null, null));
        assertSame(reviewParentResponse.getStatusCode(), HttpStatus.OK);
        assertNotNull(reviewParentResponse.getBody());
        var reviewParentId = reviewParentResponse.getBody();

        for(var i = 0; i < 10; i++) {
            var reviewResponse = reviewService.addReview(new ReviewOnReviewDTO(quentin.id(), reviewParentId, "CONTENT CHILD REVIEW:" + i));
            assertSame(reviewResponse.getStatusCode(), HttpStatus.OK);
            assertNotNull(reviewResponse.getBody());
        }

        assertEquals(10, reviewService.getReviews(reviewParentId).size());
    }

    private AuthorsIdOnVote setupVote() {
        var user = new User("qtdrake", "qtdrake", "qtellier", "qt@email.com");
        var user2 = new User("sacane", "sacane", "qtellier", "qt@email.com");
        var user3 = new User("verestah", "verestah", "qtellier", "qt@email.com");
        var user4 = new User("qtdrake", "qtdrake", "qtellier", "qt@email.com");

        var userSaved = userRepository.save(user);
        var userSaved2 =userRepository.save(user2);
        var userSaved3 =userRepository.save(user3);
        var userSaved4 =userRepository.save(user4);
        var authorIds = List.of(
                userSaved2.getId(),
                userSaved.getId(),
                userSaved3.getId(),
                userSaved4.getId()
        );
        var question = new QuestionCreateDTO(userSaved.getId(), "I DONT KNOW", "IDJZAODIJZD", new byte[0], new byte[0]);
        var response = questionService.create(question);
        var responseReview = questionService.addReview(new QuestionReviewCreateDTO(user.getId(), response.getBody(), "fzaerzearza", 0, 3));
        assertSame(HttpStatus.OK, response.getStatusCode());
        var id = responseReview.getBody();
        assertNotNull(id);
        var savedReview = reviewRepository.findById(id);
        assertTrue(savedReview.isPresent());

        reviewService.vote(userSaved2.getId(), id, true);
        reviewService.vote(userSaved3.getId(), id, true);
        reviewService.vote(userSaved4.getId(), id, false);
        return new AuthorsIdOnVote(authorIds, id);
    }

    private record AuthorsIdOnVote(List<Long> authorIds, long reviewId) {}

    @Test
    @DisplayName("votes on Review")
    void upvoteOnQuestionTest() {
        var id = setupVote();
        assertEquals(2, reviewVoteRepository.findUpvoteNumberByReviewId(id.reviewId));
        assertEquals(1, reviewVoteRepository.findDownvoteNumberByReviewId(id.reviewId));
    }
    @Test
    @DisplayName("cancel vote on Review")
    void cancelVoteOnReviewTest() {
        var id = setupVote();

        reviewService.cancelVote(id.authorIds.get(0), id.reviewId);
        reviewService.cancelVote(id.authorIds.get(3), id.reviewId);

        assertEquals(1, reviewVoteRepository.findUpvoteNumberByReviewId(id.reviewId));
        assertEquals(0, reviewVoteRepository.findDownvoteNumberByReviewId(id.reviewId));
    }

}
