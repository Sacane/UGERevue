package fr.pentagon.ugeoverflow.controllers;

import fr.pentagon.ugeoverflow.controllers.dtos.responses.VoteDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.service.VoteServiceAdapter;
import fr.pentagon.ugeoverflow.utils.Routes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.logging.Logger;

@RestController
public final class VoteController {

    private static final Logger LOGGER = Logger.getLogger(VoteController.class.getName());

    private final VoteServiceAdapter voteService;

    public VoteController(VoteServiceAdapter voteService){
        this.voteService = Objects.requireNonNull(voteService);
    }

    @GetMapping(Routes.Vote.WITH_QUESTION_ID)
    public ResponseEntity<VoteDTO> howManyVotes(@PathVariable long questionId){
        LOGGER.info("GET performed on /api/votes/questions/" + questionId);
        var votes = voteService.votes(questionId);
        if(votes.isPresent()){
            return ResponseEntity.ok(votes.get());
        }
        throw HttpException.notFound("");
    }

    @PostMapping(Routes.Vote.UP_VOTE)
    public ResponseEntity<Void> upVoteQuestion(@PathVariable long questionId){
        var vote = voteService.votesQuestion(questionId, true);
        if(vote.isPresent()){
            return ResponseEntity.ok().build();
        }
       throw HttpException.notFound("");
    }

    @PostMapping(Routes.Vote.DOWN_VOTE)
    public ResponseEntity<Void> downVoteQuestion(@PathVariable long questionId){
        voteService.votesQuestion(questionId, false);
        return ResponseEntity.ok().build();
    }


    @GetMapping(Routes.Vote.HAS_VOTED_ON_QUESTION)
    public ResponseEntity<Boolean> hasAlreadyVotedQuestion(
            @PathVariable("questionId") long questionId,
            @PathVariable("userId") long userId
    ) {
        return ResponseEntity.ok(voteService.hasVotedOnQuestion(questionId, userId));
    }
}
