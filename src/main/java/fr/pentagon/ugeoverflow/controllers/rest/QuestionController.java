package fr.pentagon.ugeoverflow.controllers.rest;

import fr.pentagon.ugeoverflow.config.authorization.RequireUser;
import fr.pentagon.ugeoverflow.config.security.SecurityContext;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.*;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDetailsDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewQuestionResponseDTO;
import fr.pentagon.ugeoverflow.service.QuestionService;
import fr.pentagon.ugeoverflow.utils.Routes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@RestController
public class QuestionController {

    private static final Logger LOGGER = Logger.getLogger(QuestionController.class.getName());
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping(Routes.Question.ROOT)
    public ResponseEntity<List<QuestionDTO>> allQuestions() {
        LOGGER.info("GET performed on /api/questions");
        return ResponseEntity.ok(questionService.getQuestions());
    }

    @PostMapping(
            value = Routes.Question.ROOT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @RequireUser
    public ResponseEntity<Long> createQuestion(
            @RequestPart("title") String title,
            @RequestPart("description") String description,
            @RequestPart("javaFile")MultipartFile javaFile,
            @RequestPart(value = "testFile", required = false) MultipartFile testFile
    ) throws IOException {
        LOGGER.info("POST performed on /api/questions");
        var userDetail = SecurityContext.checkAuthentication();
        return ResponseEntity.ok(questionService.create(new NewQuestionDTO(
                title, description, javaFile.getBytes(), testFile == null ? null : testFile.getBytes()
        ), userDetail.id()));
    }

    @DeleteMapping(Routes.Question.ROOT + "/{questionId}")
    @RequireUser
    public ResponseEntity<Void> removeQuestion(@PathVariable long questionId) {
        LOGGER.info("DELETE performed on /api/questions/" + questionId);
        var user = SecurityContext.checkAuthentication();
        questionService.remove(new QuestionRemoveDTO(user.id(), questionId));
        return ResponseEntity.ok().build();
    }


    // TODO : Use commentaries and responses to create a "CompleteQuestionInfoDTO".

    @GetMapping(Routes.Question.ROOT + "/{questionId}")
    public ResponseEntity<QuestionDetailsDTO> getQuestion(@PathVariable long questionId) {
        LOGGER.info("GET performed on /api/questions/" + questionId);
        return ResponseEntity.ok(questionService.findById(questionId));
    }

    @PostMapping(Routes.Question.ROOT + "/reviews")
    public ResponseEntity<ReviewQuestionResponseDTO> addReview(@RequestBody QuestionReviewCreateBodyDTO questionReviewCreateBodyDTO) {
        var userDetail = SecurityContext.checkAuthentication();

        return ResponseEntity.ok(questionService.addReview(new QuestionReviewCreateDTO(userDetail.id(), questionReviewCreateBodyDTO.questionId(), questionReviewCreateBodyDTO.content(), questionReviewCreateBodyDTO.lineStart(), questionReviewCreateBodyDTO.lineEnd())));
    }
}