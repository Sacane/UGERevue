package fr.pentagon.ugeoverflow.algorithm.search;

import static fr.pentagon.ugeoverflow.algorithm.SearchQuestionByLabelStrategy.DESCRIPTION_POINT;
import static fr.pentagon.ugeoverflow.algorithm.SearchQuestionByLabelStrategy.countOccurrences;

public class OnDescriptionContainsAlgorithm implements SearchAlgorithm{
    private final SearchAlgorithm searchAlgorithm;
    private final String description;
    public OnDescriptionContainsAlgorithm(SearchAlgorithm searchAlgorithm, String description) {
        this.searchAlgorithm = searchAlgorithm;
        this.description = description;
    }

    @Override
    public int apply(String token) {
        return (countOccurrences(description, token) * (DESCRIPTION_POINT + token.length())) + searchAlgorithm.apply(token);
    }
}
