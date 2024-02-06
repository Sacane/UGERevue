package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewCreateDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.model.User;
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
}
