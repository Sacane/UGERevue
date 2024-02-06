package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewCreateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserRegisterDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ReviewServiceTest {
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserService userService;

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
        var quentin = userService.register(new UserRegisterDTO("qtdrake", "qt@email.com", "qtellier", "123"));
        System.out.println(quentin);
    }
}
