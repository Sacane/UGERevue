package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.DatasourceTestConfig;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.NewQuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionReviewCreateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserRegisterDTO;
import fr.pentagon.ugeoverflow.model.Review;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.repository.QuestionRepository;
import fr.pentagon.ugeoverflow.repository.ReviewRepository;
import fr.pentagon.ugeoverflow.repository.TagRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

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
        var questionId = questionService.create(new NewQuestionDTO("TITLE", "DESCRIPTION", new byte[0], null), mathis.id());
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
        var questionId = questionService.create(new NewQuestionDTO("TITLE", "DESCRIPTION", new byte[0], null), mathis.id());
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

}
