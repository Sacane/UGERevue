package fr.pentagon.ugeoverflow.controllers.rest;

import fr.pentagon.ugeoverflow.config.authorization.RequireUser;
import fr.pentagon.ugeoverflow.config.security.SecurityContext;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.NewQuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionRemoveDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionReviewCreateBodyDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionReviewCreateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDetailsDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewQuestionResponseDTO;
import fr.pentagon.revevue.common.exception.HttpException;
import fr.pentagon.ugeoverflow.service.QuestionService;
import fr.pentagon.ugeoverflow.utils.Routes;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.logging.Logger;

import static org.springframework.http.ResponseEntity.ok;


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
    return ok(questionService.getQuestions());
  }

    @GetMapping(Routes.Question.SEARCH)
    public ResponseEntity<List<QuestionDTO>> allQuestionByParameters(@RequestParam("label") @NotBlank String label, @RequestParam(required = false, value = "username") String username) {
        LOGGER.info("Get performed on " + Routes.Question.SEARCH);
        return ok(questionService.getQuestions(label, username));
    }

  @GetMapping(Routes.Question.CURRENT_USER)
  public ResponseEntity<List<QuestionDTO>> getAllQuestionsFromCurrentUser(Principal principal) {
    if (principal == null) {
      throw HttpException.forbidden("No user currently authenticated");
    }
    return ResponseEntity.ok(questionService.getQuestionsFromCurrentUser(principal.getName()));
  }

    @PostMapping(
            value = Routes.Question.ROOT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @RequireUser
    public ResponseEntity<Long> createQuestion(
            @RequestPart("title") @NotBlank @NotNull String title,
            @RequestPart("description") @NotBlank @NotNull String description,
            @RequestPart("javaFile") @NotNull MultipartFile javaFile,
            @RequestPart(value = "testFile", required = false) MultipartFile testFile
    ) throws IOException {
        LOGGER.info("POST performed on /api/questions");
        var userDetail = SecurityContext.checkAuthentication();
        return ResponseEntity.ok(questionService.create(new NewQuestionDTO(
                title, description, javaFile.getBytes(), testFile == null ? null : testFile.getBytes(), javaFile.getOriginalFilename(), testFile == null ? null : testFile.getOriginalFilename()
        ), userDetail.id()));
    }

  @DeleteMapping(Routes.Question.ROOT + "/{questionId}")
  @RequireUser
  public ResponseEntity<Void> removeQuestion(@PathVariable(name = "questionId") long questionId) {
    LOGGER.info("DELETE performed on /api/questions/" + questionId);
    var user = SecurityContext.checkAuthentication();
    questionService.remove(new QuestionRemoveDTO(user.id(), questionId));
    return ok().build();
  }

  @GetMapping(Routes.Question.ROOT + "/{questionId}")
  public ResponseEntity<QuestionDetailsDTO> getQuestion(@PathVariable(name = "questionId") long questionId) {
    LOGGER.info("GET performed on /api/questions/" + questionId);
    return ok(questionService.findById(questionId));
  }

  @PostMapping(Routes.Question.ROOT + "/reviews")
  @RequireUser
  public ResponseEntity<ReviewQuestionResponseDTO> addReview(@Valid @RequestBody QuestionReviewCreateBodyDTO questionReviewCreateBodyDTO) {
      LOGGER.info("review => " + questionReviewCreateBodyDTO);
      var userDetail = SecurityContext.checkAuthentication();
      return ok(questionService.addReview(new QuestionReviewCreateDTO(userDetail.id(), questionReviewCreateBodyDTO.questionId(), questionReviewCreateBodyDTO.content(), questionReviewCreateBodyDTO.lineStart(), questionReviewCreateBodyDTO.lineEnd(), questionReviewCreateBodyDTO.tags())));
  }

    @GetMapping(Routes.Question.ROOT + "/followers")
    @RequireUser
    public ResponseEntity<List<QuestionDTO>> getQuestionsFromFollowers() {
        var userDetail = SecurityContext.checkAuthentication();

        return ResponseEntity.ok(questionService.getQuestionsFromFollows(userDetail.id()));
    }
}
