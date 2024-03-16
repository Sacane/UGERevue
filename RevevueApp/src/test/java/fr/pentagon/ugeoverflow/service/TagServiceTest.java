package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.DatasourceTestConfig;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.NewQuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionReviewCreateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewRemoveDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserRegisterDTO;
import fr.pentagon.ugeoverflow.model.Review;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.repository.QuestionRepository;
import fr.pentagon.ugeoverflow.repository.ReviewRepository;
import fr.pentagon.ugeoverflow.repository.TagRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import fr.pentagon.ugeoverflow.testutils.UserTestProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(DatasourceTestConfig.class)
public class TagServiceTest {
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private UserTestProvider userTestProvider;
    @Autowired
    private ReviewService reviewService;

    @AfterEach
    void purge(){
        reviewRepository.deleteAll();
        questionRepository.deleteAll();
        userRepository.deleteAll();
        tagRepository.deleteAll();
    }

    @Test
    @DisplayName("Get review with one tag")
    void getTagFromReview(){
        //Init un user
        var mathis = userService.register(new UserRegisterDTO("verestah", "verestah@email.com", "verestah", "password"));
        assertNotNull(mathis);
        //Init une question
        var questionId = questionService.create(new NewQuestionDTO("TITLE", "DESCRIPTION", new byte[0], null, "test.java", null), mathis.id());
        //Init une review avec des tags
        var review = questionService.addReview(new QuestionReviewCreateDTO(mathis.id(), questionId, "CONTENT", null, null, List.of("Test")));
        var tagList = tagRepository.findAll();
        assertFalse(tagList.isEmpty());
        //Recup les tags de la review
        var reviews = reviewRepository.findByTagName("Test");
        assertFalse(reviews.isEmpty());
        var reviewFromBDD = reviews.toArray(Review[]::new)[0];
        assertEquals(reviewFromBDD.getId(), review.id());
        assertEquals(reviewFromBDD.getContent(), review.content());

        //Recupe les tags du User
        var users = userRepository.findByTagName("Test");
        assertFalse(users.isEmpty());
        var userFromBDD = users.toArray(User[]::new)[0];
        assertEquals(userFromBDD.getUsername(), mathis.username());
        assertEquals(userFromBDD.getId(), mathis.id());
        //verifier que la review et le user présent dans les listes du tag
        var tag = tagRepository.findTagByName("Test");
        assertTrue(tag.isPresent());
        var tagGet = tag.get();
        assertTrue(tagGet.getReviewsOf().stream().anyMatch(e -> e.getId() == reviewFromBDD.getId()));
        assertTrue(tagGet.getUsersOf().contains(userFromBDD));
    }

    @Test
    @DisplayName("Get review with many tags")
    void getTagsFromReview(){
        //Init un user
        var mathis = userService.register(new UserRegisterDTO("verestah1", "verestah@email.com", "verestah1", "password"));
        assertNotNull(mathis);
        //Init une question
        var questionId = questionService.create(new NewQuestionDTO("TITLE", "DESCRIPTION", new byte[0], null, "test.java", null), mathis.id());
        //Init une review avec des tags
        var review = questionService.addReview(new QuestionReviewCreateDTO(mathis.id(), questionId, "CONTENT", null, null, List.of("Test", "java", "compiler", "requireNonNull")));
        var tagList = tagRepository.findAll();
        assertFalse(tagList.isEmpty());
        //Recup les tags de la review
        var reviews = reviewRepository.findByTagName("Test");
        assertFalse(reviews.isEmpty());
        var reviewFromBDD = reviews.toArray(Review[]::new)[0];
        assertEquals(reviewFromBDD.getId(), review.id());
        assertEquals(reviewFromBDD.getContent(), review.content());

        //Recupe les tags du User
        var users = userRepository.findByTagName("Test");
        assertFalse(users.isEmpty());
        var userFromBDD = users.toArray(User[]::new)[0];
        assertEquals(userFromBDD.getUsername(), mathis.username());
        assertEquals(userFromBDD.getId(), mathis.id());
        //verifier que la review et le user présent dans les listes du tag
        var tag = tagRepository.findTagByName("Test");
        assertTrue(tag.isPresent());
        var tagGet = tag.get();
        assertTrue(tagGet.getReviewsOf().stream().anyMatch(e -> e.getId() == reviewFromBDD.getId()));
        assertTrue(tagGet.getUsersOf().contains(userFromBDD));

        tag = tagRepository.findTagByName("java");
        assertTrue(tag.isPresent());
        tagGet = tag.get();
        assertTrue(tagGet.getReviewsOf().stream().anyMatch(e -> e.getId() == reviewFromBDD.getId()));
        assertTrue(tagGet.getUsersOf().contains(userFromBDD));

        tag = tagRepository.findTagByName("compiler");
        assertTrue(tag.isPresent());
        tagGet = tag.get();
        assertTrue(tagGet.getReviewsOf().stream().anyMatch(e -> e.getId() == reviewFromBDD.getId()));
        assertTrue(tagGet.getUsersOf().contains(userFromBDD));

        tag = tagRepository.findTagByName("requireNonNull");
        assertTrue(tag.isPresent());
        tagGet = tag.get();
        assertTrue(tagGet.getReviewsOf().stream().anyMatch(e -> e.getId() == reviewFromBDD.getId()));
        assertTrue(tagGet.getUsersOf().contains(userFromBDD));
    }

    @Test
    @DisplayName("""
            After removing a review with a tag, and if the user has no more reference of this tag on his reviews.
            Then the same tag should be detach from this user.
            """)
    void removeReviewThenDetachTagsUserTest() throws IOException {
        userTestProvider.addSomeUserIntoDatabase();
        final var userId = userRepository.findByLogin("loginSacane").orElseThrow();
        final var question = questionRepository.findByAuthorOrderByCreatedAtDesc(userId).get(0);
        var review = questionService.addReview(
                new QuestionReviewCreateDTO(
                        userId.getId(),
                        question.getId(),
                        "Je ne sais pas quoi mettre dans cette review",
                        null,
                        null,
                        List.of("test")
                )
        );

        assertEquals(1, tagRepository.findAll().size());
        reviewService.remove(new ReviewRemoveDTO(userId.getId(), review.id()));

        var nowUser = userRepository.findByIdWithTag(userId.getId()).orElseThrow();
        assertTrue(nowUser.getTagsCreated().isEmpty());

        var tag = tagRepository.findTagByNameWithUsers("test");
        assertTrue(tag.isPresent());
        var t = tag.get();
        assertFalse(t.getUsersOf().contains(userId));
    }

}
