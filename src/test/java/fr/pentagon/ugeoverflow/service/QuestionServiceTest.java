package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionCreateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionReviewCreateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserRegisterDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.model.Question;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.repository.QuestionRepository;
import fr.pentagon.ugeoverflow.repository.QuestionVoteRepository;
import fr.pentagon.ugeoverflow.repository.ReviewRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class QuestionServiceTest {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private QuestionVoteRepository questionVoteRepository;

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

    @Test
    @DisplayName("Review a non-existent question")
    void reviewNonExistentQuestion() {
        var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123"));
        var question = questionRepository.save(new Question("TITLE", "DESCRIPTION", new byte[0], null, null, true, new Date()));

        assertThrows(HttpException.class, () -> questionService.addReview(new QuestionReviewCreateDTO(quentin.getId(), question.getId() + 1, "CONTENT", 1, 2)));
    }

    @Test
    @DisplayName("Review a question with non-existent user")
    void reviewNonExistentUser() {
        var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123"));
        var question = questionRepository.save(new Question("TITLE", "DESCRIPTION", new byte[0], null, null, true, new Date()));

        assertThrows(HttpException.class, () -> questionService.addReview(new QuestionReviewCreateDTO(quentin.getId() + 1, question.getId(), "CONTENT", 1, 2)));
    }

    @Test
    @DisplayName("Review a question")
    void reviewQuestion() {
        var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123"));
        var question = questionRepository.save(new Question("TITLE", "DESCRIPTION", new byte[0], null, null, true, new Date()));

        var responseReview = questionService.addReview(new QuestionReviewCreateDTO(quentin.getId(), question.getId(), "CONTENT", 1, 2));
        assertSame(responseReview.getStatusCode(), HttpStatus.OK);

        var reviewId = responseReview.getBody();
        assertEquals(1, reviewId);

        var userOptional = userRepository.findByIdWithReviews(quentin.getId());
        assertTrue(userOptional.isPresent());
        var user = userOptional.get();
        assertEquals(1, user.getReviews().size());
        var questionOptional = questionRepository.findByIdWithReviews(question.getId());
        assertTrue(questionOptional.isPresent());
        var q = questionOptional.get();
        assertEquals(1, q.getReviews().size());
    }

    @Test
    @DisplayName("Review a lot the same question")
    void reviewALotQuestion() {
        var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123"));
        var question = questionRepository.save(new Question("TITLE", "DESCRIPTION", new byte[0], null, null, true, new Date()));

        for(var i = 0; i < 10; i++) {
            var responseReview = questionService.addReview(new QuestionReviewCreateDTO(quentin.getId(), question.getId(), "CONTENT:" + i, 1, 2));
            assertSame(responseReview.getStatusCode(), HttpStatus.OK);
        }

        var userOptional = userRepository.findByIdWithReviews(quentin.getId());
        assertTrue(userOptional.isPresent());
        var user = userOptional.get();
        assertEquals(10, user.getReviews().size());
        var questionOptional = questionRepository.findByIdWithReviews(question.getId());
        assertTrue(questionOptional.isPresent());
        var q = questionOptional.get();
        assertEquals(10, q.getReviews().size());
    }

    @Test
    @DisplayName("Upvote on question")
    void upvoteOnQuestionTest() {
        var user = new User("qtdrake", "qtdrake", "qtellier", "qt@email.com");
        var user2 = new User("sacane", "sacane", "qtellier", "qt@email.com");
        var user3 = new User("verestah", "verestah", "qtellier", "qt@email.com");
        var user4 = new User("qtdrake", "qtdrake", "qtellier", "qt@email.com");

        var userSaved = userRepository.save(user);
        var userSaved2 =userRepository.save(user2);
        var userSaved3 =userRepository.save(user3);
        var userSaved4 =userRepository.save(user4);
        var question = new QuestionCreateDTO(userSaved.getId(), "I DONT KNOW", "IDJZAODIJZD", new byte[0], new byte[0]);
        var response = questionService.create(question);
        assertSame(HttpStatus.OK, response.getStatusCode());
        var id = response.getBody();
        assertNotNull(id);
        var savedQuestion = questionRepository.findById(id);
        assertTrue(savedQuestion.isPresent());

        questionService.vote(userSaved2.getId(), id, true);
        questionService.vote(userSaved3.getId(), id, true);
        questionService.vote(userSaved4.getId(), id, false);

        assertEquals(2, questionVoteRepository.findUpvoteNumberByQuestionId(id));
        assertEquals(1, questionVoteRepository.findDownvoteNumberByQuestionId(id));

    }

    @Test
    @DisplayName("Get reviews from question")
    void getReviewsFromQuestion() {
        var quentinResponse = userService.register(new UserRegisterDTO("qtdrake", "qt@email.com", "qtellier", "123"));
        assertSame(quentinResponse.getStatusCode(), HttpStatus.OK);
        var quentin = quentinResponse.getBody();
        var questionResponse = questionService.create(new QuestionCreateDTO(quentin.id(), "TITLE", "DESCRIPTION", "LINE1\nLINE2\nLINE3\n".getBytes(StandardCharsets.UTF_8), null));
        assertSame(questionResponse.getStatusCode(), HttpStatus.OK);
        var questionId = questionResponse.getBody();

        for(var i = 0; i < 3; i++) {
            questionService.addReview(new QuestionReviewCreateDTO(quentin.id(), questionId, "CONTENT:" + i, i + 1, i + 1));
        }

        var reviewsResponse = questionService.getReviews(questionId);
        assertSame(reviewsResponse.getStatusCode(), HttpStatus.OK);
        System.out.println(reviewsResponse.getBody());
    }
}
