package fr.pentagon.ugeoverflow.algorithm.search;

public class OnTitleContainsAlgorithm implements SearchAlgorithm{
    private final String title;
    private final SearchAlgorithm searchAlgorithm;
    public OnTitleContainsAlgorithm(SearchAlgorithm searchAlgorithm, String title) {
        this.searchAlgorithm = searchAlgorithm;
        this.title = title;
    }

    @Override
    public int apply(String token) {
        return title.contains(token) ? token.length() * token.length() + searchAlgorithm.apply(token) : searchAlgorithm.apply(token);
    }
}
