package fr.pentagon.ugeoverflow.controllers;

import fr.pentagon.ugeoverflow.config.authorization.RequireUser;
import fr.pentagon.ugeoverflow.config.security.SecurityContext;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.NewQuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDetailDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.service.QuestionService;
import fr.pentagon.ugeoverflow.service.QuestionServiceAdapter;
import fr.pentagon.ugeoverflow.utils.Routes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@RestController
public class QuestionController {

    private static final Logger LOGGER = Logger.getLogger(QuestionController.class.getName());

    private final QuestionServiceAdapter questionServiceAdapter;
    private final QuestionService questionService;

    public QuestionController(QuestionServiceAdapter questionServiceAdapter, QuestionService questionService) {
        this.questionServiceAdapter = Objects.requireNonNull(questionServiceAdapter);
        this.questionService = questionService;
    }

    @GetMapping(Routes.Question.ROOT)
    public ResponseEntity<List<QuestionDTO>> allQuestions() {
        LOGGER.info("GET performed on /api/questions");
        return ResponseEntity.ok(questionService.getQuestions());
    }

    @PostMapping(Routes.Question.ROOT)
    @RequireUser
    public ResponseEntity<Long> createQuestion(@Valid @RequestBody NewQuestionDTO newQuestionDTO) {
        LOGGER.info("POST performed on /api/questions");
        var userDetail = SecurityContext.checkAuthentication();
        //var registeredQuestionDTO = questionServiceAdapter.registerQuestion(newQuestionDTO, userDetail.id());
        return ResponseEntity.ok(questionService.create(newQuestionDTO, userDetail.id()));
    }

    @DeleteMapping(Routes.Question.WITH_QUESTION_ID)
    public ResponseEntity<String> removeQuestion(@PathVariable long questionId) {
        LOGGER.info("DELETE performed on /api/questions/" + questionId);
        SecurityContext.checkAuthentication();
        if(questionServiceAdapter.removeQuestion(questionId)){
            return ResponseEntity.ok("Question with id : " + questionId + " was removed.");
        }
        throw HttpException.notFound("Question with id : " + questionId + " not found.");
    }


    // TODO : Use commentaries and responses to create a "CompleteQuestionInfoDTO".

    @GetMapping(Routes.Question.WITH_QUESTION_ID)
    public ResponseEntity<QuestionDetailDTO> getQuestion(@PathVariable long questionId) {
        LOGGER.info("GET performed on /api/questions/" + questionId);
        var question = questionServiceAdapter.question(questionId);
        if(question.isPresent()){
            return ResponseEntity.ok(question.get());
        }
        throw HttpException.notFound("Question with id : " + questionId + " not found.");
    }

}
