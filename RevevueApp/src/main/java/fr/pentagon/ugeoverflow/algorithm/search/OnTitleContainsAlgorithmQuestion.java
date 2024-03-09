package fr.pentagon.ugeoverflow.algorithm.search;

import fr.pentagon.ugeoverflow.model.Question;

public class OnTitleContainsAlgorithmQuestion implements QuestionSearchAlgorithm {
    private final String title;
    private final QuestionSearchAlgorithm questionSearchAlgorithm;
    public OnTitleContainsAlgorithmQuestion(QuestionSearchAlgorithm questionSearchAlgorithm, String title) {
        this.questionSearchAlgorithm = questionSearchAlgorithm;
        this.title = title;
    }

    @Override
    public int apply(String token, Question question) {
        return title.contains(token) ? token.length() * token.length() + questionSearchAlgorithm.apply(token, question) : questionSearchAlgorithm.apply(token, question);
    }
}
