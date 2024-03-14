package fr.pentagon.ugeoverflow.algorithm.search;

import fr.pentagon.ugeoverflow.model.Question;

import static fr.pentagon.ugeoverflow.algorithm.SearchQuestionByLabelStrategy.DESCRIPTION_POINT;
import static fr.pentagon.ugeoverflow.algorithm.SearchQuestionByLabelStrategy.countOccurrences;

public class OnDescriptionContainsAlgorithmQuestion implements QuestionSearchAlgorithm {
    private final QuestionSearchAlgorithm questionSearchAlgorithm;
    private final String description;
    public OnDescriptionContainsAlgorithmQuestion(QuestionSearchAlgorithm questionSearchAlgorithm, String description) {
        this.questionSearchAlgorithm = questionSearchAlgorithm;
        this.description = description;
    }

    @Override
    public int apply(String token, Question question) {
        return (countOccurrences(description, token) * (DESCRIPTION_POINT + token.length())) + questionSearchAlgorithm.apply(token, question);
    }
}
