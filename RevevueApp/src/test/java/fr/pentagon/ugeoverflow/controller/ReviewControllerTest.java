package fr.pentagon.ugeoverflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pentagon.ugeoverflow.DatasourceTestConfig;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.CredentialsDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewOnReviewBodyDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.VoteBodyDTO;
import fr.pentagon.ugeoverflow.controllers.rest.ReviewController;
import fr.pentagon.ugeoverflow.exception.HttpExceptionHandler;
import fr.pentagon.ugeoverflow.repository.*;
import fr.pentagon.ugeoverflow.service.QuestionService;
import fr.pentagon.ugeoverflow.service.ReviewService;
import fr.pentagon.ugeoverflow.service.UserService;
import fr.pentagon.ugeoverflow.testutils.LoginTestService;
import fr.pentagon.ugeoverflow.testutils.UserTestProvider;
import fr.pentagon.ugeoverflow.utils.Routes;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(DatasourceTestConfig.class)
public class ReviewControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc reviewMVC;
    @Autowired
    private UserTestProvider userTestProvider;
    @Autowired
    private LoginTestService loginTestService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReviewVoteRepository reviewVoteRepository;
    @Autowired
    private TagRepository tagRepository;


    @BeforeEach
    public void setup(){
        var httpExceptionHandler = new HttpExceptionHandler();
        this.reviewMVC = MockMvcBuilders.standaloneSetup(new ReviewController(reviewService))
                .setControllerAdvice(httpExceptionHandler)
                .build();
    }
    @AfterEach
    void clearUp() {
        reviewVoteRepository.deleteAll();
        userRepository.deleteAll();
        tagRepository.deleteAll();
        reviewRepository.deleteAll();
        questionRepository.deleteAll();
    }

    @Test
    @DisplayName("Get information about an existing simple review")
    void getReviewDetails() throws Exception {
        userTestProvider.addSomeUserIntoDatabaseWithReview();
        var listOfReview = reviewRepository.findAll();
        assertFalse(listOfReview.isEmpty());
        var reviewId = listOfReview.getFirst().getId();
        reviewMVC.perform(MockMvcRequestBuilders.get(Routes.Review.ROOT+"/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("Sacane2"))
                .andExpect(jsonPath("$.content").value("Vraiment nul comme idée"))
                .andDo(print());
    }

    @Test
    @DisplayName("Get information about an existing review with review")
    void getReviewDetailsMoreDetails() throws Exception {
        userTestProvider.addSomeUserIntoDatabaseWithReview();
        var listOfReview = reviewRepository.findAll();
        assertTrue(listOfReview.size() >= 2);
        var reviewId = listOfReview.get(1).getId();
        reviewMVC.perform(MockMvcRequestBuilders.get(Routes.Review.ROOT+"/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("Sacane3"))
                .andExpect(jsonPath("$.content").value("Ping"))
                .andExpect(jsonPath("$.reviews").isNotEmpty())
                .andDo(print());
    }

    @Test
    @DisplayName("Get details about review that doesn't exist")
    void getReviewDetailDoesntExist() throws Exception {
        reviewMVC.perform(MockMvcRequestBuilders.get(Routes.Review.ROOT+"/{reviewId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Find reviews of a question")
    void getAllReviewOfAQuestion() throws Exception {
        userTestProvider.addSomeUserIntoDatabaseWithReview();
        var listOfQuestions = questionService.getQuestions();
        var questionId = listOfQuestions.stream().filter(q -> q.nbAnswers() >=1).toList().getFirst().id();
        reviewMVC.perform(MockMvcRequestBuilders.get(Routes.Review.ROOT+Routes.Question.IDENT + "/{questionId}", questionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author").value("Sacane4"))
                .andExpect(jsonPath("$[0].content").value("Ping"))
                .andExpect(jsonPath("$[0].reviews").isNotEmpty())
                .andExpect(jsonPath("$[0].reviews[0].author").value("Sacane3")) // Vérifie l'auteur de la première review
                .andExpect(jsonPath("$[0].reviews[0].content").value("Pong"))
                .andDo(print());
    }

    @Test
    @DisplayName("Find reviews about question that doesn't exist")
    void getAllReviewOfAQuestionNotExist() throws Exception {
        userTestProvider.addSomeUserIntoDatabaseWithReview();
        reviewMVC.perform(MockMvcRequestBuilders.get(Routes.Review.ROOT+Routes.Question.IDENT + "/{questionId}", 1000)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("This question does not exists"));
    }


    @Test
    @DisplayName("Add a review to an existing review")
    void addAReviewToAReview() throws Exception {
        userTestProvider.addSomeUserIntoDatabaseWithReview();
        loginTestService.login(new CredentialsDTO("loginSacane", "SacanePassword"));
        var listOfReview = reviewRepository.findAll();
        assertFalse(listOfReview.isEmpty());
        var reviewId = listOfReview.getFirst().getId();
        var dto = new ReviewOnReviewBodyDTO(reviewId, "I'm the batman", List.of());
        reviewMVC.perform(MockMvcRequestBuilders.post(Routes.Review.ROOT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andDo(print());

        reviewMVC.perform(MockMvcRequestBuilders.get(Routes.Review.ROOT+"/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviews").isNotEmpty())
                .andExpect(jsonPath("$.reviews[0].content").value("I'm the batman"))
                .andDo(print());
    }

    @Test
    @DisplayName("Add a review to an existing review when not no one is logged")
    void addAReviewToAReviewWhenNotAuth() throws Exception {
        userTestProvider.addSomeUserIntoDatabaseWithReview();
        var listOfReview = reviewRepository.findAll();
        assertFalse(listOfReview.isEmpty());
        var reviewId = listOfReview.getFirst().getId();
        var dto = new ReviewOnReviewBodyDTO(reviewId, "I'm the batman", List.of());
        reviewMVC.perform(MockMvcRequestBuilders.post(Routes.Review.ROOT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("Add a review to a review which doesn't exist")
    void addAReviewToAFakeReview() throws Exception {
        userTestProvider.addSomeUserIntoDatabaseWithReview();
        loginTestService.login(new CredentialsDTO("loginSacane", "SacanePassword"));
        var dto = new ReviewOnReviewBodyDTO(1000, "I'm the batman", List.of());
        reviewMVC.perform(MockMvcRequestBuilders.post(Routes.Review.ROOT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Review not exist"))
                .andDo(print());
    }

    @Test
    @DisplayName("Try to delete a review of an other user")
    void deleteAReviewFromAnotherUser() throws Exception {
        userTestProvider.addSomeUserIntoDatabaseWithReview();
        loginTestService.login(new CredentialsDTO("loginSacane", "SacanePassword"));
        var listOfReview = reviewRepository.findAll();
        assertTrue(listOfReview.size() >= 2);
        var reviewId = listOfReview.get(1).getId();
        reviewMVC.perform(MockMvcRequestBuilders.delete(Routes.Review.ROOT + "/{reviewId}", reviewId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("Delete a review")
    void deleteAReview() throws Exception {
        userTestProvider.addSomeUserIntoDatabaseWithReview();
        loginTestService.login(new CredentialsDTO("loginSacane3", "SacanePassword3"));
        var listOfReview = reviewRepository.findAll();
        assertTrue(listOfReview.size() >= 2);
        var reviewId = listOfReview.get(1).getId();
        reviewMVC.perform(MockMvcRequestBuilders.delete(Routes.Review.ROOT + "/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Try to delete a review which doesn't exist")
    void deleteAReviewNotExist() throws Exception {
        userTestProvider.addSomeUserIntoDatabaseWithReview();
        loginTestService.login(new CredentialsDTO("loginSacane3", "SacanePassword3"));
        reviewMVC.perform(MockMvcRequestBuilders.delete(Routes.Review.ROOT + "/{reviewId}", 1000)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Review not exist"))
                .andDo(print());
    }

    @Test
    @DisplayName("Try to vote a review with a bad request")
    void voteAReviewWhenBadRequest() throws Exception {
        userTestProvider.addSomeUserIntoDatabaseWithReview();
        loginTestService.login(new CredentialsDTO("loginSacane3", "SacanePassword3"));
        var listOfReview = reviewRepository.findAll();
        assertFalse(listOfReview.isEmpty());
        var reviewId = listOfReview.getFirst().getId();
        reviewMVC.perform(MockMvcRequestBuilders.post(Routes.Review.ROOT + "/{reviewId}/vote", reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("Vote a review")
    void voteAReview() throws Exception {
        userTestProvider.addSomeUserIntoDatabaseWithReview();
        loginTestService.login(new CredentialsDTO("loginSacane3", "SacanePassword3"));
        var listOfReview = reviewRepository.findAll();
        assertFalse(listOfReview.isEmpty());
        var reviewId = listOfReview.getFirst().getId();
        reviewMVC.perform(MockMvcRequestBuilders.post(Routes.Review.ROOT + "/{reviewId}/vote", reviewId)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new VoteBodyDTO(false))))
                .andExpect(status().isOk())
                .andDo(print());

        reviewMVC.perform(MockMvcRequestBuilders.get(Routes.Review.ROOT+"/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.downvotes").value("1"))
                .andDo(print());
    }

    @Test
    @DisplayName("Vote a review when not auth")
    void voteAReviewWhenNotAuth() throws Exception {
        userTestProvider.addSomeUserIntoDatabaseWithReview();
        var listOfReview = reviewRepository.findAll();
        assertFalse(listOfReview.isEmpty());
        var reviewId = listOfReview.getFirst().getId();
        reviewMVC.perform(MockMvcRequestBuilders.post(Routes.Review.ROOT + "/{reviewId}/vote", reviewId)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new VoteBodyDTO(false))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("User is not authenticated"))
                .andDo(print());
    }

    @Test
    @DisplayName("Vote a review which doesn't exist")
    void voteAReviewNotExist() throws Exception {
        userTestProvider.addSomeUserIntoDatabaseWithReview();
        loginTestService.login(new CredentialsDTO("loginSacane3", "SacanePassword3"));
        reviewMVC.perform(MockMvcRequestBuilders.post(Routes.Review.ROOT + "/{reviewId}/vote", 100)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new VoteBodyDTO(false))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("The review does not exists"))
                .andDo(print());
    }

    @Test
    @DisplayName("Cancel a vote on a review")
    void cancelVoteOnReview() throws Exception {
        userTestProvider.addSomeUserIntoDatabaseWithReview();
        loginTestService.login(new CredentialsDTO("loginSacane3", "SacanePassword3"));
        var listOfReview = reviewRepository.findAll();
        assertFalse(listOfReview.isEmpty());
        var reviewId = listOfReview.getFirst().getId();
        reviewMVC.perform(MockMvcRequestBuilders.post(Routes.Review.ROOT + "/{reviewId}/vote", reviewId)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new VoteBodyDTO(false))))
                .andExpect(status().isOk())
                .andDo(print());

        reviewMVC.perform(MockMvcRequestBuilders.get(Routes.Review.ROOT+"/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.downvotes").value("1"))
                .andDo(print());

        reviewMVC.perform(MockMvcRequestBuilders.delete(Routes.Review.ROOT + "/{reviewId}/cancelVote", reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        reviewMVC.perform(MockMvcRequestBuilders.get(Routes.Review.ROOT+"/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.downvotes").value("0"))
                .andDo(print());
    }

    @Test
    @DisplayName("Try to cancel a vote on a review when not auth ")
    void cancelVoteOnReviewNotAuth() throws Exception {
        userTestProvider.addSomeUserIntoDatabaseWithReview();
        loginTestService.login(new CredentialsDTO("loginSacane3", "SacanePassword3"));
        var listOfReview = reviewRepository.findAll();
        assertFalse(listOfReview.isEmpty());
        var reviewId = listOfReview.getFirst().getId();
        reviewMVC.perform(MockMvcRequestBuilders.post(Routes.Review.ROOT + "/{reviewId}/vote", reviewId)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new VoteBodyDTO(false))))
                .andExpect(status().isOk())
                .andDo(print());

        loginTestService.logout();

        reviewMVC.perform(MockMvcRequestBuilders.get(Routes.Review.ROOT+"/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.downvotes").value("1"))
                .andDo(print());

        reviewMVC.perform(MockMvcRequestBuilders.delete(Routes.Review.ROOT + "/{reviewId}/cancelVote", reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("User is not authenticated"))
                .andDo(print());
    }

    @Test
    @DisplayName("Try to cancel a vote on a review which doesn't exists")
    void cancelVoteOnReviewNotExist() throws Exception {
        userTestProvider.addSomeUserIntoDatabaseWithReview();
        loginTestService.login(new CredentialsDTO("loginSacane3", "SacanePassword3"));
        reviewMVC.perform(MockMvcRequestBuilders.delete(Routes.Review.ROOT + "/{reviewId}/cancelVote", 10000)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("The review does not exists"))
                .andDo(print());
    }


    @Test
    @DisplayName("Try to get reviews with specific tag when not auth")
    void findByTagNotAuth() throws Exception {
        userTestProvider.addSomeUserIntoDatabaseWithReview();
        reviewMVC.perform(MockMvcRequestBuilders.get(Routes.Review.ROOT + "/tags/{tag}", "Java")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("User is not authenticated"))
                .andDo(print());
    }

    @Test
    @DisplayName("Get reviews with specific tag")
    void findByTag() throws Exception {
        userTestProvider.addSomeUserIntoDatabaseWithReview(); //Set a review with the tag : Java
        loginTestService.login(new CredentialsDTO("loginSacane3", "SacanePassword3"));
        reviewMVC.perform(MockMvcRequestBuilders.get(Routes.Review.ROOT + "/tags/{tag}", "Java")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("Get reviews with unknown tag")
    void findByTagUnknown() throws Exception {
        userTestProvider.addSomeUserIntoDatabaseWithReview();
        loginTestService.login(new CredentialsDTO("loginSacane3", "SacanePassword3"));
        reviewMVC.perform(MockMvcRequestBuilders.get(Routes.Review.ROOT + "/tags/{tag}", "MR.ROBOT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0))
                .andDo(print());
    }
}
