package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.DatasourceTestConfig;
import fr.pentagon.ugeoverflow.config.authorization.Role;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.*;
import fr.pentagon.ugeoverflow.model.Question;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.repository.QuestionRepository;
import fr.pentagon.ugeoverflow.repository.ReviewRepository;
import fr.pentagon.ugeoverflow.repository.ReviewVoteRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(DatasourceTestConfig.class)
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

  @AfterEach
  void purge() {
    reviewVoteRepository.deleteAll();
    reviewRepository.deleteAll();
    questionRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("Review on review")
  void reviewOnReview() {
    var quentin = userService.register(new UserRegisterDTO("qtdrake", "qt@email.com", "qtellier", "123"));
    assertNotNull(quentin);
    var questionId = questionService.create(new NewQuestionDTO("TITLE", "DESCRIPTION", new byte[0], null), quentin.id());

    var reviewParentResponse = questionService.addReview(new QuestionReviewCreateDTO(quentin.id(), questionId, "CONTENT", null, null));

    var reviewResponse = reviewService.addReview(new ReviewOnReviewDTO(quentin.id(), reviewParentResponse.id(), "CONTENT CHILD REVIEW"));

    var questionOptional = questionRepository.findByIdWithReviews(questionId);
    assertTrue(questionOptional.isPresent());
    var question = questionOptional.get();
    assertEquals(1, question.getReviews().size());

    var reviewParentOptional = reviewRepository.findByIdWithReviews(reviewParentResponse.id());
    assertTrue(reviewParentOptional.isPresent());
    var reviewParent = reviewParentOptional.get();
    assertEquals(1, reviewParent.getReviews().size());
    assertNull(reviewParent.getParentReview());

    var reviewOptional = reviewRepository.findByIdWithReviews(reviewResponse.id());
    assertTrue(reviewOptional.isPresent());
    var review = reviewOptional.get();
    assertEquals(0, review.getReviews().size());
    assertNotNull(review.getParentReview());
    assertEquals(reviewParent.getId(), review.getParentReview().getId());
  }

  @Test
  @DisplayName("Get reviews from review")
  void reviewsFromReview() {
    var quentin = userService.register(new UserRegisterDTO("qtdrake", "qt@email.com", "qtellier", "123"));
    assertNotNull(quentin);
    var questionId = questionService.create(new NewQuestionDTO("TITLE", "DESCRIPTION", new byte[0], null), quentin.id());

    var reviewParentResponse = questionService.addReview(new QuestionReviewCreateDTO(quentin.id(), questionId, "CONTENT", null, null));

    for (var i = 0; i < 10; i++) {
      reviewService.addReview(new ReviewOnReviewDTO(quentin.id(), reviewParentResponse.id(), "CONTENT CHILD REVIEW:" + i));
    }

    assertEquals(10, reviewService.getReviews(reviewParentResponse.id()).size());
  }

  private AuthorsIdOnVote setupVote() {
    var user = new User("qtdrake", "qtdrake", "qtellier", "qt@email.com", Role.USER);
    var user2 = new User("sacane", "sacane", "qtellier", "qt@email.com", Role.USER);
    var user3 = new User("verestah", "verestah", "qtellier", "qt@email.com", Role.USER);
    var user4 = new User("qtdrake", "qtdrake", "qtellier", "qt@email.com", Role.USER);

    var userSaved = userRepository.save(user);
    var userSaved2 = userRepository.save(user2);
    var userSaved3 = userRepository.save(user3);
    var userSaved4 = userRepository.save(user4);
    var authorIds = List.of(
        userSaved2.getId(),
        userSaved.getId(),
        userSaved3.getId(),
        userSaved4.getId()
    );
    var question = new NewQuestionDTO( "I DONT KNOW", "IDJZAODIJZD", new byte[0], new byte[0]);
    var questionId = questionService.create(question, userSaved.getId());
    var reviewResponse = questionService.addReview(new QuestionReviewCreateDTO(user.getId(), questionId, "fzaerzearza", 0, 3));
    var savedReview = reviewRepository.findById(reviewResponse.id());
    assertTrue(savedReview.isPresent());

    reviewService.vote(userSaved2.getId(), reviewResponse.id(), true);
    reviewService.vote(userSaved3.getId(), reviewResponse.id(), true);
    reviewService.vote(userSaved4.getId(), reviewResponse.id(), false);
    return new AuthorsIdOnVote(authorIds, reviewResponse.id());
  }

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

  @Test
  @DisplayName("Remove a review from a question")
  void removeQuestionReview() {
    var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123", Role.USER));
    var question = questionRepository.save(new Question("TITLE", "DESCRIPTION", new byte[0], null, null, true, new Date()));

    var reviewParentResponse = questionService.addReview(new QuestionReviewCreateDTO(quentin.getId(), question.getId(), "CONTENT", 1, 2));
    assertEquals(1, reviewRepository.findAll().size());

    reviewService.remove(new ReviewRemoveDTO(quentin.getId(), reviewParentResponse.id()));

    assertEquals(0, reviewRepository.findAll().size());
    var userOptional = userRepository.findByIdWithReviews(quentin.getId());
    assertTrue(userOptional.isPresent());
    var user = userOptional.get();
    assertEquals(0, user.getReviews().size());
    var questionOptional = questionRepository.findByIdWithReviews(question.getId());
    assertTrue(questionOptional.isPresent());
    question = questionOptional.get();
    assertEquals(0, question.getReviews().size());
  }

  @Test
  @DisplayName("Remove a review from a question with children reviews")
  void removeQuestionReviewWithChildren() {
    var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123", Role.USER));
    var question = questionRepository.save(new Question("TITLE", "DESCRIPTION", new byte[0], null, null, true, new Date()));

    var reviewParentResponse = questionService.addReview(new QuestionReviewCreateDTO(quentin.getId(), question.getId(), "CONTENT", 1, 2));
    assertEquals(1, reviewRepository.findAll().size());

    var reviewResponse = reviewService.addReview(new ReviewOnReviewDTO(quentin.getId(), reviewParentResponse.id(), "CONTENT"));

    assertEquals(2, reviewRepository.findAll().size());

    reviewService.addReview(new ReviewOnReviewDTO(quentin.getId(), reviewResponse.id(), "CONTENT"));

    assertEquals(3, reviewRepository.findAll().size());

    reviewService.remove(new ReviewRemoveDTO(quentin.getId(), reviewParentResponse.id()));

    assertEquals(0, reviewRepository.findAll().size());
    var userOptional = userRepository.findByIdWithReviews(quentin.getId());
    assertTrue(userOptional.isPresent());
    var user = userOptional.get();
    assertEquals(0, user.getReviews().size());
    var questionOptional = questionRepository.findByIdWithReviews(question.getId());
    assertTrue(questionOptional.isPresent());
    question = questionOptional.get();
    assertEquals(0, question.getReviews().size());
  }

  @Test
  @DisplayName("Remove review on review with no children")
  void removeReviewOnReviewWithNoChildren() {
    var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123", Role.USER));
    var question = questionRepository.save(new Question("TITLE", "DESCRIPTION", new byte[0], null, null, true, new Date()));

    var reviewParentResponse = questionService.addReview(new QuestionReviewCreateDTO(quentin.getId(), question.getId(), "CONTENT", 1, 2));
    assertEquals(1, reviewRepository.findAll().size());

    var reviewResponse = reviewService.addReview(new ReviewOnReviewDTO(quentin.getId(), reviewParentResponse.id(), "CONTENT"));

    assertEquals(2, reviewRepository.findAll().size());

    var rResponse = reviewService.addReview(new ReviewOnReviewDTO(quentin.getId(), reviewResponse.id(), "CONTENT"));

    assertEquals(3, reviewRepository.findAll().size());

    reviewService.remove(new ReviewRemoveDTO(quentin.getId(), rResponse.id()));

    assertEquals(2, reviewRepository.findAll().size());
    var userOptional = userRepository.findByIdWithReviews(quentin.getId());
    assertTrue(userOptional.isPresent());
    var user = userOptional.get();
    assertEquals(2, user.getReviews().size());
    var questionOptional = questionRepository.findByIdWithReviews(question.getId());
    assertTrue(questionOptional.isPresent());
    question = questionOptional.get();
    assertEquals(1, question.getReviews().size());
  }

  @Test
  @DisplayName("Remove review on review with children")
  void removeReviewOnReviewWithChildren() {
    var quentin = userRepository.save(new User("qtdrake", "qt@email.com", "qtellier", "123", Role.USER));
    var question = questionRepository.save(new Question("TITLE", "DESCRIPTION", new byte[0], null, null, true, new Date()));

    var reviewParentResponse = questionService.addReview(new QuestionReviewCreateDTO(quentin.getId(), question.getId(), "CONTENT", 1, 2));
    assertEquals(1, reviewRepository.findAll().size());

    var reviewResponse = reviewService.addReview(new ReviewOnReviewDTO(quentin.getId(), reviewParentResponse.id(), "CONTENT"));

    assertEquals(2, reviewRepository.findAll().size());

    reviewService.addReview(new ReviewOnReviewDTO(quentin.getId(), reviewResponse.id(), "CONTENT"));

    assertEquals(3, reviewRepository.findAll().size());

    reviewService.remove(new ReviewRemoveDTO(quentin.getId(), reviewResponse.id()));

    assertEquals(1, reviewRepository.findAll().size());
    var userOptional = userRepository.findByIdWithReviews(quentin.getId());
    assertTrue(userOptional.isPresent());
    var user = userOptional.get();
    assertEquals(1, user.getReviews().size());
    var questionOptional = questionRepository.findByIdWithReviews(question.getId());
    assertTrue(questionOptional.isPresent());
    question = questionOptional.get();
    assertEquals(1, question.getReviews().size());
  }

  private record AuthorsIdOnVote(List<Long> authorIds, long reviewId) {
  }
}
