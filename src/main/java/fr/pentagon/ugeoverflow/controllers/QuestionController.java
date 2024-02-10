package fr.pentagon.ugeoverflow.controllers;

import fr.pentagon.ugeoverflow.config.security.SecurityContext;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.NewQuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.service.QuestionServiceAdapter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@RestController
@RequestMapping("api/questions")
public final class QuestionController {

    private static final Logger LOGGER = Logger.getLogger(QuestionController.class.getName());

    private final QuestionServiceAdapter questionService;

    public QuestionController(QuestionServiceAdapter questionService) {
        this.questionService = Objects.requireNonNull(questionService);
    }

    @GetMapping
    public ResponseEntity<List<QuestionDTO>> allQuestions() {
        LOGGER.info("GET performed on /api/questions");
        return ResponseEntity.ok(questionService.allQuestions());
    }

    @PostMapping
    public ResponseEntity<QuestionDTO> createQuestion(@Valid @RequestBody NewQuestionDTO newQuestionDTO) {
        LOGGER.info("POST performed on /api/questions");
        var userDetail = SecurityContext.checkAuthentication();
        var registeredQuestionDTO = questionService.registerQuestion(newQuestionDTO, userDetail.id());
        return ResponseEntity.ok(registeredQuestionDTO);
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<String> removeQuestion(@PathVariable long questionId) {
        LOGGER.info("DELETE performed on /api/questions/" + questionId);
        SecurityContext.checkAuthentication();
        if(questionService.removeQuestion(questionId)){
            return ResponseEntity.ok("Question with id : " + questionId + " was removed.");
        }
        throw HttpException.notFound("Question with id : " + questionId + " not found.");
    }


    // TODO : Use commentaries and responses to create a "CompleteQuestionInfoDTO".

    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionDTO> getQuestion(@PathVariable long questionId) {
        LOGGER.info("GET performed on /api/questions/" + questionId);
        var question = questionService.question(questionId);
        if(question.isPresent()){
            return ResponseEntity.ok(question.get());
        }
        throw HttpException.notFound("Question with id : " + questionId + " not found.");
    }

}
