package fr.pentagon.ugeoverflow.controllers.rest;

import fr.pentagon.ugeoverflow.controllers.dtos.responses.VoteDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.service.VoteServiceAdapter;
import fr.pentagon.ugeoverflow.utils.Routes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.logging.Logger;

@RestController
public class VoteController {

  private static final Logger LOGGER = Logger.getLogger(VoteController.class.getName());

  private final VoteServiceAdapter voteService;

  public VoteController(VoteServiceAdapter voteService) {
    this.voteService = Objects.requireNonNull(voteService);
  }

  @GetMapping(Routes.Vote.ROOT + "/questions/{questionId}")
  public ResponseEntity<VoteDTO> howManyVotes(@PathVariable long questionId) {
    LOGGER.info("GET performed on " + Routes.Vote.ROOT + "/" + questionId);
    var votes = voteService.votes(questionId);
    if (votes.isPresent()) {
      return ResponseEntity.ok(votes.get());
    }
    throw HttpException.notFound("");
  }

  @PostMapping(Routes.Vote.UP_VOTE + "/questions/{questionId}")
  public ResponseEntity<Void> upVoteQuestion(@PathVariable long questionId) {
    var vote = voteService.votesQuestion(questionId, true);
    if (vote.isPresent()) {
      return ResponseEntity.ok().build();
    }
    throw HttpException.notFound("");
  }

  @PostMapping(Routes.Vote.DOWN_VOTE + "/questions/{questionId}")
  public ResponseEntity<Void> downVoteQuestion(@PathVariable long questionId) {
    voteService.votesQuestion(questionId, false);
    return ResponseEntity.ok().build();
  }


  @GetMapping(Routes.Vote.ROOT + "/questions/{questionId}/users/{userId}")
  public ResponseEntity<Boolean> hasAlreadyVotedQuestion(
      @PathVariable("questionId") long questionId,
      @PathVariable("userId") long userId
  ) {
    return ResponseEntity.ok(voteService.hasVotedOnQuestion(questionId, userId));
  }
}
