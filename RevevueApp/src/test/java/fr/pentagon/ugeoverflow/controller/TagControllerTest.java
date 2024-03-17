package fr.pentagon.ugeoverflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pentagon.ugeoverflow.DatasourceTestConfig;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.CredentialsDTO;
import fr.pentagon.ugeoverflow.controllers.rest.ReviewController;
import fr.pentagon.ugeoverflow.controllers.rest.TagController;
import fr.pentagon.ugeoverflow.exception.HttpExceptionHandler;
import fr.pentagon.ugeoverflow.repository.*;
import fr.pentagon.ugeoverflow.service.QuestionService;
import fr.pentagon.ugeoverflow.service.ReviewService;
import fr.pentagon.ugeoverflow.service.TagService;
import fr.pentagon.ugeoverflow.testutils.LoginTestService;
import fr.pentagon.ugeoverflow.testutils.UserTestProvider;
import fr.pentagon.ugeoverflow.utils.Routes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(DatasourceTestConfig.class)
public class TagControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc tagMVC;
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
    private QuestionService questionService;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReviewVoteRepository reviewVoteRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private TagService tagService;

    @BeforeEach
    public void setup(){
        var httpExceptionHandler = new HttpExceptionHandler();
        this.tagMVC = MockMvcBuilders.standaloneSetup(new TagController(tagService))
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
    @DisplayName("Try to get all tags registered by users when not auth")
    void getAllTagsNotAuth() throws Exception {
        userTestProvider.addSomeUserIntoDatabaseWithReview();
        tagMVC.perform(MockMvcRequestBuilders.get(Routes.Tag.ROOT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("User is not authenticated"))
                .andDo(print());
    }

    @Test
    @DisplayName("Get all tags registered by users")
    void getAllTags() throws Exception {
        userTestProvider.addSomeUserIntoDatabaseWithReview(); //Add two tag for the user Sacane4
        loginTestService.login(new CredentialsDTO("loginSacane4", "SacanePassword4"));
        tagMVC.perform(MockMvcRequestBuilders.get(Routes.Tag.ROOT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andDo(print());
    }

}
