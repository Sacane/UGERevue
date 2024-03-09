package fr.pentagon.ugeoverflow.algorithm.search;

import fr.pentagon.ugeoverflow.model.Question;

public class OnTitleContainsAlgorithm implements SearchAlgorithm{
    private final String title;
    private final SearchAlgorithm searchAlgorithm;
    public OnTitleContainsAlgorithm(SearchAlgorithm searchAlgorithm, String title) {
        this.searchAlgorithm = searchAlgorithm;
        this.title = title;
    }

    @Override
    public int apply(String token, Question question) {
        return title.contains(token) ? token.length() * token.length() + searchAlgorithm.apply(token, question) : searchAlgorithm.apply(token, question);
    }
}
