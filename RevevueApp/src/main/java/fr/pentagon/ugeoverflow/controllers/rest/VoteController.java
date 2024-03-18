package fr.pentagon.ugeoverflow.controllers.rest;

import fr.pentagon.ugeoverflow.config.authorization.RequireUser;
import fr.pentagon.ugeoverflow.config.security.SecurityContext;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.VoteDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.service.QuestionService;
import fr.pentagon.ugeoverflow.service.VoteServiceAdapter;
import fr.pentagon.ugeoverflow.utils.Routes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;
import java.util.Objects;
import java.util.logging.Logger;

@RestController
public class VoteController {

  private static final Logger LOGGER = Logger.getLogger(VoteController.class.getName());

  //private final VoteServiceAdapter voteService;
  private final QuestionService questionService;

  public VoteController(QuestionService questionService) {
    this.questionService = questionService;
  }

  @GetMapping(Routes.Vote.ROOT + "/questions/{questionId}")
  @RequireUser
  public ResponseEntity<VoteDTO> howManyVotes(@PathVariable(name = "questionId") long questionId) {
    LOGGER.info("GET performed on " + Routes.Vote.ROOT + "/" + questionId);
    SecurityContext.checkAuthentication();
    return ResponseEntity.ok(questionService.getVoteOnQuestionById(questionId));
  }

  @PostMapping(Routes.Vote.UP_VOTE + "/questions/{questionId}")
  @RequireUser
  public ResponseEntity<Void> upVoteQuestion(@PathVariable(name = "questionId") long questionId) {
    var user = SecurityContext.checkAuthentication();
    questionService.vote(user.id(), questionId, true);
    return ResponseEntity.ok().build();
  }

  @PostMapping(Routes.Vote.DOWN_VOTE + "/questions/{questionId}")
  @RequireUser
  public ResponseEntity<Void> downVoteQuestion(@PathVariable(name = "questionId") long questionId) {
    var user = SecurityContext.checkAuthentication();
    questionService.vote(user.id(), questionId, false);
    return ResponseEntity.ok().build();
  }
}
