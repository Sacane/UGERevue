package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.CommentReviewDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewCreateDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.model.Review;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.repository.ReviewRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReviewServiceTest {
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Create a review with non-existent user")
    void createWithNonExistentUser() {
        assertThrows(HttpException.class, () -> {
            reviewService.create(new ReviewCreateDTO(50, "TITLE", new byte[0], null));
        });
    }

    @Test
    @DisplayName("Create a review")
    void create() {
        var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123"));

        var responseReview = reviewService.create(new ReviewCreateDTO(quentin.getId(), "TITLE", new byte[0], null));
        assertSame(responseReview.getStatusCode(), HttpStatus.OK);

        var review = responseReview.getBody();
        assertEquals("TITLE", review.title());
        assertEquals(quentin.getId(), review.authorID());

        var user = userRepository.findByIdWithReviews(review.authorID());
        assertTrue(user.isPresent() && user.get().getReviews().size() == 1);
    }

    @Test
    @DisplayName("Comment a non-existent review")
    void commentNonExistentReview() {
        var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123"));

        assertThrows(HttpException.class, () -> reviewService.comment(new CommentReviewDTO("CONTENT", quentin.getId(), 0)));
    }

    @Test
    @DisplayName("Comment with a non-existent user")
    void commentNonExistentUser() {
        var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123"));
        var review = reviewRepository.save(new Review("TITLE", new byte[0], new byte[0], true, "OPEN", quentin, new Date()));

        assertThrows(HttpException.class, () -> reviewService.comment(new CommentReviewDTO("CONTENT", 0, review.getId())));
    }

    @Test
    @DisplayName("Comment a review")
    void commentReview() {
        var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123"));
        var review = reviewRepository.save(new Review("TITLE", new byte[0], new byte[0], true, "OPEN", quentin, new Date()));

        reviewService.comment(new CommentReviewDTO("CONTENT", quentin.getId(), review.getId()));

        var reviewAfterComment = reviewRepository.findByIdWithComments(review.getId());
        assertTrue(reviewAfterComment.isPresent() && reviewAfterComment.get().getComments().size() == 1);
    }

    @Test
    @DisplayName("Comment a lot the same review")
    void commentALotReview() {
        var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123"));
        var review = reviewRepository.save(new Review("TITLE", new byte[0], new byte[0], true, "OPEN", quentin, new Date()));

        for(var i = 0; i < 10; i++) {
            reviewService.comment(new CommentReviewDTO("CONTENT" + i, quentin.getId(), review.getId()));
        }

        var reviewAfterComment = reviewRepository.findByIdWithComments(review.getId());
        assertTrue(reviewAfterComment.isPresent() && reviewAfterComment.get().getComments().size() == 10);
    }
}
