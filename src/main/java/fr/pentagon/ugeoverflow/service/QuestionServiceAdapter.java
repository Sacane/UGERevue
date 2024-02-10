package fr.pentagon.ugeoverflow.service;


import fr.pentagon.ugeoverflow.controllers.dtos.requests.NewQuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDetailDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;

import java.util.List;
import java.util.Optional;

public interface QuestionServiceAdapter {

    List<QuestionDetailDTO> allQuestions();

    QuestionDetailDTO registerQuestion(NewQuestionDTO newReviewDTO, long authorId) throws HttpException;

    boolean removeQuestion(long questionId);

    Optional<QuestionDetailDTO> question(long questionId);

}
