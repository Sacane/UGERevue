package fr.pentagon.ugeoverflow.service;

import fr.pentagon.revevue.common.exception.HttpException;
import fr.pentagon.ugeoverflow.DatasourceTestConfig;
import fr.pentagon.ugeoverflow.config.authorization.Role;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.*;
import fr.pentagon.ugeoverflow.model.Question;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.repository.QuestionRepository;
import fr.pentagon.ugeoverflow.repository.QuestionVoteRepository;
import fr.pentagon.ugeoverflow.repository.ReviewRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import fr.pentagon.ugeoverflow.testutils.UserTestProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(DatasourceTestConfig.class)
public class QuestionServiceTest {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private ReviewService reviewService;
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
    @Autowired
    private UserTestProvider userTestProvider;

    @AfterEach
    void purge() {
        reviewRepository.deleteAll();
        questionVoteRepository.deleteAll();
        questionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Create a question with non-existent user")
    void createWithNonExistentUser() {
        assertThrows(HttpException.class, () -> questionService.create(new NewQuestionDTO("TITLE", "DESCRIPTION", new byte[0], null, "test.java", "test.java"), 9999));
    }

    @Test
    @DisplayName("Create a question")
    void create() {
        var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123", Role.USER));

        byte[] file = "FILE CONTENT".getBytes(StandardCharsets.UTF_8);
        var questionId = questionService.create(new NewQuestionDTO("TITLE", "DESCRIPTION", file, null, "test.java", "test.java"), quentin.getId());

        assertEquals(1, questionRepository.findAll().size());

        var user = userRepository.findByIdWithQuestions(quentin.getId());
        assertTrue(user.isPresent() && user.get().getQuestions().size() == 1);
        var questionOptional = questionRepository.findById(questionId);
        assertTrue(questionOptional.isPresent());
        var question = questionOptional.get();
        assertArrayEquals(file, question.getFile());
    }

    @Test
    @DisplayName("Update a question")
    void update() {
        var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123", Role.USER));

        var questionId = questionService.create(new NewQuestionDTO("TITLE", "DESCRIPTION", new byte[0], null, "test.java", "test.java"), quentin.getId());
        assertEquals(1, questionRepository.findAll().size());
        var questionOptional = questionRepository.findById(questionId);
        assertTrue(questionOptional.isPresent());
        var question = questionOptional.get();
        assertEquals("TITLE", question.getTitle());
        assertEquals("DESCRIPTION", question.getDescription());

        questionService.update(quentin.getId(), questionId, new QuestionUpdateDTO("NEW DESCRIPTION", new byte[0], ""));

        questionOptional = questionRepository.findById(questionId);
        assertTrue(questionOptional.isPresent());
        question = questionOptional.get();
        assertEquals("NEW DESCRIPTION", question.getDescription());
    }

    @Test
    @DisplayName("Update a lot the same question")
    void updateALot() {
        var threads = new ArrayList<Thread>();
        var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123", Role.USER));

        var questionId = questionService.create(new NewQuestionDTO("TITLE", "DESCRIPTION", new byte[0], null, "test.java", "test.java"), quentin.getId());
        assertEquals(1, questionRepository.findAll().size());
        var questionOptional = questionRepository.findById(questionId);
        assertTrue(questionOptional.isPresent());
        var question = questionOptional.get();
        assertEquals("TITLE", question.getTitle());
        assertEquals("DESCRIPTION", question.getDescription());

        byte[] testFile = "TEST CONTENT".getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < 10; i++) {
            final var number = i;
            var thread = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    questionService.update(quentin.getId(), questionId, new QuestionUpdateDTO("NEW DESCRIPTION", testFile, "test"+ number + ".java"));
                }
            });
            threads.add(thread);
            thread.start();
        }

        for (var thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        questionOptional = questionRepository.findById(questionId);
        assertTrue(questionOptional.isPresent());
        question = questionOptional.get();
        assertEquals("NEW DESCRIPTION", question.getDescription());
        assertArrayEquals(testFile, question.getTestFile());
    }

    @Test
    @DisplayName("Review a non-existent question")
    void reviewNonExistentQuestion() {
        var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123", Role.USER));
        var question = questionRepository.save(new Question("TITLE", "DESCRIPTION", new byte[0], null, null, true, new Date()));

        assertThrows(HttpException.class, () -> questionService.addReview(new QuestionReviewCreateDTO(quentin.getId(), question.getId() + 1, "CONTENT", 1, 2, List.of())));
    }

    @Test
    @DisplayName("Review a question with non-existent user")
    void reviewNonExistentUser() {
        var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123", Role.USER));
        var question = questionRepository.save(new Question("TITLE", "DESCRIPTION", new byte[0], null, null, true, new Date()));

        assertThrows(HttpException.class, () -> questionService.addReview(new QuestionReviewCreateDTO(quentin.getId() + 1, question.getId(), "CONTENT", 1, 2, List.of())));
    }

    @Test
    @DisplayName("Review a question")
    void reviewQuestion() {
        var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123", Role.USER));
        var question = questionRepository.save(new Question("TITLE", "DESCRIPTION", new byte[0], null, null, true, new Date()));

        var reviewResponse = questionService.addReview(new QuestionReviewCreateDTO(quentin.getId(), question.getId(), "CONTENT", null, null, List.of()));
        assertEquals(1, reviewRepository.findAll().size());

        var userOptional = userRepository.findByIdWithReviews(quentin.getId());
        assertTrue(userOptional.isPresent());
        var user = userOptional.get();
        assertEquals(1, user.getReviews().size());
        var questionOptional = questionRepository.findByIdWithReviews(question.getId());
        assertTrue(questionOptional.isPresent());
        var q = questionOptional.get();
        assertEquals(1, q.getReviews().size());
        var reviewOptional = reviewRepository.findByIdWithQuestion(reviewResponse.id());
        assertTrue(reviewOptional.isPresent());
        var review = reviewOptional.get();
        assertEquals(question.getId(), review.getQuestion().getId());
    }

    @Test
    @DisplayName("Review a lot the same question")
    void reviewALotQuestion() {
        var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123", Role.USER));
        var question = questionRepository.save(new Question("TITLE", "DESCRIPTION", new byte[0], null, null, true, new Date()));

        for (var i = 0; i < 10; i++) {
            questionService.addReview(new QuestionReviewCreateDTO(quentin.getId(), question.getId(), "CONTENT:" + i, null, null, List.of()));
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
        var user = new User("qtdrake", "qtdrake", "qtellier", "qt@email.com", Role.USER);
        var user2 = new User("sacane", "sacane", "qtellier", "qt@email.com", Role.USER);
        var user3 = new User("verestah", "verestah", "qtellier", "qt@email.com", Role.USER);
        var user4 = new User("qtdrake", "qtdrake", "qtellier", "qt@email.com", Role.USER);

        var userSaved = userRepository.save(user);
        var userSaved2 = userRepository.save(user2);
        var userSaved3 = userRepository.save(user3);
        var userSaved4 = userRepository.save(user4);
        var question = new NewQuestionDTO("I DONT KNOW", "IDJZAODIJZD", new byte[0], new byte[0], "test.java", "test.java");
        var id = questionService.create(question, userSaved.getId());
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
        assertNotNull(quentinResponse);
        var questionId = questionService.create(new NewQuestionDTO("TITLE", "DESCRIPTION", "LINE1\nLINE2\nLINE3\n".getBytes(StandardCharsets.UTF_8), null, "test.java", "test.java"), quentinResponse.id());

        for (var i = 0; i < 3; i++) {
            questionService.addReview(new QuestionReviewCreateDTO(quentinResponse.id(), questionId, "CONTENT:" + i, i + 1, i + 1, List.of()));
        }

        var reviews = reviewService.findReviewsByQuestionId(questionId);
        assertEquals(3, reviews.size());
    }

    @Test
    @DisplayName("Get reviews from question with children reviews")
    void getReviewsFromQuestionWithChildrenReviews() {
        var quentin = userService.register(new UserRegisterDTO("qtdrake", "qt@email.com", "qtellier", "123"));
        assertNotNull(quentin);
        var questionId = questionService.create(new NewQuestionDTO("TITLE", "DESCRIPTION", "LINE1\nLINE2\nLINE3\n".getBytes(StandardCharsets.UTF_8), null, "main.java", "test.java"), quentin.id());

        for (var i = 0; i < 3; i++) {
            var reviewResponse = questionService.addReview(new QuestionReviewCreateDTO(quentin.id(), questionId, "CONTENT:" + i, i + 1, i + 1, List.of()));

            reviewService.addReview(new ReviewOnReviewDTO(quentin.id(), reviewResponse.id(), "SUPER CONTENT", List.of()));
        }

        var reviews = reviewService.findReviewsByQuestionId(questionId);
        assertEquals(3, reviews.size());

        for (var review : reviews) {
            assertEquals(1, review.reviews().size());
        }
    }

    @Test
    @DisplayName("Remove non-existent question")
    void removeNonExistentQuestion() {
        var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123", Role.USER));
        assertThrows(HttpException.class, () -> questionService.remove(new QuestionRemoveDTO(quentin.getId(), 1)));
    }

    @Test
    @DisplayName("Remove a question with non-existent user")
    void removeNonExistentUser() {
        var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123", Role.USER));
        var questionId = questionService.create(new NewQuestionDTO("TITLE", "DESCRIPTION", new byte[0], null, "test.java", "test.java"), quentin.getId());

        assertThrows(HttpException.class, () -> questionService.remove(new QuestionRemoveDTO(50, questionId)));
    }

    @Test
    @DisplayName("Remove with unauthorized user")
    void removeUnauthorizedUser() {
        var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123", Role.USER));
        var quentin2 = userRepository.save(new User("qtdrake2", "qt@email.com", "qtellier", "123", Role.USER));
        var questionId = questionService.create(new NewQuestionDTO("TITLE", "DESCRIPTION", new byte[0], new byte[0], "test.java", "test.java"), quentin.getId());

        assertThrows(HttpException.class, () -> questionService.remove(new QuestionRemoveDTO(quentin2.getId(), questionId)));
    }

    @Test
    @DisplayName("Remove a question")
    void remove() {
        var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123", Role.USER));
        var questionId = questionService.create(new NewQuestionDTO("TITLE", "DESCRIPTION", new byte[0], new byte[0], "test.java", "test.java"), quentin.getId());
        assertEquals(1, questionRepository.findAll().size());

        for (var i = 0; i < 3; i++) {
            questionService.addReview(new QuestionReviewCreateDTO(quentin.getId(), questionId, "CONTENT:" + i, null, null, List.of()));
        }

        var user = userRepository.findByIdWithQuestions(quentin.getId());
        assertTrue(user.isPresent() && user.get().getQuestions().size() == 1);

        var reviews = reviewRepository.findAll();
        assertEquals(3, reviews.size());

        questionService.remove(new QuestionRemoveDTO(quentin.getId(), questionId));

        var questions = questionRepository.findAll();
        assertEquals(0, questions.size());

        reviews = reviewRepository.findAll();
        assertEquals(0, reviews.size());

        user = userRepository.findByIdWithQuestions(quentin.getId());
        assertTrue(user.isPresent() && user.get().getQuestions().isEmpty());
    }

    @Test
    @DisplayName("Remove a question with review on review")
    void removeWithReviewOnReview() {
        var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123", Role.USER));
        var questionId = questionService.create(new NewQuestionDTO("TITLE", "DESCRIPTION", new byte[0], new byte[0], "test.java", "test.java"), quentin.getId());
        assertEquals(1, questionRepository.findAll().size());

        var reviewResponse = questionService.addReview(new QuestionReviewCreateDTO(quentin.getId(), questionId, "CONTENT", null, null, List.of()));

        reviewService.addReview(new ReviewOnReviewDTO(quentin.getId(), reviewResponse.id(), "CONTENT", List.of()));

        assertEquals(2, reviewRepository.findAll().size());

        questionService.remove(new QuestionRemoveDTO(quentin.getId(), questionId));

        assertEquals(0, reviewRepository.findAll().size());

        var userOptional = userRepository.findByIdWithReviews(quentin.getId());
        assertTrue(userOptional.isPresent());
        var user = userOptional.get();
        assertEquals(0, user.getReviews().size());
    }

    @Test
    void findQuestionByIdTest() {
        var sacaneUser = userRepository.save(new User("Sacane", "sacane", "sacane", "sacane.email", Role.USER));
        var questionContentBytes = """
                public class test() {
                  private final String content;
                  private final String testContent;
                }
                """.getBytes(StandardCharsets.UTF_8);
        var questionId = questionService.create(
                new NewQuestionDTO(
                        "ma classe de test ne marche pas",
                        "ma classe de test ne marche plus",
                        questionContentBytes,
                        null,
                        "test.java",
                        "test.java"
                ),
                sacaneUser.getId()
        );
        var questionResponse = assertDoesNotThrow(() -> questionService.findById(questionId));
        assertEquals("""
                public class test() {
                  private final String content;
                  private final String testContent;
                }
                """, questionResponse.classContent());
    }

    @Test
    void getQuestionsFromFollowsWithOneFollow() {
        var quentin = userRepository.save(new User("quentin", "quentin", "quentin", "quentin", Role.USER));
        var quentin2 = userRepository.save(new User("quentin2", "quentin2", "quentin2", "quentin2", Role.USER));

        userService.follow(quentin.getId(), quentin2.getId());

        var followQuestion = questionService.create(new NewQuestionDTO("TITLE2", "DESCRIPTION2", new byte[0], null, "test.java", "test.java"), quentin2.getId());
        questionService.create(new NewQuestionDTO("TITLE", "DESCRIPTION", new byte[0], null, "test.java", "test.java"), quentin.getId());

        var questions = questionService.getQuestionsFromFollows(quentin.getId());

        assertEquals(questions.getFirst().id(), followQuestion);
    }

    @Test
    void getQuestionsFromFollowsWithManyFollow() {
        var quentin = userRepository.save(new User("quentin", "quentin", "quentin", "quentin", Role.USER));
        var lastUser = quentin;

        for (var i = 0; i < 1000; i++) {
            var user = userRepository.save(new User("quentin" + i, "quentin" + i, "quentin" + i, "quentin" + i, Role.USER));

            userService.follow(lastUser.getId(), user.getId());
            lastUser = user;

            if (i == 999) {
                questionService.create(new NewQuestionDTO("TITLE", "DESCRIPTION", new byte[0], null, "test.java", "test.java"), user.getId());
            }
        }

        var questions = questionService.getQuestionsFromFollows(quentin.getId());

        assertEquals(questions.getFirst().title(), "TITLE");
    }

    @Test
    @DisplayName("Check the representation of question with positive score")
    public void scoreQuestionTest1() throws Exception {
        userTestProvider.addSomeUsersIntoDbWithQuestionVote();
        var questions = questionService.getQuestions();
        assertFalse(questions.isEmpty());
        var questionTest = questions.getFirst();
        assertEquals(questionTest.nbVotes(), 3L);
    }

    @Test
    @DisplayName("Check the representation of question neutral score")
    public void scoreQuestionTest2() throws Exception {
        userTestProvider.addSomeUsersIntoDbWithQuestionVote();
        var questions = questionService.getQuestions();
        assertTrue(questions.size() >= 2);
        var questionTest = questions.get(1);
        assertEquals(questionTest.nbVotes(), 0L);
    }

    @Test
    @DisplayName("Check the representation of question negative score")
    public void scoreQuestionTest3() throws Exception {
        userTestProvider.addSomeUsersIntoDbWithQuestionVote();
        var questions = questionService.getQuestions();
        assertTrue(questions.size() >= 2);
        var questionTest = questions.get(2);
        assertEquals(questionTest.nbVotes(), -3L);
    }

    @Test
    @DisplayName("Check the representation of question score mixed")
    public void scoreQuestionTest4() throws Exception {
        userTestProvider.addSomeUsersIntoDbWithQuestionVote();
        var questions = questionService.getQuestions();
        assertTrue(questions.size() >= 3);
        var questionTest = questions.get(3);
        assertEquals(questionTest.nbVotes(), 1L);
    }



}
