package fr.pentagon.ugeoverflow.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pentagon.ugeoverflow.DatasourceTestConfig;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.CredentialsDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDTO;
import fr.pentagon.ugeoverflow.controllers.rest.LoginController;
import fr.pentagon.ugeoverflow.controllers.rest.QuestionController;
import fr.pentagon.ugeoverflow.exception.HttpExceptionHandler;
import fr.pentagon.ugeoverflow.repository.QuestionRepository;
import fr.pentagon.ugeoverflow.service.LoginManager;
import fr.pentagon.ugeoverflow.service.QuestionService;
import fr.pentagon.ugeoverflow.service.UserService;
import fr.pentagon.ugeoverflow.testutils.LoginTestService;
import fr.pentagon.ugeoverflow.testutils.UserTestProvider;
import fr.pentagon.ugeoverflow.utils.Routes;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(DatasourceTestConfig.class)
class QuestionControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserService userService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private LoginManager authenticationManager;
    @Autowired
    private UserTestProvider userTestProvider;
    private MockMvc questionMVC;
    @Autowired
    private LoginTestService loginTestService;

    @Autowired
    private QuestionRepository questionRepository;
    @BeforeEach
    public void setup(){
        var httpExceptionHandler = new HttpExceptionHandler();
        this.questionMVC = MockMvcBuilders.standaloneSetup(new QuestionController(questionService))
                .setControllerAdvice(httpExceptionHandler)
                .build();
    }
    @AfterEach
    void clearUp(){
        questionRepository.deleteAll();
    }

    @Nested
    @Tag("GET")
    @DisplayName("Get all questions")
    final class GetAllQuestions {

        private final List<QuestionDTO> results;
        private final ResultActions request;


        GetAllQuestions() throws Exception {
            var httpExceptionHandler = new HttpExceptionHandler();
            userTestProvider.addSomeUserIntoDatabase();
            questionMVC = MockMvcBuilders.standaloneSetup(new QuestionController(questionService))
                    .setControllerAdvice(httpExceptionHandler)
                    .build();
            request = questionMVC.perform(MockMvcRequestBuilders.get(Routes.Question.ROOT).contentType(MediaType.APPLICATION_JSON));
            var responseBody = request.andReturn().getResponse().getContentAsString();
            var typeReference = new TypeReference<List<QuestionDTO>>() {
            };
            this.results = objectMapper.readValue(responseBody, typeReference);
        }

        @Test
        @DisplayName("Basic tests")
        void allOpenReviews() throws IOException {
            assertAll(
                    () -> request.andExpect(status().isOk()),
                    () -> request.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(8)),
                    () -> assertEquals(8, results.size()),
                    () -> assertEquals(8, results.stream().distinct().count())
            );
        }
    }

    @Nested
    @Tag("POST")
    @DisplayName("Post a new question")
    final class CreateQuestion {
        @Test
        void requestAsUnknown() throws Exception {
            MockMultipartFile titlePart = new MockMultipartFile("title", "title.txt", "text/plain", "Your Title".getBytes());
            MockMultipartFile descriptionPart = new MockMultipartFile("description", "description.txt", "text/plain", "Your Description".getBytes());
            MockMultipartFile javaFilePart = new MockMultipartFile("javaFile", "javaFile.txt", "text/plain", "Your Java File Content".getBytes());
            MockMultipartFile testFilePart = new MockMultipartFile("testFile", "testFile.txt", "text/plain", "Your Test File Content".getBytes());
            questionMVC.perform(
                    MockMvcRequestBuilders.multipart(Routes.Question.ROOT)
                            .file(titlePart)
                            .file(descriptionPart)
                            .file(javaFilePart)
                            .file(testFilePart)
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            ).andExpect(status().is(401));
        }

        @Test
        void createAQuestion() throws Exception {
            userTestProvider.addSomeUserIntoDatabase();
            loginTestService.login(new CredentialsDTO("loginSacane", "SacanePassword"));

            MockMultipartFile titlePart = new MockMultipartFile("title", "title.txt", "text/plain", "Your Title".getBytes());
            MockMultipartFile descriptionPart = new MockMultipartFile("description", "description.txt", "text/plain", "Your Description".getBytes());
            MockMultipartFile javaFilePart = new MockMultipartFile("javaFile", "javaFile.txt", "text/plain", "Your Java File Content".getBytes());
            MockMultipartFile testFilePart = new MockMultipartFile("testFile", "testFile.txt", "text/plain", "Your Test File Content".getBytes());
            questionMVC.perform(
                    MockMvcRequestBuilders.multipart(Routes.Question.ROOT)
                            .file(titlePart)
                            .file(descriptionPart)
                            .file(javaFilePart)
                            .file(testFilePart)
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            ).andExpect(status().is(200));
        }
    }
}
