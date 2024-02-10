package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.NewQuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.VoteDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

// TODO : Remplacer celui-ci (prod) par celui de Quentin TELLIER

@Service
public final class QuestionService implements QuestionServiceAdapter {

    @Override
    public List<QuestionDTO> allQuestions() {
        return Collections.emptyList();
    }

    @Override
    public QuestionDTO registerQuestion(NewQuestionDTO newReviewDTO, long authorId) throws HttpException {
        return null;
    }

    @Override
    public boolean removeQuestion(long questionId) {
        return false;
    }

    @Override
    public Optional<QuestionDTO> question(long questionId) {
        return Optional.empty();
    }

}
