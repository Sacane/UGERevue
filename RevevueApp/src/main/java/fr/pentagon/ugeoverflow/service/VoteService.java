package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.responses.VoteDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VoteService implements VoteServiceAdapter {

  @Override
  public Optional<VoteDTO> votes(long questionId) {
    return Optional.empty();
  }

  @Override
  public Optional<VoteDTO> votesQuestion(long questionId, boolean isUp) {
    return Optional.empty();
  }

  @Override
  public Optional<VoteDTO> votesReview(long questionId, boolean isUp) {
    return Optional.empty();
  }

  @Override
  public boolean hasVotedOnQuestion(long questionId, long userId) {
    return false;
  }

}
