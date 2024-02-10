package fr.pentagon.ugeoverflow.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pentagon.ugeoverflow.DatasourceTestConfig;
import fr.pentagon.ugeoverflow.controllers.LoginController;
import fr.pentagon.ugeoverflow.controllers.QuestionController;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.NewQuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.exception.HttpExceptionHandler;
import fr.pentagon.ugeoverflow.service.FakeQuestionService;
import fr.pentagon.ugeoverflow.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(DatasourceTestConfig.class)
class QuestionControllerTest {

    private static final String DEFAULT_ROUTE = "/api/questions/";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserService userService;

    private final MockMvc questionMVC = MockMvcBuilders.standaloneSetup(new QuestionController(new FakeQuestionService()))
            .setControllerAdvice(new HttpExceptionHandler())
            .build();

    @Autowired
    private LoginController loginController;

    private MockMvc loginMVC;

    @BeforeEach
    void setUp() {
        loginMVC = MockMvcBuilders.standaloneSetup(loginController)
                .setControllerAdvice(new HttpExceptionHandler())
                .build();
    }

    @Nested
    @Tag("GET")
    @DisplayName("Get all questions")
    final class GetAllQuestions {

        private final List<QuestionDTO> results;

        private final ResultActions request;

        GetAllQuestions() throws Exception {
            request = questionMVC.perform(MockMvcRequestBuilders.get(DEFAULT_ROUTE).contentType(MediaType.APPLICATION_JSON));
            var responseBody = request.andReturn().getResponse().getContentAsString();
            var typeReference = new TypeReference<List<QuestionDTO>>() {
            };
            this.results = objectMapper.readValue(responseBody, typeReference);
        }

        @Test
        @DisplayName("Basic tests")
        void allOpenReviews() {
            assertAll(
                    () -> request.andExpect(MockMvcResultMatchers.status().isOk()),
                    () -> request.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3)),
                    () -> assertEquals(3, results.size()),
                    () -> assertEquals(3, results.stream().distinct().count())
            );
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 2})
        @DisplayName("IDs tests")
        void allOpenReviewsIDs(int index) {
            assertEquals(7L * index, results.get(index).id());
        }

        @Test
        @DisplayName("Complex test")
        void allValuesAllOpenReviews() {
            assertAll(
                    () -> assertEquals("How to concat string in for statement", results.get(0).title()),
                    () -> assertEquals("StringBuilder", results.get(0).javaFile()),
                    () -> assertTrue(results.get(2).testFile().isEmpty()),
                    () -> assertEquals("@Test", results.get(1).testFile()),
                    () -> assertEquals(118218L, results.get(0).authorID()),
                    () -> assertEquals(3630L, results.get(1).authorID()),
                    () -> assertEquals(17L, results.get(2).authorID())
            );
        }

    }

    @Nested
    @Tag("POST")
    @DisplayName("Post a new question")
    final class PostQuestion {

        @Test
        @DisplayName("Check security")
        void requestAsUnknown() throws Exception {
            var question = new NewQuestionDTO("New", "WOW".getBytes(StandardCharsets.UTF_8), new byte[]{});
            questionMVC.perform(MockMvcRequestBuilders
                    .post(DEFAULT_ROUTE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(question))
            ).andExpect(MockMvcResultMatchers.status().is(401));
        }

    }


}
