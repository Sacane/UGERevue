package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionCreateDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.repository.QuestionRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class QuestionServiceTest {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Create a question with non-existent user")
    void createWithNonExistentUser() {
        assertThrows(HttpException.class, () -> questionService.create(new QuestionCreateDTO(50, "TITLE", "DESCRIPTION", new byte[0], null)));
    }

    @Test
    @DisplayName("Create a question")
    void create() {
        var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123"));

        var responseQuestion = questionService.create(new QuestionCreateDTO(quentin.getId(), "TITLE", "DESCRIPTION", new byte[0], null));
        assertSame(responseQuestion.getStatusCode(), HttpStatus.OK);

        var questionId = responseQuestion.getBody();
        assertEquals(1, questionId);

        var user = userRepository.findByIdWithQuestions(quentin.getId());
        assertTrue(user.isPresent() && user.get().getQuestions().size() == 1);
    }
}
