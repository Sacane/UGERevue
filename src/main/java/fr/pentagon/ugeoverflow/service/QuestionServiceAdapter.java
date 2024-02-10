package fr.pentagon.ugeoverflow.service;


import fr.pentagon.ugeoverflow.controllers.dtos.requests.NewQuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.VoteDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;

import java.util.List;
import java.util.Optional;

public interface QuestionServiceAdapter {

    List<QuestionDTO> allQuestions();

    QuestionDTO registerQuestion(NewQuestionDTO newReviewDTO, long authorId) throws HttpException;

    boolean removeQuestion(long questionId);

    Optional<QuestionDTO> question(long questionId);

    Optional<VoteDTO> votes(long questionId);

}
