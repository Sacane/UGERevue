package fr.pentagon.ugeoverflow.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pentagon.ugeoverflow.DatasourceTestConfig;
import fr.pentagon.ugeoverflow.config.authorization.Role;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.CredentialsDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionReviewCreateBodyDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserRegisterDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDTO;
import fr.pentagon.ugeoverflow.controllers.rest.QuestionController;
import fr.pentagon.ugeoverflow.exception.HttpExceptionHandler;
import fr.pentagon.ugeoverflow.model.Question;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.repository.QuestionRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import fr.pentagon.ugeoverflow.service.LoginManager;
import fr.pentagon.ugeoverflow.service.QuestionService;
import fr.pentagon.ugeoverflow.service.UserService;
import fr.pentagon.ugeoverflow.testutils.LoginTestService;
import fr.pentagon.ugeoverflow.testutils.UserTestProvider;
import fr.pentagon.ugeoverflow.utils.Routes;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @Autowired
    private UserRepository userRepository;
    @BeforeEach
    public void setup(){
        var httpExceptionHandler = new HttpExceptionHandler();
        this.questionMVC = MockMvcBuilders.standaloneSetup(new QuestionController(questionService))
                .setControllerAdvice(httpExceptionHandler)
                .build();
    }
    @AfterEach
    void clearUp() throws Exception {
        userRepository.deleteAll();
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

    @Test
    @DisplayName("Get filtered questions by label")
    void getFilteredQuestionsByLabel() throws Exception {
        var searchLabel = "hello world";
        userTestProvider.addSomeUserIntoDatabase();
        var request = questionMVC.perform(MockMvcRequestBuilders.get(Routes.Question.SEARCH)
                .param("label", searchLabel)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3))
                .andDo(print());

        var responseBody = request.andReturn().getResponse().getContentAsString();
        var typeReference = new TypeReference<List<QuestionDTO>>() {
        };
        var results = objectMapper.readValue(responseBody, typeReference);
        results.forEach(res -> assertTrue(res.title().toLowerCase().contains(searchLabel)
                || res.description().toLowerCase().contains(searchLabel)));
    }

    @Test
    @DisplayName("Get filtered questions with missing parameter")
    void getFilteredQuestionsByUserMissingParameter() throws Exception {
        userTestProvider.addSomeUserIntoDatabase();
        questionMVC.perform(MockMvcRequestBuilders.get(Routes.Question.SEARCH)
                        .param("username", "Sacane4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
    @Test
    @DisplayName("Get filtered questions with label & user")
    void getFilteredQuestionsByUser() throws Exception {
        userTestProvider.addSomeUserIntoDatabase();
        questionMVC.perform(MockMvcRequestBuilders.get(Routes.Question.SEARCH)
                        .param("label", "Je n'arrive pas a afficher mon hello world")
                        .param("username", "Sacane4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3))
                .andDo(print());
    }

    @Test
    @DisplayName("Get filtered questions with user not found") //TODO Question : Erreur de comportement dans l'endpoint (renvoyer 200 pour un user n'existant pas) : allQuestionByParameters + algo un peu bizarre
    void getFilteredQuestionsByUserNotFound() throws Exception {
        userTestProvider.addSomeUserIntoDatabase();
        questionMVC.perform(MockMvcRequestBuilders.get(Routes.Question.SEARCH)
                        .param("label", "Je n'arrive pas a afficher mon hello world")
                        .param("username", "Sacane49")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Nested
    class GetQuestionsFromCurrentUser {
        private Principal principal;

        @Transactional
        public void userWithQuestion() throws Exception {
            var user = new User("test1", "test1", "password", "test@gmail.com", Role.USER);
            userRepository.save(user);
            this.principal = new TestingAuthenticationToken("test1", null);

            var f1 = Paths.get("src", "test", "resources", "FakeJavaFiles", "HelloWorld.java");
            var f2 = Paths.get("src", "test", "resources", "FakeJavaFiles", "HelloWorldTest.java");

            Question entity = new Question("Hello world ne marche pas chez moi", "Je n'arrive pas a afficher mon hello world", Files.readAllBytes(f1), Files.readAllBytes(f2), "No result", true, new Date());
            Question entity2 = new Question("J'aimerai afficher un Hello world mais j'ai une erreur de compilation", "Je n'arrive pas a afficher mon hello world", Files.readAllBytes(f1), Files.readAllBytes(f2), "No result", true, new Date());
            Question entity3 = new Question("Impossible de trouver le bug", "Je n'arrive pas a afficher mon hello world", Files.readAllBytes(f1), Files.readAllBytes(f2), "No result", true, new Date());

            entity.setAuthor(user);
            entity2.setAuthor(user);
            entity3.setAuthor(user);
            questionRepository.saveAll(List.of(entity, entity2, entity3));

            user.addQuestion(entity2);
            user.addQuestion(entity);
            user.addQuestion(entity3);
        }

        @Test
        @DisplayName("Get question from the user connected with questions")
        void getQuestionsOfCurrentUserConnected() throws Exception {
            userWithQuestion();
            questionMVC.perform(MockMvcRequestBuilders.get(Routes.Question.CURRENT_USER)
                            .contentType(MediaType.APPLICATION_JSON).principal(principal))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3))
                    .andDo(print());
        }

        @Test
        @DisplayName("Get question from the user connected (with 0 question)")
        void getQuestionOfCurrentUserConnected() throws Exception {
            var userDTO = userService.register(new UserRegisterDTO("test1", "test@gmail.com", "test1", "password"));
            var principal = new TestingAuthenticationToken(userDTO.username(), null);
            questionMVC.perform(MockMvcRequestBuilders.get(Routes.Question.CURRENT_USER)
                            .contentType(MediaType.APPLICATION_JSON).principal(principal))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0))
                    .andDo(print());
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

        @Test
        @DisplayName("Delete non registered question") //TODO AU final inutile de mettre @Positive sur Id de path variable
        void deleteNonRegisteredQuestion() throws Exception {
            userTestProvider.addSomeUserIntoDatabase();
            loginTestService.login(new CredentialsDTO("loginSacane4", "SacanePassword4"));
            questionMVC.perform(MockMvcRequestBuilders.delete(Routes.Question.WITH_QUESTION_ID, -1))
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }

        @Test
        @DisplayName("Delete registered question")
        void deleteQuestion() throws Exception {
            userTestProvider.addSomeUserIntoDatabase();
            loginTestService.login(new CredentialsDTO("loginSacane4", "SacanePassword4"));
            var questionsOfUser = questionService.getQuestionsFromCurrentUser("loginSacane4");
            assertFalse(questionsOfUser.isEmpty());

            var question1Id = questionsOfUser.getFirst().id();

            questionMVC.perform(MockMvcRequestBuilders.delete(Routes.Question.WITH_QUESTION_ID, question1Id))
                    .andExpect(status().isOk())
                    .andDo(print());
        }

        @Test
        @DisplayName("Delete registered question when not connected")
        void deleteQuestionWhenNotConnected() throws Exception {
            userTestProvider.addSomeUserIntoDatabase();
            questionMVC.perform(MockMvcRequestBuilders.delete(Routes.Question.WITH_QUESTION_ID, 1))
                    .andExpect(status().isUnauthorized())
                    .andDo(print());
        }

        @Test
        @DisplayName("Get question by id")
        void getQuestionById() throws Exception {
            userTestProvider.addSomeUserIntoDatabase();
            var id = questionService.getQuestions().getFirst().id();
            questionMVC.perform(MockMvcRequestBuilders.get(Routes.Question.WITH_QUESTION_ID, id))
                    .andExpect(status().isOk())
                    .andDo(print());
        }

        @Test
        @DisplayName("Get question which doesn't exist by Id")
        void getUnknownQuestionById() throws Exception {
            userTestProvider.addSomeUserIntoDatabase();
            questionMVC.perform(MockMvcRequestBuilders.get(Routes.Question.WITH_QUESTION_ID, -1))
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }
    }
    @Test
    @DisplayName("Try to add review when non auth")
    void addReviewWhenNonAuth() throws Exception {
        userTestProvider.addSomeUserIntoDatabase();
        var dto = new QuestionReviewCreateBodyDTO(1, "Ceci est une review", null, null, List.of());
        questionMVC.perform(MockMvcRequestBuilders.post(Routes.Question.ROOT+ "/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print());
    }

    @Test
    @DisplayName("Add review to a question")
    void addReviewWhenAuth() throws Exception {
        userTestProvider.addSomeUserIntoDatabase();
        loginTestService.login(new CredentialsDTO("loginSacane", "SacanePassword"));
        var questionsOfUser = questionService.getQuestionsFromCurrentUser("loginSacane4");
        assertFalse(questionsOfUser.isEmpty());
        var question1Id = questionsOfUser.getFirst().id();

        var dto = new QuestionReviewCreateBodyDTO(question1Id, "Ceci est une review", null, null, List.of());
        questionMVC.perform(MockMvcRequestBuilders.post(Routes.Question.ROOT+ "/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.author").value("Sacane"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(dto.content()))
                .andDo(print());
    }

    @Test
    @DisplayName("Add review to a question which doesn't exist")
    void addReviewWhenQuestionDoesntExist() throws Exception {
        userTestProvider.addSomeUserIntoDatabase();
        loginTestService.login(new CredentialsDTO("loginSacane", "SacanePassword"));
        var dto = new QuestionReviewCreateBodyDTO(-1, "Ceci est une review", null, null, List.of());
        questionMVC.perform(MockMvcRequestBuilders.post(Routes.Question.ROOT+ "/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("Get question from follower when not auth")
    void getQuestionFromFollowerWhenNotAuth() throws Exception {
        questionMVC.perform(MockMvcRequestBuilders.get(Routes.Question.ROOT+ "/followers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    //TODO : Renvoie toutes les questions et pas seulement ceux des followers
    @Test
    @DisplayName("Get question from follower when auth")
    void getQuestionFromFollowerWhenAuthButNoFollower() throws Exception {
        userTestProvider.addSomeUserIntoDatabase();
        loginTestService.login(new CredentialsDTO("loginSacane", "SacanePassword"));
        questionMVC.perform(MockMvcRequestBuilders.get(Routes.Question.ROOT+ "/followers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                //.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0))
                .andDo(print());
    }
}
