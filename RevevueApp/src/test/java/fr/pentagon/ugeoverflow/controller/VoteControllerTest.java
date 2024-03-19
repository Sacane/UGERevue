package fr.pentagon.ugeoverflow.controller;

import fr.pentagon.ugeoverflow.DatasourceTestConfig;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.CredentialsDTO;
import fr.pentagon.ugeoverflow.controllers.rest.VoteController;
import fr.pentagon.ugeoverflow.exception.HttpExceptionHandler;
import fr.pentagon.ugeoverflow.repository.QuestionRepository;
import fr.pentagon.ugeoverflow.repository.QuestionVoteRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import fr.pentagon.ugeoverflow.service.QuestionService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(DatasourceTestConfig.class)
public class VoteControllerTest {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private UserTestProvider userTestProvider;
    private MockMvc voteMVC;
    @Autowired
    private LoginTestService loginTestService;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuestionVoteRepository questionVoteRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup(){
        var httpExceptionHandler = new HttpExceptionHandler();
        this.voteMVC = MockMvcBuilders.standaloneSetup(new VoteController(questionService))
                .setControllerAdvice(httpExceptionHandler)
                .build();
    }
    @AfterEach
    void clearUp() {
        questionVoteRepository.deleteAll();
        userRepository.deleteAll();
        questionRepository.deleteAll();
    }

    @Test
    @DisplayName("Get the number of upVote and downVote of a question when not connected")
    void getNumberOfVoteOfQuestionNotAuth() throws Exception {
        userTestProvider.addSomeUserIntoDatabase();
        var questionList = questionService.getQuestions();
        assertFalse(questionList.isEmpty());
        var questionId = questionList.getFirst().id();
        voteMVC.perform(MockMvcRequestBuilders.get(Routes.Vote.ROOT + "/questions/" + questionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("Try to get the number of upVote and downVote of a question which doesn't exist")
    void getNumberOfVoteOfQuestionBadId() throws Exception {
        userTestProvider.addSomeUserIntoDatabase();
        loginTestService.login(new CredentialsDTO("loginSacane", "SacanePassword"));
        voteMVC.perform(MockMvcRequestBuilders.get(Routes.Vote.ROOT + "/questions/" + 1000)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("This question doesn't exist"))
                .andDo(print());

    }

    @Test
    @DisplayName("Get the number of upVote and downVote of a question with 0 vote")
    void getNumberOfVoteOfQuestion1() throws Exception {
        userTestProvider.addSomeUserIntoDatabase();
        loginTestService.login(new CredentialsDTO("loginSacane", "SacanePassword"));
        var questionList = questionService.getQuestions();
        assertFalse(questionList.isEmpty());
        var questionId = questionList.getFirst().id();
        voteMVC.perform(MockMvcRequestBuilders.get(Routes.Vote.ROOT + "/questions/" + questionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.upVotes").value("0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.downVotes").value("0"))
                .andDo(print());

    }

    @Test
    @DisplayName("Get the number of upVote and downVote of a question with votes")
    void getNumberOfVoteOfQuestion2() throws Exception {
        userTestProvider.addSomeUserIntoDatabase();
        loginTestService.login(new CredentialsDTO("loginSacane", "SacanePassword"));
        var questionList = questionService.getQuestions();
        assertFalse(questionList.isEmpty());
        var questionId = questionList.getFirst().id();
        voteMVC.perform(MockMvcRequestBuilders.get(Routes.Vote.ROOT + "/questions/" + questionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.upVotes").value("0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.downVotes").value("0"))
                .andDo(print());

        voteMVC.perform(MockMvcRequestBuilders.post(Routes.Vote.UP_VOTE + "/questions/{questionId}", questionId)
                .contentType(MediaType.APPLICATION_JSON));

        voteMVC.perform(MockMvcRequestBuilders.get(Routes.Vote.ROOT + "/questions/" + questionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.upVotes").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.downVotes").value("0"))
                .andDo(print());
    }

    @Test
    @DisplayName("UpVote a question when not auth")
    void upVoteQuestionNotAuth() throws Exception {
        userTestProvider.addSomeUserIntoDatabase();
        var questionList = questionService.getQuestions();
        assertFalse(questionList.isEmpty());
        var questionId = questionList.getFirst().id();
        voteMVC.perform(MockMvcRequestBuilders.post(Routes.Vote.UP_VOTE + "/questions/{questionId}", questionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("DownVote a question when not auth")
    void downVoteQuestionNotAuth() throws Exception {
        userTestProvider.addSomeUserIntoDatabase();
        var questionList = questionService.getQuestions();
        assertFalse(questionList.isEmpty());
        var questionId = questionList.getFirst().id();
        voteMVC.perform(MockMvcRequestBuilders.post(Routes.Vote.DOWN_VOTE + "/questions/{questionId}", questionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("UpVote a question")
    void upVoteQuestion() throws Exception {
        userTestProvider.addSomeUserIntoDatabase();
        loginTestService.login(new CredentialsDTO("loginSacane", "SacanePassword"));
        var questionList = questionService.getQuestions();
        assertFalse(questionList.isEmpty());
        var questionId = questionList.getFirst().id();
        voteMVC.perform(MockMvcRequestBuilders.post(Routes.Vote.UP_VOTE + "/questions/{questionId}", questionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        var question = questionService.findById(questionId);
        assertEquals(1, question.voteCount());
        assertEquals(1, questionVoteRepository.findUpvoteNumberByQuestionId(questionId));
        assertEquals(0, questionVoteRepository.findDownvoteNumberByQuestionId(questionId));
    }

    @Test
    @DisplayName("A specific user can vote only one time on a question")
    void upOrDownVoteQuestionManyTime() throws Exception {
        userTestProvider.addSomeUserIntoDatabase();
        loginTestService.login(new CredentialsDTO("loginSacane", "SacanePassword"));
        var questionList = questionService.getQuestions();
        assertFalse(questionList.isEmpty());
        var questionId = questionList.getFirst().id();
        voteMVC.perform(MockMvcRequestBuilders.post(Routes.Vote.UP_VOTE + "/questions/{questionId}", questionId)
                .contentType(MediaType.APPLICATION_JSON));
        voteMVC.perform(MockMvcRequestBuilders.post(Routes.Vote.UP_VOTE + "/questions/{questionId}", questionId)
                .contentType(MediaType.APPLICATION_JSON));
        voteMVC.perform(MockMvcRequestBuilders.post(Routes.Vote.UP_VOTE + "/questions/{questionId}", questionId)
                        .contentType(MediaType.APPLICATION_JSON));
        voteMVC.perform(MockMvcRequestBuilders.post(Routes.Vote.DOWN_VOTE + "/questions/{questionId}", questionId)
                .contentType(MediaType.APPLICATION_JSON));
        voteMVC.perform(MockMvcRequestBuilders.post(Routes.Vote.DOWN_VOTE + "/questions/{questionId}", questionId)
                .contentType(MediaType.APPLICATION_JSON));

        var question = questionService.findById(questionId);
        assertEquals(1, question.voteCount());
        assertEquals(0, questionVoteRepository.findUpvoteNumberByQuestionId(questionId));
        assertEquals(1, questionVoteRepository.findDownvoteNumberByQuestionId(questionId));
    }

    @Test
    @DisplayName("downVote a question")
    void downVoteQuestion() throws Exception {
        userTestProvider.addSomeUserIntoDatabase();
        loginTestService.login(new CredentialsDTO("loginSacane", "SacanePassword"));
        var questionList = questionService.getQuestions();
        assertFalse(questionList.isEmpty());
        var questionId = questionList.getFirst().id();
        voteMVC.perform(MockMvcRequestBuilders.post(Routes.Vote.DOWN_VOTE + "/questions/{questionId}", questionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        var question = questionService.findById(questionId);
        assertEquals(1, question.voteCount());
        assertEquals(0, questionVoteRepository.findUpvoteNumberByQuestionId(questionId));
        assertEquals(1, questionVoteRepository.findDownvoteNumberByQuestionId(questionId));
    }

    @Test
    @DisplayName("Few users vote on a question")
    void manyVoteQuestion() throws Exception {
        userTestProvider.addSomeUserIntoDatabase();
        loginTestService.login(new CredentialsDTO("loginSacane", "SacanePassword"));
        var questionList = questionService.getQuestions();
        assertFalse(questionList.isEmpty());
        var questionId = questionList.getFirst().id();
        voteMVC.perform(MockMvcRequestBuilders.post(Routes.Vote.DOWN_VOTE + "/questions/{questionId}", questionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        loginTestService.logout();
        loginTestService.login(new CredentialsDTO("loginSacane3", "SacanePassword3"));

        voteMVC.perform(MockMvcRequestBuilders.post(Routes.Vote.DOWN_VOTE + "/questions/{questionId}", questionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        loginTestService.logout();
        loginTestService.login(new CredentialsDTO("loginSacane2", "SacanePassword2"));
        voteMVC.perform(MockMvcRequestBuilders.post(Routes.Vote.UP_VOTE + "/questions/{questionId}", questionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        var question = questionService.findById(questionId);
        assertEquals(3, question.voteCount());
        assertEquals(1, questionVoteRepository.findUpvoteNumberByQuestionId(questionId));
        assertEquals(2, questionVoteRepository.findDownvoteNumberByQuestionId(questionId));
    }

    @Test
    @DisplayName("Try to upVote a question which doesn't exist")
    void upVoteOfQuestionBadId() throws Exception {
        userTestProvider.addSomeUserIntoDatabase();
        loginTestService.login(new CredentialsDTO("loginSacane", "SacanePassword"));
        voteMVC.perform(MockMvcRequestBuilders.post(Routes.Vote.UP_VOTE + "/questions/{questionId}", 1000)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Question does not exists"))
                .andDo(print());

    }

    @Test
    @DisplayName("Try to downVote a question which doesn't exist")
    void downVoteOfQuestionBadId() throws Exception {
        userTestProvider.addSomeUserIntoDatabase();
        loginTestService.login(new CredentialsDTO("loginSacane", "SacanePassword"));
        voteMVC.perform(MockMvcRequestBuilders.post(Routes.Vote.DOWN_VOTE + "/questions/{questionId}", 1000)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Question does not exists"))
                .andDo(print());

    }
}
