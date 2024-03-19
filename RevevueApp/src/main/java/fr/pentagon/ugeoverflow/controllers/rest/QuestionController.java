package fr.pentagon.ugeoverflow.controllers.rest;

import fr.pentagon.revevue.common.exception.HttpException;
import fr.pentagon.ugeoverflow.config.authorization.RequireUser;
import fr.pentagon.ugeoverflow.config.security.SecurityContext;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.*;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDetailsWithVotesDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionUpdateResponseDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewQuestionResponseDTO;
import fr.pentagon.ugeoverflow.service.QuestionService;
import fr.pentagon.ugeoverflow.utils.Routes;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public ResponseEntity<List<QuestionDTO>> getAllQuestionsFromCurrentUser() {
        var user = SecurityContext.checkAuthentication();
        return ResponseEntity.ok(questionService.getQuestionsFromCurrentUser(user.getUsername()));
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
  public ResponseEntity<QuestionDetailsWithVotesDTO> getQuestion(@PathVariable(name = "questionId") long questionId) {
    LOGGER.info("GET performed on /api/questions/" + questionId);
    var user = SecurityContext.authentication();

    return ok(questionService.findByIdWithVotes(user, questionId));
  }
    @PostMapping(value = Routes.Question.ROOT + "/{questionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<QuestionUpdateResponseDTO> updateQuestion(@PathVariable(name = "questionId") long questionId,
                                                                    @RequestPart(value = "description", required = false) @Nullable String description,
                                                                    @RequestPart(value = "testFile", required = false) @Nullable MultipartFile testFile) {
        var user = SecurityContext.checkAuthentication();
        try {
          return ok(questionService.update(user.id(), questionId, new QuestionUpdateDTO(description, (testFile != null) ? testFile.getBytes() : null, (testFile != null) ? testFile.getOriginalFilename() : null)));
        }catch (IOException e){
          throw HttpException.badRequest("Erreur lors de l'ouverture du fichier de test");
        }
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

  @DeleteMapping(Routes.Question.ROOT + "/{questionId}/cancelVote")
  @RequireUser
  public ResponseEntity<Void> cancelVoteQuestion(@PathVariable(name = "questionId") long questionId) {
    var user = SecurityContext.checkAuthentication();

    questionService.cancelVote(user.id(), questionId);

    return ResponseEntity.ok().build();
  }
}
