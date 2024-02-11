package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.NewQuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDetailDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

// TODO : Remplacer celui-ci (prod) par celui de Quentin TELLIER

@Service
public final class QuestionFakeService implements QuestionServiceAdapter {

    @Override
    public List<QuestionDetailDTO> allQuestions() {
        return Collections.emptyList();
    }

    @Override
    public QuestionDetailDTO registerQuestion(NewQuestionDTO newReviewDTO, long authorId) throws HttpException {
        return null;
    }

    @Override
    public boolean removeQuestion(long questionId) {
        return false;
    }

    @Override
    public Optional<QuestionDetailDTO> question(long questionId) {
        return Optional.empty();
    }

}
