package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.responses.VoteDTO;

import java.util.Optional;

public interface VoteServiceAdapter {

    Optional<VoteDTO> votes(long questionId);
    Optional<VoteDTO> votesQuestion(long questionId, boolean isUp);
    Optional<VoteDTO> votesReview(long questionId, boolean isUp);

    boolean hasVotedOnQuestion(long questionId, long userId);
}
