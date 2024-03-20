package fr.pentagon.ugeoverflow.controllers.rest;

import fr.pentagon.revevue.common.exception.HttpException;
import fr.pentagon.ugeoverflow.config.authorization.RequireUser;
import fr.pentagon.ugeoverflow.config.security.AuthenticationChecker;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.*;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDetailsWithVotesDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionUpdateResponseDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewQuestionResponseDTO;
import fr.pentagon.ugeoverflow.service.QuestionService;
import fr.pentagon.ugeoverflow.utils.Routes;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    @PostMapping(Routes.Question.SEARCH)
    public ResponseEntity<List<QuestionDTO>> allQuestionByParameters(@RequestBody SearchQuestionDTO searchQuestionDTO) {
        LOGGER.info("Get performed on " + Routes.Question.SEARCH + " WITH BODY " + searchQuestionDTO);
        return ok(questionService.getQuestions(searchQuestionDTO.questionLabelSearch(), searchQuestionDTO.usernameSearch()));
    }

    @GetMapping(Routes.Question.CURRENT_USER)
    public ResponseEntity<List<QuestionDTO>> getAllQuestionsFromCurrentUser() {
        var user = AuthenticationChecker.checkAuthentication();
        return ResponseEntity.ok(questionService.getQuestionsFromUserByLogin(user.getUsername()));
    }

    @PostMapping(
            value = Routes.Question.ROOT
    )
    @RequireUser
    public ResponseEntity<Long> createQuestion(
            @RequestPart("title") @NotNull byte[] title,
            @RequestPart("description") @NotNull byte[] description,
            @RequestPart("javaFile") @NotNull MultipartFile javaFile,
            @RequestPart(value = "testFile", required = false) MultipartFile testFile
    ) throws IOException {
        LOGGER.info("POST performed on /api/questions");
        var userDetail = AuthenticationChecker.checkAuthentication();
        try {
            var question = new NewQuestionDTO(
                    new String(title, StandardCharsets.UTF_8),
                    new String(description, StandardCharsets.UTF_8),
                    javaFile.getBytes(),
                    testFile == null ? null : testFile.getBytes(),
                    javaFile.getOriginalFilename(),
                    testFile == null ? null : testFile.getOriginalFilename()
            );
            return ResponseEntity.ok(questionService.create(question, userDetail.id()));
        } catch (IOException e) {
            throw HttpException.badRequest("Error occurred when reading files.");
        }
    }

    @DeleteMapping(Routes.Question.ROOT + "/{questionId}")
    @RequireUser
    public ResponseEntity<Void> removeQuestion(@PathVariable(name = "questionId") long questionId) {
        LOGGER.info("DELETE performed on /api/questions/" + questionId);
        var user = AuthenticationChecker.checkAuthentication();
        questionService.remove(new QuestionRemoveDTO(user.id(), questionId));
        return ok().build();
    }

    @GetMapping(Routes.Question.ROOT + "/{questionId}")
    public ResponseEntity<QuestionDetailsWithVotesDTO> getQuestion(@PathVariable(name = "questionId") long questionId) {
        LOGGER.info("GET performed on /api/questions/" + questionId);
        var user = AuthenticationChecker.authentication();

        return ok(questionService.findByIdWithVotes(user, questionId));
    }

    @PostMapping(value = Routes.Question.ROOT + "/{questionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<QuestionUpdateResponseDTO> updateQuestion(@PathVariable(name = "questionId") long questionId,
                                                                    @RequestPart(value = "description", required = false) @Nullable String description,
                                                                    @RequestPart(value = "testFile", required = false) @Nullable MultipartFile testFile) {
        var user = AuthenticationChecker.checkAuthentication();
        try {
            return ok(questionService.update(user.id(), questionId, new QuestionUpdateDTO(description, (testFile != null) ? testFile.getBytes() : null, (testFile != null) ? testFile.getOriginalFilename() : null)));
        } catch (IOException e) {
            throw HttpException.badRequest("Erreur lors de l'ouverture du fichier de test");
        }
    }

    @PostMapping(Routes.Question.ROOT + "/reviews")
    @RequireUser
    public ResponseEntity<ReviewQuestionResponseDTO> addReview(@Valid @RequestBody QuestionReviewCreateBodyDTO questionReviewCreateBodyDTO, BindingResult bindingResult) {
        LOGGER.info("review => " + questionReviewCreateBodyDTO);
        var userDetail = AuthenticationChecker.checkAuthentication();
        if (bindingResult.hasErrors()) {
            throw HttpException.badRequest("La review est invalide");
        }
        return ok(questionService.addReview(new QuestionReviewCreateDTO(userDetail.id(), questionReviewCreateBodyDTO.questionId(), questionReviewCreateBodyDTO.content(), questionReviewCreateBodyDTO.lineStart(), questionReviewCreateBodyDTO.lineEnd(), questionReviewCreateBodyDTO.tags())));
    }

    @GetMapping(Routes.Question.ROOT + "/followers")
    @RequireUser
    public ResponseEntity<List<QuestionDTO>> getQuestionsFromFollowers() {
        var userDetail = AuthenticationChecker.checkAuthentication();

        return ResponseEntity.ok(questionService.getQuestionsFromFollows(userDetail.id()));
    }

    @DeleteMapping(Routes.Question.ROOT + "/{questionId}/cancelVote")
    @RequireUser
    public ResponseEntity<Void> cancelVoteQuestion(@PathVariable(name = "questionId") long questionId) {
        var user = AuthenticationChecker.checkAuthentication();

        questionService.cancelVote(user.id(), questionId);

        return ResponseEntity.ok().build();
    }
}
